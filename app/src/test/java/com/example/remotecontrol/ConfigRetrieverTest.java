package com.example.remotecontrol;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.util.FileURLStream;
import com.example.remotecontrol.util.*;

import java.util.ArrayList;

@RunWith(MockitoJUnitRunner.class)
public class ConfigRetrieverTest {

    @Mock
    DBConnector mockDB;


    @Test(expected=ParseConfigException.class)
    public void syncToRemote_failedToDownloadDB() throws Exception {
        LogUtil.enableLogToTerminal();
        when(mockDB.getReadableDatabase()).thenReturn(null);
        DBHelperImpl dbHelper = new DBHelperImpl(mockDB);
        FileURLStream stream = new FileURLStream();
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

    @Test(expected=ParseConfigException.class)
    public void syncToRemote_newConfigAddedButNotHandled() throws Exception {
        LogUtil.enableLogToTerminal();
        String testDir = "/src/test/java/com/example/remotecontrol/data/";
        String url = "file://" + System.getProperty("user.dir") + testDir;
        DBHelperImpl dbHelper = new DBHelperImpl(mockDB);
        FileURLStream stream = new FileURLStream();
        ConfigRetriever remote = new ConfigRetriever(dbHelper, url, stream);
        ArrayList<String> newConfigs = new ArrayList<>();
        newConfigs.add("samsungConfig1");
        newConfigs.add("appleConfig1");
        newConfigs.add("newConfigAddedButNotHandled");
        remote.setRSTConfigs(newConfigs);
        remote.syncToRemote();
    }
}

