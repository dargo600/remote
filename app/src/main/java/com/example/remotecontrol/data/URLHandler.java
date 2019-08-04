package com.example.remotecontrol.data;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import com.example.remotecontrol.data.streams.URLStream;
import com.example.remotecontrol.util.LogUtil;
import com.example.remotecontrol.util.ParseConfigException;

public class URLHandler {
    private static final String TAG = URLHandler.class.getSimpleName();

    private URLStream stream;
    public URLHandler(URLStream stream) {
        this.stream = stream;
    }

    public String processURL(String reqUrl) throws Exception {
        String response = null;
        try {
            URL url = new URL(reqUrl);
            InputStream in = stream.create(reqUrl, url);
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