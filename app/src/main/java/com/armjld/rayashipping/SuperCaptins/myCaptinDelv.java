package com.armjld.rayashipping.SuperCaptins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.armjld.rayashipping.Adapters.DeliveryAdapter;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.models.Data;

import java.util.ArrayList;

public class myCaptinDelv extends Fragment {

    public static ArrayList<Data> filterList = new ArrayList<>();
    public static SwipeRefreshLayout mSwipeRefreshLayout;
    public static RecyclerView recyclerView;
    public static DeliveryAdapter orderAdapter;
    public static Context mContext;
    static LinearLayout EmptyPanel;

    public myCaptinDelv() {
    }

    public static void getOrders() {
        filterList = MyCaptinInfo.delv;
        if (mContext != null) {
            orderAdapter = new DeliveryAdapter(mContext, filterList, "Home");
        }
        if (recyclerView != null) {
            recyclerView.setAdapter(orderAdapter);
            updateNone(filterList.size());
        }
    }

    @SuppressLint("SetTextI18n")
    private static void updateNone(int listSize) {
        if (listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_captin_recived, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        EmptyPanel = view.findViewById(R.id.EmptyPanel);
        recyclerView = view.findViewById(R.id.recycler);

        EmptyPanel.setVisibility(View.GONE);

        //Recycler
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // ------------------------ Refresh the recycler view ------------------------------- //
        mSwipeRefreshLayout.setOnRefreshListener(MyCaptinInfo::getRaya);

        getOrders();

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