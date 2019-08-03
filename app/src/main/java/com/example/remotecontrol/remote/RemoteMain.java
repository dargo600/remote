package com.example.remotecontrol.remote;

import android.database.sqlite.SQLiteException;

import com.example.remotecontrol.data.*;
import com.example.remotecontrol.data.streams.*;
import com.example.remotecontrol.util.DBReadException;
import com.example.remotecontrol.util.ParseConfigException;

import org.json.JSONException;

public class RemoteMain {

    private final String TAG = RemoteMain.class.getSimpleName();
    private final String baseURL = "http://phaedra:5000/api/";

    private ConfigManager configManager;
    private ConfigRetriever configRetriever;
    private ConfigLocal configLocal;
    private DBHelper dbHelper;
    private IRHandler irHandler;

    public RemoteMain(IRHandler irHandler, DBHelper dbHelper) {
        this.irHandler = irHandler;
        this.dbHelper = dbHelper;
        URLStream stream = new HTTPURLStream();
        configRetriever = new ConfigRetriever(dbHelper, baseURL, stream);
        configLocal = new ConfigLocal(dbHelper);
        configManager = new ConfigManager(configRetriever, configLocal);
    }

    public String doBackgroundTask() {
        String errorMessage = "";
        try {
            configManager.initLocal();
            irHandler.updateDeviceConfigs(configManager.getRequestedConfigs());
        } catch (DBReadException | ParseConfigException e) {
            errorMessage = "Failed to get json from server " + e.getMessage();
        } catch (JSONException e) {
            errorMessage = "Json parsing error " + e.getMessage();
        } catch (SQLiteException e) {
            errorMessage = "DB error " + e.getMessage();
        } catch (Exception e) {
            errorMessage = "Error: " + e.getMessage();
        }

        return errorMessage;
    }

    public String processMediaId(String cd, String media) {
        String error = "";
        if (!irHandler.processMediaId(cd, media)) {
            error = "Unrecognized " + media + " button" + cd;
        }

        return error;
    }
}
