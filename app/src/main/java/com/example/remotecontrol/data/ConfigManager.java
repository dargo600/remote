package com.example.remotecontrol.data;

import java.util.HashMap;

import com.example.remotecontrol.util.LogUtil;

public class ConfigManager {
    private final String TAG = ConfigManager.class.getSimpleName();

    private ConfigRetriever retriever;
    private ConfigLocal localConfig;

    public ConfigManager(ConfigRetriever retriever, ConfigLocal local) {
        this.retriever = retriever;
        this.localConfig = local;
    }

    public void initLocal() throws Exception {
        if (isLocalEmpty()) {
            processEmptyLocal();
        }
        initFromLocal();
    }

    private boolean isLocalEmpty() throws Exception {
       return localConfig.isConfigEmpty();
    }

    private void processEmptyLocal() throws Exception {
        localConfig.addDefaultDesiredConfig();
        retriever.syncToRemote();
    }

    private void initFromLocal()  throws Exception {
        localConfig.initFromLocal();
        LogUtil.logDebug(TAG, "Finished Initialization");
    }

    public HashMap<String, DeviceConfiguration> getRequestedConfigs() {
        return localConfig.getDeviceConfigs();
    }
}
