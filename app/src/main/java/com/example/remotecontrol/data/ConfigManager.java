package com.example.remotecontrol.data;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.remotecontrol.util.ParseConfigException;

public class ConfigManager {
    private ArrayList<String> desiredConfigs;
    private HashMap<String, DeviceConfiguration> requestedConfigs;
    private ConfigRetriever retriever;
    private Context mainContext;

    public ConfigManager(ConfigRetriever retriever, Context mainContext) {
        this.retriever = retriever;
        this.mainContext = mainContext;
        requestedConfigs = new HashMap<>();
        desiredConfigs = new ArrayList<>();
    }

    public HashMap<String, DeviceConfiguration> initializeConfigs() throws Exception {
        requestedConfigs = retriever.getRequestedConfigs();
        if (requestedConfigs.isEmpty()) {
            throw new ParseConfigException("Device Configuration is empty");
        }

        return requestedConfigs;
    }
}
