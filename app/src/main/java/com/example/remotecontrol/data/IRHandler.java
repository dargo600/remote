package com.example.remotecontrol.data;

import android.hardware.ConsumerIrManager;

import com.example.remotecontrol.util.LogUtil;

import java.util.HashMap;

public class IRHandler {

    private static final String TAG = IRHandler.class.getSimpleName();

    private ConsumerIrManager irManager;
    private HashMap<String, DeviceConfiguration> deviceConfigs;

    public IRHandler(ConsumerIrManager irManager) {
        this.irManager = irManager;
    }

    public boolean detectRemoteControl() {
        return irManager.hasIrEmitter();
    }

    public void
    updateDeviceConfigs(HashMap<String, DeviceConfiguration> deviceConfigs) {
        this.deviceConfigs = deviceConfigs;
    }

    public boolean processMediaId(String id, String configName) {
        DeviceConfiguration device = null;
        RCButton button = null;
        if (deviceConfigs != null) {
            device = deviceConfigs.get(configName);
            if (device != null) {
                button = device.getRCButton(id);
                if (button != null) {
                    sendIRCode(button);
                    return true;
                }
            }
        }
        LogUtil.logDebug(TAG, "Failed to send IR code for " + configName +
                " device " + device + " button " + button);

        return false;
    }

    /**
     * Convert Pronto Hex format to obtain frequency
     * http://www.hifi-remote.com/infrared/IR-PWM.shtml
     * as per irData code.  Then send the code using the frequency.
     */
    private void sendIRCode(final RCButton button) {
        int[] irPattern = button.getIrPattern();
        int frequency = button.getFrequency();
        LogUtil.logDebug(TAG, "Sending IRButton for " + button.getButtonType()
                + " frequency " + frequency);
        irManager.transmit(frequency, irPattern);
    }
}
