package com.example.remotecontrol;

import android.net.wifi.WifiManager;

import com.example.remotecontrol.util.LogUtil;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;

public class SSDPHandler {
    private final String TAG = SSDPHandler.class.getSimpleName();

    private static String SSDP_DETECT_ADDRESS = "239.255.255.250";
    private static int SSDP_PORT = 1900;
    private static final String END_LINE = "\r\n";
    private static final String SSDP_DETECT_ROKU_QUERY =
            "M-SEARCH * HTTP/1.1" + END_LINE +
                    "HOST: " + SSDP_DETECT_ADDRESS + ":" + SSDP_PORT + END_LINE +
                    "MAN: \"ssdp:discover\"" + END_LINE +
                    "ST: roku:ecp" + END_LINE +
                    END_LINE;
    private static final int ONE_SECOND = 1000;
    private static final int SSDP_DETECT_TIMEOUT = ONE_SECOND;

    private HashSet<String> addresses;
    private WifiManager wifi;

    public SSDPHandler(WifiManager wifi) {
       this.wifi = wifi;
       addresses = new HashSet<>();
    }

    public void ifWifiEnabledDoSSDP() throws Exception {
        if(wifi != null) {
            WifiManager.MulticastLock lock = wifi.createMulticastLock("lock_wifi");
            lock.acquire();
            queryForRokuDevices(SSDP_DETECT_ROKU_QUERY);
            lock.release();
        } else {
            throw new Exception("Wifi Is not enabled");
        }
    }

    private void queryForRokuDevices(String query) {
        DatagramSocket socket = null;
        try {
            InetAddress group = InetAddress.getByName(SSDP_DETECT_ADDRESS);
            socket = new DatagramSocket(SSDP_PORT);
            socket.setReuseAddress(true);
            DatagramPacket dgram = new DatagramPacket(query.getBytes(),
                    query.length(), group, SSDP_PORT);
            socket.send(dgram);
            checkForResponse(socket);
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            socket.close();
        }
    }

    private void checkForResponse(DatagramSocket socket) throws IOException {
        long time = System.currentTimeMillis();
        long curTime = System.currentTimeMillis();
        while (curTime - time < SSDP_DETECT_TIMEOUT) {
            DatagramPacket p = new DatagramPacket(new byte[12], 12);
            socket.receive(p);
            String s = new String(p.getData(), 0, p.getLength());
            if (s.toUpperCase().equals("HTTP/1.1 200")) {
                String curAddress = p.getAddress().getHostAddress();
                addresses.add(curAddress);
            }
            curTime = System.currentTimeMillis();
        }
    }

    public HashSet<String> getAddresses() {
        return addresses;
    }
}
