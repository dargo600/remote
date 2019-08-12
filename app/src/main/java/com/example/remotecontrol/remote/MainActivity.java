package com.example.remotecontrol.remote;

import android.app.Activity;
import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.R;

import com.example.remotecontrol.util.LogUtil;

public class MainActivity extends Activity {
    private final String TAG = MainActivity.class.getSimpleName();

    private GenericNotify notify;
    private RemoteMain remoteMain;
    private boolean downloadAttempted = false;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("downloadAttempted", downloadAttempted);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            downloadAttempted = savedInstanceState.getBoolean("downloadAttempted");
        }
        notify = new WidgetNotify(this);
        ConsumerIrManager ir = (ConsumerIrManager) this.getSystemService(Context.CONSUMER_IR_SERVICE);
        IRHandler irHandler = new IRHandler(ir);
        if (irHandler.detectRemoteControl()) {
            processIRDetected(irHandler);
        } else {
            notify.displayMessage("IR emitter unavailable");
            setContentView(R.layout.activity_unsupported);
        }
    }

    private void processIRDetected(IRHandler irHandler) {
        DBConnector dbConnector = new DBConnector(this);
        DBHelper dbHelper = new DBHelperImpl(dbConnector);
        initRemoteMain(irHandler, dbHelper);
        new GetRemoteConfiguration().execute();
        setContentView(R.layout.activity_main);
    }

    public void initRemoteMain(IRHandler irHandler, DBHelper dbHelper) {
        remoteMain = new RemoteMain(irHandler, dbHelper, notify);
    }

    private class GetRemoteConfiguration extends AsyncTask<Void, Void, Boolean> {
        private String errorMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            errorMessage = remoteMain.doBackgroundTask();

            return (errorMessage.length() == 0) ? true : false;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            String msg = "Configuration processed";
            if (!success) {
                msg = errorMessage;
                LogUtil.logError(TAG, msg);
            } else {
                LogUtil.logDebug(TAG, msg);
            }
            MainActivity.this.notify.displayMessage(msg);
        }
    }

    public void processTVButton(View view) {
        String cd = view.getContentDescription().toString();
        remoteMain.processMediaId(cd, "tv");
    }

    public void processMediaButton(View view) {
        String cd = view.getContentDescription().toString();
        remoteMain.processMediaId(cd, "media");
    }

    public RemoteMain getRemoteMain() {
        return remoteMain;
    }
}
