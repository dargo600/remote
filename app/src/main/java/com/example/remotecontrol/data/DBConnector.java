package com.example.remotecontrol.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.remotecontrol.util.ParseConfigException;

public class DBConnector extends SQLiteOpenHelper {

    private static final String DB_NAME = "remote";
    private static final int DB_VERSION = 2;

    private String[] tables = {
            "desired_configs",
            "device_configs",
            "devices",
            "rc_buttons"
    };

    public DBConnector(Context context) {
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
        if (oldVersion < 2) {
            dropAllTables(db);
            createDefaultTables(db);
        }
    }

    private void dropAllTables(SQLiteDatabase db) {
        for (String table : tables) {
            String sql = "drop table if exists " + table + ";";
            db.execSQL(sql);
        }
    }

    private void createDefaultTables(SQLiteDatabase db) {
        createDesiredConfigsTable(db);
        createDevicesConfigsTable(db);
        createDevicesTable(db);
        createRCButtonsTable(db);
    }

    private void createDesiredConfigsTable(SQLiteDatabase db) {
        db.execSQL("create table desired_configs " +
                "(_id integer primary key autoincrement, " +
                "name text)");
    }

    private void createDevicesConfigsTable(SQLiteDatabase db) {
        db.execSQL("create table device_configs " +
                "(device_config_id integer primary key, " +
                "name text);");
    }

    private void createDevicesTable(SQLiteDatabase db) {
        db.execSQL("create table devices " +
                "(_id integer primary key autoincrement, " +
                "device_type text, " +
                "manufacturer text, " +
                "model_num text, " +
                "device_config_id integer, " +
                "constraint fk_device_configs " +
                " foreign key (device_config_id) " +
                " references device_configs(device_config_id));");
    }

    private void createRCButtonsTable(SQLiteDatabase db) {
        db.execSQL("create table rc_buttons " +
                "(_id integer primary key autoincrement, " +
                "rc_type text, " +
                "ir_code text, " +
                "device_config_id integer, " +
                "constraint fk_device_configs " +
                " foreign key (device_config_id) " +
                " references device_configs(device_config_id));");
    }

    public SQLiteDatabase initRead() throws Exception {
        try {
            return getReadableDatabase();
        } catch (Exception e) {
            throw new ParseConfigException("Failed to Access Readable Database");
        }
    }

    public SQLiteDatabase initWrite() throws Exception {
        try {
            return getWritableDatabase();
        } catch (Exception e) {
            throw new ParseConfigException("Failed to Access Writable Database");
        }
    }

    public ContentValues makeContentValues() {
        return new ContentValues();
    }
}
