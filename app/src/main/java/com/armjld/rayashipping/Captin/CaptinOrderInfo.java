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
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class CaptinOrderInfo extends AppCompatActivity {

    Button btnDelivered,btnRate,btnChat,btnRecived,btnOrderBack, btnDelete;
    ImageView supPP,btnOrderMap;
    TextView orderid;
    TextView ddUsername;
    RatingBar ddRate;
    public static Data orderData;
    TextView txtCustomerName,txtDDate,txtDAddress,txtPostDate2,orderto,OrderFrom,txtPack,txtPDate,txtPAddress,txtWeight,txtCash,txtFees,txtOrder;
    DatabaseReference mDatabase, nDatabase, rDatabase,uDatabase;
    ImageView btnClose, icTrans;
    private static final int PHONE_CALL_CODE = 100;
    String hID = "";
    FloatingActionButton btnCall;
    TextView txtNotes;
    caculateTime _cacu = new caculateTime();
    LinearLayout linSupplier, linPackage, advice;
    ConstraintLayout constClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        hID = getIntent().getStringExtra("orderid");

        setContentView(R.layout.activity_captin_order_info);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        rDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("comments");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");


        TextView tbTitle2 = findViewById(R.id.toolbar_title);
        tbTitle2.setText("بيانات الشحنه");

        txtCustomerName= findViewById(R.id.txtCustomerName);
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
        txtFees = findViewById(R.id.txtFees);
        btnClose = findViewById(R.id.btnClose);
        supPP = findViewById(R.id.supPP);
        ddUsername = findViewById(R.id.ddUsername);
        btnOrderMap = findViewById(R.id.btnOrderMap);
        ddRate = findViewById(R.id.ddRate);

        btnDelivered = findViewById(R.id.btnDelivered);
        btnRate = findViewById(R.id.btnRate);
        btnChat = findViewById(R.id.btnChat);
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

        if(orderData == null) {
            finish();
        } else {
            setOrderData();
            setUserData(orderData.getuId());
            UpdateUI(orderData.getStatue());
        }

        btnClose.setOnClickListener(v-> finish());

        /*btnOrderMap.setOnClickListener(v -> {
            MapOneOrder.orderData = orderData;
            Intent map = new Intent(this, MapOneOrder.class);
            startActivity(map);
        });*/

        if(orderData.getProvider().equals("Esh7nly")) {
            linPackage.setVisibility(View.VISIBLE);
            linSupplier.setVisibility(View.VISIBLE);
            constClient.setVisibility(View.VISIBLE);
            advice.setVisibility(View.VISIBLE);
            txtFees.setVisibility(View.VISIBLE);
        } else {
            advice.setVisibility(View.GONE);
            txtFees.setVisibility(View.GONE);
            switch (orderData.getStatue()) {
                case "accepted" :
                case "recived" :
                case "recived2" :
                    linSupplier.setVisibility(View.VISIBLE);
                    linPackage.setVisibility(View.GONE);
                    constClient.setVisibility(View.GONE);
                    break;
                case "readyD" :
                    linSupplier.setVisibility(View.VISIBLE);
                    linPackage.setVisibility(View.VISIBLE);
                    constClient.setVisibility(View.VISIBLE);
                    break;
                case "capDenied" :
                    linSupplier.setVisibility(View.VISIBLE);
                    linPackage.setVisibility(View.VISIBLE);
                    constClient.setVisibility(View.GONE);
                    break;
            }
        }

        orderid.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TrackID", orderData.getTrackid());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "تم نسخ رقم التتبع الخاص بالشحنه", Toast.LENGTH_LONG).show();
        });

        btnCall.setOnClickListener(v -> {
            if(!orderData.getDPhone().equals("")) {
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
                ordersClass.orderRecived(orderData.getProvider(), orderData.getId(), orderData.getuId());

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
                ordersClass.orderDelvered(orderData.getProvider(), orderData.getId(),orderData.getDName(), orderData.getuId(), orderData.getuAccepted());

                // -------- Update UI ----------
                orderData.setStatue("delivered");
                UpdateUI(orderData.getStatue());

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        btnChat.setOnClickListener(v-> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(UserInFormation.getId(), orderData.getuId(), this);
            Messages.cameFrom = "Profile";
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

                String one = "0";
                String two = "0";
                String three = "0";
                String four = "0";
                String five = "0";
                if(snapshot.child("rating").child("one").exists()) {
                    one = Objects.requireNonNull(snapshot.child("rating").child("one").getValue()).toString();
                }
                if(snapshot.child("rating").child("two").exists()) {
                    two = Objects.requireNonNull(snapshot.child("rating").child("two").getValue()).toString();
                }
                if(snapshot.child("rating").child("three").exists()) {
                    three = Objects.requireNonNull(snapshot.child("rating").child("three").getValue()).toString();
                }
                if(snapshot.child("rating").child("four").exists()) {
                    four = Objects.requireNonNull(snapshot.child("rating").child("four").getValue()).toString();
                }
                if(snapshot.child("rating").child("five").exists()) {
                    five = Objects.requireNonNull(snapshot.child("rating").child("five").getValue()).toString();
                }
                Ratings _rat = new Ratings();
                ddRate.setRating(_rat.calculateRating(one,two,three,four,five));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void UpdateUI(String Statue) {
        btnRate.setText("تقييم التاجر");
        btnRate.setVisibility(View.GONE);
        btnCall.setVisibility(View.VISIBLE);
        btnDelete.setVisibility(View.GONE);
        switch (Statue) {
            case "placed" :
            case "deleted": {
                finish();
                break;
            }

            case "accepted" : {
                btnDelivered.setVisibility(View.GONE);
                btnChat.setVisibility(View.VISIBLE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                btnOrderMap.setVisibility(View.VISIBLE);
                txtOrder.setText("في انتظار استلام الشحنه من التاجر");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }
            case "recived" : {
                btnChat.setVisibility(View.VISIBLE);
                btnRecived.setVisibility(View.VISIBLE);
                btnDelivered.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                txtOrder.setText("في انتظار تأكيدك لاستلام الشحنه");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                break;
            }

            case "recived2" : {
                btnChat.setVisibility(View.GONE);
                btnDelivered.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                txtOrder.setText("في انتظار تسليم الشحنه للمخزن");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            case "readyD" : {
                btnChat.setVisibility(View.GONE);
                btnDelivered.setVisibility(View.VISIBLE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                txtOrder.setText("جاري تسليم الشحنه للمستلم");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            case "capDenied" : {
                btnChat.setVisibility(View.GONE);
                btnDelivered.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.VISIBLE);
                txtOrder.setText("جاري إرجاع الشحنه للراسل");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                break;
            }

            default: {
                btnChat.setVisibility(View.GONE);
                btnDelivered.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                txtOrder.setVisibility(View.GONE);
                break;
            }

            /*case "delivered" : {
                btnDelivered.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                btnCall.setVisibility(View.GONE);
                btnOrderMap.setVisibility(View.GONE);
                if(orderData.getSrated().equals("false")) {
                    btnRate.setVisibility(View.VISIBLE);
                }
                txtOrder.setText("تم تسليم الشحنه بنجاح");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                break;
            }
            case "deniedback" : {
                btnDelivered.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                btnCall.setVisibility(View.GONE);
                btnOrderMap.setVisibility(View.GONE);
                if(orderData.getSrated().equals("false")) {
                    btnRate.setVisibility(View.VISIBLE);
                }
                txtOrder.setText("تم استلام المرتع من التاجر");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));

                break;
            }
            case "denied": {
                btnDelivered.setVisibility(View.GONE);
                btnChat.setVisibility(View.VISIBLE);
                btnRecived.setVisibility(View.GONE);
                btnOrderBack.setVisibility(View.GONE);
                btnCall.setVisibility(View.GONE);
                btnOrderMap.setVisibility(View.VISIBLE);
                txtOrder.setText("لم يتم تأكيد تسليم المرتجع للتاجر");
                txtOrder.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                break;
            }*/


        }
    }

    @SuppressLint("SetTextI18n")
    private void setOrderData() {
        if(!orderData.getuAccepted().equals(UserInFormation.getId())) finish();
        if(orderData.getNotes().equals("")) txtNotes.setVisibility(View.GONE);
        txtCustomerName.setText(orderData.getDName());
        txtDDate.setText(orderData.getDDate());
        txtDAddress.setText("عنوان التسليم : " + orderData.reStateD() + " - " + orderData.getDAddress());
        txtPostDate2.setText(_cacu.setPostDate(orderData.getDate()));
        orderto.setText(orderData.reStateD());
        OrderFrom.setText(orderData.reStateP());
        txtPack.setText("محتوي الشحنه : " + orderData.getPackType());
        txtPDate.setText(orderData.getpDate());
        txtPAddress.setText ("عنوان الاستلام : " + orderData.reStateP() + " - " + orderData.getmPAddress());
        txtWeight.setText( "وزن الشحنه : " + orderData.getPackWeight() + " كيلو");
        txtFees.setText("مصاريف التوصيل : " + orderData.getGGet() + " ج");
        txtCash.setText("سعر الشحنه : " + orderData.getGMoney() + " ج");
        orderid.setText("رقم تتبع الشحنه : " + orderData.getTrackid());
        setIcon(UserInFormation.getTrans());
        txtNotes.setText("الملاحظات : " + orderData.getNotes());
    }

    private String getDate () {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(new Date());
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setIcon(String trans) {
        switch (trans) {
            case "Trans" :
                icTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_run));
                break;
            case "Car" :
                icTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_car));
                break;
            case "Bike" :
                icTrans.setImageDrawable(getResources().getDrawable(R.drawable.ic_bicycle));
                break;
            case "Motor" :
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