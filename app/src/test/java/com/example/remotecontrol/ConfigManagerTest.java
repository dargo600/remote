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
    ConfigRemoteRetriever mockRemote;

    @Mock
    ConfigLocal mockLocal;

    @Mock
    DBHelper mockDB;

    /** @todo Fixme investigate issue further
    @Test(expected=DBReadException.class)
    public void isLocalEmpty_whenDBisNULL() throws Exception {
        LogUtil.enableLogToTerminal();
        String msg = "Failed to readDB";
   //     when(mockDB.getReadableDB()).thenThrow(new DBReadException(msg));
        ConfigLocal cl = new ConfigLocalImpl(mockDB);
        ConfigManager cm = new ConfigManager(mockContext, mockRemote, cl);
            cm.isLocalEmpty();
    }
**/
    @Test
    public void isLocalEmpty_success() throws Exception {
        when(mockDB.isDBEmpty()).thenReturn(true);
        ConfigLocal cl = new ConfigLocalImpl(mockDB);
        ConfigManager cm = new ConfigManager(mockContext, mockRemote, cl);
        cm.isLocalEmpty();
    }

    @Test
    public void processEmptyLocal() throws Exception {
        ConfigLocal cl = new ConfigLocalImpl(mockDB);
        ConfigManager cm = new ConfigManager(mockContext, mockRemote, cl);
        cm.processEmptyLocal();
        verify(mockDB, times(1)).addDefaultRequestedConfigs();
        verify(mockRemote, times(1)).syncToRemote();
    }

    @Test
    public void initFromLocal_noException() throws Exception {
        LogUtil.enableLogToTerminal();
        ConfigManager cm = new ConfigManager(mockContext, mockRemote, mockLocal);
        cm.initFromLocal();
        verify(mockLocal, times(1)).initFromLocal();
    }

    @Test
    public void getRequestedConfigs_canReturnEmptyList() {
        LogUtil.enableLogToTerminal();
        ConfigManager cm = new ConfigManager(mockContext, mockRemote, mockLocal);
        HashMap<String, DeviceConfiguration> configs = cm.getRequestedConfigs();
        assertTrue(configs.isEmpty());
    }
}