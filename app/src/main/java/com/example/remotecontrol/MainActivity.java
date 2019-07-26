package com.example.remotecontrol;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private boolean downloadAttempted = false;
    private ArrayList<HashMap<String, String>> deviceList;
    private IRHandler irHandler;
    private ConfigManager configManager;
    private DBHelper dbHelper;

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
        irHandler = new IRHandler(ir);
        if (irHandler.detectRemoteControl()) {
            processIRDetected();
        } else {
            Toast toast = Toast.makeText(this, "IR emitter unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
            setContentView(R.layout.activity_unsupported);
        }
    }

    private void processIRDetected() {
        dbHelper = new DBHelper(this);
        configManager = new ConfigManager(dbHelper, this);
        new GetRemoteConfiguration().execute();
        setContentView(R.layout.activity_main);
    }

    private class GetRemoteConfiguration extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            Boolean succeeded = false;
            try {
                boolean processedConfig = configManager.processConfiguration();
                if (!processedConfig) {

                    Log.e(TAG, "Failed to get json from server.");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                             Toast t = Toast.makeText(getApplicationContext(),
                                     "Failed to get json from server",
                             Toast.LENGTH_LONG);
                             t.show();
                        }
                    });
                } else {
                    boolean isInitialized =
                            configManager.initializeDeviceConfigurations();
                    if (isInitialized)
                    {
                        HashMap<String, DeviceConfiguration> configs =
                                configManager.getRequestedConfigs();
                        irHandler.updateDeviceConfigs(configs);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast t = Toast.makeText(getApplicationContext(),
                                        "Config fully processed",
                                        Toast.LENGTH_LONG);
                                t.show();
                            }
                        });
                        succeeded = true;
                    } else {
                        Log.e(TAG, "Config not initialized ");
                        succeeded = false;
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, "Json parsing error " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast t = Toast.makeText(getApplicationContext(),
                                "Json parsing error",
                                Toast.LENGTH_LONG);
                        t.show();
                    }
                });
            } catch (SQLiteException e) {
                Log.e(TAG, "DB error " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast t = Toast.makeText(getApplicationContext(),
                                "db error",
                                Toast.LENGTH_LONG);
                        t.show();
                    }
                });
            }
            downloadAttempted = true;

            return succeeded;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (!success) {
                Toast toast = Toast.makeText(MainActivity.this,
                        "Database unavailable", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    public void processTVButton(View view) {
        String cd = view.getContentDescription().toString();
        if (!irHandler.processMediaId(cd, "tv")) {
            displayError("Unrecognized tv button" + cd);
        }
    }

    public void processMediaButton(View view) {
        String cd = view.getContentDescription().toString();
        if (!irHandler.processMediaId(cd, "media")) {
            displayError("Unrecognized media Button " + cd);
        }
    }

    private void displayError(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        toast.show();

    }
}
