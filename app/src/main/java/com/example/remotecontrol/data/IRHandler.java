package com.example.remotecontrol.data;

import android.hardware.ConsumerIrManager;

import com.example.remotecontrol.util.LogUtil;

import java.util.HashMap;

public class IRHandler {

    private ConsumerIrManager irManager;
    private HashMap<String, DeviceConfiguration> deviceConfigs;
    private HashMap<String, String> desiredConfigs;

    public IRHandler(ConsumerIrManager irManager) {
        this.irManager = irManager;
        desiredConfigs = new HashMap<>();
        desiredConfigs.put("tv", "samsungConfig1");
        desiredConfigs.put("media", "appleConfig1");
    }

    public boolean detectRemoteControl() {
        return irManager.hasIrEmitter();
    }

    public void
    updateDesiredConfigs(HashMap<String, String> configs) {
        desiredConfigs = configs;
    }

    public void
    updateDeviceConfigs(HashMap<String, DeviceConfiguration> deviceConfigs) {
        this.deviceConfigs = deviceConfigs;
    }

    public boolean processMediaId(String id, String mediaType) {
        String desiredConfig = desiredConfigs.get(mediaType);
        if (deviceConfigs == null) {
            LogUtil.logError("FIXME", "why is device config null in procesmedia");
            return false;
        }
        if (desiredConfig.length() > 0) {
            DeviceConfiguration device = deviceConfigs.get(desiredConfig);
            if (device == null) {
                return false;
            }
            RCButton button = device.getRCButton(id);
            if (button != null) {
                sendIRCode(button);

                return true;
            }
        }

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
        irManager.transmit(frequency, irPattern);
    }
}
