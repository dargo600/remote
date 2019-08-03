package com.example.remotecontrol.data.streams;

import java.io.InputStream;
import java.net.URL;

public interface URLStream {
    InputStream create(String reqUrl, URL url) throws Exception;
}
