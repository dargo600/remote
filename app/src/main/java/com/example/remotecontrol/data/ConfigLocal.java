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

    public boolean isConfigValid() throws DBReadException {
        boolean valid;
        try {
            if (!dbHelper.initDesiredConfigs()) {
                desiredConfigs = configManager.getDefaultDesiredConfigs();
                dbHelper.addDefaultDesiredConfigs(desiredConfigs);
            }
            desiredConfigs = dbHelper.getDesiredConfigs();
            valid = dbHelper.isDeviceConfigsValid();
            dbHelper.isDevicesValid();
        } catch (Exception e) {
            throw new DBReadException("Error: " + e.getMessage());
        }
        if (valid) {
            LogUtil.logDebug(TAG, "Database Detected");
        }

        return valid;
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
        int configsInitialized = 0;
        dbHelper.initRead();
        for (String configName : desiredConfigs) {
            dbHelper.cacheConfig(configName);
            configsInitialized++;
        }
        dbHelper.closeDB();
        LogUtil.logDebug(TAG, "Initialized " + configsInitialized + " configs");
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
