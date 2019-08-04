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
    public void processMediaId() {
        //when(mockIRM.hasIrEmitter()).thenReturn(true);
        IRHandler irHandler = new IRHandler(mockIRM) ;
        String id = "up";
        String mediaType = "appleConfig1";
        irHandler.processMediaId(id, mediaType);
        /**
        int frequency = 0;
        int[] pattern = new int[];
        verify(mockIRM, times(1)).transmit(frequency, pattern);
**/
    }
}
