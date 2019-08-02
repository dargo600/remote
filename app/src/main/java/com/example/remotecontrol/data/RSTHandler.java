package com.example.remotecontrol.data;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RSTHandler {
    private DBHelper dbHelper;

    public RSTHandler(DBHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public void
    parseDeviceConfigurations(String jsonStr) throws JSONException, SQLiteException {
        JSONArray deviceConfigs = new JSONArray(jsonStr);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < deviceConfigs.length(); i++) {
            JSONObject dc = deviceConfigs.getJSONObject(i);
            parseDeviceConfig(dc, db);
        }
    }

    private void
    parseDeviceConfig(JSONObject dc, SQLiteDatabase db) throws JSONException {
        int id = dc.getInt("device_config_id");
        String name = dc.getString("device_config_name");

        dbHelper.insertDeviceConfig(db, id, name);
        JSONArray buttons = dc.getJSONArray("buttons");
        for (int i = 0; i < buttons.length(); i++) {
            JSONObject b = buttons.getJSONObject(i);
            String rcType = b.getString("rc_type");
            String rcIRCode = b.getString("rc_ir_code");
            dbHelper.insertButton(db, rcType, rcIRCode, id);
        }
    }

    public void
    parseDevices(String jsonStr) throws JSONException, SQLiteException {
        JSONArray devices = new JSONArray(jsonStr);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        for (int i = 0; i < devices.length(); i++) {
            JSONObject d = devices.getJSONObject(i);
            parseDevice(d, db);
        }
    }

    private void
    parseDevice(JSONObject d, SQLiteDatabase db) throws JSONException {
        JSONArray deviceConfigs = d.getJSONArray("device_config");
        int deviceConfigId = 0;
        for (int i = 0; i < deviceConfigs.length(); i++) {
            JSONObject dc = deviceConfigs.getJSONObject(i);
            deviceConfigId = dc.getInt("device_config_id");
        }
        String deviceType = d.getString("device_type");
        String manufacturer = d.getString("manufacturer");
        String modelNum = d.getString("model_num");
        dbHelper.insertDevice(db, deviceType, manufacturer, modelNum,
                deviceConfigId);
    }
}
