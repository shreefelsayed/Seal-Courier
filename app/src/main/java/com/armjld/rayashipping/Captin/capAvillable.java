package com.armjld.rayashipping.Captin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
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
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.Order;

import java.util.ArrayList;

import timber.log.Timber;

public class capAvillable extends Fragment {

    public static ArrayList<Order> filterList = new ArrayList<>();
    public static RecyclerView recyclerView;
    public static DeliveryAdapter orderAdapter;
    public static Context mContext;
    static LinearLayout EmptyPanel;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public capAvillable() { }

    public static void getOrders() {
        Timber.i("Setting orders in Home Fragment");
        filterList = Home.captinAvillable;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            filterList.sort((o1, o2) -> {
                String one = o1.getStatue();
                String two = o2.getStatue();
                return two.compareTo(one);
            });
        }

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
        View view = inflater.inflate(R.layout.fragment_captin_avillable, container, false);

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
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            Home.getDeliveryOrders();
            mSwipeRefreshLayout.setRefreshing(false);
        });

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