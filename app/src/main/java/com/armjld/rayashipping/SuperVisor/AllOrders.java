package com.armjld.rayashipping.SuperVisor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.armjld.rayashipping.Adapters.SuperVisorPageAdapter;
import com.armjld.rayashipping.Filters;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.MapsActivity;
import com.armjld.rayashipping.QRScanner;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.FirebaseDatabase;
import com.hypertrack.sdk.HyperTrack;
import com.hypertrack.sdk.TrackingError;
import com.hypertrack.sdk.TrackingStateObserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AllOrders extends Fragment implements TrackingStateObserver.OnTrackingStateChangeListener {

    @SuppressLint("StaticFieldLeak")
    public static Context mContext;
    public static TabLayout tabs;
    public static int CAMERA_CODE = 80;
    String htID = "00F4wm01NAr4ZHVE4gjrtQiQw8BxYC9dJNjeoM0LE4eEpm928geMz-2Tt8bZgOzJnTlE64SKDs_ZUEYyBJE4wQ";
    public static HyperTrack sdkInstance;
    ImageView btnTrack;


    public AllOrders() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_all_orders, container, false);

        // Buttons -------
        ImageView btnFilters = view.findViewById(R.id.filters_btn);
        ImageView btnMaps = view.findViewById(R.id.btnMaps);
        ImageView btnScan = view.findViewById(R.id.btnScan);
        btnTrack = view.findViewById(R.id.btnTrack);

        TextView fitlerTitle = view.findViewById(R.id.toolbar_title);
        fitlerTitle.setText("الشحنات");

        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new SuperVisorPageAdapter(mContext, getChildFragmentManager()));
        tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        sdkInstance = HyperTrack.getInstance(htID);

        if(UserInFormation.getUser().getAccountType().equals("Supervisor")) btnTrack.setVisibility(View.GONE);

        btnTrack.setOnClickListener(v-> {
            if(sdkInstance.isRunning()) {
                stopTracking();
            } else {
                startTracking();
            }
        });

        trackButton();

        btnScan.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_DENIED) {
                Intent i = new Intent(mContext, QRScanner.class);
                mContext.startActivity(i);
            } else {
                ActivityCompat.requestPermissions((Activity) mContext, new String[] {Manifest.permission.CAMERA}, CAMERA_CODE);
            }

        });

        btnFilters.setOnClickListener(v -> OpenFilters());

        btnMaps.setOnClickListener(v -> openMaps());

        return view;
    }

    private void trackButton() {
        // --- Change Filter
        if(sdkInstance.isRunning()) {
            btnTrack.setColorFilter(Color.GREEN);
        } else {
            btnTrack.setColorFilter(Color.WHITE);
        }
    }
    private void startTracking() {
        HyperTrack.enableDebugLogging();
        sdkInstance.addTrackingListener(this);

        sdkInstance.setDeviceName(UserInFormation.getUser().getName());
        Map<String, Object> myMetadata = new HashMap<>();
        myMetadata.put("vehicle_type", UserInFormation.getUser().getTransType());
        myMetadata.put("group_id", UserInFormation.getUser().getAccountType());

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getUser().getId()).child("trackId").setValue(sdkInstance.getDeviceID());

        sdkInstance.setDeviceMetadata(myMetadata);
        sdkInstance.start();
        btnTrack.setColorFilter(Color.GREEN);
        Toast.makeText(mContext, "تم تشغيل البرنامج", Toast.LENGTH_SHORT).show();

    }

    private void stopTracking() {
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getUser().getId()).child("trackId").setValue("");
        sdkInstance.stop();
        sdkInstance.removeTrackingListener(this);
        btnTrack.setColorFilter(Color.WHITE);
        Toast.makeText(mContext, "تم ايقاف البرنامج", Toast.LENGTH_SHORT).show();

    }

    private void OpenFilters() {
        Intent i = new Intent(getActivity(), Filters.class);
        if (UserInFormation.getUser().getAccountType().equals("Supervisor")) {
            if (tabs.getSelectedTabPosition() == 0) {
                Filters.mainListm = Home.mm;
                Filters.what = "recive";
            } else if (tabs.getSelectedTabPosition() == 1) {
                Filters.mainListm = Home.delvOrders;
                Filters.what = "drop";
            }
        } else {
            if (tabs.getSelectedTabPosition() == 0) {
                Filters.mainListm = Home.captinAvillable;
                Filters.what = "recive";

            } else if (tabs.getSelectedTabPosition() == 1) {
                Filters.mainListm = Home.captinDelv;
                Filters.what = "drop";

            }
        }
        startActivityForResult(i, 1);

    }

    private void openMaps() {
        Intent i = new Intent(getActivity(), MapsActivity.class);
        ArrayList<Order> bothLists = new ArrayList<>();
        if (UserInFormation.getUser().getAccountType().equals("Supervisor")) {
            // -------- Compine Lists
            bothLists.addAll(Home.mm);
            bothLists.addAll(Home.delvOrders);
        } else {
            // -------- Compine Lists
            bothLists.addAll(Home.captinAvillable);
            bothLists.addAll(Home.captinDelv);
        }
        MapsActivity.filterList = bothLists;
        startActivityForResult(i, 1);
    }

    @Override
    public void onError(TrackingError trackingError) {

    }

    @Override
    public void onTrackingStart() {

    }

    @Override
    public void onTrackingStop() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sdkInstance.removeTrackingListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (sdkInstance.isRunning()) {
            onTrackingStart();
        } else {
            onTrackingStop();
        }

        sdkInstance.requestPermissionsIfNecessary();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }
}