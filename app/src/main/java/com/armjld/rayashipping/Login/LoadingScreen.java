package com.armjld.rayashipping.Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.armjld.rayashipping.Captin.capAvillable;
import com.armjld.rayashipping.Captin.captinRecived;
import com.armjld.rayashipping.Notifications.NotificationFragment;
import com.armjld.rayashipping.SuperVisor.MyCaptins;
import com.armjld.rayashipping.SuperVisor.SuperAvillable;
import com.armjld.rayashipping.SuperVisor.SuperRecived;
import com.armjld.rayashipping.SuperVisor.SuperVisorHome;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.models.Captins;
import com.armjld.rayashipping.models.ChatsData;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.notiData;
import com.armjld.rayashipping.rquests;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.armjld.rayashipping.SuperVisor.SuperVisorHome.captinDelv;

public class LoadingScreen extends AppCompatActivity {

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    @SuppressLint("StaticFieldLeak")
    private static TextView txtLoading;
    rquests _req = new rquests(this);

    @Override
    public void onBackPressed() { }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        if(UserInFormation.getId() == null) {
            finish();
            startActivity(new Intent(this, StartUp.class));
            return;
        }

        txtLoading = findViewById(R.id.txtLoading);

        txtLoading.setText("Getting Account Type ..");
        if(UserInFormation.getAccountType().equals("Supervisor")) {
            getCaptins();
        } else if(UserInFormation.getAccountType().equals("Delivery Worker")) {
            getDeliveryOrders();
        }
    }

    private void getDeliveryOrders() {
        txtLoading.setText("جاري تحضير شحنات الشركاء ..");
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Esh7nly");

        SuperVisorHome.captinAvillable.clear();
        SuperVisorHome.captinAvillable.trimToSize();
        SuperVisorHome.captinDelv.clear();
        SuperVisorHome.captinDelv.trimToSize();

        mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        if(orderData.getStatue().equals("accepted") || orderData.getStatue().equals("recived") || orderData.getStatue().equals("recived2")) {
                            // ------ Add to Avillabe
                            SuperVisorHome.captinAvillable.add(orderData);
                        } else if (orderData.getStatue().equals("readyD")) {
                            // ------ Add to to Delivered
                            captinDelv.add(orderData);
                        }
                    }
                }
                getForRaya();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void getForRaya() {
        txtLoading.setText("جاري تحضير شحنات الشركه ..");
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");

        mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        if(orderData.getStatue().equals("accepted") || orderData.getStatue().equals("recived") || orderData.getStatue().equals("recived2")) {
                            // ------ Add to Avillabe
                            SuperVisorHome.captinAvillable.add(orderData);
                        } else if (orderData.getStatue().equals("readyD")) {
                            // ------ Add to to Delivered
                            captinDelv.add(orderData);
                        }
                    }
                }

                // -------- Set orders in Fragment
                capAvillable.getOrders();
                captinRecived.getOrders();
                getNoti();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // --------------- Get My Captins ------------- \\
    private void getCaptins() {
        txtLoading.setText("مراجعه المندوبين ..");
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("captins").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                SuperVisorHome.mCaptins.clear();
                SuperVisorHome.mCaptins.trimToSize();
                if(snapshot.exists()) {
                    // ------ Get my Captins Data
                    for(DataSnapshot captin : snapshot.getChildren()) {
                        Captins captins = captin.getValue(Captins.class);
                        assert captins != null;
                        SuperVisorHome.mCaptins.add(captins); // Add the Captin data
                        SuperVisorHome.mCaptinsIDS.add(captins.getId()); // Add the id to id List
                    }
                }
                // ------ Send data to captins fragment
                MyCaptins.getCaptins();
                _req.ImportCuurentRequests();
                getOrdersByLatest();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // ------------- Get Avillable Esh7nly Orders ------------ \\
    @SuppressLint("SetTextI18n")
    public void getOrdersByLatest() {
        txtLoading.setText("جاري تحضير الشحنات المتاحة ..");
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Esh7nly");
        mDatabase.orderByChild("statue").equalTo("placed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // --------- Get Avillable Orders Data ------
                SuperVisorHome.mm.clear();
                SuperVisorHome.mm.trimToSize();
                SuperVisorHome.avillableIDS.clear();
                SuperVisorHome.avillableIDS.trimToSize();
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.getChildrenCount() < 5) return;
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        Date orderDate = null;
                        Date myDate = null;
                        try {
                            orderDate = format.parse(Objects.requireNonNull(ds.child("ddate").getValue()).toString());
                            myDate =  format.parse(format.format(Calendar.getInstance().getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert orderDate != null;
                        assert myDate != null;

                        if(orderDate.compareTo(myDate) >= 0 && orderData.getStatue().equals("placed")) {
                            if(orderData.getProvider().equals("Esh7nly") || orderData.getProvider().equals("Raya")) {
                                SuperVisorHome.mm.add(orderData);
                                SuperVisorHome.avillableIDS.add(orderData.getId());
                            }
                        }
                    }

                    // ------- Sort According to Date
                    SuperVisorHome.mm.sort((o1, o2) -> {
                        String one = o1.getDate();
                        String two = o2.getDate();
                        return one.compareTo(two);
                    });
                }

                getRayaOrders();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
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
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.getChildrenCount() < 5) return;
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        if(orderData.getProvider().equals("Raya") && orderData.getStatue().equals("placed")) {
                            SuperVisorHome.mm.add(orderData);
                            SuperVisorHome.avillableIDS.add(orderData.getId());
                        }
                    }
                    // ------- Sort According to Date
                    SuperVisorHome.mm.sort((o1, o2) -> {
                        String one = o1.getDate();
                        String two = o2.getDate();
                        return one.compareTo(two);
                    });
                }

                // -------- Add data to Fragment
                SuperAvillable.getOrders();
                getRayaDelv();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    // ---------- Get To Deliver Raya Orders --------- \\
    private void getRayaDelv() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");
        txtLoading.setText("جاري تحضير الشحنات الشركه ..");

        SuperVisorHome.delvOrders.clear();
        SuperVisorHome.delvOrders.trimToSize();

        mDatabase.orderByChild("dSupervisor").equalTo(UserInFormation.getSup_code()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot allOrders) {
                if(allOrders.exists()) {
                    for(DataSnapshot notDelv : allOrders.getChildren()) {
                        Data orderData = notDelv.getValue(Data.class);
                        if(orderData.getStatue().equals("supD")) {
                            SuperVisorHome.delvOrders.add(orderData);
                        }
                    }
                }

                getEsh7nlyDelv();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // ---------- Get To Deliver Esh7nly Orders --------- \\
    private void getEsh7nlyDelv() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Esh7nly");
        txtLoading.setText("جاري تحضير الشحنات ..");
        mDatabase.orderByChild("dSupervisor").equalTo(UserInFormation.getSup_code()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot allOrders) {
                if(allOrders.exists()) {
                    for(DataSnapshot notDelv : allOrders.getChildren()) {
                        Data orderData = notDelv.getValue(Data.class);
                        if(orderData.getStatue().equals("supD")) {
                            SuperVisorHome.delvOrders.add(orderData);
                        }
                    }
                }

                //set in Fragment -------
                SuperRecived.getOrders();
                getNoti();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    // ---------- Get Notifications ------------- \\
    @SuppressLint("SetTextI18n")
    public void getNoti() {
        txtLoading.setText("جاري تجهيز الإشعارات ..");
        SuperVisorHome.notiList.clear();
        SuperVisorHome.notiList.trimToSize();
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests").child(UserInFormation.getId()).limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int unRead = 0; // Unread Noti Count

                // -------- Check for Notifications
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if(ds.getChildrenCount() < 5) continue;
                        String notiID = ds.getKey();
                        assert notiID != null;
                        notiData notiDB = ds.getValue(notiData.class);
                        assert notiDB != null;

                        SuperVisorHome.notiList.add(notiDB);
                        notiDB.setNotiID(notiID);
                        if(notiDB.getIsRead().equals("false")) {
                            unRead++;
                        }
                    }
                }

                SuperVisorHome.notiCount = unRead; // Set unread Count

                // ------ Add to Fragment
                NotificationFragment.setNoti();

                getMessageCount();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    // --------------- Get Messages ------------ \\
    public void getMessageCount() {
        txtLoading.setText("تجهيز الدردشات ..");
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("chats").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int newCount = 0; // New Messages Count
                SuperVisorHome.mChat.clear();
                SuperVisorHome.mChat.trimToSize();

                // ------- Check for Chat Rooms
                if(snapshot.exists()) {
                    for(DataSnapshot ds:snapshot.getChildren()) {
                        if(ds.child("roomid").exists()) {
                            ChatsData cchatData = ds.getValue(ChatsData.class);
                            String talk = "true";

                            if(ds.child("talk").exists()) {
                                talk = Objects.requireNonNull(ds.child("talk").getValue()).toString();
                            }

                            if(ds.child("timestamp").exists() && talk.equals("true")) {
                                SuperVisorHome.mChat.add(cchatData);
                            }

                            if(ds.child("newMsg").exists()) {
                                if(Objects.requireNonNull(ds.child("newMsg").getValue()).toString().equals("true") && Objects.requireNonNull(ds.child("talk").getValue()).toString().equals("true")) {
                                    newCount++;
                                }
                            }
                        }
                    }
                }

                SuperVisorHome.msgCount = newCount; // Set Count

                whatToDo(); // Move to Next Activity
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void whatToDo() {
        SuperVisorHome.whichFrag = "Home";
        startActivity(new Intent(LoadingScreen.this, SuperVisorHome.class));
    }
}