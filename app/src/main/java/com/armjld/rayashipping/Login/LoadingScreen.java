package com.armjld.rayashipping.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.armjld.rayashipping.Captin.capAvillable;
import com.armjld.rayashipping.Captin.captinRecived;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.Notifications.NotificationFragment;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperCaptins.MyCaptins;
import com.armjld.rayashipping.SuperVisor.SuperAvillable;
import com.armjld.rayashipping.SuperVisor.SuperRecived;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.Models.ChatsData;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.notiData;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Objects;

public class LoadingScreen extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    private static TextView txtLoading;

    @Override
    public void onBackPressed() { }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        if (UserInFormation.getId() == null) {
            finish();
            startActivity(new Intent(this, StartUp.class));
            return;
        }

        txtLoading = findViewById(R.id.txtLoading);

        txtLoading.setText("Getting Account Type ..");
        if (UserInFormation.getAccountType().equals("Supervisor")) {
            getCaptins();
        } else if (UserInFormation.getAccountType().equals("Delivery Worker")) {
            getForRaya();
        }
    }

    // --- Captins
    private void getForRaya() {
        txtLoading.setText("جاري تحضير شحنات الشركه ..");
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");

        Home.captinAvillable.clear();
        Home.captinAvillable.trimToSize();
        Home.captinDelv.clear();
        Home.captinDelv.trimToSize();

        mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Order orderData = ds.getValue(Order.class);
                        assert orderData != null;
                        if (orderData.getStatue().equals("accepted") || orderData.getStatue().equals("recived") || orderData.getStatue().equals("recived2")) {
                            // ------ Add to Avillabe
                            Home.captinAvillable.add(orderData);
                        } else if (orderData.getStatue().equals("readyD") || orderData.getStatue().equals("denied") || orderData.getStatue().equals("capDenied")) {
                            // ------ Add to to Delivered
                            Home.captinDelv.add(orderData);
                        }
                    }
                }

                // ------- Sort According to Date
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Home.captinAvillable.sort((o1, o2) -> {
                        String one = o1.getpDate();
                        String two = o2.getpDate();
                        return two.compareTo(one);
                    });
                }

                // ------- Sort According to Date
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    Home.captinDelv.sort((o1, o2) -> {
                        String one = o1.getDDate();
                        String two = o2.getDDate();
                        return two.compareTo(one);
                    });
                }

                // -------- Set orders in Fragment
                capAvillable.getOrders();
                captinRecived.getOrders();
                getNoti();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // --------------- Get My Captins ------------- \\
    private void getCaptins() {
        txtLoading.setText("مراجعه المندوبين ..");
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").orderByChild("mySuper").equalTo(UserInFormation.getSup_code()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Home.mCaptins.clear();
                Home.mCaptins.trimToSize();
                if (snapshot.exists()) {
                    // ------ Get my Captins Data
                    for (DataSnapshot captin : snapshot.getChildren()) {
                        UserData user = captin.getValue(UserData.class);
                        assert user != null;
                        Home.mCaptinsIDS.add(user.getId()); // Add the id to id List
                        Home.mCaptins.add(user); // Add the Captin data
                    }
                }

                // ------ Send data to captins fragment
                MyCaptins.getCaptins();
                getRayaOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // -------------- Get Avillable Company Orders ------------ \\
    private void getRayaOrders() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");
        txtLoading.setText("جاري تحضير الشحنات الشركه ..");
        mDatabase.orderByChild("statue").equalTo("placed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // --------- Get Avillable Orders Data ------
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getChildrenCount() < 5) return;
                        Order orderData = ds.getValue(Order.class);
                        assert orderData != null;

                        Home.mm.add(orderData);
                        Home.avillableIDS.add(orderData.getId());
                    }

                    // ------- Sort According to Date
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Home.mm.sort((o1, o2) -> {
                            String one = o1.getpDate();
                            String two = o2.getpDate();
                            return two.compareTo(one);
                        });
                    }
                }

                // -------- Add data to Fragment
                SuperAvillable.getOrders();
                getRayaDelv();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // ---------- Get To Deliver Raya Orders --------- \\
    private void getRayaDelv() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");
        txtLoading.setText("جاري تحضير الشحنات الشركه ..");

        Home.delvOrders.clear();
        Home.delvOrders.trimToSize();

        mDatabase.orderByChild("dSupervisor").equalTo(UserInFormation.getSup_code()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot allOrders) {
                if (allOrders.exists()) {
                    for (DataSnapshot notDelv : allOrders.getChildren()) {
                        Order orderData = notDelv.getValue(Order.class);
                        assert orderData != null;
                        if (orderData.getStatue().equals("supD") || orderData.getStatue().equals("supDenied")) {
                            Home.delvOrders.add(orderData);
                        }
                    }
                }

                //set in Fragment -------
                SuperRecived.getOrders();
                getNoti();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    // ---------- Get Notifications ------------- \\
    @SuppressLint("SetTextI18n")
    public void getNoti() {
        txtLoading.setText("جاري تجهيز الإشعارات ..");
        Home.notiList.clear();
        Home.notiList.trimToSize();
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests").child(UserInFormation.getId()).limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unRead = 0; // Unread Noti Count

                // -------- Check for Notifications
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getChildrenCount() < 5) continue;
                        String notiID = ds.getKey();
                        assert notiID != null;
                        notiData notiDB = ds.getValue(notiData.class);
                        assert notiDB != null;

                        Home.notiList.add(notiDB);
                        notiDB.setNotiID(notiID);
                        if (notiDB.getIsRead().equals("false")) {
                            unRead++;
                        }
                    }
                }

                Home.notiCount = unRead; // Set unread Count

                // ------ Add to Fragment
                NotificationFragment.setNoti();

                getMessageCount();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    // --------------- Get Messages ------------ \\
    public void getMessageCount() {
        txtLoading.setText("تجهيز الدردشات ..");
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("chats").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newCount = 0; // New Messages Count
                Home.mChat.clear();
                Home.mChat.trimToSize();

                // ------- Check for Chat Rooms
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("roomid").exists()) {
                            ChatsData cchatData = ds.getValue(ChatsData.class);
                            String talk = "true";

                            if (ds.child("talk").exists()) {
                                talk = Objects.requireNonNull(ds.child("talk").getValue()).toString();
                            }

                            if (ds.child("timestamp").exists() && talk.equals("true")) {
                                Home.mChat.add(cchatData);
                            }

                            if (ds.child("newMsg").exists()) {
                                if (Objects.requireNonNull(ds.child("newMsg").getValue()).toString().equals("true") && Objects.requireNonNull(ds.child("talk").getValue()).toString().equals("true")) {
                                    newCount++;
                                }
                            }
                        }
                    }
                }

                Home.msgCount = newCount; // Set Count

                whatToDo(); // Move to Next Activity
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void whatToDo() {
        Home.whichFrag = "Home";
        startActivity(new Intent(LoadingScreen.this, Home.class));
    }
}