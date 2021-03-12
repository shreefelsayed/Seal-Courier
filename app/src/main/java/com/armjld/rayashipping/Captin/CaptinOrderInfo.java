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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.armjld.rayashipping.Chat.Messages;
import com.armjld.rayashipping.Chat.chatListclass;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.MapsActivity;
import com.armjld.rayashipping.OrdersBySameUser;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Ratings.Ratings;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class CaptinOrderInfo extends AppCompatActivity {

    private static final int PHONE_CALL_CODE = 100;
    public static Data orderData;
    public static boolean toClick = true;
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
    LinearLayout constClient,linSupplier, linPackage, advice,linSender;
    TextView txtMoreOrders;
    private boolean hasMore = false;

    String strOwnerPhone = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hID = getIntent().getStringExtra("orderid");
        String from = getIntent().getStringExtra("from");

        toClick = !from.equals("SameUser");

        setContentView(R.layout.activity_captin_order_info);

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
        txtMoreOrders = findViewById(R.id.txtMoreOrders);
        txtGGet = findViewById(R.id.txtGGet);

        btnCall = findViewById(R.id.btnCall);
        orderid = findViewById(R.id.orderid);
        txtOrder = findViewById(R.id.txtOrder);

        linSupplier = findViewById(R.id.linSupplier);
        linPackage = findViewById(R.id.linPackage);
        constClient = findViewById(R.id.constClient);
        advice = findViewById(R.id.advice);

        txtNotes = findViewById(R.id.txtNotes);

        btnCallSupplier = findViewById(R.id.btnCallSupplier);

        boolean toPick = orderData.getStatue().equals("placed") || orderData.getStatue().equals("accepted") || orderData.getStatue().equals("recived") || orderData.getStatue().equals("recived2");
        boolean toDelv = orderData.getStatue().equals("readyD") || orderData.getStatue().equals("supD") || orderData.getStatue().equals("capDenied") || orderData.getStatue().equals("supDenied");


        if (toPick) {
            constClient.setVisibility(View.GONE);
        }

        if (orderData == null) {
            finish();
        } else {
            setOrderData();
            setUserData(orderData.getuId());
            UpdateUI(orderData.getStatue());
        }

        supPP.setOnClickListener(v -> {
            if (hasMore && toClick) {
                Intent otherOrders = new Intent(this, OrdersBySameUser.class);

                otherOrders.putExtra("userid", orderData.getuId());
                otherOrders.putExtra("name", orderData.getOwner());

                OrdersBySameUser.filterList.clear();
                OrdersBySameUser.filterList.addAll(Home.captinAvillable);
                startActivity(otherOrders);
            }
        });

        btnClose.setOnClickListener(v -> finish());

        btnOrderMap.setOnClickListener(v -> {
            ArrayList<Data> order = new ArrayList<>();
            order.add(orderData);

            MapsActivity.filterList = order;
            Intent map = new Intent(this, MapsActivity.class);
            startActivity(map);
        });

        if (orderData.getProvider().equals("Esh7nly")) {
            advice.setVisibility(View.VISIBLE);
        } else {
            advice.setVisibility(View.GONE);
        }

        orderid.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TrackID", orderData.getTrackid());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "تم نسخ رقم التتبع الخاص بالشحنه", Toast.LENGTH_LONG).show();
        });

        linSender.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("SenderPhone", strOwnerPhone);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "تم نسخ رقم هاتف الراسل", Toast.LENGTH_LONG).show();
        });

        txtCustomerName.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("ReciverPhone", orderData.getDPhone());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "تم نسخ رقم هاتف المرسل له", Toast.LENGTH_LONG).show();
        });

        btnCallSupplier.setOnClickListener(v-> {
            if(strOwnerPhone.equals("")) return;

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(CaptinOrderInfo.this).setMessage("هل تريد الاتصال بالتاجر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {
                checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + strOwnerPhone));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        btnCall.setOnClickListener(v -> {
            if (!orderData.getDPhone().equals("")) {
                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(CaptinOrderInfo.this).setMessage("هل تريد الاتصال بالعميل ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {
                    checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + orderData.getDPhone()));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);

                    dialogInterface.dismiss();
                }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
                mBottomSheetDialog.show();
            } else {
                Toast.makeText(this, "التاجر لم يضع رقم هاتف", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUserData(String owner) {
        uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ownerName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String dsPP = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                Picasso.get().load(Uri.parse(dsPP)).into(supPP);
                ddUsername.setText(ownerName);
                strOwnerPhone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();
                checkForMoreOrders();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void checkForMoreOrders() {
        int count = 0;

        for (int i = 0; i < Home.captinAvillable.size(); i++) {
            Data orderD = Home.captinAvillable.get(i);
            if (orderD.getuId().equals(orderData.getuId())) {
                count++;
            }
        }

        if (count > 1) {
            hasMore = true;
            txtMoreOrders.setVisibility(View.VISIBLE);
        } else {
            hasMore = false;
            txtMoreOrders.setVisibility(View.GONE);
        }
    }

    private void UpdateUI(String Statue) {
        btnCall.setVisibility(View.VISIBLE);
        switch (Statue) {
            case "accepted": {
                txtOrder.setText("في انتظار استلام الشحنه من التاجر");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }
            case "recived": {
                txtOrder.setText("في انتظار تأكيدك لاستلام الشحنه");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                break;
            }

            case "recived2": {
                txtOrder.setText("في انتظار تسليم الشحنه للمخزن");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            case "readyD": {
                txtOrder.setText("جاري تسليم الشحنه للمستلم");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            case "capDenied": {
                txtOrder.setText("جاري إرجاع الشحنه للراسل");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            default: {
                txtOrder.setVisibility(View.GONE);
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setOrderData() {
        if (!orderData.getuAccepted().equals(UserInFormation.getId())) finish();
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


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}