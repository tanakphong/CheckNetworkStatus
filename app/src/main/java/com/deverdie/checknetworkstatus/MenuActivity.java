package com.deverdie.checknetworkstatus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.deverdie.checknetworkstatus.Adapters.MenuAdapter;
import com.deverdie.checknetworkstatus.Models.BandwidthActivity;
import com.deverdie.checknetworkstatus.Models.MenuModel;

import java.util.ArrayList;
import java.util.List;

public class MenuActivity extends AppCompatActivity implements MenuAdapter.ItemClickListener {
    private static final String TAG = "dlg-" + MenuActivity.class.getSimpleName();
    private MenuAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        List<MenuModel> menu = new ArrayList<>();
        menu.add(new MenuModel("Network", ">api24", "Check Network Status", "ใช้งานได้", MainActivity.class.getName()));
        menu.add(new MenuModel("Network", ">api19", "Check Network Status", "ใช้งานได้ ยังไม่สมบูรณ์", Main2Activity.class.getName()));
        menu.add(new MenuModel("Network", "use reactivenetwork", "Check Network Status", "ใช้งานได้", Main3Activity.class.getName()));
        menu.add(new MenuModel("Network", "use Pinger", "Ping", "ใช้งานได้", PingActivity.class.getName()));
        menu.add(new MenuModel("Network", "use network-connection-class", "Bandwidth", "ใช้งานได้", BandwidthActivity.class.getName()));
        menu.add(new MenuModel("Network", "TrafficStats", "Bandwidth", "ใช้งานได้", Bandwidth2Activity.class.getName()));
        menu.add(new MenuModel("RxJava", "use rxjava-extras", "read inputstream", "ทดลองก่อน", RxJavaExtrasActivity.class.getName()));

        RecyclerView recyclerView = findViewById(R.id.recycler);

        adapter = new MenuAdapter(getApplicationContext(), menu, MenuActivity.this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(getApplicationContext(), adapter.getItem(position).getLinkTo(), Toast.LENGTH_SHORT).show();
        try {
            startActivity(new Intent(getApplicationContext(), Class.forName(adapter.getItem(position).getLinkTo())));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
