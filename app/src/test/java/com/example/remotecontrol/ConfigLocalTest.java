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

import java.util.ArrayList;
import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class ConfigLocalTest {

    @Mock
    DBConnector mockConnect;

    @Mock
    SQLiteDatabase mockDB;

    @Mock
    Cursor mockCursor;

    @Test(expected=ParseConfigException.class)
    public void initFromLocal_desiredConfigEmptyAsDefaultInitializationFailed() throws Exception {
        LogUtil.enableLogToTerminal();
        ArrayList<String> desiredConfigs = new ArrayList<>();
        when(mockConnect.initRead()).thenReturn(mockDB);
        when(mockDB.query("requested_configs", new String[] {"name"},
                null, null,  null, null, null))
                .thenReturn(mockCursor);
        when(mockCursor.isAfterLast()).thenReturn(true);
        DBHelperImpl dbHelper = new DBHelperImpl(mockConnect);
        dbHelper.setDesiredConfigs(desiredConfigs);
        ConfigLocal cl = new ConfigLocal(dbHelper);
        cl.initFromLocal();
        verify(mockCursor, atLeastOnce()).isBeforeFirst();
    }

    @Test(expected=ParseConfigException.class)
    public void initFromLocal_retrievesDesiredConfigFromDB() throws Exception {
        LogUtil.enableLogToTerminal();
        ArrayList<String> desiredConfigs = new ArrayList<>();
        desiredConfigs.add("samsungConfig1");
        desiredConfigs.add("appleConfig1");
        when(mockConnect.initRead()).thenReturn(mockDB);
        when(mockDB.query("requested_configs", new String[] {"name"},
                null, null,  null, null, null))
                .thenReturn(mockCursor);
        when(mockCursor.isAfterLast()).thenReturn(false)
            .thenReturn(false).thenReturn(true);
        when(mockCursor.getString(0)).thenReturn(desiredConfigs.get(0))
                .thenReturn(desiredConfigs.get(1));
        DBHelperImpl dbHelper = new DBHelperImpl(mockConnect);
        dbHelper.setDesiredConfigs(desiredConfigs);
        ConfigLocal cl = new ConfigLocal(dbHelper);
        cl.initFromLocal();
        verify(mockCursor, atLeastOnce()).isBeforeFirst();
    }

}
