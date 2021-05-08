package com.armjld.rayashipping.SuperCaptins;

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

import com.armjld.rayashipping.BaseScannerActivity;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserData;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collections;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class QRScanOrderForCaptin extends BaseScannerActivity implements ZXingScannerView.ResultHandler {

    TextView txtCounter;
    CountDownTimer resend;
    ImageView btnBack;
    SpinKitView loading;
    public static UserData user;
    private ZXingScannerView mScannerView;
    OrdersClass ordersClass = new OrdersClass(this);

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_q_r_scanner);

        if(user == null) finish();

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        txtCounter = findViewById(R.id.txtCounter);
        btnBack = findViewById(R.id.btnBack);
        loading = findViewById(R.id.loading);

        btnBack.setOnClickListener(v -> {
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
        scanOrder(rawResult.getText());
    }

    private void scanOrder(String trackID) {
        loading.setVisibility(View.VISIBLE);
        getRefrence ref = new getRefrence();

        ref.getRef("Raya").orderByChild("trackid").equalTo(trackID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Order orderData = ds.getValue(Order.class);
                        assert orderData != null;

                        ordersClass.asignDelv(orderData, user.getId());
                        finish();
                        break;
                    }
                } else {
                    Toast.makeText(QRScanOrderForCaptin.this, "Wrong QR Number", Toast.LENGTH_SHORT).show();
                }

                // -------------- Start Scanning Again in 5 Secs ----------
                loading.setVisibility(View.GONE);
                Handler handler = new Handler();
                int secDelay = 3000;
                handler.postDelayed(() -> mScannerView.resumeCameraPreview(QRScanOrderForCaptin.this), secDelay);
                startCounter(secDelay);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
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
                mScannerView.resumeCameraPreview(QRScanOrderForCaptin.this);
            }
        }.start();
    }
}