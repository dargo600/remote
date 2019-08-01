package com.example.remotecontrol.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.remotecontrol.util.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;

public class ConfigRetrieverImpl implements ConfigRetriever {

    private final String TAG = ConfigRetrieverImpl.class.getSimpleName();

    private ArrayList<String> desiredConfigs;
    private HashMap<String, DeviceConfiguration> requestedConfigs;

    private DBHelper dbHelper;
    private RSTHandler rstHandler;

    public ConfigRetrieverImpl(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        rstHandler = new RSTHandler(dbHelper);
        requestedConfigs = new HashMap<>();
        desiredConfigs = new ArrayList<>();
    }

    /**
     * This seems to get called twice for some reason not too sure why.  We
     * just do nothing on the second try otherwise there are problems */
    public HashMap<String, DeviceConfiguration>
    getRequestedConfigs() throws Exception {
        LogUtil.logDebug(TAG, "Attempting Requested Config Update");
        if (desiredConfigs.isEmpty()) {
            initDBIfEmpty();
            boolean isInitialized = initDeviceConfigs();
            if (isInitialized) {
                requestedConfigs = getRequestedConfigs();
            }
        } else {
            LogUtil.logDebug(TAG, "Requested Config Already Retrieved");
        }

        return requestedConfigs;
    }

    private void initDBIfEmpty() throws Exception  {
        LogUtil.logDebug(TAG, "Downloading Database...");
        if(dbHelper.isDBEmpty()){
            dbHelper.addDefaultRequestedConfigs();
            LogUtil.logDebug(TAG, "Downloading Database...");
            rstHandler.downloadAndProcessConfigData();
        }

        LogUtil.logDebug(TAG, "Database initialized");
    }

    public boolean
    initDeviceConfigs() throws SQLiteException {
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
            LogUtil.logDebug(TAG, "Desired Config: " +
                    nameText + " " + desiredConfigs.size());
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
}
