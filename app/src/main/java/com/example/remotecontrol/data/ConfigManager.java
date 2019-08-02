package com.example.remotecontrol.data;

import android.content.Context;

import java.util.HashMap;

import com.example.remotecontrol.util.LogUtil;

public class ConfigManager {
    private final String TAG = ConfigManager.class.getSimpleName();
    private final String baseURL = "http://phaedra:5000/api/";

    private Context mainContext;
    private ConfigRemoteRetriever retriever;
    private ConfigLocal localConfig;

    public ConfigManager(Context mainContext, ConfigRemoteRetriever retriever,
                         ConfigLocal local) {
        this.mainContext = mainContext;
        this.retriever = retriever;
        this.localConfig = local;
    }

    public boolean isLocalEmpty() throws Exception {
       return localConfig.isConfigEmpty();
    }

    public void processEmptyLocal() throws Exception {
        localConfig.addDefaultDesiredConfig();
        retriever.syncToRemote();
    }

    public void initFromLocal()  throws Exception {
        localConfig.initFromLocal();
        LogUtil.logDebug(TAG, "Finished Initialization");
    }

    public HashMap<String, DeviceConfiguration> getRequestedConfigs() {
        return localConfig.getDeviceConfigs();
    }
}
