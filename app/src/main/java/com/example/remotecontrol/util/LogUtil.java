package com.example.remotecontrol.util;

import android.util.Log;

public class LogUtil {
    private static boolean logToTerminal = false;

    public static void enableLogToTerminal() {
        logToTerminal = true;
    }

    public static void logError(String tag, String msg) {
        if (logToTerminal) {
            System.out.println("ERROR: " + tag + ": " + msg);
        } else {
            Log.e(tag, msg);
        }
    }

    public static void logDebug(String tag, String msg) {
        if (logToTerminal) {
            System.out.println("DEBUG: " + tag + ": " + msg);
        } else {
            Log.d(tag, msg);
        }
    }
}
