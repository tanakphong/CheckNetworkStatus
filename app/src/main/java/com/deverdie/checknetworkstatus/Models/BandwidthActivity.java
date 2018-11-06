package com.deverdie.checknetworkstatus.Models;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.deverdie.checknetworkstatus.R;
import com.facebook.network.connectionclass.ConnectionClassManager;
import com.facebook.network.connectionclass.ConnectionQuality;
import com.facebook.network.connectionclass.DeviceBandwidthSampler;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class BandwidthActivity extends AppCompatActivity {

    private static final String TAG = "dlg-" + BandwidthActivity.class.getSimpleName();

    private ConnectionClassManager mConnectionClassManager;
    private DeviceBandwidthSampler mDeviceBandwidthSampler;
    private ConnectionChangedListener mListener;
    private TextView mTextView;
    private View mRunningBar;

    private String mURL = "https://gobrolly.com/wp-content/uploads/2017/04/gobrolly-infographic-complete-sm.png";
    private int mTries = 0;
    private ConnectionQuality mConnectionClass = ConnectionQuality.UNKNOWN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandwidth);

        mConnectionClassManager = ConnectionClassManager.getInstance();
        mDeviceBandwidthSampler = DeviceBandwidthSampler.getInstance();
        findViewById(R.id.test_btn).setOnClickListener(testButtonClicked);
        mTextView = findViewById(R.id.connection_class);
        mTextView.setText(String.valueOf(mConnectionClassManager.getCurrentBandwidthQuality()));
        mRunningBar = findViewById(R.id.runningBar);
        mRunningBar.setVisibility(View.GONE);
        mListener = new ConnectionChangedListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mConnectionClassManager.remove(mListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mConnectionClassManager.register(mListener);
    }

    /**
     * Listener to update the UI upon connectionclass change.
     */
    private class ConnectionChangedListener
            implements ConnectionClassManager.ConnectionClassStateChangeListener {

        @Override
        public void onBandwidthStateChange(ConnectionQuality bandwidthState) {
            mConnectionClass = bandwidthState;
            Log.d(TAG, "onBandwidthStateChange: " + bandwidthState.getDeclaringClass());
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mTextView.setText(mConnectionClass.toString());
//                }
//            });
            runOnUiThread(() -> mTextView.setText(mConnectionClass.toString() + mConnectionClassManager.getDownloadKBitsPerSecond()));
        }
    }

//    private final View.OnClickListener testButtonClicked = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            new DownloadImage().execute(mURL);
//        }
//    };

    private final View.OnClickListener testButtonClicked = v -> new DownloadImage().execute(mURL);

    /**
     * AsyncTask for handling downloading and making calls to the timer.
     */
    private class DownloadImage extends AsyncTask<String, Void, Void> {

        @Override
        protected void onPreExecute() {
            mDeviceBandwidthSampler.startSampling();
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
            mDeviceBandwidthSampler.stopSampling();
            // Retry for up to 10 times until we find a ConnectionClass.
            if (mConnectionClass == ConnectionQuality.UNKNOWN && mTries < 10) {
                mTries++;
                new DownloadImage().execute(mURL);
            }
            if (!mDeviceBandwidthSampler.isSampling()) {
                mRunningBar.setVisibility(View.GONE);
            }
        }
    }
}