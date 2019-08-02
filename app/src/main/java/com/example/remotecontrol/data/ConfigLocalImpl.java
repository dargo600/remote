package com.example.remotecontrol.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.remotecontrol.util.*;

public class ConfigLocalImpl implements ConfigLocal {
    private final String TAG = ConfigLocalImpl.class.getSimpleName();

    private DBHelper dbHelper;

    private ArrayList<String> desiredConfigs;
    private HashMap<String, DeviceConfiguration> requestedConfigs;

    public ConfigLocalImpl(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        desiredConfigs = new ArrayList<>();
        requestedConfigs = new HashMap<>();
    }

    public boolean isConfigEmpty() throws DBReadException {
        boolean ret = true;
        try {
            ret = dbHelper.isDBEmpty();
        } catch (Exception e) {
            LogUtil.logError(TAG, "Error: " + e);
            throw new DBReadException("Failed to Determine Config");
        }

        return ret;
    }

    public void addDefaultDesiredConfig() throws Exception {
        dbHelper.addDefaultRequestedConfigs();
    }

    public void initFromLocal() throws ParseConfigException {
        try {
            initDeviceConfigs();
            if (requestedConfigs.isEmpty()) {
                throw new ParseConfigException("Could not get requested Configs");
            }
        } catch (Exception e) {
            throw new ParseConfigException("Failed to ParseDB" + e.getMessage());
        }
    }

    private void
    initDeviceConfigs() throws Exception {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        initializeDesiredConfigs(db);
        for (String configName : desiredConfigs) {
            populateDeviceConfig(db, configName);
        }
        db.close();
        if (desiredConfigs.isEmpty()) {
            throw new ParseConfigException("Desired configs are empty");
        }
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
    populateDeviceConfig(SQLiteDatabase db, String configName) throws Exception {
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
    addButtonsToConfig(SQLiteDatabase db, DeviceConfiguration dc) throws Exception {
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

    public HashMap<String, DeviceConfiguration> getDeviceConfigs() {
        return requestedConfigs;
    }
}
