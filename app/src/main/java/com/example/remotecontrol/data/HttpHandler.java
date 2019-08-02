package com.example.remotecontrol.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.example.remotecontrol.util.LogUtil;
import com.example.remotecontrol.util.ParseConfigException;

public class HttpHandler {
    private static final String TAG = HttpHandler.class.getSimpleName();

    public HttpHandler() {
    }

    public String processURL(String reqUrl) throws Exception {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            InputStream in;
            if (reqUrl.startsWith("file:///")) {
                URLConnection conn = url.openConnection();
                in = new BufferedInputStream(conn.getInputStream());
            } else {
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                in = new BufferedInputStream(conn.getInputStream());
            }
            response = convertStreamToString(in);
        } catch (Exception e) {
            String msg = "Exception: " + e.getMessage();
            LogUtil.logError(TAG, msg);
            throw new ParseConfigException(msg);
        }

        return response;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;

        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}