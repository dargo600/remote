package com.example.remotecontrol.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.remotecontrol.util.*;

public class DBHelper extends SQLiteOpenHelper  {

    private final String TAG = DBHelper.class.getSimpleName();
    private final int MAX_DB_TRIES = 3;
    private static final String DB_NAME = "remote";
    private static final int DB_VERSION = 1;

    public DBHelper(Context context) {
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

    public void insertRequestedConfig(SQLiteDatabase db, String name)
    {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert("requested_configs", null, values);
    }

    public void insertDeviceConfig(SQLiteDatabase db, int deviceConfigId,
                                   String name)
    {
        ContentValues values = new ContentValues();
        values.put("device_config_id", deviceConfigId);
        values.put("name", name);
        db.insert("device_configs", null, values);
    }

    public void insertDevice(SQLiteDatabase db, String deviceType,
                              String manufacturer, String modelNum,
                              int deviceConfigId)
    {
        ContentValues values = new ContentValues();
        values.put("device_type", deviceType);
        values.put("manufacturer", manufacturer);
        values.put("model_num", modelNum);
        values.put("device_config_id", deviceConfigId);
        db.insert("devices", null, values);
    }

    public void insertButton(SQLiteDatabase db, String type,
                             String prontoCode, int deviceConfigId)
    {
        ContentValues values = new ContentValues();
        values.put("rc_type", type);
        values.put("ir_code", prontoCode);
        values.put("device_config_id", deviceConfigId);
        db.insert("rc_buttons", null, values);
    }


    public boolean isDBEmpty() throws DBReadException {
        int count = 0;
        SQLiteDatabase db = getReadableDB();

        if (db != null) {
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

    public SQLiteDatabase getReadableDB() throws DBReadException {
        SQLiteDatabase db = null;
        for (int attempts = 1; true; attempts++) {
            try {
                db = getReadableDatabase();
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

        return db;
    }

    private void attemptThreadDelay(int seconds) {
        try {
            Thread.sleep(1000 * seconds);
        } catch (InterruptedException ie) {
            LogUtil.logError(TAG, "Failed to sleep " + seconds + " s ");
        }
    }

    private void handleEmptyDetectFailed() throws DBReadException {
        String msg = "Failed to determine if db was empty after " + MAX_DB_TRIES + " tries";
        LogUtil.logError(TAG, msg);
        throw new DBReadException(msg);
    }

    public void addDefaultRequestedConfigs() {
        SQLiteDatabase db = getWritableDatabase();
        if (db != null) {
            insertRequestedConfig(db, "samsungConfig1");
            insertRequestedConfig(db, "appleConfig1");
            db.close();
        }
    }
}
