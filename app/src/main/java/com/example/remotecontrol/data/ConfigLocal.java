package com.example.remotecontrol.data;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.remotecontrol.util.*;

public class ConfigLocal {
    private final String TAG = ConfigLocal.class.getSimpleName();

    private ConfigManager configManager;
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
        } catch (Exception e) {
            throw new DBReadException("Error: " + e.getMessage());
        }
        if (!isEmpty) {
            LogUtil.logDebug(TAG, "Database Detected");
        }

        return isEmpty;
    }

    public void addDefaultDesiredConfig() throws Exception {
        try {
            dbHelper.addDefaultDesiredConfigs(configManager.getDefaultDesiredConfigs());
        } catch (Exception e) {
            throw new ParseConfigException("Failed to set Default Desired Config");
        }
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
            throw new ParseConfigException("Accessing Default Desired Configs Failed");
        }
    }

    public HashMap<String, DeviceConfiguration> getDeviceConfigs() {
        return requestedConfigs;
    }

    public void setConfigManager(ConfigManager cm) {
        this.configManager = cm;
    }
}
