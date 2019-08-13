package com.example.remotecontrol;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.data.streams.HTTPURLStream;
import com.example.remotecontrol.data.streams.URLStream;
import com.example.remotecontrol.util.FileURLStream;
import com.example.remotecontrol.util.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigRetrieverTest {

    @Mock
    DBConnector mockDB;


    @Test(expected=ParseConfigException.class)
    public void syncToRemote_failedToDownloadDB() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.getReadableDatabase()).thenReturn(null);
        DBHelperImpl dbHelper = new DBHelperImpl(mockDB);
        URLStream stream = new HTTPURLStream();
        ConfigRetriever remote = new ConfigRetriever(dbHelper, "http://", stream);
        remote.syncToRemote();
    }

    @Test
    public void syncToRemote_emptyJsonFiles() throws Exception {
        LogUtil.enableLogToTerminal();
        String testDir = "/src/test/java/com/example/remotecontrol/data/empty_files/";
        String url = "file://" + System.getProperty("user.dir") + testDir;
        DBHelperImpl dbHelper = new DBHelperImpl(mockDB);
        FileURLStream stream = new FileURLStream();
        ConfigRetriever remote = new ConfigRetriever(dbHelper, url, stream);
        remote.syncToRemote();
    }
}

