package com.armjld.rayashipping.SuperVisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armjld.rayashipping.Adapters.CaptinsAdapter;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.models.Captins;

import java.util.ArrayList;

public class AsignOrder extends AppCompatActivity {

    public static String orderId;
    public static String orderOwner;
    public static String type;
    public static String dName;
    public static int position;
    public static String orderProvider;
    private LinearLayout EmptyPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asign_order);

        orderId = getIntent().getStringExtra("orderId");
        orderOwner = getIntent().getStringExtra("orderOwner");
        type = getIntent().getStringExtra("type");
        dName = getIntent().getStringExtra("dName");
        position = getIntent().getIntExtra("position", 0);
        orderProvider = getIntent().getStringExtra("provider");

        EmptyPanel = findViewById(R.id.EmptyPanel);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v-> finish());

        EmptyPanel.setVisibility(View.GONE);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اختار مندوب");

        //Recycler
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        CaptinsAdapter captinsAdapter = new CaptinsAdapter(this, SuperVisorHome.mCaptins, "asign");
        recyclerView.setAdapter(captinsAdapter);
        updateNone(SuperVisorHome.mCaptins.size());
    }

    private void updateNone(int size) {
        if(size > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }
}