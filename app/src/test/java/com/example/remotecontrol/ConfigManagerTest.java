package com.example.remotecontrol;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.util.*;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class ConfigManagerTest {

    @Mock
    ConfigRetriever mockRemote;

    @Mock
    ConfigLocal mockLocal;

    @Mock
    SQLiteDatabase mockDB;

    @Mock
    DBConnector mockConnect;

    @Mock
    Cursor mockCursor;

    @Test(expected=DBReadException.class)
    public void isLocalEmpty_whenDBisNULLThrowDBReadException() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockConnect.initRead()).thenThrow(SQLiteException.class);
        DBHelper dbHelper = new DBHelperImpl(mockConnect);
        ConfigLocal cl = new ConfigLocal(dbHelper);
        ConfigManager cm = new ConfigManager(mockRemote, cl);
        cm.initLocal();
    }

    @Test(expected=ParseConfigException.class)
    public void isLocalEmpty_FailsToWriteDesiredConfig() throws Exception {
        int countOfRequestedConfigsInDB = 0;
        LogUtil.enableLogToTerminal();
        when(mockConnect.initRead()).thenReturn(mockDB);
        when(mockDB.query("requested_configs", new String[] { "_id", "name"},
                null, null,  null, null, null))
                .thenReturn(mockCursor);
        when(mockCursor.getCount()).thenReturn(countOfRequestedConfigsInDB);
        DBHelper dbHelper = new DBHelperImpl(mockConnect);
        ConfigLocal cl = new ConfigLocal(dbHelper);
        ConfigManager cm = new ConfigManager(mockRemote, cl);
        cm.initLocal();
    }
    @Test
    public void getRequestedConfigs_canReturnEmptyList() {
        LogUtil.enableLogToTerminal();
        ConfigManager cm = new ConfigManager(mockRemote, mockLocal);
        HashMap<String, DeviceConfiguration> configs = cm.getRequestedConfigs();
        assertTrue(configs.isEmpty());
    }
}