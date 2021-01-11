package com.armjld.rayashipping.SuperVisor;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Adapters.CaptinsAdapter;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.models.Data;

import java.util.ArrayList;

public class AsignOrder extends AppCompatActivity {

    public static String type;
    public static int position;
    public static ArrayList<Data> assignToCaptin = new ArrayList<>();
    private LinearLayout EmptyPanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asign_order);

        EmptyPanel = findViewById(R.id.EmptyPanel);
        RecyclerView recyclerView = findViewById(R.id.recycler);
        ImageView btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        EmptyPanel.setVisibility(View.GONE);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("اختار مندوب");

        //Recycler
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        CaptinsAdapter captinsAdapter = new CaptinsAdapter(this, Home.mCaptins, "asign");
        recyclerView.setAdapter(captinsAdapter);
        updateNone(Home.mCaptins.size());
    }

    private void updateNone(int size) {
        if (size > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }
}