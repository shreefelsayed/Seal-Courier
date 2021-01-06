package com.armjld.rayashipping.SuperVisor;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.armjld.rayashipping.Adapters.SuperVisorAdapter;
import com.armjld.rayashipping.Filters;
import com.armjld.rayashipping.QRScanner;
import com.armjld.rayashipping.R;
import com.google.android.material.tabs.TabLayout;

public class AllOrders extends Fragment {

    public static Context mContext;
    public static TabLayout tabs;


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
        ImageView filtrs_btn = view.findViewById(R.id.filters_btn);
        ImageView btnMaps = view.findViewById(R.id.btnMaps);
        ImageView btnScan = view.findViewById(R.id.btnScan);

        TextView fitlerTitle = view.findViewById(R.id.toolbar_title);
        fitlerTitle.setText("الشحنات");

        ViewPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new SuperVisorAdapter(mContext, getChildFragmentManager()));
        tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        filtrs_btn.setOnClickListener(v -> {
            Intent i = new Intent(getActivity(), Filters.class);
            startActivityForResult(i, 1);
        });

        btnScan.setOnClickListener(v-> {
            Intent i = new Intent(mContext, QRScanner.class);
            mContext.startActivity(i);
        });

        //btnMaps.setOnClickListener(v -> startActivityForResult(new Intent(getActivity(), MapsActivity.class), 1));

        return view;
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