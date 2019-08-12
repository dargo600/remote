package com.example.remotecontrol;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import com.example.remotecontrol.data.*;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DBConnectorTest {

    @Mock
    SQLiteDatabase mockDB;

    @Mock
    Context mainContext;

    @Test
    public void onCreate_newDB() {
        DBConnector testDB = new DBConnector(mainContext);
        testDB.onCreate(mockDB);
        verify(mockDB, times(1))
                .execSQL("drop table if exists desired_configs;");
        verify(mockDB, times(1))
                .execSQL("drop table if exists device_configs;");
        verify(mockDB, times(1))
                .execSQL("drop table if exists devices;");
        verify(mockDB, times(1))
                .execSQL("drop table if exists rc_buttons;");
        verify(mockDB, times(1))
                .execSQL("create table desired_configs "
                        + "(_id integer primary key autoincrement, "
                        + "name text)");
        verify(mockDB, times(1))
                .execSQL("create table device_configs "
                        + "(device_config_id integer primary key, "
                        + "name text);");
        verify(mockDB, times(1))
                .execSQL("create table devices "
                        + "(_id integer primary key autoincrement, "
                        + "device_type text, "
                        + "manufacturer text, "
                        + "model_num text, "
                        + "device_config_id integer, "
                        + "constraint fk_device_configs "
                        + " foreign key (device_config_id) "
                        + " references device_configs(device_config_id)"
                        + ");");
        verify(mockDB, times(1))
                .execSQL("create table rc_buttons "
                        + "(_id integer primary key autoincrement, "
                        + "rc_type text, "
                        + "ir_code text, "
                        + "device_config_id integer, "
                        + "constraint fk_device_configs "
                        + " foreign key (device_config_id) "
                        + " references device_configs(device_config_id));");
    }

    @Test
    public void onUpgrade_success() {
        DBConnector testDB = new DBConnector(mainContext);
        testDB.onUpgrade(mockDB, 1, 2);
    }

    @Test
    public void onDowngrade_success() {
        DBConnector testDB = new DBConnector(mainContext);
        testDB.onDowngrade(mockDB, 2, 1);
    }

    @Test
    public void testMakeContent() {
        DBConnector testDB = new DBConnector(mainContext);
        ContentValues cv = testDB.makeContentValues();
    }

    @Test
    public void testInitRead() {
        try {
            DBConnector testDB = new DBConnector(mainContext);
            testDB.initRead();
        } catch (Exception e) {}
    }

    @Test
    public void testInitWrite() {
        try {
            DBConnector testDB = new DBConnector(mainContext);
            testDB.initWrite();
        } catch (Exception e) {}
    }
}
