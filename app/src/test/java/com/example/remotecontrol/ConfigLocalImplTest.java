package com.example.remotecontrol;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.util.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigLocalImplTest {

   @Mock
   DBHelper mockDB;

   @Test(expected=ParseConfigException.class)
   public void initFromLocal_throwsException() throws Exception {
      when(mockDB.getReadableDatabase()).thenReturn(null);
      ConfigLocalImpl cli = new ConfigLocalImpl(mockDB);
      cli.initFromLocal();
   }

}
