package com.deverdie.checknetworkstatus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class Main2Activity extends AppCompatActivity {
    private static final String TAG = "cns, " + MainActivity.class.getSimpleName();

    private boolean isConnected;

    final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            switch (action) {
                case WifiManager.WIFI_STATE_CHANGED_ACTION:
                    int extra = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                    if (extra == WifiManager.WIFI_STATE_ENABLED) {
                        Log.d(TAG, "onReceive: WIFI_STATE_ENABLED");
                    } else if (extra == WifiManager.WIFI_STATE_DISABLED) {
                        Log.d(TAG, "onReceive: WIFI_STATE_DISABLED");
                    } else if (extra == WifiManager.WIFI_STATE_DISABLING) {
                        Log.d(TAG, "onReceive: WIFI_STATE_DISABLING");
                    } else if (extra == WifiManager.WIFI_STATE_ENABLING) {
                        Log.d(TAG, "onReceive: WIFI_STATE_ENABLING");
                    } //...else WIFI_STATE_DISABLED, WIFI_STATE_DISABLING, WIFI_STATE_ENABLING
                    break;
            }
//            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//            NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//
//            boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
//                    mobile != null && mobile.isConnectedOrConnecting();
//            if (isConnected) {
//                Log.d(TAG,"Network Available: "+ "YES");
//            } else {
//                Log.d(TAG,"Network Available: "+ "NO");
//            }
//            if (intent == null || intent.getAction() == null)
//                return;
//            switch (intent.getAction()) {
//                case WifiManager.NETWORK_STATE_CHANGED_ACTION:
//                case WifiManager.WIFI_STATE_CHANGED_ACTION:
//                    ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//                    NetworkInfo wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
//                    NetworkInfo mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
//
//                    Log.d(TAG, "wifi: " + wifi.isConnected());
//                    Log.d(TAG, "mobile: " + mobile.isConnected());
//
//                    boolean isConnected = wifi != null && wifi.isConnectedOrConnecting() ||
//                            mobile != null && mobile.isConnectedOrConnecting();
//                    if (isConnected) {
//                        Log.d(TAG, "Network Available: " + "YES");
//                    } else {
//                        Log.d(TAG, "Network Available: " + "NO");
//                    }
////                    if (!isConnected && isOnline(Main2Activity.this)) {
////                        isConnected = true;
////                        // do stuff when connected
////                        Log.i(TAG,"Connected");
////                    }else{
////                        isConnected = isOnline(Main2Activity.this);
////                        Log.i(TAG,"Disconnected");
////                    }
//                    break;
//            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        isConnected = isOnline(this);
        final IntentFilter filters = new IntentFilter();
        filters.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filters.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(broadcastReceiver, filters);
    }

    public static boolean isOnline(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null
                ? cm.getActiveNetworkInfo()
                : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }
}
