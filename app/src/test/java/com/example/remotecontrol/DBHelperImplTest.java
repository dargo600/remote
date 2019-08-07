package com.example.remotecontrol;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.remotecontrol.data.*;

import java.util.List;
import java.util.Arrays;
import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class DBHelperImplTest {

    @Mock
    DBConnector mockConnect;

    @Mock
    SQLiteDatabase mockDB;

    @Mock
    Cursor mockCursor;

    @Mock
    Cursor mockButtonCursor;

    @Mock
    ContentValues mockContentValues;

    private int[] determineExpectedIRPattern(List<String> irList, int frequency) {
        int pulses = 1000000 / frequency;
        int[] irPattern = new int[irList.size()];

        for (int i = 0; i < irList.size(); i++) {
            int count = Integer.parseInt(irList.get(i), 16);
            irPattern[i] = count * pulses;
        }

        return irPattern;
    }


    @Test
    public void insertRequestedConfig_success() throws Exception {
        String configName = "samsungConfig1";
        when(mockConnect.initWrite()).thenReturn(mockDB);
        when(mockConnect.makeContentValues())
                .thenReturn(mockContentValues);

        DBHelperImpl dbHelper = new DBHelperImpl(mockConnect);
        dbHelper.initWrite();
        dbHelper.insertRequestedConfig(configName);
        verify(mockContentValues, times(1))
                .put("name", configName);
        verify(mockDB, times(1))
                .insert("requested_configs", null, mockContentValues);
    }

    @Test
    public void insertDeviceConfig_success() throws Exception {
        int deviceConfigId = 1;
        String configName = "samsungConfig1";
        when(mockConnect.initWrite()).thenReturn(mockDB);
        when(mockConnect.makeContentValues())
                .thenReturn(mockContentValues);

        DBHelperImpl dbHelper = new DBHelperImpl(mockConnect);
        dbHelper.initWrite();
        dbHelper.insertDeviceConfig(deviceConfigId, configName);
        verify(mockContentValues, times(1))
                .put("device_config_id", deviceConfigId);
        verify(mockContentValues, times(1))
                .put("name", configName);
        verify(mockDB, times(1))
                .insert("device_configs", null, mockContentValues);
    }

    @Test
    public void insertDevice_success() throws Exception {
        String deviceType = "media";
        String manufacturer = "apple";
        String modelNum = "v2";
        int deviceConfigId = 1;
        when(mockConnect.initWrite()).thenReturn(mockDB);
        when(mockConnect.makeContentValues())
                .thenReturn(mockContentValues);

        DBHelperImpl dbHelper = new DBHelperImpl(mockConnect);
        dbHelper.initWrite();
        dbHelper.insertDevice(deviceType, manufacturer, modelNum, deviceConfigId);
        verify(mockContentValues, times(1))
                .put("device_type", deviceType);
        verify(mockContentValues, times(1))
                .put("manufacturer", manufacturer);
        verify(mockContentValues, times(1))
                .put("model_num", modelNum);
        verify(mockContentValues, times(1))
                .put("device_config_id", deviceConfigId);
        verify(mockDB, times(1))
                .insert("devices", null, mockContentValues);
    }
    @Test
    public void insertButton_success() throws Exception {
        String type = "power";
        String prontoCode = "huge ir hex string";
        int deviceConfigId = 1;
        when(mockConnect.initWrite()).thenReturn(mockDB);
        when(mockConnect.makeContentValues())
                .thenReturn(mockContentValues);

        DBHelperImpl dbHelper = new DBHelperImpl(mockConnect);
        dbHelper.initWrite();
        dbHelper.insertButton(type, prontoCode, deviceConfigId);
        verify(mockContentValues, times(1))
                .put("rc_type", type);
        verify(mockContentValues, times(1))
                .put("ir_code", prontoCode);
        verify(mockContentValues, times(1))
                .put("device_config_id", deviceConfigId);
        verify(mockDB, times(1))
                .insert("rc_buttons", null, mockContentValues);
    }

    @Test
    public void cacheConfig_success() throws Exception {
        String configName = "samsungConfig1";
        int deviceConfigId = 1;
        when(mockConnect.initWrite()).thenReturn(mockDB);
        String deviceConfigQuery = "SELECT dc.device_config_id, d.device_type FROM " +
                "device_configs AS dc, devices AS d WHERE " +
                "d.device_config_id==dc.device_config_id AND dc.name = ?";
        when(mockDB.rawQuery(deviceConfigQuery, new String[] { configName}))
                .thenReturn(mockCursor);
        when(mockCursor.moveToFirst()).thenReturn(true);
        when(mockCursor.getInt(0))
                .thenReturn(deviceConfigId);
        when(mockCursor.getString(1))
                .thenReturn("tv");
        String buttonQuery = "SELECT r.rc_type, r.ir_code FROM rc_buttons AS r " +
                "WHERE r.device_config_id= ?";
        when(mockDB.rawQuery(buttonQuery,
                new String[]{"1"}))
                .thenReturn(mockButtonCursor);
        when(mockButtonCursor.moveToFirst()).thenReturn(true);
        when(mockButtonCursor.isAfterLast()).thenReturn(false).thenReturn(true);
        when(mockButtonCursor.getString(0))
                .thenReturn("power");
        when(mockButtonCursor.getString(1))
                .thenReturn(BUTTON_SAMSUNG_POWER);

        DBHelperImpl dbHelper = new DBHelperImpl(mockConnect);
        dbHelper.initWrite();
        HashMap<String, DeviceConfiguration> initDeviceConfigs = new HashMap<>();
        dbHelper.setRequestedConfigs(initDeviceConfigs);
        dbHelper.cacheConfig(configName);
        HashMap<String, DeviceConfiguration> retConfigs = dbHelper.getRequestedConfigs();
        DeviceConfiguration dc = retConfigs.get(configName);
        assertEquals(dc.getConfigName(), configName);
        RCButton button = dc.getRCButton("power");
        int expectedFrequency = 38380;
        assertEquals(button.getFrequency(), expectedFrequency);
        String unconvertedIRPattern = "00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";
        List<String> irList = Arrays.asList(unconvertedIRPattern.split(" "));
        int[] expectedIRPattern = determineExpectedIRPattern(irList, expectedFrequency);
        assertArrayEquals(button.getIrPattern(), expectedIRPattern);
    }

    private final String BUTTON_SAMSUNG_POWER = "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";
}

