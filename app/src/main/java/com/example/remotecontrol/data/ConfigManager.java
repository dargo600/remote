package com.example.remotecontrol.data;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.remotecontrol.util.LogUtil;

public class ConfigManager {
    private final String TAG = ConfigManager.class.getSimpleName();

    ArrayList<String> defaultDesiredConfigs;
    private ConfigRetriever retriever;
    private ConfigLocal localConfig;

    public ConfigManager(ConfigRetriever retriever, ConfigLocal local) {
        this.retriever = retriever;
        this.localConfig = local;
        local.setConfigManager(this);
        defaultDesiredConfigs = new ArrayList<String>();
        defaultDesiredConfigs.add("samsungConfig1");
        defaultDesiredConfigs.add("appleConfig1");
    }

    public void initLocal() throws Exception {
        if (!localConfig.isConfigValid()) {
            retriever.syncToRemote();
        }
        localConfig.initFromLocal();
        LogUtil.logDebug(TAG, "Finished Initialization");
    }

    public HashMap<String, DeviceConfiguration> getRequestedConfigs() {
        return localConfig.getDeviceConfigs();
    }

    public ArrayList<String> getDefaultDesiredConfigs() {
        return defaultDesiredConfigs;
    }
}
