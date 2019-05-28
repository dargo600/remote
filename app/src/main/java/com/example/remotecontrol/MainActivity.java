package com.example.remotecontrol;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // IR Commands for Samsung TV UE32H...
    // from http://www.remotecentral.com/cgi-bin/codes/samsung/tv_functions/

    private final static String CMD_TV_POWER =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";

    private final static String CMD_TV_VOLUP =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";

    private final static String CMD_TV_VOLDOWN =
              "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";

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

    /**
    private final static String CMD_TV_SOURCE =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 0041 0016 06FB";
    private final static String CMD_TV_CH_NEXT =
            "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 003f 0015 003f 0015 0015 0015 0040 0015 003f 0015 003f 0015 0702 00a9 00a8 0015 0015 0015 0e6e";
    private final static String CMD_TV_CH_PREV =
            "0000 006d 0022 0003 00a9 00a8 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 0015 003f 0015 0015 0015 0015 0015 0015 0015 003f 0015 003f 0015 003f 0015 003f 0015 0015 0015 003f 0015 003f 0015 003f 0015 0702 00a9 00a8 0015 0015 0015 0e6e";
    private final static String CMD_TV_BACK =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 06FB";
**/
    // from http://irdb.tk/codes/

    private final static String CMD_TV_LEFT =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0016 0016 0016 0016 0041 0016 06FB";
    private final static String CMD_TV_RIGHT =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0041 0016 06FB";
    private final static String CMD_TV_ENTER =
            "0000 006C 0000 0022 00AD 00AD 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0016 0041 0016 0016 0016 0041 0016 0041 0016 0016 0016 0041 0016 0041 0016 0041 0016 0016 0016 0041 0016 0016 0016 0016 0016 0041 0016 06FB";

    // IR Commands for Samsung Soundbar HW-H450
    // from https://www.remotecentral.com/cgi-bin/forums/viewpost.cgi?932443

    private final static String CMD_SB_POWER =
            "0000 006C 0000 0027 00AC 00AC 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 003A 0013 0013 0013 0013 0013 003A 0013 003A 0013 003A 0013 003A 0013 0013 0013 0013 0013 0013 0013 0013 0013 00AC 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 003A 0013 003A 0013 0013 0013 003A 0013 003A 0013 003A 0013 003A 0013 0013 0013 0013 0013 0013 0013 003A 0013 0856";
    private final static String CMD_SB_SOURCE =
            "0000 006C 0000 0027 00AC 00AC 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 003A 0013 0013 0013 0013 0013 003A 0013 003A 0013 003A 0013 003A 0013 0013 0013 0013 0013 0013 0013 0013 0013 00AC 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 0013 0013 003A 0013 0013 0013 003A 0013 003A 0013 003A 0013 0013 0013 0013 0013 003A 0013 0013 0013 003A 0013 0013 0013 0013 0013 0013 0013 003A 0013 0856";
    private final static String CMD_SB_VOLUP =
            "0000 006C 0000 0027 00AC 00AC 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 003A 0013 0013 0013 0013 0013 003A 0013 003A 0013 003A 0013 003A 0013 0013 0013 0013 0013 0013 0013 0013 0013 00AC 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 003A 0013 003A 0013 0013 0013 003A 0013 003A 0013 003A 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 0013 0013 0013 0013 0013 0013 003A 0013 0856";
    private final static String CMD_SB_VOLDOWN =
            "0000 006C 0000 0027 00AC 00AC 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 003A 0013 0013 0013 0013 0013 003A 0013 003A 0013 003A 0013 003A 0013 0013 0013 0013 0013 0013 0013 0013 0013 00AC 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 003A 0013 003A 0013 003A 0013 0013 0013 003A 0013 003A 0013 003A 0013 0013 0013 0013 0013 0013 0013 0013 0013 003A 0013 0856";

    private ConsumerIrManager irManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        irManager = (ConsumerIrManager)this.getSystemService(Context.CONSUMER_IR_SERVICE);
        if (!irManager.hasIrEmitter()) {
            Toast toast = Toast.makeText(this, "IR emitter unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
            setContentView(R.layout.activity_unsupported);

            return;
        }
        setContentView(R.layout.activity_main);

        Toast toast = Toast.makeText(this, "Detected IR emitter available",
                    Toast.LENGTH_SHORT);
        toast.show();
    }

    public void updateTVButton(View view) {
        switch (view.getId()) {
            case R.id.tv_power:
                sendIRCode("power", CMD_TV_POWER);
                break;
            case R.id.tv_volume_inc:
                sendIRCode("vol_inc", CMD_TV_VOLUP);
                break;
            case R.id.tv_volume_dec:
                sendIRCode("vol_dec", CMD_TV_VOLDOWN);
                break;
            case R.id.apple_up:
                sendIRCode("apple_up", CMD_APPLE_UP);
                break;
            case R.id.apple_down:
                sendIRCode("apple_down", CMD_APPLE_DOWN);
                break;
            case R.id.apple_left:
                sendIRCode("apple_left", CMD_APPLE_LEFT);
                break;
            case R.id.apple_right:
                sendIRCode("apple_right", CMD_APPLE_RIGHT);
                break;
            case R.id.apple_select:
            case R.id.apple_play:
                sendIRCode("apple_select", CMD_APPLE_SELECT);
                break;
            case R.id.apple_menu:
                sendIRCode("apple_menu", CMD_APPLE_MENU);
                break;
        }
    }

    /**
     * Convert Pronto Hex format to obtain frequency
     * http://www.hifi-remote.com/infrared/IR-PWM.shtml
     * as per irData code.  Then send the code using the frequency.
     */
    private void sendIRCode(final String type, final String irData) {
        List<String> list = new ArrayList<String>(Arrays.asList(irData.split(" ")));
        list.remove(0); // dummy
        int frequency = Integer.parseInt(list.remove(0), 16); // frequency
        list.remove(0); // seq1
        list.remove(0); // seq2

        frequency = (int) (1000000 / (frequency * 0.241246));
        int pulses = 1000000 / frequency;
        int count;

        int[] pattern = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            count = Integer.parseInt(list.get(i), 16);
            pattern[i] = count * pulses;
        }

        android.util.Log.d("Remote", "frequency" + type + " " + frequency);
        android.util.Log.d("Remote", "pattern" + Arrays.toString(pattern));
        irManager.transmit(frequency, pattern);
    }
}
