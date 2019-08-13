package com.example.remotecontrol;

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

import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class MainActivityTest {

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
        main.initRemoteMain(mockIR, mockDB);
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
}
