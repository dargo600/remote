package com.example.remotecontrol;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.util.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigRetrieverTest {

    @Mock
    DBHelperImpl mockDB;

    @Test(expected=ParseConfigException.class)
    public void syncToRemote_failedToDownloadDB() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.getReadableDatabase()).thenReturn(null);
        FileURLStream stream = new FileURLStream();
        ConfigRetriever remote = new ConfigRetriever(mockDB, "http://", stream);
        remote.syncToRemote();
    }

    @Test
    public void syncToRemote_success() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.getReadableDatabase()).thenReturn(null);
        String testDir = "/src/test/java/com/example/remotecontrol/data/";
        String url = "file://" + System.getProperty("user.dir") + testDir;
        FileURLStream stream = new FileURLStream();
        ConfigRetriever remote = new ConfigRetriever(mockDB, url, stream);
        remote.syncToRemote();
        //verify(mockDB, atLeastOnce()).insertDeviceConfig(anyInt(), anyString());
        //verify(mockDB, atLeastOnce()).insertButton(anyString(), anyString(), anyInt());
        //verify(mockDB, atLeastOnce()).insertDevice(anyString(), anyString(), anyString(), anyInt());

    }

    @Test
    public void syncToRemote_emptyJsonFiles() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.getReadableDatabase()).thenReturn(null);
        String testDir = "/src/test/java/com/example/remotecontrol/data/empty_files/";
        String url = "file://" + System.getProperty("user.dir") + testDir;
        FileURLStream stream = new FileURLStream();
        ConfigRetriever remote = new ConfigRetriever(mockDB, url, stream);
        remote.syncToRemote();
    }
}

