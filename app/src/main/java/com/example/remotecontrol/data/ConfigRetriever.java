package com.example.remotecontrol.data;

import java.util.HashMap;

public interface ConfigRetriever {
    public HashMap<String, DeviceConfiguration> getRequestedConfigs() throws Exception;
}
