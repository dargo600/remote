package com.example.remotecontrol.data;

import android.database.sqlite.SQLiteException;

import com.example.remotecontrol.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RSTHandler {
    private final String TAG = RSTHandler.class.getSimpleName();

    private DBHelper dbHelper;

    public RSTHandler(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void
    parseDeviceConfigurations(String jsonStr) throws Exception {
        JSONArray deviceConfigs = new JSONArray(jsonStr);
        LogUtil.logDebug(TAG, "Parsing Device Configurations...");
        int processedDeviceConfigs = 0;
        dbHelper.initWrite();
        for (int i = 0; i < deviceConfigs.length(); i++) {
            JSONObject dc = deviceConfigs.getJSONObject(i);
            parseDeviceConfig(dc);
            processedDeviceConfigs++;
        }
        dbHelper.closeDB();
        LogUtil.logDebug(TAG, "Processed " + processedDeviceConfigs + " configurations");
    }

    private void parseDeviceConfig(JSONObject dc) throws JSONException {
        int id = dc.getInt("device_config_id");
        String name = dc.getString("device_config_name");
        dbHelper.insertDeviceConfig(id, name);
        JSONArray buttons = dc.getJSONArray("buttons");
        for (int i = 0; i < buttons.length(); i++) {
            JSONObject b = buttons.getJSONObject(i);
            String prontoCode = b.getString("rc_ir_code");
            String type = b.getString("rc_type");
            dbHelper.insertButton(type, prontoCode, id);
        }
        JSONArray devices = dc.getJSONArray("devices");
        for (int i = 0; i < devices.length(); i++) {
            JSONObject d = devices.getJSONObject(i);
            parseDevice(d, id);
        }
    }

    private void parseDevice(JSONObject d, int deviceConfigId) throws JSONException {
        String dt = d.getString("device_type");
        String man = d.getString("manufacturer");
        String modelNum = d.getString("model_num");
        dbHelper.insertDevice(dt, man, modelNum, deviceConfigId);
    }
}
