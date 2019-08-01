package com.example.remotecontrol;

import android.content.Context;

import com.example.remotecontrol.data.ConfigRetriever;
import com.example.remotecontrol.data.DBHelper;
import com.example.remotecontrol.data.RSTHandler;

import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;

public class ConfigRetrieverImplTest {

    @Mock
    Context mockContext;

    @Mock
    DBHelper mockDB;

    @Mock
    ConfigRetriever mockRetrieve;

    @Mock
    RSTHandler mockRST;

    @Test
    public void initDBIfEmpty_ReturnsTrueIfDBExists() {
 /**
    LogUtil.enableLogToTerminal();
        when(mockDB.isDBEmpty()).thenReturn(false);


        ConfigRetrieverImpl myObjectUnderTest = new ConfigRetrieverImpl(mockDB, mockContext);
        boolean result;
        try {
            result = myObjectUnderTest.getRequestedConfigs();
        } catch (JSONException e) {
            result = false;
        }
  assertEquals(result, true);
  **/
    }


    @Test
    public void initDBIfEmpty_returnsFalseOnHTTPFail() {
   /**     LogUtil.enableLogToTerminal();
        when(mockDB.isDBEmpty()).thenReturn(true);
        when(mockRST.downloadAndProcessConfigData()).thenReturn(false);

        ConfigManager myObjectUnderTest = new ConfigManager(mockDB, mockContext);

        try {
            boolean result = myObjectUnderTest.initDBIfEmpty();
            assertEquals(result, false);
        } catch (JSONException e) {
            boolean exceptionOccurred = true;
            assertEquals(exceptionOccurred, true);
        } **/
    }

    @Test
    public void initializeTestConfiguration_returnsFalseWhenDBEmpty() {


    }

}
