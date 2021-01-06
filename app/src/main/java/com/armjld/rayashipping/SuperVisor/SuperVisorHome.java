package com.armjld.rayashipping.SuperVisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.armjld.rayashipping.Captin.capAvillable;
import com.armjld.rayashipping.Captin.captinRecived;
import com.armjld.rayashipping.Chat.ChatFragmet;
import com.armjld.rayashipping.Login.LoginManager;
import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.Notifications.NotificationFragment;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Settings.SettingFragment;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.models.Captins;
import com.armjld.rayashipping.models.ChatsData;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.notiData;
import com.armjld.rayashipping.rquests;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

public class SuperVisorHome extends AppCompatActivity {

    private static DatabaseReference mDatabase;


    // ---------- Super Visor Data ----------- \\
    public static ArrayList<Data> mm = new ArrayList<>(); // Avillable Orders Data
    public static ArrayList<String> avillableIDS = new ArrayList<>(); // Avillable Orders IDS
    public static ArrayList<Captins> mCaptins = new ArrayList<>(); // My Captins Data
    public static ArrayList<String> mCaptinsIDS = new ArrayList<>(); // My Captins IDS
    public static ArrayList<Data> delvOrders = new ArrayList<>(); // To Deliver Orders Data

    // ---------- Captins Data -------------- \\
    public static ArrayList<Data> captinAvillable = new ArrayList<>();
    public static ArrayList<Data> captinDelv = new ArrayList<>();

    // ---------- Common Data ----------- \\
    public static ArrayList<notiData> notiList = new ArrayList<>(); // Notifications Data
    public static ArrayList<ChatsData> mChat = new ArrayList<>(); // Chats Data

    public static int notiCount = 0;
    public static int msgCount = 0;


    boolean doubleBackToExitPressedOnce = false;
    public static String whichFrag = "Home";
    public static BottomNavigationView bottomNavigationView;

    static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    public static BadgeDrawable badge, chatsBadge;

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("Home");
        if (fragment != null && fragment.isVisible()) {
            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                System.exit(0);
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "اضغط مرة اخري للخروج من التطبيق", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        } else {
            whichFrag = "Home";
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new AllOrders(), whichFrag).addToBackStack("Home").commit();
            bottomNavigationView.setSelectedItemId(R.id.home);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supervisor_home);

        if (UserInFormation.getId() == null) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavMethod);

        Menu menu = bottomNavigationView.getMenu();
        MenuItem item = menu.findItem(R.id.noti);
        badge = bottomNavigationView.getOrCreateBadge(item.getItemId());
        chatsBadge = bottomNavigationView.getOrCreateBadge(menu.findItem(R.id.chats).getItemId());


        if (notiCount == 0) {
            bottomNavigationView.removeBadge(R.id.noti);
        } else {
            badge.setNumber(notiCount);
        }

        if (msgCount == 0) {
            bottomNavigationView.removeBadge(R.id.chats);
        } else {
            badge.setNumber(msgCount);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.container, whichFrag(), whichFrag).addToBackStack("Home").commit();

        if(UserInFormation.getAccountType().equals("Delivery Worker")) {
            bottomNavigationView.getMenu().removeItem(R.id.profile);
        }

    }

    private Fragment whichFrag() {
        Fragment frag = null;
        switch (whichFrag) {
            case "Home": {
                frag = new AllOrders();
                bottomNavigationView.setSelectedItemId(R.id.home);
                break;
            }
            case "Profile": {
                frag = new MyCaptins();
                bottomNavigationView.setSelectedItemId(R.id.profile);
                break;
            }

            case "Chats": {
                frag = new ChatFragmet();
                bottomNavigationView.setSelectedItemId(R.id.chats);
                break;
            }

            case "Noti": {
                frag = new NotificationFragment();
                bottomNavigationView.setSelectedItemId(R.id.noti);
                break;
            }

            case "Settings": {
                frag = new SettingFragment();
                bottomNavigationView.setSelectedItemId(R.id.settings);
                break;
            }

        }
        return frag;
    }

    @SuppressLint("NonConstantResourceId")
    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod = item -> {
        Fragment fragment = null;
        String fragTag = "";
        switch (item.getItemId()) {
            case R.id.home: {
                fragment = new AllOrders();
                fragTag = "Home";
                break;
            }

            case R.id.settings: {
                fragment = new SettingFragment();
                fragTag = "Settings";
                break;
            }

            case R.id.profile: {
                fragment = new MyCaptins();
                fragTag = "Profile";
                break;
            }

            case R.id.chats: {
                fragment = new ChatFragmet();
                fragTag = "Chats";
                break;
            }

            case R.id.noti: {
                fragment = new NotificationFragment();
                fragTag = "Noti";
                break;
            }
        }
        assert fragment != null;
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment, fragTag).addToBackStack("Home").commit();
        return true;
    };

    public static void getMessageCount() {
        if (chatsBadge == null || bottomNavigationView == null) {
            return;
        }
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    msgCount = 0;
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.child("newMsg").exists()) {
                            if (Objects.requireNonNull(ds.child("newMsg").getValue()).toString().equals("true") && Objects.requireNonNull(ds.child("talk").getValue()).toString().equals("true")) {
                                msgCount++;
                            }
                        }
                    }

                    if (msgCount > 0) {
                        chatsBadge.setNumber(msgCount);
                    } else {
                        bottomNavigationView.removeBadge(R.id.chats);
                    }
                } else {
                    bottomNavigationView.removeBadge(R.id.chats);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getOrdersByLatest() {
        mDatabase.orderByChild("statue").equalTo("placed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mm.clear();
                mm.trimToSize();
                avillableIDS.clear();
                avillableIDS.trimToSize();

                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getChildrenCount() < 5) return;
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        Date orderDate = null;
                        Date myDate = null;
                        try {
                            orderDate = format.parse(Objects.requireNonNull(ds.child("ddate").getValue()).toString());
                            myDate = format.parse(sdf.format(Calendar.getInstance().getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert orderDate != null;
                        assert myDate != null;

                        if (orderDate.compareTo(myDate) >= 0 && orderData.getStatue().equals("placed")) {
                            if(orderData.getProvider().equals("Esh7nly") || orderData.getProvider().equals("Raya")) {
                                mm.add(orderData);
                                avillableIDS.add(orderData.getId());
                                Timber.i(orderData.getId());
                            }
                        }

                        mm.sort((o1, o2) -> {
                            String one = o1.getDate();
                            String two = o2.getDate();
                            return one.compareTo(two);
                        });
                    }
                }

                getRayaOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void getRayaOrders() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");
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
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    public static void getCaptins(Context mContext) {
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
                rquests _req = new rquests(mContext);
                _req.ImportCuurentRequests();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // ---------- Get To Deliver Raya Orders --------- \\
    public static void getRayaDelv() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");
        delvOrders.clear();
        delvOrders.trimToSize();
        mDatabase.orderByChild("dSupervisor").equalTo(UserInFormation.getSup_code()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot allOrders) {
                if(allOrders.exists()) {
                    for(DataSnapshot notDelv : allOrders.getChildren()) {
                        Data orderData = notDelv.getValue(Data.class);
                        if(orderData.getStatue().equals("supD")) {
                            delvOrders.add(orderData);
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
    public static void getEsh7nlyDelv() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Esh7nly");

        mDatabase.orderByChild("dSupervisor").equalTo(UserInFormation.getSup_code()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot allOrders) {
                if(allOrders.exists()) {
                    for(DataSnapshot notDelv : allOrders.getChildren()) {
                        Data orderData = notDelv.getValue(Data.class);
                        if(orderData.getStatue().equals("supD")) {
                            delvOrders.add(orderData);
                        }
                    }
                }

                //set in Fragment -------
                SuperRecived.getOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public static void getNoti() {
        if (badge == null || bottomNavigationView == null) {
            return;
        }

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests").child(UserInFormation.getId()).limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notiList.clear();
                notiList.trimToSize();
                notiCount = 0;
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        if (ds.getChildrenCount() < 5) return;
                        String notiID = ds.getKey();
                        assert notiID != null;
                        notiData notiDB = ds.getValue(notiData.class);
                        assert notiDB != null;

                        notiList.add(notiDB);
                        notiDB.setNotiID(notiID);
                        if (notiDB.getIsRead().equals("false")) {
                            notiCount++;
                        }
                        Timber.i("Added This : %s", notiID);

                    }
                    if (notiCount > 0) {
                        badge.setNumber(notiCount);
                    } else {
                        bottomNavigationView.removeBadge(R.id.noti);
                    }
                    NotificationFragment.setNoti();

                } else {
                    bottomNavigationView.removeBadge(R.id.noti);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public static void getMessage() {
        mChat.clear();
        mChat.trimToSize();
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("chats").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    if (ds.child("roomid").exists()) {
                        ChatsData cchatData = ds.getValue(ChatsData.class);
                        String talk = "true";

                        if (ds.child("talk").exists()) {
                            talk = Objects.requireNonNull(ds.child("talk").getValue()).toString();
                        }

                        if (ds.child("timestamp").exists() && talk.equals("true")) {
                            mChat.add(cchatData);
                            //ChatFragmet.getMessages();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public static void getDeliveryOrders() {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Esh7nly");

        captinAvillable.clear();
        captinAvillable.trimToSize();

        captinDelv.clear();
        captinDelv.trimToSize();

        mDatabase.orderByChild("uAccepted").equalTo(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        if(orderData.getStatue().equals("accepted") || orderData.getStatue().equals("recived") || orderData.getStatue().equals("recived2")) {
                            // ------ Add to Avillabe
                            captinAvillable.add(orderData);
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

    public static void getForRaya() {
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
                            captinAvillable.add(orderData);
                        } else if (orderData.getStatue().equals("readyD")) {
                            captinDelv.add(orderData);
                        }
                    }
                }

                // -------- Set orders in Fragment
                capAvillable.getOrders();
                captinRecived.getOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    public void openWebURL(String inURL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
        startActivity(browse);
    }

}