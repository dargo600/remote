package com.example.remotecontrol.remote;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.R;

import com.example.remotecontrol.util.LogUtil;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

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
        ConsumerIrManager ir = (ConsumerIrManager)this.getSystemService(Context.CONSUMER_IR_SERVICE);
        IRHandler irHandler = new IRHandler(ir);
        if (irHandler.detectRemoteControl()) {
            processIRDetected(irHandler);
        } else {
            Toast toast = Toast.makeText(this, "IR emitter unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
            setContentView(R.layout.activity_unsupported);
        }
    }

    private void processIRDetected(IRHandler irHandler) {
        DBHelperImpl dbHelper = new DBHelperImpl(this);
        initRemoteMain(irHandler, dbHelper);
        new GetRemoteConfiguration().execute();
        setContentView(R.layout.activity_main);
    }

    public void initRemoteMain(IRHandler irHandler, DBHelper dbHelper) {
        remoteMain = new RemoteMain(irHandler, dbHelper);
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
            }
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    public void processTVButton(View view) {
        String cd = view.getContentDescription().toString();
        String msg = remoteMain.processMediaId(cd, "tv");
        if (msg.length() > 0)
            displayError(msg);
    }

    public void processMediaButton(View view) {
        try {
            String cd = view.getContentDescription().toString();
            String msg = remoteMain.processMediaId(cd, "media");
            if (msg.length() > 0)
                displayError(msg);
        } catch (Exception e) {
            displayError("Error: " + e.getMessage());
        }
    }

    private void displayError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
