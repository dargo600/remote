package com.example.remotecontrol.data;

import com.example.remotecontrol.data.streams.URLStream;
import com.example.remotecontrol.util.LogUtil;

public class ConfigRetriever {

    private final String TAG = ConfigRetriever.class.getSimpleName();

    private RSTHandler rstHandler;
    private URLHandler URLHandler;
    private String baseURL;

    public ConfigRetriever(DBHelper dbHelper, String baseURL, URLStream stream) {
        rstHandler = new RSTHandler(dbHelper);
        URLHandler = new URLHandler(stream);
        this.baseURL = baseURL;
    }

    public void
    syncToRemote() throws Exception {
        LogUtil.logDebug(TAG, "Downloading Database...");
        String[] configs = { "device_configs", "devices"};
        for (String config : configs) {
            final String url = baseURL + config;
            String jsonStr = URLHandler.processURL(url);
            if (jsonStr == null || jsonStr.length() == 0) {
                continue;
            }
            if (config.equals("device_configs")) {
                rstHandler.parseDeviceConfigurations(jsonStr);
            } else if (config.equals("devices")) {
                rstHandler.parseDevices(jsonStr);
            } else {
                continue;
            }
        }
    }


}
