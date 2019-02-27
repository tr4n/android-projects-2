package com.example.buinam.mapchatdemo.design;

import android.app.Application;

/**
 * Created by buinam on 9/24/16.
 */
// Check Internet
public class InternetCheck extends Application {

    private static InternetCheck mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static synchronized InternetCheck getInstance() {
        return mInstance;
    }

    public void setConnectivityListener(ConnectivityReceiver.ConnectivityReceiverListener listener) {
        ConnectivityReceiver.connectivityReceiverListener = listener;
    }
}
