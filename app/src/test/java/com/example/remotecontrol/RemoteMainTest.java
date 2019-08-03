package com.example.remotecontrol;

import android.hardware.ConsumerIrManager;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.util.*;
import com.example.remotecontrol.remote.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class RemoteMainTest {

    @Mock
    IRHandler mockIR;

    @Mock
    DBHelper mockDB;

    @Mock
    ConsumerIrManager mockIRM;

    @Test
    public void doBackgroundTask_fails() {
        LogUtil.enableLogToTerminal();
        RemoteMain main = new RemoteMain(mockIR, mockDB);
        main.doBackgroundTask();
    }

    public void  processMedia_fails() {
        LogUtil.enableLogToTerminal();
        IRHandler ir = new IRHandler(mockIRM);
        ir.processMediaId("appleConfig1", "media");
    }
}
