package com.example.remotecontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RemoteDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "remote";
    private static final int DB_VERSION = 1;

    RemoteDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE device_configs("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "device_config_id INTEGER, "
                    + "name TEXT);");
            db.execSQL("CREATE TABLE devices("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "device_type TEXT, "
                    + "manufacturer TEXT, "
                    + "model_num TEXT, "
                    + "device_config_id INTEGER, "
                    + "CONTRAINT fk_device_configs "
                    + " FOREIGN KEY (device_config_id) "
                    + " REFERENCES device_configs(device_config_id)"
                    + ");");
            db.execSQL("CREATE TABLE rc_buttons("
                    + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "name TEXT, "
                    + "rc_type TEXT, "
                    + "ir_code TEXT, "
                    + "device_config_id INTEGER, "
                    + "CONTRAINT fk_device_configs "
                    + " FOREIGN KEY (device_config_id) "
                    + " REFERENCES device_configs(device_config_id)"
                    + ");");
        }
    }

    public void insertDeviceConfig(SQLiteDatabase db, int deviceConfigId,
                                   String name)
    {
        ContentValues values = new ContentValues();
        values.put("device_config_id", deviceConfigId);
        values.put("name", name);
        db.insert("rc_buttons", null, values);
    }

    public void insertDevices(SQLiteDatabase db, String deviceType,
                              String manufacturer, String modelNum,
                              int deviceConfigId)
    {
        ContentValues values = new ContentValues();
        values.put("device_type", deviceType);
        values.put("manufacturer", manufacturer);
        values.put("model_num", modelNum);
        values.put("device_config_id", deviceConfigId);
        db.insert("devices", null, values);
    }

    public void insertButton(SQLiteDatabase db, String name, String type,
                             String prontoCode, int deviceConfigId)
    {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("rc_type", type);
        values.put("ir_code", prontoCode);
        values.put("device_config_id", deviceConfigId);
        db.insert("rc_button", null, values);
    }
}
