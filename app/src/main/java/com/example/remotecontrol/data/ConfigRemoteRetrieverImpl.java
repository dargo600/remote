package com.example.remotecontrol.data;

import com.example.remotecontrol.util.LogUtil;

public class ConfigRemoteRetrieverImpl implements ConfigRemoteRetriever {

    private final String TAG = ConfigRemoteRetrieverImpl.class.getSimpleName();

    private RSTHandler rstHandler;
    private HttpHandler httpHandler;
    private String baseURL;

    public ConfigRemoteRetrieverImpl (DBHelper dbHelper, String url) {
        rstHandler = new RSTHandler(dbHelper);
        httpHandler = new HttpHandler();
        baseURL = url;
    }

    public void
    syncToRemote() throws Exception {
        LogUtil.logDebug(TAG, "Downloading Database...");
        String[] configs = { "device_configs", "devices"};
        for (String config : configs) {
            final String url = baseURL + config;
            String jsonStr = httpHandler.processURL(url);
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
