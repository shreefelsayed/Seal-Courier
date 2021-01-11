package com.armjld.rayashipping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Adapters.DeliveryAdapter;
import com.armjld.rayashipping.Adapters.MyAdapter;
import com.armjld.rayashipping.Login.LoginManager;
import com.armjld.rayashipping.Login.Login_Options;
import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.SuperVisor.AsignOrder;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;

import timber.log.Timber;

public class OrdersBySameUser extends AppCompatActivity {

    public static ArrayList<Data> filterList = new ArrayList<>();
    ArrayList<Data> mm = new ArrayList<>();
    String userID;
    String dName = "";
    ImageView btnAsignAll;
    //Recycler view
    private RecyclerView recyclerView;
    private LinearLayout EmptyPanel;

    @Override
    protected void onResume() {
        super.onResume();
        if (!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders_by_same_user);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            finish();
            startActivity(new Intent(this, Login_Options.class));
            Toast.makeText(this, "الرجاء تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }

        userID = getIntent().getStringExtra("userid");
        dName = getIntent().getStringExtra("name");

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("شحنات / " + dName);


        btnAsignAll = findViewById(R.id.btnAsignAll);
        EmptyPanel = findViewById(R.id.EmptyPanel);
        recyclerView = findViewById(R.id.recycler);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        if (UserInFormation.getAccountType().equals("Supervisor")) {
            btnAsignAll.setVisibility(View.VISIBLE);
        } else {
            btnAsignAll.setVisibility(View.GONE);
        }


        btnAsignAll.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل تريد تسليم  جميع شحنات " + dName + " للمندوب ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {

                Intent intent = new Intent(this, AsignOrder.class);
                AsignOrder.assignToCaptin.clear();
                AsignOrder.assignToCaptin.addAll(mm);
                startActivityForResult(intent, 1);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        getOrdersByLatest(userID);

    }

    private void getOrdersByLatest(String uIDD) {
        Timber.i("Getting Orders");

        mm.clear();

        for (int i = 0; i < filterList.size(); i++) {
            if (filterList.get(i).getuId().equals(uIDD)) {
                mm.add(filterList.get(i));
            }
        }

        if (UserInFormation.getAccountType().equals("Supervisor")) {
            MyAdapter orderAdapter = new MyAdapter(OrdersBySameUser.this, mm, "SameUser");
            recyclerView.setAdapter(orderAdapter);
        } else {
            DeliveryAdapter orderAdapter = new DeliveryAdapter(OrdersBySameUser.this, mm, "SameUser");
            recyclerView.setAdapter(orderAdapter);
        }

        updateNone(mm.size());
    }

    private void updateNone(int listSize) {
        Timber.i("List size is now : %s", listSize);
        if (listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }

}