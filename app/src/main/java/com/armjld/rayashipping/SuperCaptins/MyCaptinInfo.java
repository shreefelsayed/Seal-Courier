package com.armjld.rayashipping.SuperCaptins;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import com.armjld.rayashipping.Chat.Messages;
import com.armjld.rayashipping.Chat.chatListclass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Ratings.Ratings;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.UserData;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;

public class MyCaptinInfo extends AppCompatActivity {

    public static TabLayout tabs;
    public static UserData user;

    public static ArrayList<Order> placed = new ArrayList<>();
    public static ArrayList<Order> delv = new ArrayList<>();
    ImageView imgPPP, btnBack, btnMessage, btnInfo, btnTrack,btnQR;
    ConstraintLayout linWallet;
    TextView txtUsername, txtWalletMoney;
    RatingBar rbProfile;
    ImageView imgIndec;

    public static int CAMERA_CODE = 80;


    private static void turnSwipes(String st) {
        if (st.equals("ON")) {
            if (myCaptinRecived.mSwipeRefreshLayout != null) {
                myCaptinRecived.mSwipeRefreshLayout.setRefreshing(true);
            }

            if (myCaptinDelv.mSwipeRefreshLayout != null) {
                myCaptinDelv.mSwipeRefreshLayout.setRefreshing(true);
            }

        } else {
            if (myCaptinRecived.mSwipeRefreshLayout != null) {
                myCaptinRecived.mSwipeRefreshLayout.setRefreshing(false);
            }

            if (myCaptinDelv.mSwipeRefreshLayout != null) {
                myCaptinDelv.mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    public static void getRaya() {

        turnSwipes("ON");

        placed.clear();
        delv.clear();

        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");

        mDatabase.orderByChild("uAccepted").equalTo(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Order orderData = ds.getValue(Order.class);
                        assert orderData != null;
                        if (orderData.getStatue().equals("accepted") || orderData.getStatue().equals("recived") || orderData.getStatue().equals("recived2")) {
                            placed.add(orderData);
                        } else if (orderData.getStatue().equals("readyD") || orderData.getStatue().equals("denied") || orderData.getStatue().equals("capDenied")) {
                            delv.add(orderData);
                        }
                    }
                }

                Collections.sort(delv, (lhs, rhs) -> rhs.getDDate().compareTo(lhs.getDDate()));
                Collections.sort(placed, (lhs, rhs) -> rhs.getpDate().compareTo(lhs.getpDate()));

                myCaptinDelv.getOrders();
                myCaptinRecived.getOrders();
                turnSwipes("OFF");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_captin_info);

        if (user == null) finish();

        imgPPP = findViewById(R.id.imgPPP);
        btnBack = findViewById(R.id.btnBack);
        rbProfile = findViewById(R.id.rbProfile);
        txtUsername = findViewById(R.id.txtUsername);
        btnQR = findViewById(R.id.btnQR);

        btnInfo = findViewById(R.id.btnInfo);
        btnMessage = findViewById(R.id.btnMessage);

        txtWalletMoney = findViewById(R.id.txtWalletMoney);
        linWallet = findViewById(R.id.linWallet);
        btnTrack = findViewById(R.id.btnTrack);
        imgIndec = findViewById(R.id.imgIndec);


        btnBack.setOnClickListener(v -> finish());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new myCaptinAdapter(this, getSupportFragmentManager()));
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        btnInfo.setOnClickListener(v -> {
            MyCaptinEdit.user = user;
            startActivity(new Intent(this, MyCaptinEdit.class));
        });

        linWallet.setOnClickListener(v -> {
            CaptinWalletInfo.user = user;
            startActivity(new Intent(this, CaptinWalletInfo.class));
        });

        btnTrack.setOnClickListener(v -> openMaps());

        btnMessage.setOnClickListener(v -> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(UserInFormation.getId(), user.getId(), this);
            Messages.cameFrom = "Profile";
        });

        btnQR.setOnClickListener(v-> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                QRScanOrderForCaptin.user = user;
                startActivity(new Intent(this, QRScanOrderForCaptin.class));
            } else {
                ActivityCompat.requestPermissions((Activity) this, new String[] {Manifest.permission.CAMERA}, CAMERA_CODE);
            }


        });

        checkAvillablity();
        setCaptinsData();
        getRaya();
    }

    private void checkAvillablity() {
        if(user.getTrackId().equals("")) {
            btnTrack.setVisibility(View.GONE);
            imgIndec.setColorFilter(Color.GRAY);
        } else {
            btnTrack.setVisibility(View.VISIBLE);
            imgIndec.setColorFilter(Color.GREEN);
        }
    }

    private void openMaps() {
        if (user.getTrackId().equals("")) {
            return;
        }

        Intent i = new Intent(this, MapCaptinTrack.class);

        MapCaptinTrack.user = user;
        MapCaptinTrack.DEVICE_ID = user.getTrackId();

        ArrayList<Order> bothLists = new ArrayList<>();

        bothLists.addAll(placed);
        bothLists.addAll(delv);

        MapCaptinTrack.filterList = bothLists;

        startActivityForResult(i, 1);
    }

    @SuppressLint("SetTextI18n")
    private void setCaptinsData() {
        // --------- Main Account Date -------------
        Picasso.get().load(Uri.parse(user.getPpURL())).into(imgPPP);
        txtUsername.setText(user.getName());

        setRatings(); // Get Realtime Ratings

        // --------- Wallet Money Number ----------- \\
        txtWalletMoney.setText(user.getWalletmoney() + "");
        int currentMoney = user.getWalletmoney();
        if (currentMoney == 0) {
            txtWalletMoney.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
        } else if (currentMoney > 0) {
            txtWalletMoney.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
        } else {
            txtWalletMoney.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
        }
    }

    private void setRatings() {
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ------------ Ratings ----------- \\
                String one = "0";
                String two = "0";
                String three = "0";
                String four = "0";
                String five = "0";
                if (snapshot.child("rating").child("one").exists()) {
                    one = snapshot.child("rating").child("one").getValue().toString();
                }
                if (snapshot.child("rating").child("two").exists()) {
                    two = snapshot.child("rating").child("two").getValue().toString();
                }
                if (snapshot.child("rating").child("three").exists()) {
                    three = snapshot.child("rating").child("three").getValue().toString();
                }
                if (snapshot.child("rating").child("four").exists()) {
                    four = snapshot.child("rating").child("four").getValue().toString();
                }
                if (snapshot.child("rating").child("five").exists()) {
                    five = snapshot.child("rating").child("five").getValue().toString();
                }
                Ratings _rat = new Ratings();
                rbProfile.setRating(_rat.calculateRating(one, two, three, four, five));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


}