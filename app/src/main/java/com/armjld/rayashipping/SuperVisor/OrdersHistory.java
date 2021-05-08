package com.armjld.rayashipping.SuperVisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.armjld.rayashipping.Adapters.UpdateAdapter;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.Update;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OrdersHistory extends AppCompatActivity {

    ImageView btnBack,btnAdd;
    public static Order orderData;
    RecyclerView recyclerView;

    ArrayList<Update> arr = new ArrayList<>();
    TextView txtError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_history);

        recyclerView = findViewById(R.id.recyclerView);
        btnBack = findViewById(R.id.btnBack);
        txtError = findViewById(R.id.txtError);
        btnAdd = findViewById(R.id.btnAdd);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تحديثات الشحنه");

        // -- Set the Recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        btnBack.setOnClickListener(v-> finish());

        if(!UserInFormation.getUser().getAccountType().equals("Supervisor")) {
            btnAdd.setVisibility(View.GONE);
        }

        btnAdd.setOnClickListener(v-> {
            OrderUpdates.orderData = orderData;
            startActivity(new Intent(this, OrderUpdates.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUpdates();
    }

    private void getUpdates() {
        getRefrence ref = new getRefrence();
        arr.clear();
        ref.getRef("Raya").child(orderData.getId()).child("updates").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Update update = ds.getValue(Update.class);
                        arr.add(update);
                    }
                }

                UpdateAdapter updateAdapter = new UpdateAdapter(OrdersHistory.this, arr);
                recyclerView.setAdapter(updateAdapter);
                updateNone();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void updateNone() {
        if(arr.size() > 0) {
            txtError.setVisibility(View.GONE);
        } else {
            txtError.setVisibility(View.VISIBLE);
        }
    }
}