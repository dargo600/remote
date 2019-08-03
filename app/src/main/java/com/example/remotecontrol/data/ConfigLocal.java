package com.example.remotecontrol.data;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.remotecontrol.util.*;

public class ConfigLocal {
    private final String TAG = ConfigLocal.class.getSimpleName();

    private DBHelper dbHelper;

    private ArrayList<String> desiredConfigs;
    private HashMap<String, DeviceConfiguration> requestedConfigs;

    public ConfigLocal(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
        desiredConfigs = new ArrayList<>();
        requestedConfigs = new HashMap<>();
    }

    public boolean isConfigEmpty() throws DBReadException {
        boolean isEmpty;
        try {
            isEmpty = dbHelper.isDBEmpty();
            LogUtil.logDebug(TAG, "FIXME " + isEmpty);
        } catch (Exception e) {
            throw new DBReadException("Error: " + e.getMessage());
        }
        if (!isEmpty) {
            LogUtil.logDebug(TAG, "Database Detected");
        }

        return isEmpty;
    }

    public void addDefaultDesiredConfig() throws Exception {
        dbHelper.addDefaultRequestedConfigs();
    }

    public void initFromLocal() throws ParseConfigException {
        try {
            initDeviceConfigs();
            requestedConfigs = dbHelper.getRequestedConfigs();
            if (requestedConfigs.isEmpty()) {
                throw new ParseConfigException("Could not get requested Configs");
            }
        } catch (Exception e) {
            throw new ParseConfigException("Failed to ParseDB " + e.getMessage());
        }
    }

    private void
    initDeviceConfigs() throws Exception {
        dbHelper.initRead();
        desiredConfigs = dbHelper.initializeDesiredConfigs();
        for (String configName : desiredConfigs) {
            dbHelper.cacheConfig(configName);
        }
        dbHelper.closeDB();
        if (desiredConfigs.isEmpty()) {
            throw new ParseConfigException("Desired configs are empty");
        }
    }

    public HashMap<String, DeviceConfiguration> getDeviceConfigs() {
        return requestedConfigs;
    }
}
