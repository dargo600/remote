package com.example.remotecontrol;

import com.example.remotecontrol.util.LogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ECPTransmitter extends Thread {

    private static final String END_LINE = "\r\n";
    private final String USER_AGENT = "User-Agent: curl/7.54.0";
    private final String POST_CONTENT = "Accept: */*" + END_LINE +
            "Content-Length: 0" + END_LINE +
            "Content-Type: application/x-www-form-urlencoded" + END_LINE;
    private final String POST_TRAILER = USER_AGENT + END_LINE +
            POST_CONTENT + END_LINE;
    private final String TAG = ECPTransmitter.class.getSimpleName();
    private final BlockingQueue<String> buttonPressed;
    private Socket socket;

    private OutputStream requestStream;
    private InputStream responseStream;
    private InetAddress address;
    private int port = 8060;

    public ECPTransmitter(BlockingQueue<String> q) {
        buttonPressed = q;
    }

    public void run() {
        try {
            while (true) {
                processMediaId(buttonPressed.take());
            }
        } catch (InterruptedException ex) {
            LogUtil.logError("FIXME", "should we handle this exception");
        }
    }

    public void update(InetAddress address) {
        this.address = address;
    }

    private boolean checkConnect() {
        boolean connected = socket.isConnected() && !socket.isOutputShutdown();
        if (!connected && isTargeted()) {
            try {
                InetSocketAddress sa = new InetSocketAddress(address, port);
                socket.connect(sa);
                connected = socket.isConnected();
                socket.setTcpNoDelay(true);
                requestStream = socket.getOutputStream();
                responseStream = socket.getInputStream();
            } catch (IOException e) {
                LogUtil.logDebug(TAG, "connect error, " + e.toString());
            }
        }

        return connected;
    }


    private boolean isTargeted() {
        return address != null && port > 0;
    }

    public void processMediaId(String command) {
        if (address == null) {
            LogUtil.logError(TAG, "IP Address empty can not process " + command);
            return;
        }
        socket = new Socket();
        StringBuffer request = generatePostRequest(command);
        try {
            if (checkConnect()) {
                requestStream.write(request.toString().getBytes());
                requestStream.flush();
                consumeResponse();
            } else {
                LogUtil.logError(TAG, "Not connected");
            }
        } catch (IOException e) {
            LogUtil.logError(TAG, "transmit error, " + e.toString());
        } finally {
            try {
                if (requestStream != null) {
                    requestStream.close();
                    requestStream = null;
                }
                if (responseStream != null) {
                    responseStream.close();
                    responseStream = null;
                }
                socket.close();
            } catch (IOException e) {
                LogUtil.logError(TAG, "Failed to close socket");
            }
        }
    }

    private StringBuffer generatePostRequest(String command) {
        StringBuffer request = new StringBuffer();
        if (command.contains("launch")) {
            request.append("POST /" + command + " HTTP/1.1" + END_LINE);
        } else {
            request.append("POST /keypress/" + command + " HTTP/1.1" + END_LINE);
        }
        request.append("Host: " + address.getHostAddress() + ":" + port + END_LINE);
        request.append(POST_TRAILER);

        return request;
    }

    private void consumeResponse() {
        try {
            int length = responseStream.available();
            if (length > 0) {
                byte[] result = new byte[length];
                int total = 0, read = 0;
                do {
                    read = responseStream.read(result, total, length - total);
                    total += read;
                } while (read > 0 && total < length);
            }
        } catch (IOException e) {
            LogUtil.logError(TAG, "response error, " + e.toString());
        }
    }
}

