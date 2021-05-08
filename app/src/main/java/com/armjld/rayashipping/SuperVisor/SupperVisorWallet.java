package com.armjld.rayashipping.SuperVisor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.armjld.rayashipping.Adapters.WalletAdapter;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.CaptinMoney;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class SupperVisorWallet extends AppCompatActivity {

    ArrayList<CaptinMoney> capMoneyList = new ArrayList<>();
    ImageView btnBack;
    TextView txtBouns, txtMoney;
    RecyclerView recyclerWallet;
    WalletAdapter walletAdapter;
    LinearLayout EmptyPanel;
    String walletMoney, packMoney;
    LinearLayout linPack, linBouns;
    SwipeRefreshLayout swipeRefresh;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supper_visor_wallet);


        TextView title = findViewById(R.id.toolbar_title);
        title.setText("المحفظه");

        btnBack = findViewById(R.id.btnBack);
        txtBouns = findViewById(R.id.txtBouns);
        txtMoney = findViewById(R.id.txtMoney);
        recyclerWallet = findViewById(R.id.recyclerWallet);
        EmptyPanel = findViewById(R.id.EmptyPanel);
        linPack = findViewById(R.id.linPack);
        linBouns = findViewById(R.id.linBouns);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        btnBack.setOnClickListener(v -> finish());

        walletMoney = UserInFormation.getWalletmoney();
        packMoney = UserInFormation.getPackMoney();

        txtBouns.setText(walletMoney + " ج");
        txtMoney.setText(packMoney + " ج");

        recyclerWallet.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerWallet.setLayoutManager(layoutManager);

        swipeRefresh.setRefreshing(true);
        refreshData();

        swipeRefresh.setOnRefreshListener(this::refreshData);

    }

    @SuppressLint("SetTextI18n")
    private void refreshData() {
        swipeRefresh.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user = snapshot.getValue(UserData.class);
                assert user != null;

                walletMoney = String.valueOf(user.getWalletmoney());
                packMoney = user.getPackMoney();

                UserInFormation.setWalletmoney(walletMoney);
                UserInFormation.setPackMoney(packMoney);

                txtBouns.setText(walletMoney + " ج");
                txtMoney.setText(packMoney + " ج");

                getMoney(UserInFormation.getId());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void getMoney(String uId) {
        capMoneyList.clear();
        capMoneyList.trimToSize();

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("payments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CaptinMoney captinMoney = ds.getValue(CaptinMoney.class);
                        assert captinMoney != null;
                        if (captinMoney.getIsPaid().equals("false")) {
                            capMoneyList.add(captinMoney);
                        }
                    }
                }


                Collections.sort(capMoneyList, (lhs, rhs) -> lhs.getDate().compareTo(rhs.getDate()));

                walletAdapter = new WalletAdapter(capMoneyList, SupperVisorWallet.this);
                recyclerWallet.setAdapter(walletAdapter);
                swipeRefresh.setRefreshing(false);
                updateNone(capMoneyList.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void updateNone(int listSize) {
        if (listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }
}