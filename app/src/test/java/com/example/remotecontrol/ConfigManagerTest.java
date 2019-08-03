package com.example.remotecontrol;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.remote.MainActivity;
import com.example.remotecontrol.util.*;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class ConfigManagerTest {

    private final String TAG = MainActivity.class.getSimpleName();

    private static final String FAKE_STRING = "HELLO WORLD";


    @Mock
    Context mockContext;

    @Mock
    ConfigRetriever mockRemote;

    @Mock
    ConfigLocal mockLocal;

    @Mock
    FakeDBHelper mockDB;

    /** @todo Fixme investigate issue further
    @Test(expected=DBReadException.class)
    public void isLocalEmpty_whenDBisNULL() throws Exception {
        LogUtil.enableLogToTerminal();
        String msg = "Failed to readDB";
   //     when(mockDB.getReadableDB()).thenThrow(new DBReadException(msg));
        ConfigLocal cl = new ConfigLocal(mockDB);
        ConfigManager cm = new ConfigManager(mockContext, mockRemote, cl);
            cm.isLocalEmpty();
    }
    @Test
    public void initLocal_isLocalEmptysuccess() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.isDBEmpty()).thenReturn(true);
        ConfigLocal cl = new ConfigLocal(mockDB);
        ConfigManager cm = new ConfigManager(mockRemote, cl);
        cm.initLocal();
    }
**/

    @Test
    public void doProcessEmptyLocal_whenDBEmpty() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.isDBEmpty()).thenReturn(true);
        ConfigLocal cl = new ConfigLocal(new FakeDBHelper());
        ConfigManager cm = new ConfigManager(mockRemote, cl);
        cm.initLocal();
    }

    @Test
    public void initFromLocal_noException() throws Exception {
        LogUtil.enableLogToTerminal();
        ConfigManager cm = new ConfigManager(mockRemote, mockLocal);
        cm.initLocal();
        verify(mockLocal, times(1)).initFromLocal();
    }

    @Test
    public void getRequestedConfigs_canReturnEmptyList() {
        LogUtil.enableLogToTerminal();
        ConfigManager cm = new ConfigManager(mockRemote, mockLocal);
        HashMap<String, DeviceConfiguration> configs = cm.getRequestedConfigs();
        assertTrue(configs.isEmpty());
    }
}