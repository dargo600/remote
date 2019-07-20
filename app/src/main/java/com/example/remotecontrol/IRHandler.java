package com.example.remotecontrol;

import android.hardware.ConsumerIrManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IRHandler {
    private ConsumerIrManager irManager;
    private TVType tv = TVType.SAMSUNG;
    private MediaType media = MediaType.APPLE;

    public IRHandler(ConsumerIrManager irManager) {
        this.irManager = irManager;
    }

    public final String getTv() {
        if (tv == TVType.SAMSUNG)
            return "samsung";
        return "";
    }

    public final String getMedia() {
        if (media == MediaType.APPLE)
            return "apple";
        return "";
    }

    public boolean detectRemoteControl() {
        return irManager.hasIrEmitter();
    }

    public boolean processTVId(int id) {
        GenericTVButton type = getGenericTVButtonType(id);
        if (type != GenericTVButton.UNKNOWN) {
            String prontoCode = "";
            if (tv == TVType.SAMSUNG) {
                prontoCode = getSamsungTVProntoCode(type);
            }
            if (prontoCode.length() > 0) {
                sendIRCode(prontoCode);
                return true;
            }
        }

        return false;
    }

    private GenericTVButton getGenericTVButtonType(int id) {
        switch (id) {
            case R.id.tv_power:
                return GenericTVButton.POWER;
            case R.id.tv_volume_inc:
                return GenericTVButton.VOLUME_UP;
            case R.id.tv_volume_dec:
                return GenericTVButton.VOLUME_DOWN;
            case R.id.tv_source:
                return GenericTVButton.SOURCE;
        }

        return GenericTVButton.UNKNOWN;
    }

    private String getSamsungTVProntoCode(GenericTVButton type) {
        switch (type) {
            case POWER:
                return CMD_TV_POWER;
            case VOLUME_UP:
                return CMD_TV_VOLUP;
            case VOLUME_DOWN:
                return CMD_TV_VOLDOWN;
            case SOURCE:
                return CMD_TV_SOURCE;
        }

        return "";
    }

    public boolean processMediaId(int id) {
        IRHandler.GenericMediaButton type = getGenericMediaButtonType(id);
        if(type != IRHandler.GenericMediaButton.UNKNOWN) {
            String prontoCode = determineProntoCode(type);
            if (prontoCode.length() != 0) {
                sendIRCode(prontoCode);
                return true;
            }
        }

        return false;
    }


    private GenericMediaButton getGenericMediaButtonType(int id) {
        switch (id) {
            case R.id.up:
                return GenericMediaButton.UP;
            case R.id.down:
                return GenericMediaButton.DOWN;
            case R.id.left:
                return GenericMediaButton.LEFT;
            case R.id.right:
                return GenericMediaButton.RIGHT;
            case R.id.select:
            case R.id.play:
                return GenericMediaButton.PLAY;
            case R.id.menu:
                return GenericMediaButton.MENU;
        }

        return GenericMediaButton.UNKNOWN;
    }

    private String determineProntoCode(GenericMediaButton type) {
        if (media == MediaType.APPLE) {
            return getAppleProntoCode(type);
        } else if (media == MediaType.ROKU) {
            return getRokuProntoCode(type);
        } else {
            return "";
        }
    }

    private String getAppleProntoCode(GenericMediaButton type) {
        switch (type) {
            case UP:
                return CMD_APPLE_UP;
            case DOWN:
                return CMD_APPLE_DOWN;
            case LEFT:
                return CMD_APPLE_LEFT;
            case RIGHT:
                return CMD_APPLE_RIGHT;
            case SELECT:
            case PLAY:
                return CMD_APPLE_SELECT;
            case MENU:
                return CMD_APPLE_MENU;
        }

        return "";
    }

    private String getRokuProntoCode(GenericMediaButton type) {

        return "";
    }

    /**
     * Convert Pronto Hex format to obtain frequency
     * http://www.hifi-remote.com/infrared/IR-PWM.shtml
     * as per irData code.  Then send the code using the frequency.
     */
    private void sendIRCode(final String irData) {
        List<String> list = new ArrayList<String>(
                Arrays.asList(irData.split(" ")));
        int frequency = calculateFrequency(list);
        int[] irPattern = calculateIRPattern(frequency, list);

        android.util.Log.d("Remote", "frequency" + frequency);
        android.util.Log.d("Remote", "pattern" + Arrays.toString(irPattern));
        irManager.transmit(frequency, irPattern);
    }

    /** As per README, the frequency value is represented in terms of the Pronto Internal
     * CLock and is expected to be translated to be about 40,000 +- 10%
     * @param irList A list of pronto codes for this button
     * @return Frequency used to transmit and determine IR Pattern
     */
    private int calculateFrequency(List<String> irList) {
        irList.remove(0); // dummy
        int frequency = Integer.parseInt(irList.remove(0), 16); // frequency
        irList.remove(0); // seq1
        irList.remove(0); // seq2

        return (int) (1000000 / (frequency * 0.241246));
    }

    private int[] calculateIRPattern(int frequency, List<String> irList) {
        int pulses = 1000000 / frequency;
        int[] pattern = new int[irList.size()];

        for (int i = 0; i < irList.size(); i++) {
            int count = Integer.parseInt(irList.get(i), 16);
            pattern[i] = count * pulses;
        }

        return pattern;
    }

    enum TVType { UNKNOWN, HAIER, SAMSUNG,  };
    enum MediaType { UNKNOWN, APPLE, ROKU };
    enum GenericTVButton { UNKNOWN, POWER, SOURCE, VOLUME_UP, VOLUME_DOWN };
    enum GenericMediaButton { UNKNOWN, UP, DOWN, LEFT, RIGHT, SELECT, PLAY, MENU };

    // IR Commands for Samsung TV
    //http://irdb.tk/codes/
    private final static String CMD_TV_POWER =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";

    private final static String CMD_TV_VOLUP =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";

    private final static String CMD_TV_VOLDOWN =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";

    private final static String CMD_TV_SOURCE =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";

    private final static String CMD_APPLE_DOWN =
            "0000 006E 0022 0002 0156 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 05E6 0156 0056 0015 0E45";

    private final static String CMD_APPLE_UP =
            "0000 006E 0022 0002 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 05E6 0155 0056 0015 0E45";

    private final static String CMD_APPLE_LEFT =
            "0000 006E 0022 0002 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 063B 0155 0055 0015 0E44";

    private final static String CMD_APPLE_RIGHT =
            "0000 006E 0022 0002 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 05E6 0155 0056 0015 0E44";

    private final static String CMD_APPLE_MENU =
            "0000 006E 0022 0002 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 063A 0155 0055 0015 0E44";

    /**
     * @todo It seems APPLY_PLAY == APPLE_SELECT_PAUSE.  This other code from
     * http://www.remotecentral.com/cgi-bin/mboard/rc-custom/thread.cgi?28503
    private final static String CMD_APPLE_PLAY_PAUSE_EXTENDED=
    "0000 006F 0022 0014 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0015 0015 0041 0015 0041 0015 0590 0155 0056 0015 0590 0155 0056 0015 0590 0155 0056 0015 0590 0155 0056 0015 0590 0155 0056 0015 0590 0155 0056 0015 0590 0155 0056 0015 0590 0155 0056 0015 0590 0155 0056 0015 0590 0155 0056 0015 04C4";
     */

    /** not sure this really exists on standard remote, play/pause/select is pretty much the same thing */
    private final static String CMD_APPLE_SELECT =
            "0000 006E 0022 0002 0155 00AC 0015 0015 0015 0041 0015 0041 0015 0041 0015 0015 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0041 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0041 0015 0041 0015 0015 0015 063B 0155 0056 0015 0E44";

}
