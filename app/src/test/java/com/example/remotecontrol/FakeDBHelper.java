package com.example.remotecontrol;

import com.example.remotecontrol.data.*;

import java.util.ArrayList;
import java.util.HashMap;

public class FakeDBHelper implements DBHelper {

    private int id = 1;
    private ArrayList<String> desiredConfigs = new ArrayList<>();
    private HashMap<String, DeviceConfiguration> requestedConfigs = new HashMap<>();

    public FakeDBHelper() {
    }

    @Override
    public void initRead() {
    }

    @Override
    public void initWrite() {
    }

    @Override
    public boolean isDBEmpty() throws Exception {
        return true;
    }

    @Override
    public void addDefaultRequestedConfigs() {

    }

    @Override
    public void insertRequestedConfig(String name) {

    }

    @Override
    public void insertDeviceConfig(int deviceConfigId, String name) {

    }

    @Override
    public void insertDevice(String deviceType, String manufacturer, String modelNum, int deviceConfigId) {

    }

    @Override
    public void insertButton(String type, String prontoCode, int deviceConfigId) {

    }

    @Override
    public void closeDB() {

    }

    @Override
    public ArrayList<String> initializeDesiredConfigs() {
        desiredConfigs.add("samsungConfig1");
        desiredConfigs.add("appleConfig1");
        return desiredConfigs;
    }

    @Override
    public void cacheConfig(String configName) throws Exception {
        DeviceConfiguration dc;
        String deviceType = "tv";
        if (configName.equals("appleConfig1"))
            deviceType = "media";
        dc = new DeviceConfiguration(id, configName, deviceType);
        if (configName.equals("appleConfig1")) {
           addAppleButtons(dc);
        }
        requestedConfigs.put(configName, dc);
        id++;
    }

    private void addAppleButtons(DeviceConfiguration dc) {
        String type = "up";
        dc.addRCButton(type, new RCButton(type, BUTTON_APPLE_UP));
        type = "right";
        dc.addRCButton(type, new RCButton(type, BUTTON_APPLE_RIGHT));
        type = "play";
        dc.addRCButton(type, new RCButton(type, BUTTON_APPLE_PLAY));
    }

    @Override
    public HashMap<String, DeviceConfiguration> getRequestedConfigs() {
        return requestedConfigs;
    }

    final String BUTTON_APPLE_UP = "0000 006E 0022 0002 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 05E6 0155 0056 0015 0E45";
    final String BUTTON_APPLE_RIGHT = "0000 006E 0022 0002 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 05E6 0155 0056 0015 0E44";
    final String BUTTON_APPLE_PLAY = "0000 006E 0022 0002 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 063B 0155 0056 0015 0E44";
}
