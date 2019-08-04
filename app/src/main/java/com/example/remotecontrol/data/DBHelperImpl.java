package com.example.remotecontrol.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.remotecontrol.util.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelperImpl implements DBHelper {

    private static final String TAG = DBHelperImpl.class.getSimpleName();
    private DBConnector dbConnector;
    private SQLiteDatabase db;
    private static final int MAX_DB_TRIES = 3;
    private ArrayList<String> desiredConfigs = new ArrayList<>();
    private HashMap<String, DeviceConfiguration> requestedConfigs = new HashMap<>();

    public DBHelperImpl(DBConnector dbConnector) {
       this.dbConnector = dbConnector;
    }

    public void setDesiredConfigs(ArrayList<String> desiredConfigs) {
        this.desiredConfigs = desiredConfigs;
    }

    public void setRequestedConfigs(HashMap<String, DeviceConfiguration> requestedConfigs) {
        this.requestedConfigs = requestedConfigs;
    }

    public void initRead() throws Exception {
        db = dbConnector.initRead();
    }

    public void initWrite() throws Exception {
        db = dbConnector.initWrite();
    }

    public void closeDB() {
        db.close();
    }

    public void insertRequestedConfig(String name)
    {
        ContentValues values = dbConnector.makeContentValues();
        values.put("name", name);
        db.insert("requested_configs", null, values);
    }

    public void insertDeviceConfig(int deviceConfigId, String name)
    {
        ContentValues values = dbConnector.makeContentValues();
        values.put("device_config_id", deviceConfigId);
        values.put("name", name);
        db.insert("device_configs", null, values);
    }

    public void
    insertDevice(String deviceType, String manufacturer, String modelNum,
                 int deviceConfigId)
    {
        ContentValues values = dbConnector.makeContentValues();
        values.put("device_type", deviceType);
        values.put("manufacturer", manufacturer);
        values.put("model_num", modelNum);
        values.put("device_config_id", deviceConfigId);
        db.insert("devices", null, values);
    }

    public void insertButton(String type, String prontoCode, int deviceConfigId)
    {
        ContentValues values = dbConnector.makeContentValues();
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
                db = dbConnector.initRead();
                dbConnected = true;
                break;
            } catch (Exception e) {
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

    public void addDefaultDesiredConfigs(ArrayList<String> newRequestedConfigs) throws Exception {
        db = dbConnector.initWrite();
        for (String config : newRequestedConfigs) {
            insertRequestedConfig(config);
        }
        closeDB();
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
