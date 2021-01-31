package com.armjld.rayashipping;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import timber.log.Timber;


public class QRScanner extends BaseScannerActivity implements ZXingScannerView.ResultHandler {
    TextView txtCounter;
    CountDownTimer resend;
    ImageView btnBack;
    SpinKitView loading;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());
    private ZXingScannerView mScannerView;

    @Override
    public void onBackPressed() {
        if (UserInFormation.getAccountType().equals("Supervisor")) {
            Home.getRayaDelv();
        } else {
            Home.getDeliveryOrders();
        }
        
        finish();
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_q_r_scanner);
        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        txtCounter = findViewById(R.id.txtCounter);
        btnBack = findViewById(R.id.btnBack);
        loading = findViewById(R.id.loading);

        btnBack.setOnClickListener(v -> {
            if (UserInFormation.getAccountType().equals("Supervisor")) {
                Home.getRayaDelv();
            } else {
                Home.getDeliveryOrders();
            }
            finish();
        });

        mScannerView = new ZXingScannerView(this);
        mScannerView.setFormats(Collections.singletonList(BarcodeFormat.QR_CODE));
        mScannerView.setAutoFocus(true);
        mScannerView.setLaserEnabled(true);
        mScannerView.setBorderColor(Color.WHITE);
        mScannerView.stopCameraPreview();
        mScannerView.setIsBorderCornerRounded(true);
        mScannerView.setBorderCornerRadius(300);
        contentFrame.addView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        mScannerView.stopCameraPreview();
        if (UserInFormation.getAccountType().equals("Supervisor")) {
            checkForOrder(rawResult.getText());
        } else {
            checkForCaptin(rawResult.getText());
        }
    }


    // ---------- Scan For Captin ------------ \\
    private void checkForCaptin(String trackID) {
        loading.setVisibility(View.VISIBLE);
        String strF = String.valueOf(trackID.charAt(0));
        DatabaseReference mDatabase;

        // ------------ Check for order Refrence
        getRefrence ref = new getRefrence();
        if (!strF.equals("R")) {
            mDatabase = ref.getRef("Esh7nly");
        } else {
            mDatabase = ref.getRef("Raya");
        }

        mDatabase.orderByChild("trackid").equalTo(trackID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ------------ Make Action on Order -----------
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        OrdersClass ordersClass = new OrdersClass(QRScanner.this);
                        if (orderData.getStatue().equals("supD") && ds.child("dSupervisor").getValue().toString().equals(UserInFormation.getMySup())) {
                            ordersClass.asignDelv(orderData, UserInFormation.getId());
                        } else if(orderData.getStatue().equals("supDenied")) {
                            ordersClass.asignDenied(orderData, UserInFormation.getId());
                        } else {
                            Toast.makeText(QRScanner.this, "لا يمكنك استلام تلك الشحنه من المشرف", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                } else {
                    Toast.makeText(QRScanner.this, "رقم التتبع غير صحيح، حاول مره اخري ..", Toast.LENGTH_SHORT).show();
                }

                // -------------- Start Scanning Again in 5 Secs ----------
                loading.setVisibility(View.GONE);
                Handler handler = new Handler();
                int secDelay = 3000;
                handler.postDelayed(() -> mScannerView.resumeCameraPreview(QRScanner.this), secDelay);
                startCounter(secDelay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // ---------- Scan For SuperVisor ------------ \\
    private void checkForOrder(String trackID) {
        loading.setVisibility(View.VISIBLE);
        String strF = String.valueOf(trackID.charAt(0));
        DatabaseReference mDatabase;

        long tsLong = System.currentTimeMillis() / 1000;
        String logC = Long.toString(tsLong);

        // ------------ Check for order Refrence
        getRefrence ref = new getRefrence();
        if (!strF.equals("R")) {
            mDatabase = ref.getRef("Esh7nly");
        } else {
            mDatabase = ref.getRef("Raya");
        }

        mDatabase.orderByChild("trackid").equalTo(trackID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // ------------ Make Action on Order -----------
                if (snapshot.exists()) {
                    Timber.i("Track Id Exists");
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        if (orderData.getStatue().equals("hubD") || orderData.getStatue().equals("deniedD")) {
                            // --------- Update Values
                            mDatabase.child(orderData.getId()).child("statue").setValue("supD");
                            mDatabase.child(orderData.getId()).child("supDScanTime").setValue(datee);
                            mDatabase.child(orderData.getId()).child("dSupervisor").setValue(UserInFormation.getSup_code());

                            // ---------------- Add Log
                            String Log = "تم تسليم الشحنه لمشرف التسليم " + UserInFormation.getSup_code();
                            mDatabase.child(orderData.getId()).child("logs").child(logC).setValue(Log);

                            Toast.makeText(QRScanner.this, "تم استلام الشحنه بنجاح", Toast.LENGTH_SHORT).show();
                        } else if(orderData.getStatue().equals("hub2Denied")) {
                            // --------- Update Values
                            mDatabase.child(orderData.getId()).child("statue").setValue("supDenied");
                            mDatabase.child(orderData.getId()).child("supDeniedTime").setValue(datee);

                            // ---------------- Add Log
                            String Log = "تم استلام المرتجع من المخزن. ";
                            mDatabase.child(orderData.getId()).child("logs").child(logC).setValue(Log);

                            Toast.makeText(QRScanner.this, "تم استلام المرتجع من المخزن", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(QRScanner.this, "لا يمكنك استلام تلك الشحنه راجع الشحنه مع المخزن", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                } else {
                    Toast.makeText(QRScanner.this, "رقم التتبع غير صحيح، حاول مره اخري ..", Toast.LENGTH_SHORT).show();
                }

                // -------------- Start Scanning Again in 5 Secs ----------
                loading.setVisibility(View.GONE);
                startCounter(3000);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    // ---------- Start a Delay Counter ------------ \\
    private void startCounter(int secDelay) {
        resend = new CountDownTimer(secDelay, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                txtCounter.setVisibility(View.VISIBLE);
                txtCounter.setText((l / 1000) + "");
            }

            @Override
            public void onFinish() {
                txtCounter.setVisibility(View.GONE);
                mScannerView.resumeCameraPreview(QRScanner.this);
            }
        }.start();
    }
}