package com.example.remotecontrol;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class RSTHandler {

    public RSTHandler() {
    }

    public ArrayList<HashMap<String, String>>
    parseDevices(String jsonStr) throws JSONException {
        ArrayList<HashMap<String, String>> deviceList = new ArrayList<>();
        JSONObject jsonObj = new JSONObject(jsonStr);
        JSONArray devices = jsonObj.getJSONArray("devices");
        for (int i = 0; i < devices.length(); i++) {
            JSONObject d = devices.getJSONObject(i);
            String id = d.getString("device_id");
            String type = d.getString("device_type");
            String manufacturer = d.getString("manufacturer");
            String remote_config = d.getString("remote_config");

            HashMap<String, String> device = new HashMap<>();
            device.put("id", id);
            device.put("type", type);
            device.put("manufacturer", manufacturer);
            device.put("remote_config", remote_config);

            // adding contact to contact list
            deviceList.add(device);
        }

        return deviceList;
    }
}