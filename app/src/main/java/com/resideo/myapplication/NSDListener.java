package com.resideo.myapplication;

import android.util.Log;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceListener;

import static com.resideo.myapplication.MainActivity.TAG;

public class NSDListener implements ServiceListener {


    @Override
    public void serviceAdded(ServiceEvent event) {
        Log.d(TAG, " :ServiceAdded " + event.getInfo());
    }

    @Override
    public void serviceRemoved(ServiceEvent event) {
        Log.d(TAG, " :ServiceRemoved " + event.getInfo());
    }

    @Override
    public void serviceResolved(ServiceEvent event) {
        if (event.getInfo().getInet4Address() != null)
            Log.d(TAG, "ServiceName: " + event.getName() + " -- IpAddress >> " + event.getInfo().getInet4Address());
    }
}
