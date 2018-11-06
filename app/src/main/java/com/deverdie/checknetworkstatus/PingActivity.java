package com.deverdie.checknetworkstatus;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.stealthcopter.networktools.Ping;
import com.stealthcopter.networktools.ping.PingResult;
import com.stealthcopter.networktools.ping.PingStats;

public class PingActivity extends AppCompatActivity {

    private static final String TAG = "dlg-" + PingActivity.class.getSimpleName();

    TextView result;

    Handler handler;
    Runnable runnable;
    long time = 4000L;
    //    private Pinger pinger;
    private static final String host = "www.google.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);

//        try {
//            PingResult pingResult = Ping.onAddress(host).setTimeOutMillis(1000).doPing();
//            Log.d(TAG, "time: " + String.format("%f ms", pingResult.timeTaken));
//            result.setText(String.format("%f ms", pingResult.timeTaken));
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }

        result = findViewById(R.id.tv_result);

        handler = new Handler();

        runnable = () -> {
//                pinger.pingUntilSucceeded(host, time);
            // Asynchronously
            Ping.onAddress(host).setTimeOutMillis(1000).setTimes(1).doPing(new Ping.PingListener() {
                @Override
                public void onResult(PingResult pingResult) {
                    Log.d(TAG, "onResult: pingResult.timeTaken= " + pingResult.timeTaken);
                    runOnUiThread(() -> {
                        result.setText(String.format("%d ms", Math.round(pingResult.timeTaken)));
                    });
//                    runOnUiThread(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            result.setText(String.format("%d ms", Math.round(pingResult.timeTaken)));
//                        }
//                    });
                }

                @Override
                public void onFinished(PingStats pingStats) {
                    Log.d(TAG, "onFinished: " + pingStats);
                    handler.postDelayed(runnable, time);
                }

                @Override
                public void onError(Exception e) {
                    Log.d(TAG, "onError: " + e.toString());
                    handler.postDelayed(runnable, time);
                }
            });
        };

//        pinger = new Pinger();
//        pinger.setOnPingListener(new Pinger.OnPingListener() {
//            @Override
//            public void onPingSuccess() {
//                Log.d(TAG, "onPingSuccess: ");
//                handler.postDelayed(runnable, time);
//            }
//
//            @Override
//            public void onPingFailure() {
//                Log.e(TAG, "onPingFailure: ");
//            }
//
//            @Override
//            public void onPingFinish() {
//                Log.d(TAG, "onPingFinish: ");
//            }
//        });
//        pinger.pingUntilSucceeded("www.google.com", 5000);

    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(runnable, time);
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable);
    }
}
