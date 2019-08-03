package com.example.remotecontrol;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.runners.MockitoJUnitRunner;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.util.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigLocalTest {

   @Mock
   DBHelper mockDB;

   @Test(expected=ParseConfigException.class)
   public void initFromLocal_throwsException() throws Exception {
      ConfigLocal cli = new ConfigLocal(mockDB);
      cli.initFromLocal();
   }

   @Test
   public void initFromLocal_DBAlreadyExists() throws Exception {
      LogUtil.enableLogToTerminal();
      FakeDBHelper fakeHelper = new FakeDBHelper();
      ConfigLocal cli = new ConfigLocal(fakeHelper);
      cli.initFromLocal();
   }

}
