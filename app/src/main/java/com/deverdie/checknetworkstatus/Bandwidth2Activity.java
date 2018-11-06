package com.deverdie.checknetworkstatus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Bandwidth2Activity extends AppCompatActivity {

    private static final String TAG = "dlg-" + Bandwidth2Activity.class.getSimpleName();

    private Handler mHandler = new Handler();
    private long mStartRX = 0;
    private long mStartTX = 0;
    private ProgressBar mRunningBar;
    private String mURL = "https://gobrolly.com/wp-content/uploads/2017/04/gobrolly-infographic-complete-sm.png";

    long startTime;
    long endTime;
    long fileSize;
    OkHttpClient client = new OkHttpClient();

    // bandwidth in kbps
    private int POOR_BANDWIDTH = 150;
    private int AVERAGE_BANDWIDTH = 550;
    private int GOOD_BANDWIDTH = 2000;

    long lastTotalRxBytes = 0;
    long lastTimeStamp = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandwidth2);

        mStartRX = TrafficStats.getTotalRxBytes();
        mStartTX = TrafficStats.getTotalTxBytes();

        findViewById(R.id.test_btn).setOnClickListener(testButtonClicked);
        mRunningBar = findViewById(R.id.runningBar);
        mRunningBar.setVisibility(View.GONE);

        if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Uh Oh!");
            alert.setMessage("Your device does not support traffic stat monitoring.");
            alert.show();
        } else {
            mHandler.postDelayed(mRunnable, 1000);
        }

        Request request = new Request.Builder()
                .url(mURL)
                .build();

        startTime = System.currentTimeMillis();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                Headers responseHeaders = response.headers();
                for (int i = 0, size = responseHeaders.size(); i < size; i++) {
                    Log.d(TAG, responseHeaders.name(i) + ": " + responseHeaders.value(i));
                }

                InputStream input = response.body().byteStream();

                try {
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];

                    while (input.read(buffer) != -1) {
                        bos.write(buffer);
                    }
                    byte[] docBuffer = bos.toByteArray();
                    fileSize = bos.size();

                } finally {
                    input.close();
                }

                endTime = System.currentTimeMillis();


                // calculate how long it took by subtracting endtime from starttime

                double timeTakenMills = Math.floor(endTime - startTime);  // time taken in milliseconds
                double timeTakenSecs = timeTakenMills / 1000;  // divide by 1000 to get time in seconds
                final int kilobytePerSec = (int) Math.round(1024 / 1);//timeTakenInSecs

                if (kilobytePerSec <= POOR_BANDWIDTH) {
                    // slow connection
                }

                // get the download speed by dividing the file size by time taken to download
                double speed = fileSize / timeTakenMills;

                Log.d(TAG, "Time taken in secs: " + timeTakenSecs);
                Log.d(TAG, "kilobyte per sec: " + kilobytePerSec);
                Log.d(TAG, "Download Speed: " + speed);
                Log.d(TAG, "File size: " + fileSize);


            }
        });
    }

    private final Runnable mRunnable = new Runnable() {
        @SuppressLint("SetTextI18n")
        public void run() {
            TextView RX = findViewById(R.id.RX);
            TextView TX = findViewById(R.id.TX);
            TextView BW = findViewById(R.id.BW);
            long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
            RX.setText(Long.toString(rxBytes));
            long txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
            TX.setText(Long.toString(txBytes));
            String bw = getNetSpeed(getApplicationContext());
            BW.setText(bw);
            mHandler.postDelayed(mRunnable, 1000);
        }
    };

    public String getNetSpeed(Context context) {
        long nowTotalRxBytes = TrafficStats.getUidRxBytes(context.getApplicationInfo().uid) ==
                TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);

        long nowTimeStamp = System.currentTimeMillis();

        float speed = ((nowTotalRxBytes - lastTotalRxBytes) * 1000 / (float) (nowTimeStamp - lastTimeStamp));

        lastTimeStamp = nowTimeStamp;
        lastTotalRxBytes = nowTotalRxBytes;

        return  String.format("%.1f kb/s", speed);
    }


    private final View.OnClickListener testButtonClicked = v -> new DownloadImage().execute(mURL);

    /**
     * AsyncTask for handling downloading and making calls to the timer.
     */
    private class DownloadImage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            mRunningBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... url) {
            String imageURL = url[0];
            try {
                // Open a stream to download the image from our URL.
                URLConnection connection = new URL(imageURL).openConnection();
                connection.setUseCaches(false);
                connection.connect();
                InputStream input = connection.getInputStream();
                try {
                    byte[] buffer = new byte[1024];

                    // Do some busy waiting while the stream is open.
                    while (input.read(buffer) != -1) {
                    }
                } finally {
                    input.close();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error while downloading image.");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            mRunningBar.setVisibility(View.GONE);
        }
    }
}
