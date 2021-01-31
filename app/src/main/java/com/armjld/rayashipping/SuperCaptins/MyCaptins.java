package com.armjld.rayashipping.SuperCaptins;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.armjld.rayashipping.Adapters.CaptinsAdapter;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.AllOrders;
import com.armjld.rayashipping.models.userData;

import java.util.ArrayList;
import java.util.Collections;

import timber.log.Timber;

public class MyCaptins extends Fragment {

    public static RecyclerView recyclerView;
    public static CaptinsAdapter captinsAdapter;
    public static ArrayList<userData> captinList = new ArrayList<>();
    public static Context mContext;
    static LinearLayout EmptyPanel;
    ImageView btnBack;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public MyCaptins() {
    }

    public static void getCaptins() {
        Timber.i("Setting orders in Home Fragment");
        captinList = Home.mCaptins;

        Collections.sort(captinList, (lhs, rhs) -> lhs.getTrackId().compareTo(rhs.getTrackId()));

        if (mContext != null) {
            captinsAdapter = new CaptinsAdapter(mContext, captinList, "info");
        }
        if (recyclerView != null) {
            recyclerView.setAdapter(captinsAdapter);
            updateNone(captinList.size());
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
        View view = inflater.inflate(R.layout.fragment_my_captins, container, false);

        mSwipeRefreshLayout = view.findViewById(R.id.refresh);
        EmptyPanel = view.findViewById(R.id.EmptyPanel);
        recyclerView = view.findViewById(R.id.recycler);
        btnBack = view.findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> {
            Home.whichFrag = "Home";
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.container, new AllOrders(), Home.whichFrag).addToBackStack("Home").commit();
            Home.bottomNavigationView.setSelectedItemId(R.id.home);
        });

        EmptyPanel.setVisibility(View.GONE);

        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        tbTitle.setText("المندوبين");

        //Recycler
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // ------------------------ Refresh the recycler view ------------------------------- //
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            Home.getCaptins();
            mSwipeRefreshLayout.setRefreshing(false);
        });

        getCaptins();
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