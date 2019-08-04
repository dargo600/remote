package com.example.remotecontrol.data;

import java.util.ArrayList;
import java.util.HashMap;

public interface DBHelper {
    void initRead() throws Exception;
    void initWrite() throws Exception;
    boolean isDBEmpty() throws Exception;
    void addDefaultDesiredConfigs(ArrayList<String> desiredConfigs) throws Exception;
    void insertRequestedConfig(String name);
    void insertDeviceConfig(int deviceConfigId, String name);
    void insertDevice(String deviceType, String manufacturer,
                             String modelNum, int deviceConfigId);
    void insertButton(String type, String prontoCode, int deviceConfigId);
    void closeDB();
    ArrayList<String> initializeDesiredConfigs();
    void cacheConfig(String configName) throws Exception;
    HashMap<String, DeviceConfiguration> getRequestedConfigs() throws Exception;
}
