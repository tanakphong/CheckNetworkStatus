package com.deverdie.checknetworkstatus;

import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class Main3Activity extends AppCompatActivity {

    private static final String TAG = "cns, " + Main3Activity.class.getSimpleName();
    private TextView tvConnectivityStatus;
    private TextView tvInternetStatus;
    private Disposable networkDisposable;
    private Disposable internetDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        tvConnectivityStatus = findViewById(R.id.connectivity_status);
        tvInternetStatus = findViewById(R.id.internet_status);
    }

    @Override
    protected void onResume() {
        super.onResume();

        networkDisposable = ReactiveNetwork.observeNetworkConnectivity(getApplicationContext())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(connectivity -> {
                    Log.d(TAG, connectivity.toString());
                    final NetworkInfo.State state = connectivity.state();
                    final String name = connectivity.typeName();
                    tvConnectivityStatus.setText(String.format("state: %s, typeName: %s", state, name));
                });

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(isConnected -> tvInternetStatus.setText(isConnected.toString()));
    }

    @Override
    protected void onPause() {
        super.onPause();
        safelyDispose(networkDisposable, internetDisposable);
    }

    private void safelyDispose(Disposable... disposables) {
        for (Disposable subscription : disposables) {
            if (subscription != null && !subscription.isDisposed()) {
                subscription.dispose();
            }
        }
    }
}
