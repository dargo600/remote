package com.example.remotecontrol;

import android.content.Context;
import android.hardware.ConsumerIrManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private String TAG = MainActivity.class.getSimpleName();
    private ArrayList<HashMap<String, String>> deviceList;
    private IRHandler irHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ConsumerIrManager ir = (ConsumerIrManager)this.getSystemService(Context.CONSUMER_IR_SERVICE);
        irHandler = new IRHandler(ir);
        if (!irHandler.detectRemoteControl()) {
            Toast toast = Toast.makeText(this, "IR emitter unavailable",
                    Toast.LENGTH_SHORT);
            toast.show();
            setContentView(R.layout.activity_unsupported);
            return;
        }
        new GetRemoteConfiguration().execute();

        setContentView(R.layout.activity_main);
        Toast toast = Toast.makeText(this, "Detected IR emitter available",
                    Toast.LENGTH_SHORT);
        toast.show();
    }

    private class GetRemoteConfiguration extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Json Data is downloading",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            RSTHandler rst = new RSTHandler();
            HttpHandler httpHandler = new HttpHandler();
            String url = "http://phaedra/devices/";
            String jsonStr = httpHandler.processURL(url);

            Log.e(TAG, "Response from url: " + jsonStr);
            if (jsonStr != null) {
                try {
                    deviceList = rst.parseDevices(jsonStr);
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                }

            } else {
                Log.e(TAG, "Failed to get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Failed to get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
/*            ListAdapter adapter = new SimpleAdapter(MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"email", "mobile"},
                    new int[]{R.id.email, R.id.mobile});
            lv.setAdapter(adapter);
        */
        }
    }

    public void processTVButton(View view) {
        if (!irHandler.processTVId(view.getId())) {
            Toast toast = Toast.makeText(this, "Unrecognized Button",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void processMediaButton(View view) {
        if (!irHandler.processMediaId(view.getId())) {
            Toast toast = Toast.makeText(this, "Unrecognized Button",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
