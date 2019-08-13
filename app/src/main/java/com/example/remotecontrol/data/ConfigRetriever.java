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
        String config = "device_configs";
        final String url = baseURL + config;
        LogUtil.logDebug(TAG, "Downloading Database from " + url + " ...");
        String jsonStr = URLHandler.processURL(url);
        if (jsonStr == null || jsonStr.length() == 0) {
            return;
        }
        rstHandler.parseDeviceConfigurations(jsonStr);
    }


}
