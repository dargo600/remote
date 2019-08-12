package com.example.remotecontrol.remote;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.data.streams.*;

public class RemoteMain {

    private final String TAG = RemoteMain.class.getSimpleName();
    private final String baseURL = "http://phaedra:5000/api/";

    private IRHandler irHandler;
    private DBHelper dbHelper;
    private GenericNotify notify;
    private ConfigManager configManager;

    public RemoteMain(IRHandler irHandler, DBHelper dbHelper, GenericNotify notify) {
        this.irHandler = irHandler;
        this.dbHelper = dbHelper;
        this.notify = notify;
        URLStream stream = new HTTPURLStream();
        ConfigRetriever configRetriever = new ConfigRetriever(dbHelper, baseURL, stream);
        ConfigLocal configLocal = new ConfigLocal(dbHelper);
        configManager = new ConfigManager(configRetriever, configLocal);
    }

    public void setNotify(GenericNotify notify) {
        this.notify = notify;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public String doBackgroundTask() {
        String errorMessage = "";
        try {
            configManager.initLocal();
            irHandler.updateDeviceConfigs(configManager.getRequestedConfigs());
        } catch (Exception e) {
            errorMessage = "Error: " + e.getMessage();
        }

        return errorMessage;
    }

    public void processMediaId(String cd, String media) {
        if (!irHandler.processMediaId(cd, media)) {
            String error = "Unrecognized " + media + " button " + cd;
            notify.displayMessage(error);
        }
    }
}
