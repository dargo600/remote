package com.example.remotecontrol;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigManager {

    private final int MAX_DB_TRIES = 3;
    private final String TAG = ConfigManager.class.getSimpleName();

    private ArrayList<String> desiredConfigs;
    private HashMap<String, DeviceConfiguration> requestedConfigs;
    private RSTHandler rstHandler;
    private DBHelper dbHelper;
    private Context mainContext;

    public ConfigManager(DBHelper dbHelper, Context mainContext) {
        this.dbHelper = dbHelper;
        this.mainContext = mainContext;
        requestedConfigs = new HashMap<String, DeviceConfiguration>();
        desiredConfigs = new ArrayList<String>();
    }

    public boolean processConfiguration() throws JSONException {
        rstHandler = new RSTHandler(dbHelper);
        if (isDBEmpty()) {
            addDefaultRequestedConfigsToDB();
            if (!downloadAndProcessConfigData()) {
                return false;
            }
        }

        return true;
    }

    private boolean isDBEmpty() {
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

    private SQLiteDatabase getReadableDB() {
        SQLiteDatabase db = null;
        int attempts = 1;
        for (; true; attempts++) {
            try {
                db = dbHelper.getReadableDatabase();
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
            Log.e(TAG, "Failed to sleep " + seconds + " s ");
        }
    }

    private void handleEmptyDetectFailed() {
        Log.e(TAG, "Failed to determine if db was empty ");
    }

    private void addDefaultRequestedConfigsToDB() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        dbHelper.insertRequestedConfig(db, "samsungConfig1");
        dbHelper.insertRequestedConfig(db, "appleConfig1");
        db.close();
    }

    private boolean
    downloadAndProcessConfigData() throws JSONException, SQLiteException {
        return (processDataType("device_configs") &&
                processDataType("devices"));
    }

    private boolean
    processDataType(String dataType) throws JSONException, SQLiteException {
        HttpHandler httpHandler = new HttpHandler();
        String url = "http://phaedra:5000/api/" + dataType;
        String jsonStr = httpHandler.processURL(url);

        Log.e(TAG, "Response from url: " + url + " and length " +
                jsonStr.length());
        if (jsonStr == null) {
            return false;
        }
        if (dataType.equals("device_configs")) {
            rstHandler.parseDeviceConfigurations(jsonStr);
        } else if (dataType.equals("devices")) {
            rstHandler.parseDevices(jsonStr);
        } else {
            return false;
        }

        return true;
    }

    public boolean
    initializeDeviceConfigurations() throws SQLiteException {
        boolean requestedConfigsInitialized = false;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        initializeDesiredConfigs(db);
        for (String configName : desiredConfigs) {
           populateDeviceConfig(db, configName);
           requestedConfigsInitialized = true;
        }
        db.close();

        return requestedConfigsInitialized;
    }

    private void
    initializeDesiredConfigs(SQLiteDatabase db) throws SQLiteException {
        Cursor cursor = db.query("requested_configs",
                new String[]{"name"},
                null, null, null,
                null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String nameText = cursor.getString(0);
            desiredConfigs.add(nameText);
            cursor.moveToNext();
        }
        cursor.close();
    }

    private void
    populateDeviceConfig(SQLiteDatabase db, String configName) {
        String query = "SELECT dc.device_config_id, d.device_type FROM " +
                "device_configs AS dc, devices AS d WHERE " +
                "d.device_config_id==dc.device_config_id AND dc.name = ?";
        Cursor cursor = db.rawQuery(query, new String[]{configName});

        if (cursor.moveToFirst()) {
            int deviceConfigId = cursor.getInt(0);
            String deviceType = cursor.getString(1);
            DeviceConfiguration dc = new DeviceConfiguration(deviceConfigId,
                    configName, deviceType);
            addButtonsToConfig(db, dc);
            requestedConfigs.put(configName, dc);
        }
        cursor.close();
    }

    private DeviceConfiguration
    addButtonsToConfig(SQLiteDatabase db, DeviceConfiguration dc) {
        String query = "SELECT r.rc_type, r.ir_code FROM rc_buttons AS r " +
                "WHERE r.device_config_id= ?";
        Cursor cursor = db.rawQuery(query,
                new String[] {Integer.toString(dc.getDeviceConfigID())});
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
