package com.example.remotecontrol.remote;

import android.content.Context;
import android.widget.Toast;

public class WidgetNotify implements GenericNotify {
    private Context mainContext;
    public WidgetNotify(Context c) {
        this.mainContext = c;
    }

    public void displayMessage(String msg) {
        int longIntervalDisplay = Toast.LENGTH_LONG;
        Toast.makeText(mainContext, msg, longIntervalDisplay).show();
    }
}
