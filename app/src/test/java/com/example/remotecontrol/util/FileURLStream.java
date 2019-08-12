package com.example.remotecontrol.util;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import com.example.remotecontrol.data.streams.*;

public class FileURLStream extends GenericURLStream implements URLStream {
    public String processURL(String reqUrl) throws Exception {
        String response;
        URL url = new URL(reqUrl);
        URLConnection conn = url.openConnection();
        try {
            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in);
        } catch (Exception e) {
            String msg = "Exception: " + e.getMessage();
            LogUtil.logError(TAG, msg);
            throw new ParseConfigException(msg);
        } finally {
            conn.getInputStream().close();
        }

        return response;
    }
}
