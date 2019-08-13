package com.example.remotecontrol;

import android.database.sqlite.SQLiteException;
import android.hardware.ConsumerIrManager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.util.*;

import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class IRHandlerTest {
    @Mock
    ConsumerIrManager mockIRM;

    @Test
    public void detectRemoteControl_success() {
        when(mockIRM.hasIrEmitter()).thenReturn(true);
        IRHandler irHandler = new IRHandler(mockIRM) ;
        irHandler.detectRemoteControl();
    }

    @Test
    public void processMediaId_success() throws Exception {
        HashMap<String, DeviceConfiguration> deviceConfigs = new HashMap<>();
        DeviceConfiguration dc;
        dc = new DeviceConfiguration(1, "appleConfig1", "media");
        RCButton button = new RCButton("power", "0000 006C 0000 0000 0000 0000 0000 0000");
        dc.addRCButton("power", button);
        deviceConfigs.put("appleConfig1", dc);
        IRHandler irHandler = new IRHandler(mockIRM) ;
        irHandler.updateDeviceConfigs(deviceConfigs);
        String id = "power";
        String configName = "appleConfig1";
        irHandler.processMediaId(id, configName);

        int frequency = 38380;
        int[] pattern = { 0, 0, 0, 0};
        verify(mockIRM, times(1)).transmit(frequency, pattern);
    }

    @Test
    public void processMediaId_ButtonNotInConfig() throws Exception {
        HashMap<String, DeviceConfiguration> deviceConfigs = new HashMap<>();
        DeviceConfiguration dc;
        dc = new DeviceConfiguration(1, "appleConfig1", "media");
        RCButton button = new RCButton("power", "0000 006C 0000 0000 0000 0000 0000 0000");
        dc.addRCButton("power", button);
        deviceConfigs.put("appleConfig1", dc);
        IRHandler irHandler = new IRHandler(mockIRM) ;
        irHandler.updateDeviceConfigs(deviceConfigs);
        String id = "up";
        String mediaType = "media";
        irHandler.processMediaId(id, mediaType);
    }
}
