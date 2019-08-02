package com.example.remotecontrol.data;

import com.example.remotecontrol.util.LogUtil;
import com.example.remotecontrol.util.ParseConfigException;

import java.util.ArrayList;
import java.util.HashMap;

public class DeviceConfiguration {
    private int deviceConfigID;
    private String configName;
    private DeviceType deviceType;
    private HashMap<String, RCButton> rcButtons;
    private ArrayList<Integer> supportedDevices;

    public DeviceConfiguration(int id, String name, String type) {
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

    public void addRCButton(String buttonType, RCButton button)  {
        if (rcButtons.containsKey(buttonType)) {
            /** FIXME
            String msg = "Button exists for " + buttonType;
            throw new ParseConfigException(msg);
             */
        }
        rcButtons.put(buttonType, button);
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
