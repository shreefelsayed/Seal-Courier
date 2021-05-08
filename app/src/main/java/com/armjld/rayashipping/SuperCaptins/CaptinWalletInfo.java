package com.armjld.rayashipping.SuperCaptins;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.armjld.rayashipping.Adapters.WalletAdapter;
import com.armjld.rayashipping.EnvioMoney;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.CaptinMoney;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;
import java.util.Collections;

public class CaptinWalletInfo extends AppCompatActivity {

    public static UserData user;
    ArrayList<CaptinMoney> capMoneyList = new ArrayList<>();
    ImageView btnBack;
    TextView txtBouns, txtMoney;
    RecyclerView recyclerWallet;
    WalletAdapter walletAdapter;
    LinearLayout EmptyPanel;
    String uId, walletMoney, packMoney;
    LinearLayout linPack, linBouns;
    SwipeRefreshLayout swipeRefresh;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captin_wallet_info);

        TextView title = findViewById(R.id.toolbar_title);
        title.setText("المحفظه");

        if (UserInFormation.getAccountType().equals("Supervisor")) {
            uId = user.getId();
            walletMoney = user.getWalletmoney() + "";
            packMoney = user.getPackMoney();
        } else {
            uId = UserInFormation.getId();
            walletMoney = UserInFormation.getWalletmoney();
            packMoney = UserInFormation.getPackMoney();
        }

        btnBack = findViewById(R.id.btnBack);
        txtBouns = findViewById(R.id.txtBouns);
        txtMoney = findViewById(R.id.txtMoney);
        recyclerWallet = findViewById(R.id.recyclerWallet);
        EmptyPanel = findViewById(R.id.EmptyPanel);
        linPack = findViewById(R.id.linPack);
        linBouns = findViewById(R.id.linBouns);
        swipeRefresh = findViewById(R.id.swipeRefresh);

        btnBack.setOnClickListener(v -> finish());

        txtBouns.setText(walletMoney + " ج");
        txtMoney.setText(packMoney + " ج");

        recyclerWallet.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerWallet.setLayoutManager(layoutManager);

        swipeRefresh.setRefreshing(true);

        swipeRefresh.setOnRefreshListener(() -> {
            if (UserInFormation.getAccountType().equals("Supervisor")) {
                refreshData();
            } else if (UserInFormation.getAccountType().equals("Delivery Worker")) {
                capRefresh();
            }
        });

        // --- Click to Pay Pack Money
        linPack.setOnClickListener(v -> {
            if (UserInFormation.getAccountType().equals("Supervisor") && !packMoney.equals("0")) {
                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل قمت باستلام من " + user.getName() + " مبلغ " + packMoney + " ج مستحقات التسليم ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                    EnvioMoney envioMoney = new EnvioMoney(this);
                    envioMoney.payPackMoney(user, packMoney);
                    refreshData();
                    dialogInterface.dismiss();
                }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
                mBottomSheetDialog.show();
            }
        });

        // --- Click to Pay Bouns Money
        linBouns.setOnClickListener(v -> {
            if (UserInFormation.getAccountType().equals("Supervisor") && !walletMoney.equals("0")) {
                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل قمت بتسليم " + user.getName() + " مبلغ " + walletMoney + " ج ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                    EnvioMoney envioMoney = new EnvioMoney(this);
                    envioMoney.payBouns(user, walletMoney);
                    refreshData();
                    dialogInterface.dismiss();
                }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
                mBottomSheetDialog.show();
            }
        });

        getMoney(uId);
    }

    private void refreshData() {
        swipeRefresh.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(UserData.class);
                assert user != null;
                getMoney(user.getId());
                walletMoney = String.valueOf(user.getWalletmoney());
                packMoney = user.getPackMoney();

                txtBouns.setText(walletMoney + " ج");
                txtMoney.setText(packMoney + " ج");

                Home.getCaptins();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void capRefresh() {
        swipeRefresh.setRefreshing(true);
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData _user = snapshot.getValue(UserData.class);
                assert _user != null;

                walletMoney = String.valueOf(_user.getWalletmoney());
                packMoney = _user.getPackMoney();

                UserInFormation.setWalletmoney(walletMoney);
                UserInFormation.setPackMoney(packMoney);


                txtBouns.setText(walletMoney + " ج");
                txtMoney.setText(packMoney + " ج");

                getMoney(_user.getId());

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


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    capMoneyList.sort((o1, o2) -> {
                        String one = o1.getDate();
                        String two = o2.getDate();
                        return one.compareTo(two);
                    });
                } else {
                    Collections.sort(capMoneyList, (lhs, rhs) -> lhs.getDate().compareTo(rhs.getDate()));
                }

                walletAdapter = new WalletAdapter(capMoneyList, CaptinWalletInfo.this);
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