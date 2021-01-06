package com.armjld.rayashipping.Notifications;

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

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.AllOrders;
import com.armjld.rayashipping.SuperVisor.SuperVisorHome;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.notiData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.ArrayList;

@SuppressLint("StaticFieldLeak")
public class NotificationFragment extends Fragment {

    private static DatabaseReference nDatabase;
    private static SwipeRefreshLayout refresh;
    public static NotiAdaptere notiAdaptere;
    private static LinearLayout EmptyPanel;
    private static RecyclerView recyclerView;
    String uId = UserInFormation.getId();
    public static Context mContext;
    public static ArrayList<notiData> filterList = new ArrayList<>();

    public NotificationFragment() {
        // Required empty public constructor
    }
    
    public static NotificationFragment newInstance() {
        NotificationFragment fragment = new NotificationFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_notification, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBack);
        ImageView btnClear = view.findViewById(R.id.btnClear);
        nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");

        EmptyPanel = view.findViewById(R.id.EmptyPanel);
        refresh = view.findViewById(R.id.refresh);

        //Title Bar
        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        tbTitle.setText("الاشعارات");

        //Recycler
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // ------------ Refresh View ---------- //
        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(true);
            recyclerView.setAdapter(null);
            SuperVisorHome.getNoti();
            refresh.setRefreshing(false);
        });


        // ----------- Clear All Noti ------------ //
        btnClear.setOnClickListener(v-> {
            MaterialDialog materialDialog = new MaterialDialog.Builder(requireActivity()).setMessage("هل تريد الغاء كل الاشعارات ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_delete_white, (dialogInterface, which) -> {
                nDatabase.child(uId).removeValue();
                recyclerView.setAdapter(null);
                filterList.clear();
                SuperVisorHome.notiList.clear();
                SuperVisorHome.bottomNavigationView.removeBadge(R.id.noti);
                EmptyPanel.setVisibility(View.VISIBLE);
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            materialDialog.show();
        });

        setNoti();

        SuperVisorHome.bottomNavigationView.removeBadge(R.id.noti);

        btnBack.setOnClickListener(v->{
            SuperVisorHome.whichFrag = "Home";
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.container, new AllOrders(), SuperVisorHome.whichFrag).addToBackStack("Home").commit();
            SuperVisorHome.bottomNavigationView.setSelectedItemId(R.id.home);
        });
        
        return view;
    }

    public static void setNoti() {
            filterList = SuperVisorHome.notiList;
            notiAdaptere = new NotiAdaptere(mContext, filterList, mContext, filterList.size());
            if(recyclerView != null) {
                recyclerView.setAdapter(notiAdaptere);
                updateNone(filterList.size());
                for(int i = 0; i < SuperVisorHome.notiList.size(); i++) {
                    if(filterList.get(i).getIsRead().equals("false") && !filterList.get(i).getNotiID().equals("")) {
                        nDatabase.child(UserInFormation.getId()).child(SuperVisorHome.notiList.get(i).getNotiID()).child("isRead").setValue("true");
                    }
                }
            }
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

    private static void updateNone(int listSize) {
        if(listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }
}