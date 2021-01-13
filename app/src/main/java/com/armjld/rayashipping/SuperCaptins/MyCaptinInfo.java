package com.armjld.rayashipping.SuperCaptins;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.armjld.rayashipping.CaptinWalletInfo;
import com.armjld.rayashipping.Chat.Messages;
import com.armjld.rayashipping.Chat.chatListclass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Ratings.Ratings;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.userData;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyCaptinInfo extends AppCompatActivity {

    public static TabLayout tabs;
    public static userData user;
    public static ArrayList<Data> placed = new ArrayList<>();
    public static ArrayList<Data> delv = new ArrayList<>();
    ImageView imgPPP, btnBack, btnMessage, btnInfo, btnTrack;
    ConstraintLayout linWallet;
    TextView txtUsername, txtWalletMoney;
    RatingBar rbProfile;

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
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        if (orderData.getStatue().equals("accepted") || orderData.getStatue().equals("recived") || orderData.getStatue().equals("recived2")) {
                            placed.add(orderData);
                        } else if (orderData.getStatue().equals("readyD")) {
                            delv.add(orderData);
                        }
                    }
                }

                // ------- Sort According to Date
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    placed.sort((o1, o2) -> {
                        String one = o1.getpDate();
                        String two = o2.getpDate();
                        return two.compareTo(one);
                    });
                }

                // ------- Sort According to Date
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    delv.sort((o1, o2) -> {
                        String one = o1.getDDate();
                        String two = o2.getDDate();
                        return two.compareTo(one);
                    });
                }

                myCaptinDelv.getOrders();
                myCaptinRecived.getOrders();
                turnSwipes("OFF");
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
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

        btnInfo = findViewById(R.id.btnInfo);
        btnMessage = findViewById(R.id.btnMessage);

        txtWalletMoney = findViewById(R.id.txtWalletMoney);
        linWallet = findViewById(R.id.linWallet);
        btnTrack = findViewById(R.id.btnTrack);


        btnBack.setOnClickListener(v -> finish());

        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(new myCaptinAdapter(this, getSupportFragmentManager()));
        tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


        btnInfo.setOnClickListener(v -> {
        });

        linWallet.setOnClickListener(v -> {
            CaptinWalletInfo.user = user;
            startActivity(new Intent(this, CaptinWalletInfo.class));
        });

        btnTrack.setOnClickListener(v -> {
            if (user.getTrackId().equals("")) {
                return;
            }
            MapCaptinTrack.user = user;
            MapCaptinTrack.DEVICE_ID = user.getTrackId();
            startActivity(new Intent(this, MapCaptinTrack.class));
        });

        btnMessage.setOnClickListener(v -> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(UserInFormation.getId(), user.getId(), this);
            Messages.cameFrom = "Profile";
        });

        setCaptinsData();
        getRaya();
    }

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