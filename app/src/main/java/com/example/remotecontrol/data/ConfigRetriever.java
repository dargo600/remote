package com.example.remotecontrol.data;

import com.example.remotecontrol.data.streams.URLStream;
import com.example.remotecontrol.util.LogUtil;
import com.example.remotecontrol.util.ParseConfigException;

import java.util.ArrayList;

public class ConfigRetriever {

    private final String TAG = ConfigRetriever.class.getSimpleName();

    private RSTHandler rstHandler;
    private URLHandler URLHandler;
    private String baseURL;
    private ArrayList<String> rstConfigs;

    public ConfigRetriever(DBHelper dbHelper, String baseURL, URLStream stream) {
        rstHandler = new RSTHandler(dbHelper);
        URLHandler = new URLHandler(stream);
        this.baseURL = baseURL;
        rstConfigs = new ArrayList<>();
        rstConfigs.add("device_configs");
        rstConfigs.add("devices");
    }

    public void
    setRSTConfigs(ArrayList<String> configs) {
        this.rstConfigs = configs;
    }

    public void
    syncToRemote() throws Exception {
        LogUtil.logDebug(TAG, "Downloading Database from " + baseURL + " ...");
        for (String config : rstConfigs) {
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
                throw new ParseConfigException("Unknown RST Configuration "
                        + config);
            }
        }
    }


}
