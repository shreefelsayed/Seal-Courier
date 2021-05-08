package com.armjld.rayashipping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Adapters.DeliveryAdapter;
import com.armjld.rayashipping.Adapters.MyAdapter;
import com.armjld.rayashipping.Login.LoginManager;
import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.SuperVisor.AsignOrder;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.android.material.textfield.TextInputLayout;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Filters extends AppCompatActivity {

    public static ArrayList<Order> mainListm = new ArrayList<>();
    public static String what = "";
    ArrayList<Order> filterList = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayout EmptyPanel;
    ImageView btnBack, btnAsignAll;

    AutoCompleteTextView autoGov, autoCity;

    String strGov = "";
    String strCity = "";

    ArrayList<String> filterCity = new ArrayList<>();
    ArrayList<String> govs = new ArrayList<>();

    TextInputLayout tlCity, tlGov;

    TextView txtOrderCount;

    @Override
    protected void onResume() {
        super.onResume();
        if (!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    // Disable the Back Button
    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        filterList.clear();

        recyclerView = findViewById(R.id.recyclerView);
        EmptyPanel = findViewById(R.id.EmptyPanel);
        btnBack = findViewById(R.id.btnBack);
        txtOrderCount = findViewById(R.id.txtOrderCount);
        btnAsignAll = findViewById(R.id.btnAsignAll);

        autoGov = findViewById(R.id.autoGov);
        autoCity = findViewById(R.id.autoCity);

        tlCity = findViewById(R.id.tlCity);
        tlGov = findViewById(R.id.tlGov);

        btnAsignAll.setVisibility(View.GONE);

        String[] cities = getResources().getStringArray(R.array.arrayCities);
        for (String city : cities) {
            String[] filterSep = city.split(", ");
            String filterGov = filterSep[0].trim();
            govs.add(filterGov);
        }

        Set<String> set = new HashSet<>(govs);
        govs.clear();
        govs.addAll(set);

        btnAsignAll.setOnClickListener(v -> {
            if (filterList.size() == 0) return;

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("سيتم تسليم جميع الشحنات المصفاه للمندوب، هل انت متاكد ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {

                Intent intent = new Intent(this, AsignOrder.class);
                AsignOrder.assignToCaptin.clear();
                AsignOrder.assignToCaptin.addAll(filterList);
                startActivityForResult(intent, 1);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        ArrayAdapter<String> govAdapter = new ArrayAdapter<>(this, R.layout.autofill_layout, govs);
        autoGov.setAdapter(govAdapter);
        autoGov.setAdapter(govAdapter);

        btnBack.setOnClickListener(v -> finish());

        autoGov.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strGov = "";
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        autoCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                strCity = "";
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        TextView fitlerTitle = findViewById(R.id.toolbar_title);
        fitlerTitle.setText("تصفيه الشحنات");

        tsferAdapter();
        listeners();
    }

    private void setCity() {
        if (strGov.isEmpty()) {
            return;
        }

        filterCity.clear();
        filterCity.trimToSize();
        String[] cities = getResources().getStringArray(R.array.arrayCities);
        autoCity.setText("");

        for (String city : cities) {
            String[] _filterSep = city.split(", ");
            String _filterGov = _filterSep[0].trim();
            String _filterCity = _filterSep[1].trim();
            if (_filterGov.equals(strGov)) {
                filterCity.add(_filterCity);
            }
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.autofill_layout, filterCity);
        autoCity.setAdapter(cityAdapter);
    }

    private void listeners() {
        autoGov.setOnItemClickListener((parent, view, position, id) -> {
            strGov = autoGov.getText().toString().trim();
            setCity();
            getFromList(strGov, strCity);
        });

        autoCity.setOnItemClickListener((parent, view, position, id) -> {
            strCity = autoCity.getText().toString().trim();
            getFromList(strGov, strCity);
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @SuppressLint("SetTextI18n")
    private void getFromList(String gov, String city) {

        if (gov.isEmpty() || city.isEmpty()) {
            return;
        }

        filterList.clear();

        if (what.equals("recive")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filterList = (ArrayList<Order>) mainListm.stream().filter(x -> x.getTxtPState().equals(gov) && x.getmPRegion().equals(city)).collect(Collectors.toList());
            } else {
                for(int i = 0; i < mainListm.size(); i ++) {
                    Order x = mainListm.get(i);
                    if(x.getTxtPState().equals(gov) && x.getmPRegion().equals(city)) {
                        filterList.add(x);
                    }
                }
            }
        } else if (what.equals("drop")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                filterList = (ArrayList<Order>) mainListm.stream().filter(x -> x.getTxtDState().equals(gov) && x.getmDRegion().equals(city)).collect(Collectors.toList());
            } else {
                for(int i = 0; i < mainListm.size(); i ++) {
                    Order x = mainListm.get(i);
                    if(x.getTxtDState().equals(gov) && x.getmDRegion().equals(city)) {
                        filterList.add(x);
                    }
                }
            }

        }

        updateNone(filterList.size());
        if (UserInFormation.getAccountType().equals("Supervisor")) {
            MyAdapter filterAdapter = new MyAdapter(Filters.this, filterList, "Home");
            recyclerView.setAdapter(filterAdapter);
        } else {
            DeliveryAdapter filterAdapter = new DeliveryAdapter(Filters.this, filterList, "Home");
            recyclerView.setAdapter(filterAdapter);
        }
        txtOrderCount.setText("هناك " + filterList.size() + " شحنة في خط سيرك");
    }

    private void tsferAdapter() {
        filterList.clear();
        filterList.trimToSize();
        recyclerView.setAdapter(null);
    }

    private void updateNone(int listSize) {
        if (listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
            if(UserInFormation.getAccountType().equals("Supervisor")) {
                btnAsignAll.setVisibility(View.VISIBLE);
            }
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
            btnAsignAll.setVisibility(View.GONE);
        }
    }


}

