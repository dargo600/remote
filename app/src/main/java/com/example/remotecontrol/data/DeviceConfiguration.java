package com.example.remotecontrol.data;

import com.example.remotecontrol.util.ParseConfigException;

import java.util.HashMap;

public class DeviceConfiguration {
    private int deviceConfigID;
    private String configName;
    private HashMap<String, RCButton> rcButtons;

    public DeviceConfiguration(int id, String name) {
        deviceConfigID = id;
        configName = name;
        rcButtons = new HashMap<String, RCButton>();
    }

    public int getDeviceConfigID() {

        return deviceConfigID;
    }

    public String getConfigName() {

        return configName;
    }

    public void addRCButton(String buttonType, RCButton button) throws Exception {
        if (rcButtons.containsKey(buttonType)) {
            String msg = "Button exists for " + buttonType;
            throw new ParseConfigException(msg);
        }
        rcButtons.put(buttonType, button);
    }

    public RCButton getRCButton(String buttonType) {
        if (!rcButtons.containsKey(buttonType)) {
            return null;
        }

        return rcButtons.get(buttonType);
    }
}
