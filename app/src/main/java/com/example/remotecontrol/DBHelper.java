package com.example.remotecontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private final String TAG = DBHelper.class.getSimpleName();
    private static final String DB_NAME = "remote";
    private static final int DB_VERSION = 1;

    DBHelper(Context context) {
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

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateMyDatabase(db, oldVersion, newVersion);
    }

    private void updateMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("drop table if exists requested_configs");
            db.execSQL("drop table if exists device_configs;");
            db.execSQL("drop table if exists devices;");
            db.execSQL("drop table if exists rc_buttons;");

            db.execSQL("create table requested_configs "
                     + "(_id integer primary key autoincrement, "
                     + "name text)");
            db.execSQL("create table device_configs "
                     + "(device_config_id integer primary key, "
                     + "name text);");
            db.execSQL("create table devices "
                     + "(_id integer primary key autoincrement, "
                     + "device_type text, "
                     + "manufacturer text, "
                     + "model_num text, "
                     + "device_config_id integer, "
                     + "constraint fk_device_configs "
                     + " foreign key (device_config_id) "
                     + " references device_configs(device_config_id)"
                     + ");");
            db.execSQL("create table rc_buttons "
                    +  "(_id integer primary key autoincrement, "
                    + "rc_type text, "
                    + "ir_code text, "
                    + "device_config_id integer, "
                    + "constraint fk_device_configs "
                    + " foreign key (device_config_id) "
                    + " references device_configs(device_config_id));");
        }
    }

    public void insertRequestedConfig(SQLiteDatabase db, String name)
    {
        ContentValues values = new ContentValues();
        values.put("name", name);
        db.insert("requested_configs", null, values);
    }

    public void insertDeviceConfig(SQLiteDatabase db, int deviceConfigId,
                                   String name)
    {
        ContentValues values = new ContentValues();
        values.put("device_config_id", deviceConfigId);
        values.put("name", name);
        db.insert("device_configs", null, values);
    }

    public void insertDevice(SQLiteDatabase db, String deviceType,
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

    public void insertButton(SQLiteDatabase db, String type,
                             String prontoCode, int deviceConfigId)
    {
        ContentValues values = new ContentValues();
        values.put("rc_type", type);
        values.put("ir_code", prontoCode);
        values.put("device_config_id", deviceConfigId);
        db.insert("rc_buttons", null, values);
    }
}
