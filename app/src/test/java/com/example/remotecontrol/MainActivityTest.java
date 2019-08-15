package com.example.remotecontrol;

import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.GridLayout;

import com.example.remotecontrol.data.DBHelper;
import com.example.remotecontrol.data.IRHandler;
import com.example.remotecontrol.remote.*;
import com.example.remotecontrol.remote.MainActivity;
import com.example.remotecontrol.util.LogUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.net.InetAddress;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {
    @Mock
    WifiManager mockWifi;

    @Mock
    SSDPHandler mockSSDP;

    @Mock
    GenericNotify mockNotify;

    @Mock
    IRHandler mockIR;

    @Mock
    DBHelper mockDB;

    @Mock
    View mockView;

    @Mock
    GridLayout mockGridLayout;

    @Test
    public void processMediaButton_failsWithDisplayWhenButtonNotFound() {
        LogUtil.enableLogToTerminal();
        MainActivity main = new MainActivity();
        main.initRemoteMain(mockIR, mockDB, mockSSDP);
        when(mockView.getParent())
            .thenReturn(mockGridLayout);
        when(mockGridLayout.getContentDescription())
                .thenReturn("samsungConfig1");
        when(mockView.getContentDescription())
                .thenReturn("up");
        main.getRemoteMain().setNotify(mockNotify);
        main.processMediaButton(mockView);
        String msg = "Unrecognized button up";
        verify(mockNotify, times(1)).displayMessage(msg);
    }

    @Test
    public void processMediaButton_SSDPsuccess() throws Exception {
        LogUtil.enableLogToTerminal();
        MainActivity main = new MainActivity();
        SSDPHandler ssdp = new SSDPHandler(mockWifi);
        main.initRemoteMain(mockIR, mockDB, ssdp);
        when(mockView.getParent())
                .thenReturn(mockGridLayout);
        when(mockGridLayout.getContentDescription())
                .thenReturn("rokuSSDPConfig1");
        when(mockView.getContentDescription())
                .thenReturn("select");
        main.getRemoteMain().setNotify(mockNotify);

        InetAddress inet = InetAddress.getByName("172.16.1.68");
        main.getRemoteMain().getTransmitter().update(inet);
        main.processMediaButton(mockView);
    }
}
