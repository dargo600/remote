package com.example.remotecontrol;

import android.hardware.ConsumerIrManager;
import android.net.wifi.WifiManager;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.util.*;
import com.example.remotecontrol.remote.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class RemoteMainTest {
    @Mock
    SSDPHandler mockSSDP;

    @Mock
    IRHandler mockIR;

    @Mock
    ConsumerIrManager mockIRM;

    @Mock
    WifiManager mockWifi;

    @Mock
    GenericNotify mockNotify;

    @Mock
    DBHelper mockDBHelper;

    @Test
    public void doBackgroundTask_successNoFetch() throws Exception  {
        LogUtil.enableLogToTerminal();
        ArrayList<String> requestedConfigs = new ArrayList<String>();
        requestedConfigs.add("samsungConfig1");
        requestedConfigs.add("appleConfig1");
        HashMap<String, DeviceConfiguration> deviceConfigs = new HashMap<>();
        DeviceConfiguration dc;
        dc = new DeviceConfiguration(1, "samsungConfig1");
        deviceConfigs.put("samsungConfig1", dc);
        when(mockDBHelper.isDeviceConfigsValid()).thenReturn(true);
        when(mockDBHelper.isDevicesValid()).thenReturn(true);
        when(mockDBHelper.getDesiredConfigs()).thenReturn(requestedConfigs);
        when(mockDBHelper.getRequestedConfigs()).thenReturn(deviceConfigs);
        String testDir = "/src/test/java/com/example/remotecontrol/";
        String invalidDir = testDir + "data/";
        String url = "file://" + System.getProperty("user.dir") + invalidDir;
        FileURLStream stream = new FileURLStream();
        ConfigLocal local = new ConfigLocal(mockDBHelper);
        ConfigRetriever remote = new ConfigRetriever(mockDBHelper, url, stream);
        ConfigManager manager = new ConfigManager(remote, local);
        RemoteMain main = new RemoteMain(mockIR, mockDBHelper, mockNotify, mockSSDP);
        main.setConfigManager(manager);
        String expectedStr = "";
        assertEquals(expectedStr, main.doBackgroundTask());
    }

    @Test
    public void doBackgroundTask_successFetch() throws Exception  {
        LogUtil.enableLogToTerminal();
        String newDir = "";

        HashSet<String> addresses = new HashSet<String>();
        addresses.add("172.168.1.68");
        RemoteMain main = setup_file_json_access(newDir);
        HashMap<String, DeviceConfiguration> deviceConfigs = new HashMap<>();
        DeviceConfiguration dc;
        dc = new DeviceConfiguration(1, "samsungConfig1");
        deviceConfigs.put("samsungConfig1", dc);
        when(mockDBHelper.getRequestedConfigs()).thenReturn(deviceConfigs);
        when(mockSSDP.getAddresses()).thenReturn(addresses);
        String expectedStr = "";
        assertEquals(expectedStr, main.doBackgroundTask());
    }

    @Test
    public void doBackgroundTask_FileNotUsingJSONFormat() throws Exception {
        LogUtil.enableLogToTerminal();
        String newDir = "invalid1/";

        RemoteMain main = setup_file_json_access(newDir);
        when(mockDBHelper.isDeviceConfigsValid()).thenReturn(false);
        when(mockDBHelper.isDevicesValid()).thenReturn(false);
        main.doBackgroundTask();
        String expectedStr = "Error: A JSONArray text must start with"
                + " '[' at 1 [character 2 line 1]";
        assertEquals(expectedStr, main.doBackgroundTask());
    }

    @Test
    public void doBackgroundTask_FileWithUnexpectedNames() throws Exception {
        LogUtil.enableLogToTerminal();
        String newDir = "invalid_json_names/";

        RemoteMain main = setup_file_json_access(newDir);
        main.doBackgroundTask();
        String expectedStr = "Error: JSONObject[\"buttons\"] not found.";
        assertEquals(expectedStr, main.doBackgroundTask());
    }

    @Test
    public void doBackgroundTask_FileWithNoData() throws Exception {
        LogUtil.enableLogToTerminal();
        String newDir = "empty_files/";

        RemoteMain main = setup_file_json_access(newDir);
        main.doBackgroundTask();
        String expectedStr = "Error: Failed to ParseDB Could not get requested Configs";
        assertEquals(expectedStr, main.doBackgroundTask());
    }

    /** helpers */
    public RemoteMain setup_file_json_access(String requestedDirectory) throws Exception {
        String testDir = "/src/test/java/com/example/remotecontrol/data/";
        String newDir = testDir + requestedDirectory;
        String url = "file://" + System.getProperty("user.dir") + newDir;

        IRHandler irHandler = new IRHandler(mockIRM);
        ArrayList<String> desiredConfigs = new ArrayList<String>();
        desiredConfigs.add("samsungConfig1");
        desiredConfigs.add("appleConfig1");
        when(mockDBHelper.isDeviceConfigsValid()).thenReturn(false);
        when(mockDBHelper.isDevicesValid()).thenReturn(false);
        when(mockDBHelper.getDesiredConfigs()).thenReturn(desiredConfigs);
        FileURLStream stream = new FileURLStream();
        ConfigLocal local = new ConfigLocal(mockDBHelper);
        ConfigRetriever remote = new ConfigRetriever(mockDBHelper, url, stream);
        ConfigManager manager = new ConfigManager(remote, local);
        RemoteMain main = new RemoteMain(irHandler, mockDBHelper, mockNotify, mockSSDP);
        main.setConfigManager(manager);

        return main;
    }

}
