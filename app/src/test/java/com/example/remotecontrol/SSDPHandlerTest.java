package com.example.remotecontrol;

import android.net.wifi.WifiManager;

import com.example.remotecontrol.util.LogUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class SSDPHandlerTest {

    @Mock
    WifiManager mockWifi;

    @Test
    public void ifWifiEnabledDoSSDP_success() throws Exception {
        LogUtil.enableLogToTerminal();
        SSDPHandler ssdp = new SSDPHandler(mockWifi);
        ssdp.querySSDP(SSDPHandler.ROKU_ST);
        HashSet<String> currentAddresses = ssdp.getAddresses();
        assertFalse(currentAddresses.isEmpty());
    }
}
