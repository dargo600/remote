package com.example.remotecontrol;

import android.content.Context;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.remotecontrol.data.ConfigManager;
import com.example.remotecontrol.data.ConfigRetriever;
import com.example.remotecontrol.data.ConfigRetrieverImpl;
import com.example.remotecontrol.data.DBHelper;
import com.example.remotecontrol.data.DeviceConfiguration;
import com.example.remotecontrol.data.RSTHandler;
import com.example.remotecontrol.remote.MainActivity;
import com.example.remotecontrol.util.*;

import java.util.HashMap;

import com.example.remotecontrol.data.*;


@RunWith(MockitoJUnitRunner.class)
public class ConfigManagerTest {

    private final String TAG = MainActivity.class.getSimpleName();

    private static final String FAKE_STRING = "HELLO WORLD";

    @Mock
    Context mockContext;

    @Mock
    DBHelper mockDB;

    @Mock
    RSTHandler mockRST;

    @Test
    public void initializeConfigs_throwsExceptionOnEmpty() throws Exception {
        LogUtil.enableLogToTerminal();
        HashMap<String, DeviceConfiguration> emptyMap = new HashMap<>();
        ConfigRetriever mockRetriever = mock(ConfigRetriever.class);
        when(mockRetriever.getRequestedConfigs()).thenReturn(emptyMap);
        ConfigManager cm = new ConfigManager(mockRetriever, mockContext);
        try {
            cm.initializeConfigs();
            throw new Exception("Expect a ParseConfig Exception could should not run");
        } catch (ParseConfigException e) {
        }
    }

    @Test
    public void initializeConfigs_NotEmpty() throws Exception {
        DeviceConfiguration dc;
        HashMap<String, DeviceConfiguration> result;
        LogUtil.enableLogToTerminal();
        HashMap<String, DeviceConfiguration> deviceConfigs = new HashMap<>();
        dc = new DeviceConfiguration(1, "appleConfig1", "media");
        deviceConfigs.put("appleConfig1", dc);
        dc = new DeviceConfiguration(2, "rokuConfig1", "media");
        deviceConfigs.put("rokuConfig1", dc);
        dc = new DeviceConfiguration(3, "samsungConfig1", "tv");
        deviceConfigs.put("samsungConfig1", dc);
        ConfigRetriever mockRetriever = mock(ConfigRetriever.class);
        when(mockRetriever.getRequestedConfigs()).thenReturn(deviceConfigs);
        ConfigManager cm = new ConfigManager(mockRetriever, mockContext);
        result = cm.initializeConfigs();
        assertEquals(result.size(), 3);
    }
}