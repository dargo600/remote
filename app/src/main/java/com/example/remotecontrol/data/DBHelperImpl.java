package com.example.remotecontrol.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.remotecontrol.util.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelperImpl extends SQLiteOpenHelper implements DBHelper {

    private static final String TAG = DBHelperImpl.class.getSimpleName();
    private static final int MAX_DB_TRIES = 3;
    private static final String DB_NAME = "remote";
    private static final int DB_VERSION = 1;

    private ArrayList<String> desiredConfigs = new ArrayList<>();
    private HashMap<String, DeviceConfiguration> requestedConfigs = new HashMap<>();
    private SQLiteDatabase db;

    public DBHelperImpl(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("drop table if exists requested_configs");
            db.execSQL("drop table if exists device_configs;");
            db.execSQL("drop table if exists devices;");
            db.execSQL("drop table if exists rc_buttons;");

            db.execSQL("create table requested_configs "
                     + "(_id integer primary key autoincrement, "
                     + "name text)");
            db.execSQL("create table device_configs "
                     + "(device_config_id integer primary key, "
                     + "name text);");
            db.execSQL("create table devices "
                     + "(_id integer primary key autoincrement, "
                     + "device_type text, "
                     + "manufacturer text, "
                     + "model_num text, "
                     + "device_config_id integer, "
                     + "constraint fk_device_configs "
                     + " foreign key (device_config_id) "
                     + " references device_configs(device_config_id)"
                     + ");");
            db.execSQL("create table rc_buttons "
                    +  "(_id integer primary key autoincrement, "
                    + "rc_type text, "
                    + "ir_code text, "
                    + "device_config_id integer, "
                    + "constraint fk_device_configs "
                    + " foreign key (device_config_id) "
                    + " references device_configs(device_config_id));");
        }
    }

    public void initRead() {
        db = getReadableDatabase();
    }

    public void initWrite() {
        db = getWritableDatabase();
    }

    public void closeDB() {
        db.close();
    }

    public void insertRequestedConfig(String name)
    {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert("requested_configs", null, values);
    }

    public void insertDeviceConfig(int deviceConfigId, String name)
    {
        ContentValues values = new ContentValues();
        values.put("device_config_id", deviceConfigId);
        values.put("name", name);
        db.insert("device_configs", null, values);
    }

    public void insertDevice(String deviceType, String manufacturer,
                             String modelNum, int deviceConfigId)
    {
        ContentValues values = new ContentValues();
        values.put("device_type", deviceType);
        values.put("manufacturer", manufacturer);
        values.put("model_num", modelNum);
        values.put("device_config_id", deviceConfigId);
        db.insert("devices", null, values);
    }

    public void insertButton(String type, String prontoCode, int deviceConfigId)
    {
        ContentValues values = new ContentValues();
        values.put("rc_type", type);
        values.put("ir_code", prontoCode);
        values.put("device_config_id", deviceConfigId);
        db.insert("rc_buttons", null, values);
    }

    public boolean isDBEmpty() throws Exception {
        int count = 0;
        boolean dbConnected = getReadableDB();

        if (dbConnected) {
            Cursor cursor = db.query("requested_configs",
                    new String[]{"_id", "name"},
                    null, null, null,
                    null, null);
            count = cursor.getCount();
            cursor.close();
            db.close();
        }

        return count == 0;
    }

    public boolean getReadableDB() throws DBReadException {
        boolean dbConnected = false;
        for (int attempts = 1; true; attempts++) {
            try {
                db = getReadableDatabase();
                dbConnected = true;
                break;
            } catch (SQLiteException e) {
                if (attempts < (MAX_DB_TRIES - 1)) {
                    int seconds = 1;
                    attemptThreadDelay(seconds);
                } else {
                    handleEmptyDetectFailed();
                    break;
                }
            }
        }

        return dbConnected;
    }

    private void attemptThreadDelay(int seconds) {
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException ie) {
            LogUtil.logError(TAG, "Failed to sleep " + seconds + " s ");
        }
    }

    private void handleEmptyDetectFailed() throws DBReadException {
        String msg = "Failed after " + MAX_DB_TRIES + " DB read attempts";
        LogUtil.logError(TAG, msg);
        throw new DBReadException(msg);
    }

    public void addDefaultRequestedConfigs() {
        db = getWritableDatabase();
        if (db != null) {
            insertRequestedConfig("samsungConfig1");
            insertRequestedConfig("appleConfig1");
            db.close();
        }
    }

    public ArrayList<String> initializeDesiredConfigs() throws SQLiteException {
        Cursor cursor = db.query("requested_configs",
                new String[]{"name"},
                null, null, null,
                null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String nameText = cursor.getString(0);
            desiredConfigs.add(nameText);
            LogUtil.logDebug(TAG, "Desired Config: " +
                    nameText + " " + desiredConfigs.size());
            cursor.moveToNext();
        }
        cursor.close();

        return desiredConfigs;
    }

    public void cacheConfig(String configName) throws Exception {
        String query = "SELECT dc.device_config_id, d.device_type FROM " +
                "device_configs AS dc, devices AS d WHERE " +
                "d.device_config_id==dc.device_config_id AND dc.name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{configName});

        if (cursor.moveToFirst()) {
            int deviceConfigId = cursor.getInt(0);
            String deviceType = cursor.getString(1);
            DeviceConfiguration dc = new DeviceConfiguration(deviceConfigId,
                    configName, deviceType);
            addButtonsToConfig(dc);
            requestedConfigs.put(configName, dc);
        }
        cursor.close();
    }

    private DeviceConfiguration
    addButtonsToConfig(DeviceConfiguration dc) throws Exception {
        String query = "SELECT r.rc_type, r.ir_code FROM rc_buttons AS r " +
                "WHERE r.device_config_id= ?";
        Cursor cursor = db.rawQuery(query,
                new String[]{Integer.toString(dc.getDeviceConfigID())});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String rcType = cursor.getString(0);
            String rcIRCode = cursor.getString(1);
            RCButton rc = new RCButton(rcType, rcIRCode);
            dc.addRCButton(rcType, rc);
            cursor.moveToNext();
        }
        cursor.close();

        return dc;
    }

    public HashMap<String, DeviceConfiguration> getRequestedConfigs() {
        return requestedConfigs;
    }
}
