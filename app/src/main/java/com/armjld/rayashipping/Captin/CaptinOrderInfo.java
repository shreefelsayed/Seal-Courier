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
    Button btnDelivered, btnRecived, btnOrderBack, btnDelete;
    ImageView supPP, btnOrderMap;
    TextView orderid;
    TextView ddUsername;
    RatingBar ddRate;
    TextView txtCustomerName, txtDDate, txtDAddress, txtPostDate2, orderto, OrderFrom, txtPack, txtPDate, txtPAddress, txtWeight, txtCash, txtOrder;
    DatabaseReference nDatabase, rDatabase, uDatabase;
    ImageView btnClose, icTrans;
    String hID = "";
    FloatingActionButton btnCall, btnCallSupplier;
    TextView txtNotes;
    caculateTime _cacu = new caculateTime();
    LinearLayout linPackage, advice;
    ConstraintLayout constClient,linSupplier;
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
        orderto = findViewById(R.id.orderto);
        OrderFrom = findViewById(R.id.OrderFrom);
        txtPack = findViewById(R.id.txtPack);
        txtPDate = findViewById(R.id.txtPDate);
        txtPAddress = findViewById(R.id.txtPAddress);
        txtWeight = findViewById(R.id.txtWeight);
        txtCash = findViewById(R.id.txtCash);
        btnClose = findViewById(R.id.btnClose);
        supPP = findViewById(R.id.supPP);
        ddUsername = findViewById(R.id.ddUsername);
        btnOrderMap = findViewById(R.id.btnOrderMap);
        ddRate = findViewById(R.id.ddRate);
        txtMoreOrders = findViewById(R.id.txtMoreOrders);

        btnDelivered = findViewById(R.id.btnDelivered);
        btnRecived = findViewById(R.id.btnRecived);
        btnOrderBack = findViewById(R.id.btnOrderBack);
        btnCall = findViewById(R.id.btnCall);
        orderid = findViewById(R.id.orderid);
        txtOrder = findViewById(R.id.txtOrder);
        btnDelete = findViewById(R.id.btnDelete);
        icTrans = findViewById(R.id.icTrans);

        linSupplier = findViewById(R.id.linSupplier);
        linPackage = findViewById(R.id.linPackage);
        constClient = findViewById(R.id.constClient);
        advice = findViewById(R.id.advice);

        txtNotes = findViewById(R.id.txtNotes);

        btnCallSupplier = findViewById(R.id.btnCallSupplier);

        boolean toPick = orderData.getStatue().equals("placed") || orderData.getStatue().equals("accepted") || orderData.getStatue().equals("recived");
        boolean toDelv = orderData.getStatue().equals("readyD") || orderData.getStatue().equals("supD") || orderData.getStatue().equals("capDenied") || orderData.getStatue().equals("supDenied");


        if (toPick || toDelv) btnOrderMap.setVisibility(View.VISIBLE);

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
            linPackage.setVisibility(View.VISIBLE);
            linSupplier.setVisibility(View.VISIBLE);
            constClient.setVisibility(View.VISIBLE);
            advice.setVisibility(View.VISIBLE);
        } else {
            advice.setVisibility(View.GONE);
            switch (orderData.getStatue()) {
                case "accepted":
                case "recived":
                case "recived2":
                case "capDenied":
                    linSupplier.setVisibility(View.VISIBLE);
                    linPackage.setVisibility(View.VISIBLE);
                    constClient.setVisibility(View.GONE);
                    break;

                case "readyD":
                    linSupplier.setVisibility(View.GONE);
                    linPackage.setVisibility(View.VISIBLE);
                    constClient.setVisibility(View.VISIBLE);
                    break;
            }
        }

        orderid.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TrackID", orderData.getTrackid());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "تم نسخ رقم التتبع الخاص بالشحنه", Toast.LENGTH_LONG).show();
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

        // ---------------- Set order to Recived
        btnRecived.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل قمت باستلام الشحنة من التاجر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(this);
                ordersClass.orderRecived(orderData);

                orderData.setStatue("recived2");
                UpdateUI(orderData.getStatue());

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // -----------------------   Set ORDER as Delivered
        btnDelivered.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل قمت بتسليم الشحنة ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {

                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(this);
                ordersClass.orderDelvered(orderData);

                // -------- Update UI ----------
                orderData.setStatue("delivered");
                UpdateUI(orderData.getStatue());

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
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
                String strOwnerPhone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();

                String one = "0";
                String two = "0";
                String three = "0";
                String four = "0";
                String five = "0";
                if (snapshot.child("rating").child("one").exists()) {
                    one = Objects.requireNonNull(snapshot.child("rating").child("one").getValue()).toString();
                }
                if (snapshot.child("rating").child("two").exists()) {
                    two = Objects.requireNonNull(snapshot.child("rating").child("two").getValue()).toString();
                }
                if (snapshot.child("rating").child("three").exists()) {
                    three = Objects.requireNonNull(snapshot.child("rating").child("three").getValue()).toString();
                }
                if (snapshot.child("rating").child("four").exists()) {
                    four = Objects.requireNonNull(snapshot.child("rating").child("four").getValue()).toString();
                }
                if (snapshot.child("rating").child("five").exists()) {
                    five = Objects.requireNonNull(snapshot.child("rating").child("five").getValue()).toString();
                }
                Ratings _rat = new Ratings();
                ddRate.setRating(_rat.calculateRating(one, two, three, four, five));

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
        btnDelete.setVisibility(View.GONE);
        switch (Statue) {
            case "accepted": {
                btnDelivered.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                btnOrderMap.setVisibility(View.VISIBLE);
                txtOrder.setText("في انتظار استلام الشحنه من التاجر");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }
            case "recived": {
                btnRecived.setVisibility(View.VISIBLE);
                btnDelivered.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                txtOrder.setText("في انتظار تأكيدك لاستلام الشحنه");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                break;
            }

            case "recived2": {
                btnDelivered.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                txtOrder.setText("في انتظار تسليم الشحنه للمخزن");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            case "readyD": {
                btnDelivered.setVisibility(View.VISIBLE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                txtOrder.setText("جاري تسليم الشحنه للمستلم");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            case "capDenied": {
                btnDelivered.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.VISIBLE);
                txtOrder.setText("جاري إرجاع الشحنه للراسل");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            default: {
                btnDelivered.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                txtOrder.setVisibility(View.GONE);
                break;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setOrderData() {
        if (!orderData.getuAccepted().equals(UserInFormation.getId())) finish();
        if (orderData.getNotes().equals("")) txtNotes.setVisibility(View.GONE);
        txtCustomerName.setText(orderData.getDName());
        txtDDate.setText(orderData.getDDate());
        txtDAddress.setText("عنوان التسليم : " + orderData.reStateD() + " - " + orderData.getDAddress());
        txtPostDate2.setText(_cacu.setPostDate(orderData.getDate()));
        orderto.setText(orderData.reStateD());
        OrderFrom.setText(orderData.reStateP());
        txtPack.setText("محتوي الشحنه : " + orderData.getPackType());
        txtPDate.setText(orderData.getpDate());
        txtPAddress.setText("عنوان الاستلام : " + orderData.reStateP() + " - " + orderData.getmPAddress());
        txtWeight.setText("وزن الشحنه : " + orderData.getPackWeight() + " كيلو");
        txtCash.setText("سعر الشحنه : " + orderData.getGMoney() + " ج");
        orderid.setText("رقم تتبع الشحنه : " + orderData.getTrackid());
        setIcon(UserInFormation.getTrans());
        txtNotes.setText("الملاحظات : " + orderData.getNotes());
    }

    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(new Date());
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, requestCode);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setIcon(String trans) {
        switch (trans) {
            case "Trans":
                icTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_run));
                break;
            case "Car":
                icTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_car));
                break;
            case "Bike":
                icTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_bicycle));
                break;
            case "Motor":
                icTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_vespa));
                break;
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