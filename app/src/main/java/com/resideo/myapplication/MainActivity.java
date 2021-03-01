package com.resideo.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

public class MainActivity extends AppCompatActivity {
    private WifiManager.MulticastLock multiCastLock = null;
    public static final String TAG = "NSDLibrary";
    JmDNS quickSilverJmDNS = null;
    JmDNS randomServiceJmDNS = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnRegister = findViewById(R.id.button);
        Button btnUnregister = findViewById(R.id.button2);
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        InetAddress deviceIpAddress = getDeviceIpAddress(wifi);
        multiCastLock = wifi.createMulticastLock(getClass().getName());
        multiCastLock.setReferenceCounted(true);
        multiCastLock.acquire();

        try {
            quickSilverJmDNS = JmDNS.create(deviceIpAddress, "quickSilver");
            quickSilverJmDNS.addServiceListener("_quicksilver._tcp.local.", new NSDListener());
        } catch (IOException e) {
            Log.e(TAG, "Error -> " + e.toString());
        }


        btnRegister.setOnClickListener(v -> {
            try {
                if (randomServiceJmDNS == null)
                    randomServiceJmDNS = JmDNS.create(deviceIpAddress, "MyService");
                if (deviceIpAddress == null) return;
                ServiceInfo serviceInfo = ServiceInfo.create("_quicksilver._tcp.local.",
                        "AndroidTest", 0,
                        "test from android");
                randomServiceJmDNS.registerService(serviceInfo);

            } catch (Exception e) {
                Log.e(TAG, "Registration-Error -> " + e.getMessage());
            }
        });

        btnUnregister.setOnClickListener(v -> {
            if (randomServiceJmDNS != null) randomServiceJmDNS.unregisterAllServices();
        });
    }

    private InetAddress getDeviceIpAddress(WifiManager wifi) {
        InetAddress result = null;
        try {
            // default to Android localhost
            result = InetAddress.getByName("10.0.0.2");
            WifiInfo wifiinfo = wifi.getConnectionInfo();
            int intaddr = wifiinfo.getIpAddress();
            byte[] byteaddr = new byte[]{(byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff), (byte) (intaddr >> 24 & 0xff)};
            result = InetAddress.getByAddress(byteaddr);
        } catch (UnknownHostException ex) {
            Log.w(TAG, String.format("getDeviceIpAddress Error: %s", ex.getMessage()));
        }
        return result;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (multiCastLock != null)
            multiCastLock.release();
        unRegisterQuickSilverService();
        unRegisterRandomService();

    }

    private void unRegisterQuickSilverService() {
        if (quickSilverJmDNS != null) {
            try {
                quickSilverJmDNS.unregisterAllServices();
                quickSilverJmDNS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void unRegisterRandomService() {
        if (randomServiceJmDNS != null) {
            try {
                randomServiceJmDNS.unregisterAllServices();
                randomServiceJmDNS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}