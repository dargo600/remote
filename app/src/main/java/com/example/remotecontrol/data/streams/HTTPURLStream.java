package com.example.remotecontrol.data.streams;

import com.example.remotecontrol.util.*;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HTTPURLStream extends GenericURLStream implements URLStream {

    public String processURL(String reqUrl) throws Exception {
        String response;
        URL url = new URL(reqUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try {
            conn.setRequestMethod("GET");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (Exception e) {
            String msg = "Exception: " + e.getMessage();
            LogUtil.logError(TAG, msg);
            throw new ParseConfigException(msg);
        } finally {
            conn.disconnect();
        }

        return response;
    }
}
