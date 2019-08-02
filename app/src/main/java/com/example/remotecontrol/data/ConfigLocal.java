package com.example.remotecontrol.data;

import java.util.HashMap;

public interface ConfigLocal {
    boolean isConfigEmpty() throws Exception;
    void addDefaultDesiredConfig() throws Exception;
    void initFromLocal() throws Exception;
    HashMap<String, DeviceConfiguration> getDeviceConfigs();
}
