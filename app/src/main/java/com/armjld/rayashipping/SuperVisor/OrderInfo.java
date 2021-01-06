package com.armjld.rayashipping.SuperVisor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Ratings.Ratings;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.notiData;
import com.armjld.rayashipping.models.requestsData;
import com.armjld.rayashipping.myCaptReqs;
import com.armjld.rayashipping.rquests;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class OrderInfo extends AppCompatActivity {

    String orderID;
    DatabaseReference uDatabase,nDatabase;
    String owner;
    public static Data orderData;
    public static String cameFrom = "Home Activity";
    TextView date3, date, orderto, OrderFrom,txtPack,txtWeight,ordercash2,fees2,txtPostDate2;
    TextView dsUsername;
    TextView txtTitle;
    TextView ddCount;
    TextView dsPAddress,dsDAddress,txtCallCustomer,txtCustomerName;
    ImageView supPP,btnOrderMap;
    RatingBar rbUser;
    ImageView btnClose;
    Button btnBid,btnAccept;
    TextView txtMoreOrders;
    ImageView icTrans;
    private static final int PHONE_CALL_CODE = 100;
    TextView txtNotes;
    LinearLayout linSupplier,advice;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    SimpleDateFormat orderformat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    String datee = sdf.format(new Date());
    String orderState = "placed";
    String acceptedTime = "";
    String lastEdit = "";
    String dName = "";
    String ownerName = "";
    String dPhone = "";
    String ownerPhone;
    private boolean hasMore = false;
    boolean isBid = false;
    int position = 0;

    @Override
    public void onBackPressed() {
        finish();
        //getBack();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_info);

        if(orderData == null) {
            finish();
            return;
        }

        orderID = orderData.getId();
        owner = orderData.getuId();

        position = getIntent().getIntExtra("position", 0);

        uDatabase = getInstance().getReference().child("Pickly").child("users");
        nDatabase = getInstance().getReference().child("Pickly").child("notificationRequests");

        dsUsername = findViewById(R.id.ddUsername);
        dsPAddress = findViewById(R.id.ddPhone);
        dsDAddress = findViewById(R.id.dsDAddress);
        txtTitle = findViewById(R.id.txtTitle);
        supPP = findViewById(R.id.supPP);
        rbUser = findViewById(R.id.ddRate);
        ddCount = findViewById(R.id.ddCount);
        btnClose = findViewById(R.id.btnClose);
        txtPostDate2 = findViewById(R.id.txtPostDate2);
        btnBid = findViewById(R.id.btnBid);
        txtMoreOrders = findViewById(R.id.txtMoreOrders);
        txtCustomerName = findViewById(R.id.txtCustomerName);
        txtCallCustomer = findViewById(R.id.txtCallCustomer);
        btnOrderMap = findViewById(R.id.btnOrderMap);
        date3 = findViewById(R.id.date3);
        date = findViewById(R.id.date);
        orderto = findViewById(R.id.orderto);
        OrderFrom = findViewById(R.id.OrderFrom);
        txtPack = findViewById(R.id.txtPack);
        txtWeight = findViewById(R.id.txtWeight);
        ordercash2 = findViewById(R.id.ordercash2);
        fees2 = findViewById(R.id.fees2);
        icTrans = findViewById(R.id.icTrans);
        txtNotes = findViewById(R.id.txtNotes);
        linSupplier = findViewById(R.id.linSupplier);
        advice = findViewById(R.id.advice);

        btnAccept = findViewById(R.id.btnAccept);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("بيانات الشحنة");

        btnClose.setOnClickListener(v -> finish());
        linSupplier.setVisibility(View.GONE);

        orderState = orderData.getStatue();
        acceptedTime = orderData.getAcceptedTime();
        lastEdit = orderData.getLastedit();
        dName = orderData.getDName();
        dPhone = orderData.getDPhone();


        setPostDate(orderData.getDate());

        String from = orderData.reStateP();
        String to = orderData.reStateD();

        String PAddress = "عنوان الاستلام : " + from + " - " + orderData.getmPAddress();
        String DAddress = "عنوان التسليم : " + to;
        String fees = "مصاريف التوصيل : " + orderData.getGGet();
        String money = "سعر الشحنه : " + orderData.getGMoney();
        String pDate = orderData.getpDate();
        String dDate = orderData.getDDate();
        String pack = "المحتوي : " + orderData.getPackType();
        String weight = "الوزن : " + orderData.getPackWeight() + " كيلو";
        DAddress = DAddress + " - " + orderData.getDAddress();

        // ------ Check what buttons to show depending on the provider & statue
        if(orderState.equals("placed")) {
            btnBid.setVisibility(View.VISIBLE);
            btnAccept.setVisibility(View.VISIBLE);
        } else if(orderState.equals("supD")){
            btnBid.setVisibility(View.GONE);
            btnAccept.setVisibility(View.VISIBLE);
        } else {
            btnBid.setVisibility(View.GONE);
            btnAccept.setVisibility(View.GONE);
        }

        if(orderData.getProvider().equals("Esh7nly")) {
            btnAccept.setVisibility(View.GONE);
            fees2.setVisibility(View.VISIBLE);
            advice.setVisibility(View.VISIBLE);
        } else {
            btnBid.setVisibility(View.GONE);
            fees2.setVisibility(View.GONE);
            advice.setVisibility(View.GONE);
        }

        if(orderData.getNotes().equals("")) txtNotes.setVisibility(View.GONE);

        // ----------- Set the Bidding Statue
        isBid = rquests.getRequests().stream().anyMatch(x -> x.getOrderId().equals(orderID));

        if(!isBid) {
            setBid("false");
        } else {
            setBid("true");
        }

        // ------- Set the Data
        fees2.setText(fees + " ج");
        ordercash2.setText(money + " ج");
        date3.setText(pDate);
        date.setText(dDate);
        txtPack.setText(pack);
        txtWeight.setText(weight);
        orderto.setText(to);
        OrderFrom.setText(from);
        setIcon(UserInFormation.getTrans());
        txtNotes.setText("الملاحظات : " + orderData.getNotes());
        txtCallCustomer.setText("رقم هاتف العميل : " + dPhone);
        txtCallCustomer.setPaintFlags(txtCallCustomer.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtCustomerName.setText( orderData.getDName());
        dsPAddress.setText(PAddress);
        dsDAddress.setText(DAddress);

        // ------------------------- Buttons Functions ---------------------------- \\

        // ------ Accept for Raya Orders
        btnAccept.setOnClickListener(v-> {
            Intent intent = new Intent(this, AsignOrder.class);
            intent.putExtra("orderId", orderID);
            intent.putExtra("orderOwner", owner);
            intent.putExtra("provider", orderData.getProvider());
            if(orderState.equals("placed")) {
                intent.putExtra("type", "accept");
            } else if(orderState.equals("supD")) {
                intent.putExtra("type", "delv");
            }
            intent.putExtra("dName", orderData.getDName());
            startActivityForResult(intent, 1);
        });

        // ------- Request for Esh7nly Orders
        btnBid.setOnClickListener(v1 -> {
            if(isBid) {
                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل انت متأكد من انك تريد الغاء التقديم علي هذه الشحنه ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                    // ------------------- Send Request -------------------- //
                    rquests _rquests = new rquests(this);
                    _rquests.deleteReq(orderID, owner, orderData.getProvider());

                    // ------------------ Notificatiom ------------------ //
                    setBid("false");
                    isBid = false;
                    Toast.makeText(this, "تم الغاء التقديم", Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
                mBottomSheetDialog.show();
            } else {
                Intent intent = new Intent(this, AsignOrder.class);
                intent.putExtra("orderId", orderID);
                intent.putExtra("orderOwner", owner);
                intent.putExtra("type", "bid");
                intent.putExtra("dName", orderData.getDName());
                intent.putExtra("position", position);
                intent.putExtra("provider", orderData.getProvider());
                startActivityForResult(intent, 1);
            }
        });

        /*btnOrderMap.setOnClickListener(v -> {
            MapOneOrder.orderData = orderData;
            Intent map = new Intent(this, MapOneOrder.class);
            startActivity(map);
        });*/

        supPP.setOnClickListener(v -> {
            if(hasMore) {
                Intent otherOrders = new Intent(this, OrdersBySameUser.class);
                otherOrders.putExtra("userid", owner);
                otherOrders.putExtra("name", ownerName);
                startActivity(otherOrders);
            } else {
                Toast.makeText(this, "لا يوجد شحنات متاحة اخري لهذا المستخدم", Toast.LENGTH_SHORT).show();
            }
        });

        txtCallCustomer.setOnClickListener(v -> {
            if(!dPhone.equals("")) {
                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(OrderInfo.this).setMessage("هل تريد الاتصال بالعميل ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {

                    checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + dPhone));
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

        // ------- Set More Supplier Data ------- //
        getSupData(owner);
        getSupOrders(owner);
    }

    private void getSupOrders(String owner) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // Get posted orders count
        mDatabase.orderByChild("uId").equalTo(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "ResourceAsColor"})
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                int oCount = 0;
                int currentOrders = 0;

                // --- Get Available Orders For Supplier
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        Date orderDate = null;
                        Date myDate = null;
                        try {
                            orderDate = orderformat.parse(orderData.getDDate());
                            myDate = orderformat.parse(orderformat.format(Calendar.getInstance().getTime()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        assert orderDate != null;
                        assert myDate != null;

                        if (!Objects.requireNonNull(ds.child("statue").getValue()).toString().equals("deleted")) {
                            oCount++;
                            if (orderDate.compareTo(myDate) >= 0 && Objects.requireNonNull(ds.child("statue").getValue()).toString().equals("placed")) {
                                currentOrders++;
                            }
                        }
                    }
                }

                if (oCount == 0) {
                    hasMore = false;
                    ddCount.setText("لم يقم بأضافه اي شحنة");
                } else {
                    hasMore = true;
                    ddCount.setText("اضاف " + oCount + " شحنة");
                }

                if (currentOrders <= 1) {
                    txtMoreOrders.setVisibility(View.GONE);
                } else {
                    int finalc = currentOrders - 1;
                    txtMoreOrders.setVisibility(View.VISIBLE);
                    txtMoreOrders.setText(finalc + "");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
    }

    private void getSupData(String owner) {
        uDatabase.child(owner).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Supplier Main Info ---
                ownerName = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                String dsPP = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                Picasso.get().load(Uri.parse(dsPP)).into(supPP);
                dsUsername.setText(ownerName);
                ownerPhone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();

                // Supplier Ratings ---
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
                rbUser.setRating(_rat.calculateRating(one,two,three,four,five));

                linSupplier.setVisibility(View.VISIBLE); // Show the Info
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isBid = rquests.getRequests().stream().anyMatch(x -> x.getOrderId().equals(orderID));
        if(!isBid) {
            setBid("false");
        } else {
            setBid("true");
        }
    }

    public void setPostDate(String startDate) {
        caculateTime _cacu = new caculateTime();
        txtPostDate2.setText(_cacu.setPostDate(startDate));
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

    public void setBid(String type) {
        if(type.equals("true")) {
            btnBid.setText("الغاء الطلب");
            btnBid.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_bad_square));
        } else {
            btnBid.setBackgroundColor(ContextCompat.getColor(this, R.color.yellow));
            btnBid.setText("تقديم طلب توصيل");
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
