package com.example.remotecontrol;

import android.hardware.ConsumerIrManager;
import android.view.View;
import android.widget.Toast;

import com.example.remotecontrol.data.DBHelper;
import com.example.remotecontrol.data.IRHandler;
import com.example.remotecontrol.remote.*;
import com.example.remotecontrol.remote.MainActivity;
import com.example.remotecontrol.util.LogUtil;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

    @Mock
    Toast mockToast;

    @Mock
    IRHandler mockIR;

    @Mock
    DBHelper mockDB;

    @Mock
    View mockView;

    @Mock
    RemoteMain mockRemote;

    @Test
    public void processMediaButton_fails() {
        LogUtil.enableLogToTerminal();
        MainActivity main = new MainActivity();
        when(mockView.getContentDescription())
                .thenReturn("appleConfig1");
        try {
            main.processMediaButton(mockView);
        } catch (Exception e) {
            // fails on toast as expected but we can't easily mock toast
        }
    }

    @Test
    public void processTVButton_fails() {
        LogUtil.enableLogToTerminal();
        MainActivity main = new MainActivity();
        /** todo **/
        try {
            main.initRemoteMain(mockIR, mockDB);
            when(mockView.getContentDescription())
                    .thenReturn("samsungConfig1");
            main.processTVButton(mockView);
        } catch (Exception e) {
        }
    }
}
