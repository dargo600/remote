package com.example.remotecontrol.data;

import android.hardware.ConsumerIrManager;
import java.util.HashMap;

public class IRHandler {

    private static final String TAG = IRHandler.class.getSimpleName();

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

    public boolean processMediaId(String id, String configName) {
        DeviceConfiguration device = deviceConfigs.get(configName);
        if (device != null) {
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
