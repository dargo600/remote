package com.example.remotecontrol.util;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.net.URL;
import java.net.URLConnection;

import com.example.remotecontrol.data.streams.URLStream;

public class FileURLStream implements URLStream {
    public InputStream create(String reqUrl, URL url) throws Exception {
        URLConnection conn = url.openConnection();
        return new BufferedInputStream(conn.getInputStream());
    }
}
