package com.example.remotecontrol.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import com.example.remotecontrol.util.*;

import java.util.ArrayList;
import java.util.HashMap;

public class DBHelperImpl implements DBHelper {
    public static final String DESIRED_CONFIGS_TABLE_NAME = "desired_configs";
    public static final String DESIRED_CONFIGS_COLUMN_ID = "_id";
    public static final String DESIRED_CONFIGS_COLUMN_NAME = "name";

    public static final String DEVICE_CONFIGS_TABLE_NAME = "device_configs";
    public static final String DEVICE_CONFIGS_COLUMN_ID = "device_config_id";
    public static final String DEVICE_CONFIGS_COLUMN_NAME = "name";

    public static final String DEVICES_TABLE_NAME = "devices";
    public static final String DEVICES_COLUMN_ID = "_id";
    public static final String DEVICES_COLUMN_DEVICE_TYPE = "device_type";
    public static final String DEVICES_COLUMN_MANUFACTURER = "manufacturer";
    public static final String DEVICES_COLUMN_MODEL_NUM = "model_num";
    public static final String DEVICES_COLUMN_DEVICE_CONFIG_ID = "device_config_id";

    public static final String RC_BUTTONS_TABLE_NAME = "rc_buttons";
    public static final String RC_BUTTONS_COLUMN_ID = "_id";
    public static final String RC_BUTTONS_COLUMN_RADIO_BUTTON_TYPE = "rc_type";
    public static final String RC_BUTTONS_COLUMN_PRONTO_CODE = "ir_code";
    public static final String RC_BUTTONS_COLUMN_DEVICE_CONFIG_ID = "device_config_id";

    private static final String TAG = DBHelperImpl.class.getSimpleName();
    private static final int MAX_DB_TRIES = 3;

    private DBConnector dbConnector;
    private SQLiteDatabase db;
    private ArrayList<String> desiredConfigs;
    private HashMap<String, DeviceConfiguration> requestedConfigs;

    public DBHelperImpl(DBConnector dbConnector) {
       this.dbConnector = dbConnector;
       desiredConfigs = new ArrayList<>();
       requestedConfigs = new HashMap<>();
    }

    public void setDesiredConfigs(ArrayList<String> desiredConfigs) {
        this.desiredConfigs = desiredConfigs;
    }

    public ArrayList<String> getDesiredConfigs() {
        return desiredConfigs;
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

    public void checkTableInitialized() throws Exception {
        boolean dbConnected = getReadableDB();
        int tableCount = 0;

        if (dbConnected) {
            String query = "SELECT name from sqlite_master";
            Cursor cursor = db.rawQuery(query, null);
            tableCount = cursor.getCount();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String name = cursor.getString(0);
                LogUtil.logDebug(TAG, "Found Table " + name);
                cursor.moveToNext();
            }
            cursor.close();
        }
        LogUtil.logDebug(TAG, "Found " + tableCount + " tables");
    }

    public boolean initDesiredConfigs() throws Exception {
        int reqCount = 0;
        checkTableInitialized();
        boolean dbConnected = getReadableDB();

        if (dbConnected) {
            Cursor cursor = db.query(DESIRED_CONFIGS_TABLE_NAME,
                    new String[]{DESIRED_CONFIGS_COLUMN_NAME},
                    null, null, null,
                    null, null);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                String nameText = cursor.getString(0);
                desiredConfigs.add(nameText);
                LogUtil.logDebug(TAG, "Desired Config: " + nameText);
                cursor.moveToNext();
                reqCount++;
            }
            cursor.close();
            db.close();
        }

        LogUtil.logDebug(TAG, "Desired Config: " + desiredConfigs.size());

        return reqCount > 0;
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

    public boolean isDeviceConfigsValid() throws Exception {
        int desiredCount = desiredConfigs.size();
        int deviceConfigCount = 0;
        boolean dbConnected = getReadableDB();

        if (dbConnected) {
            Cursor cursor = db.query(DEVICE_CONFIGS_TABLE_NAME,
                    new String[]{DEVICE_CONFIGS_COLUMN_NAME},
                    null, null, null,
                    null, null);
            cursor.moveToFirst();
            deviceConfigCount = cursor.getCount();
            cursor.close();
            db.close();
        }
        if (deviceConfigCount == 0) {
            LogUtil.logError(TAG, "Device Config should be greater than 0");
        } else {
            LogUtil.logDebug(TAG, "Initialized " + deviceConfigCount + " device_configs");
        }

        return desiredCount < deviceConfigCount && desiredCount > 0;
    }

    public boolean isDevicesValid() throws Exception {
        int devicesCount = 0;
        initRead();

        Cursor cursor = db.query(DEVICES_TABLE_NAME,
                new String[]{DEVICES_COLUMN_DEVICE_TYPE},
                null, null, null,
                null, null);
        cursor.moveToFirst();
        devicesCount = cursor.getCount();
        cursor.close();
        db.close();
        if (devicesCount == 0) {
            LogUtil.logError(TAG, "Devices should be greater than 0");
        } else {
            LogUtil.logDebug(TAG, "Initialized " + devicesCount + " devices");
        }

        return devicesCount > 0;
    }

    public void addDefaultDesiredConfigs(ArrayList<String> newDesiredConfigs) throws Exception {
        db = dbConnector.initWrite();
        for (String config : newDesiredConfigs) {
            insertDesiredConfig(config);
        }
        closeDB();
        desiredConfigs = newDesiredConfigs;
    }

    private void insertDesiredConfig(String name)
    {
        ContentValues values = dbConnector.makeContentValues();
        values.put("name", name);
        db.insert("desired_configs", null, values);
    }

    public ArrayList<String> initializeDesiredConfigs() throws SQLiteException {
        Cursor cursor = db.query(DESIRED_CONFIGS_TABLE_NAME,
                new String[]{DESIRED_CONFIGS_COLUMN_NAME},
                null, null, null,
                null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String nameText = cursor.getString(0);
            desiredConfigs.add(nameText);
            LogUtil.logDebug(TAG, "Desired Config: " + nameText + " " + desiredConfigs.size());
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
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            int deviceConfigId = cursor.getInt(0);
            String deviceType = cursor.getString(1);
            DeviceConfiguration dc = new DeviceConfiguration(deviceConfigId,
                    configName, deviceType);
            addButtonsToConfig(dc);
            requestedConfigs.put(configName, dc);
            LogUtil.logDebug(TAG, "Added " + configName + " as a device configuration");
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
        int buttonCount = 0;
        while (!cursor.isAfterLast()) {
            String rcType = cursor.getString(0);
            String rcIRCode = cursor.getString(1);
            RCButton rc = new RCButton(rcType, rcIRCode);
            dc.addRCButton(rcType, rc);
            cursor.moveToNext();
            buttonCount++;
        }
        cursor.close();
        LogUtil.logDebug(TAG, "Added " + buttonCount + " buttons for configuration");

        return dc;
    }

    public HashMap<String, DeviceConfiguration> getRequestedConfigs() {
        return requestedConfigs;
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
}
