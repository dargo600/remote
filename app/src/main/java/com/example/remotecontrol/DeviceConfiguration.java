package com.example.remotecontrol;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceConfiguration {
    private int deviceConfigID;
    private String configName;
    private DeviceType deviceType;
    private HashMap<String, RCButton> rcButtons;
    private ArrayList<Integer> supportedDevices;

    DeviceConfiguration(int id, String name, String type) {
        deviceConfigID = id;
        configName = name;
        deviceType = parseDeviceType(type);
        rcButtons = new HashMap<String, RCButton>();
        supportedDevices = new ArrayList<Integer>();
    }

    private DeviceType parseDeviceType(String type) {
        if(type.equals("media"))
            return DeviceType.MEDIA;
        else if(type.equals("tv"))
            return DeviceType.TV;
        else
            return DeviceType.UNKNOWN;
    }

    public int getDeviceConfigID() {
        return deviceConfigID;
    }

    public String getConfigName() {
        return configName;
    }

    public boolean addRCButton(String buttonType, RCButton button) {
        if (rcButtons.containsKey(buttonType)) {
            android.util.Log.d("Remote", "Button already exists for " +
                                buttonType);

            return false;
        }
        rcButtons.put(buttonType, button);

        return true;
    }

    public void addDevice(int deviceConfigID) {
        supportedDevices.add(deviceConfigID);
    }

    public RCButton getRCButton(String buttonType) {
        if (!rcButtons.containsKey(buttonType)) {
            return null;
        }

        return rcButtons.get(buttonType);
    }

    enum DeviceType { UNKNOWN, TV, MEDIA };
}
