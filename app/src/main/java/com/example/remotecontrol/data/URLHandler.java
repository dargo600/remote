package com.example.remotecontrol.data;

import com.example.remotecontrol.data.streams.URLStream;

public class URLHandler {
    private static final String TAG = URLHandler.class.getSimpleName();

    private URLStream stream;
    public URLHandler(URLStream stream) {
        this.stream = stream;
    }

    public String processURL(String reqUrl) throws Exception {
        return stream.processURL(reqUrl);
    }
}