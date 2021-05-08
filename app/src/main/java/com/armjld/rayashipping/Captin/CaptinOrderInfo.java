package com.armjld.rayashipping.Captin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.armjld.rayashipping.CopyingData;
import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.MapsActivity;
import com.armjld.rayashipping.Models.UserData;
import com.armjld.rayashipping.OrderStatue;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Whatsapp;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class CaptinOrderInfo extends AppCompatActivity {

    public static Order orderData;
    ImageView supPP, btnOrderMap;
    TextView orderid;
    TextView ddUsername;
    TextView txtCustomerName, txtDDate, txtDAddress,txtGGet, txtPostDate2, txtPack, txtPDate, txtPAddress, txtWeight, txtCash, txtOrder;
    DatabaseReference nDatabase, rDatabase, uDatabase;
    ImageView btnClose;
    String hID = "";
    FloatingActionButton btnCall, btnCallSupplier;
    TextView txtNotes;
    caculateTime _cacu = new caculateTime();
    LinearLayout constClient,linSupplier, linPackage,linSender;

    ImageView btnWhatsSender, btnWhatsReciver;

    Whatsapp whatsapp = new Whatsapp();
    CopyingData copyingData = new CopyingData(this);
    OrdersClass ordersClass = new OrdersClass(this);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captin_order_info);
        hID = getIntent().getStringExtra("orderid");


        if(orderData == null && hID.equals("")) {
            finish();
            startActivity(new Intent(this, StartUp.class));
            return;
        }

        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");


        TextView tbTitle2 = findViewById(R.id.toolbar_title);
        tbTitle2.setText("بيانات الشحنه");

        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtDDate = findViewById(R.id.txtDDate);
        txtDAddress = findViewById(R.id.txtDAddress);
        txtPostDate2 = findViewById(R.id.txtPostDate2);
        txtPack = findViewById(R.id.txtPack);
        txtPDate = findViewById(R.id.txtPDate);
        txtPAddress = findViewById(R.id.txtPAddress);
        txtWeight = findViewById(R.id.txtWeight);
        txtCash = findViewById(R.id.txtCash);
        btnClose = findViewById(R.id.btnClose);
        linSender = findViewById(R.id.linSender);
        supPP = findViewById(R.id.supPP);
        ddUsername = findViewById(R.id.ddUsername);
        btnOrderMap = findViewById(R.id.btnOrderMap);
        txtGGet = findViewById(R.id.txtGGet);
        btnCall = findViewById(R.id.btnCall);
        orderid = findViewById(R.id.orderid);
        txtOrder = findViewById(R.id.txtOrder);
        linSupplier = findViewById(R.id.linSupplier);
        linPackage = findViewById(R.id.linPackage);
        constClient = findViewById(R.id.constClient);
        btnWhatsSender = findViewById(R.id.btnWhatsSender);
        btnWhatsReciver = findViewById(R.id.btnWhatsReciver);
        txtNotes = findViewById(R.id.txtNotes);
        btnCallSupplier = findViewById(R.id.btnCallSupplier);

        btnClose.setOnClickListener(v -> finish());

        btnOrderMap.setVisibility(View.GONE);

        btnOrderMap.setOnClickListener(v -> {
            if(orderData.getLat().equals("") || orderData.get_long().equals("")) return;

            MapsActivity.filterList.clear();
            MapsActivity.filterList.add(orderData);
            startActivity(new Intent(this, MapsActivity.class));
        });

        btnWhatsReciver.setOnClickListener(v-> whatsapp.openWhats(orderData.getDPhone(), copyingData.getOrderDataToReciver(orderData), this));

        btnWhatsSender.setOnClickListener(v-> whatsapp.openWhats(orderData.getpPhone(), copyingData.getOrderData(orderData), this));

        orderid.setOnClickListener(v -> copyingData.copyTrack(orderData));
        linSender.setOnClickListener(v-> copyingData.copyPickUpPhone(orderData));
        txtCustomerName.setOnClickListener(v-> copyingData.copyDropPhone(orderData));
        btnCallSupplier.setOnClickListener(v-> ordersClass.callSender(orderData));
        btnCall.setOnClickListener(v -> ordersClass.callConsigne(orderData));

        if(orderData != null) {
            setOrderData();
            setUserData(orderData.getuId());
            UpdateUI(orderData.getStatue());
            checkForLocation();
        } else {
            getOrderData(hID);
        }
    }

    private void getOrderData(String hID) {
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya").child(hID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               orderData = snapshot.getValue(Order.class);

                setOrderData();
                setUserData(orderData.getuId());
                UpdateUI(orderData.getStatue());
                checkForLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void checkForLocation() {
        if(!orderData.getStatue().equals("readyD") && !orderData.getStatue().equals("denied")) return;
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("clientsLocations").child(orderData.getDPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    btnOrderMap.setVisibility(View.VISIBLE);
                } else {
                    btnOrderMap.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void setUserData(String owner) {
        ddUsername.setText(orderData.getOwner());

        if(owner.equals("")) return;
        uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user = snapshot.getValue(UserData.class);
                assert user != null;

                Picasso.get().load(Uri.parse(user.getPpURL())).into(supPP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void UpdateUI(String Statue) {
        btnCall.setVisibility(View.VISIBLE);
        OrderStatue orderStatue = new OrderStatue();

        switch (Statue) {
            case "accepted":
            case "readyD":
            case "recived2":
            case "capDenied": {
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            case "recived": {
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                break;
            }
        }

        txtOrder.setText(orderStatue.shortState(orderData.getStatue()));
    }

    @SuppressLint("SetTextI18n")
    private void setOrderData() {
        if (orderData.getNotes().equals("")) txtNotes.setVisibility(View.GONE);

        txtCustomerName.setText("اسم المستلم : " + orderData.getDName());
        txtDDate.setText("تاريخ التسليم : " + orderData.getDDate());
        txtDAddress.setText("العنوان : " + orderData.reStateD() + " - " + orderData.getDAddress());
        txtPostDate2.setText(_cacu.setPostDate(orderData.getDate()));

        txtPack.setText("محتوي الشحنه : " + orderData.getPackType());
        txtPDate.setText("تاريخ الاستلام : " + orderData.getpDate());
        txtPAddress.setText("عنوان الاستلام : " + orderData.reStateP() + " - " + orderData.getmPAddress());
        txtWeight.setText("وزن الشحنه : " + orderData.getPackWeight() + " كيلو");
        txtCash.setText(orderData.getGMoney() + " ج");
        orderid.setText("رقم تتبع الشحنه : " + orderData.getTrackid());
        txtNotes.setText("الملاحظات : " + orderData.getNotes());
        txtGGet.setText(orderData.getReturnMoney() + " ج");
    }
}