package com.armjld.rayashipping.SuperVisor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Adapters.MyAdapter;
import com.armjld.rayashipping.Login.LoginManager;
import com.armjld.rayashipping.Login.Login_Options;
import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.models.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import timber.log.Timber;

public class OrdersBySameUser extends AppCompatActivity {

    //Recycler view
    private RecyclerView recyclerView;
    private LinearLayout EmptyPanel;
    private static ArrayList<Data> mm;
    String userID;
    String dName = "";

    @Override
    protected void onResume() {
        super.onResume();
        if(!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login_Options.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        setContentView(R.layout.activity_orders_by_same_user);
        userID = getIntent().getStringExtra("userid");
        dName = getIntent().getStringExtra("name");
        mm = new ArrayList<>();
        Toolbar toolbar = findViewById(R.id.toolbar_home);
        EmptyPanel = findViewById(R.id.EmptyPanel);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> finish());

        tbTitle.setText("شحنات / " + dName);


        // ToolBar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        //Database
        // import firebase
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        mDatabase.keepSynced(true);

        //Recycler
        recyclerView=findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);

        getOrdersByLatest(userID);
    }

    private void getOrdersByLatest(String uIDD) {
        Timber.i("Getting Orders");
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        assert uIDD != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mm = (ArrayList<Data>) SuperVisorHome.mm.stream().filter(x -> x.getuId().equals(uIDD)).collect(Collectors.toList());
        } else {
            for (int i = 0; i < SuperVisorHome.mm.size(); i++) {
                if (SuperVisorHome.mm.get(i).getuId().equals(uIDD)) {
                    mm.add(SuperVisorHome.mm.get(i));
                }
            }
        }

        MyAdapter orderAdapter = new MyAdapter(OrdersBySameUser.this, mm);
        recyclerView.setAdapter(orderAdapter);
        updateNone(mm.size());
    }

    private void updateNone(int listSize) {
        Timber.i("List size is now : %s", listSize);
        if(listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }

}