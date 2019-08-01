package com.example.remotecontrol.remote;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.remotecontrol.data.ConfigRetriever;
import com.example.remotecontrol.data.ConfigRetrieverImpl;
import com.example.remotecontrol.data.DBHelper;
import com.example.remotecontrol.data.DeviceConfiguration;
import com.example.remotecontrol.data.IRHandler;
import com.example.remotecontrol.R;
import com.example.remotecontrol.data.ConfigManager;
import com.example.remotecontrol.util.DBReadException;
import com.example.remotecontrol.util.ParseConfigException;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import com.example.remotecontrol.util.LogUtil;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getSimpleName();

    private boolean downloadAttempted = false;
    private ArrayList<HashMap<String, String>> deviceList;
    private IRHandler irHandler;
    private ConfigManager configManager;
    private ConfigRetriever configRetriever;
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
        configRetriever = new ConfigRetrieverImpl(dbHelper);
        configManager = new ConfigManager(configRetriever, this);
        new GetRemoteConfiguration().execute();
        setContentView(R.layout.activity_main);
    }

    private class GetRemoteConfiguration extends AsyncTask<Void, Void, Boolean> {
        private String errorMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            Boolean succeeded = false;
            try {
                HashMap<String, DeviceConfiguration> requestedConfigs;
                requestedConfigs = configManager.initializeConfigs();
                irHandler.updateDeviceConfigs(requestedConfigs);
                succeeded = true;
            } catch (DBReadException | ParseConfigException e) {
                errorMessage = "Failed to get json from server" + e.getMessage();
                LogUtil.logError(TAG, errorMessage);
            } catch (JSONException e) {
                errorMessage = "Json parsing error " + e.getMessage();
                LogUtil.logError(TAG, errorMessage);
            } catch (SQLiteException e) {
                errorMessage = "DB error " + e.getMessage();
                LogUtil.logError(TAG, errorMessage);
            } catch (Exception e) {
                errorMessage = "Error: " + e.getMessage();
                LogUtil.logError(TAG, errorMessage);
            }

            return succeeded;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            String msg = "Config fully processed";
            if (!success) {
                msg = errorMessage;
                LogUtil.logError(TAG, msg);
            }
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
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
