package com.armjld.rayashipping.SuperVisor;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.armjld.rayashipping.Adapters.SuperVisorAdapter;
import com.armjld.rayashipping.Filters;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.MapsActivity;
import com.armjld.rayashipping.QRScanner;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class AllOrders extends Fragment {

    public static Context mContext;
    public static TabLayout tabs;
    public static int CAMERA_CODE = 80;


    public AllOrders() {
    }

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

        TextView fitlerTitle = view.findViewById(R.id.toolbar_title);
        fitlerTitle.setText("الشحنات");

        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new SuperVisorAdapter(mContext, getChildFragmentManager()));
        tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);


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

    private void OpenFilters() {
        Intent i = new Intent(getActivity(), Filters.class);
        if (UserInFormation.getAccountType().equals("Supervisor")) {
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
        if (UserInFormation.getAccountType().equals("Supervisor")) {
            // -------- Compine Lists
            ArrayList<Data> bothLists = new ArrayList<>();
            bothLists.addAll(Home.mm);
            bothLists.addAll(Home.delvOrders);
            MapsActivity.filterList = bothLists;
        } else {
            // -------- Compine Lists
            ArrayList<Data> bothLists = new ArrayList<>();
            bothLists.addAll(Home.captinAvillable);
            bothLists.addAll(Home.captinDelv);
            MapsActivity.filterList = bothLists;
        }
        startActivityForResult(i, 1);
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