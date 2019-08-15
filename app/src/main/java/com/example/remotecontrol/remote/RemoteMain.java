package com.example.remotecontrol.remote;

import com.example.remotecontrol.ECPTransmitter;
import com.example.remotecontrol.SSDPHandler;
import com.example.remotecontrol.data.*;
import com.example.remotecontrol.data.streams.*;
import com.example.remotecontrol.util.LogUtil;

import java.net.InetAddress;
import java.util.HashSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RemoteMain {

    private final String TAG = RemoteMain.class.getSimpleName();
    private final String baseURL = "http://phaedra:5000/api/";

    private IRHandler irHandler;
    private GenericNotify notify;
    private ConfigManager configManager;
    private SSDPHandler ssdpHandler;
    private HashSet<String> addresses;
    private ECPTransmitter transmitter;
    private BlockingQueue<String> buttonPressed;

    public RemoteMain(IRHandler irHandler, DBHelper dbHelper, GenericNotify notify, SSDPHandler ssdp) {
        this.irHandler = irHandler;
        this.notify = notify;
        this.ssdpHandler = ssdp;
        URLStream stream = new HTTPURLStream();
        ConfigRetriever configRetriever = new ConfigRetriever(dbHelper, baseURL, stream);
        ConfigLocal configLocal = new ConfigLocal(dbHelper);
        configManager = new ConfigManager(configRetriever, configLocal);
        buttonPressed = new ArrayBlockingQueue<String>(100);
        transmitter = new ECPTransmitter(buttonPressed);
        transmitter.start();
    }

    public ECPTransmitter getTransmitter() {
        return transmitter;
    }

    public void setNotify(GenericNotify notify) {
        this.notify = notify;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public String doBackgroundTask() {
        String errorMessage = "";
        try {
            configManager.initLocal();
            irHandler.updateDeviceConfigs(configManager.getRequestedConfigs());
            ssdpHandler.ifWifiEnabledDoSSDP();
            updateTransmitterAddress();
        } catch (Exception e) {
            errorMessage = "Error: " + e.getMessage();
        }

        return errorMessage;
    }

    private void updateTransmitterAddress() throws Exception {
        addresses = ssdpHandler.getAddresses();
        for (String address : addresses) {
            InetAddress inet = InetAddress.getByName(address);
            LogUtil.logError(TAG, "FIXME Found address" + address);
            transmitter.update(inet);
            break;
        }
    }

    public void processIRMediaId(String buttonName, String configValue) {
        if (!irHandler.processMediaId(buttonName, configValue)) {
            String error = "Unrecognized button " + buttonName;
            notify.displayMessage(error);
        }
    }

    public void processSSDPMediaId(String buttonName) {
        try {
            if (addresses == null || addresses.isEmpty()) {
                LogUtil.logError(TAG, "Updating transmitter address and local addresses");
                updateTransmitterAddress();
            }
            if (addresses == null || addresses.isEmpty()) {
                LogUtil.logError(TAG, "Missing IP: Not processing " + buttonName);
                return;
            }
            buttonPressed.put(buttonName);
        } catch (Exception e) {
            LogUtil.logError(TAG, "FIXME " + e.getMessage());
            String error = "Exception: Failed to parse " + buttonName ;
            notify.displayMessage(error);
        }
    }
}
