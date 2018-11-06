package com.deverdie.checknetworkstatus;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "cns, " + MainActivity.class.getSimpleName();
    private ConnectivityManager manager;

    private final Handler handler = new Handler();
    private final ConnectivityManager.NetworkCallback networkCallback = new ConnectivityManager.NetworkCallback() {
        @Override
        public void onAvailable(Network network) {
            super.onAvailable(network);
            Log.i(TAG, "connected to " + (manager.isActiveNetworkMetered() ? "LTE" : "WIFI"));

            // we've got a connection, remove callbacks (if we have posted any)
            handler.removeCallbacks(endCall);
        }

        @Override
        public void onLost(Network network) {
            super.onLost(network);
            Log.i(TAG, "losing active connection");

            // Schedule an event to take place in a second
            handler.postDelayed(endCall, 1000);
        }
    };

    private final Runnable endCall = new Runnable() {
        @Override
        public void run() {
            // if execution has reached here - feel free to cancel the call
            // because no connection was established in a second
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();

        manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        manager.registerDefaultNetworkCallback(networkCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        manager.unregisterNetworkCallback(networkCallback);
        handler.removeCallbacks(endCall);
    }

    private void checkPermission() {

        PermissionListener dialogPermissionListener =
                DialogOnDeniedPermissionListener.Builder
                        .withContext(getApplicationContext())
                        .withTitle("Access Network State permission")
                        .withMessage("Access Network State permission is needed to verify network connection.")
                        .withButtonText(android.R.string.ok)
                        .withIcon(R.mipmap.ic_launcher)
                        .build();

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_NETWORK_STATE)
                .withListener(dialogPermissionListener).check();
    }
}
