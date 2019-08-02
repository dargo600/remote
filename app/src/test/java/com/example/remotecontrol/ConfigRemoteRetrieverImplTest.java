package com.example.remotecontrol;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.util.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigRemoteRetrieverImplTest {

    @Mock
    DBHelper mockDB;

    @Test(expected=ParseConfigException.class)
    public void syncToRemote_failedToDownloadDB() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.getReadableDatabase()).thenReturn(null);
        ConfigRemoteRetriever remote = new ConfigRemoteRetrieverImpl(mockDB, "http://");
        remote.syncToRemote();
    }

    @Test
    public void syncToRemote_success() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.getReadableDatabase()).thenReturn(null);
        String testDir = "/src/test/java/com/example/remotecontrol/data/";
        String url = "file://" + System.getProperty("user.dir") + testDir;
        ConfigRemoteRetriever remote = new ConfigRemoteRetrieverImpl(mockDB, url);
        remote.syncToRemote();
    }

    @Test
    public void syncToRemote_emptyJsonFiles() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.getReadableDatabase()).thenReturn(null);
        String testDir = "/src/test/java/com/example/remotecontrol/data/empty_files/";
        String url = "file://" + System.getProperty("user.dir") + testDir;
        ConfigRemoteRetriever remote = new ConfigRemoteRetrieverImpl(mockDB, url);
        remote.syncToRemote();
    }
}

