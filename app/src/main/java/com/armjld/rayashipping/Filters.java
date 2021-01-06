package com.armjld.rayashipping;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.armjld.rayashipping.Adapters.MyAdapter;
import com.armjld.rayashipping.Login.LoginManager;
import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.SuperVisor.SuperVisorHome;
import com.armjld.rayashipping.models.Data;
import com.google.android.material.textfield.TextInputLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Filters extends AppCompatActivity {

    private EditText txtFilterMoney;
    ArrayList<Data> filterList = new ArrayList<>();
    RecyclerView recyclerView;
    LinearLayout EmptyPanel;
    int filterValue;
    ImageView btnBack;

    AutoCompleteTextView autoComp,autoCompDrop;
    AutoCompleteTextView autoCityPick, autoCityDrop;

    String pickVar;
    String strPickGov = "";
    String strPickCity = "";
    String dropVar;
    String strDropGov = "";
    String strDropCity = "";

    CheckBox chkPick, chkDrop;

    ArrayList<String> filterCityPick = new ArrayList<>();
    ArrayList<String> filterCityDrop = new ArrayList<>();

    ArrayList<String> govs = new ArrayList<>();

    TextInputLayout tlPickCity, tlDropCity;

    TextView txtOrderCount;

    @Override
    protected void onResume() {
        super.onResume();
        if(!LoginManager.dataset) {
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
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);

        txtFilterMoney = findViewById(R.id.txtFilterMoney);
        recyclerView = findViewById(R.id.recyclerView);
        EmptyPanel = findViewById(R.id.EmptyPanel);
        autoComp = findViewById(R.id.autoComp);
        autoCompDrop = findViewById(R.id.autoCompDrop);
        btnBack = findViewById(R.id.btnBack);

        chkPick = findViewById(R.id.chkPick);
        chkDrop = findViewById(R.id.chkDrop);

        autoCityPick = findViewById(R.id.autoCityPick);
        autoCityDrop = findViewById(R.id.autoCityDrop);

        tlPickCity = findViewById(R.id.tlPickCity);
        tlDropCity = findViewById(R.id.tlDropCity);

        txtOrderCount = findViewById(R.id.txtOrderCount);

        String[] cities = getResources().getStringArray(R.array.arrayCities);
        for (String city : cities) {
            String[] filterSep = city.split(", ");
            String filterGov = filterSep[0].trim();
            govs.add(filterGov);
        }

        Set<String> set = new HashSet<>(govs);
        govs.clear();
        govs.addAll(set);

        ArrayAdapter<String> govAdapter = new ArrayAdapter<>(this, R.layout.autofill_layout, govs);
        autoComp.setAdapter(govAdapter);
        autoCompDrop.setAdapter(govAdapter);

        btnBack.setOnClickListener(v-> finish());

        chkPick.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                tlPickCity.setVisibility(View.GONE);
                autoCityPick.setText("");
                autoCityPick.setAdapter(null);
                strPickCity = "";
            } else {
                tlPickCity.setVisibility(View.VISIBLE);
                setPickCitites();
            }
            getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
        });

        chkDrop.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                tlDropCity.setVisibility(View.GONE);
                autoCityDrop.setText("");
                strDropCity = "";
                autoCityDrop.setAdapter(null);
            } else {
                tlDropCity.setVisibility(View.VISIBLE);
                setDropCities();
            }
            getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
        });

        autoCompDrop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { dropVar = ""; }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoComp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { pickVar = ""; }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoCityPick.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { strPickCity = ""; }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        autoCityDrop.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { strDropCity = ""; }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(false);
        layoutManager.setStackFromEnd(false);
        recyclerView.setLayoutManager(layoutManager);

        TextView fitlerTitle = findViewById(R.id.toolbar_title);
        fitlerTitle.setText("إنشاء خط سير");

        tsferAdapter();
        listeners();
    }

    private void setPickCitites() {
        if(strPickGov.isEmpty()) {
            return;
        }
        filterCityPick.clear();
        filterCityPick.trimToSize();
        String[] cities = getResources().getStringArray(R.array.arrayCities);
        autoCityPick.setText("");
        for (String city : cities) {
            String[] filterSep = city.split(", ");
            String filterGov = filterSep[0].trim();
            String filterCity = filterSep[1].trim();
            if (filterGov.equals(strPickGov)) {
                filterCityPick.add(filterCity);
            }
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.autofill_layout, filterCityPick);
        autoCityPick.setAdapter(cityAdapter);
    }

    private void setDropCities() {
        if(strDropGov.isEmpty()) {
            return;
        }
        autoCityDrop.setText("");
        filterCityDrop.clear();
        filterCityDrop.trimToSize();
        String[] cities = getResources().getStringArray(R.array.arrayCities);
        for (String city : cities) {
            String[] filterSep = city.split(", ");
            String filterGov = filterSep[0].trim();
            String filterCity = filterSep[1].trim();
            if (filterGov.equals(strDropGov)) {
                filterCityDrop.add(filterCity);
            }
        }
        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(this, R.layout.autofill_layout, filterCityDrop);
        autoCityDrop.setAdapter(cityAdapter);
    }

    private void listeners() {
        autoComp.setOnItemClickListener((parent, view, position, id) -> {
            pickVar = autoComp.getText().toString().trim();
            strPickGov = pickVar;
            setPickCitites();
            getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
        });

        autoCompDrop.setOnItemClickListener((parent, view, position, id) -> {
            filterCityDrop.clear();
            dropVar = autoCompDrop.getText().toString();
            strDropGov = dropVar;
            setDropCities();
            getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
        });



        autoCityDrop.setOnItemClickListener((parent, view, position, id) -> {
            strDropCity = autoCityDrop.getText().toString().trim();
            getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
        });

        autoCityPick.setOnItemClickListener((parent, view, position, id) -> {
            strPickCity = autoCityPick.getText().toString().trim();
            getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
        });

        txtFilterMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //getFromList(strPickGov, strPickCity, strDropGov,strDropCity, txtFilterMoney.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
    @Override
    protected void onStart () {
        super.onStart();

    }

    @SuppressLint("SetTextI18n")
    private void getFromList(String spState, String spRegion, String sdState, String sdRegion, String money) {

        if(spState.isEmpty() || sdState.isEmpty()) {
            return;
        }

        filterList.clear();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            if (TextUtils.isEmpty(money) || money.equals("0") || !isNumb(money)) {
                filterValue = 5000000;
            }

            if(isNumb(money) && !money.equals("0") && !TextUtils.isEmpty(money) ) {
                filterValue = Integer.parseInt(money);
            }

            // ------------------------ CHECKING AREAS FILTERS --------------------------//
            if(chkPick.isChecked()) {
                if(chkDrop.isChecked()) {
                    filterList = (ArrayList<Data>) SuperVisorHome.mm.stream().filter(x -> x.getStatue().equals("placed") && Integer.parseInt(x.getGMoney()) <= filterValue && x.getTxtPState().equals(spState) && x.getTxtDState().equals(sdState)).collect(Collectors.toList());
                } else {
                    filterList = (ArrayList<Data>) SuperVisorHome.mm.stream().filter(x -> x.getStatue().equals("placed") && Integer.parseInt(x.getGMoney()) <= filterValue && x.getTxtPState().equals(spState) && x.getmDRegion().equals(sdRegion)).collect(Collectors.toList());
                }
            } else {
                if(chkDrop.isChecked()) {
                    filterList = (ArrayList<Data>) SuperVisorHome.mm.stream().filter(x -> x.getStatue().equals("placed") && Integer.parseInt(x.getGMoney()) <= filterValue && x.getmPRegion().equals(spRegion) && x.getTxtDState().equals(sdState)).collect(Collectors.toList());
                } else {
                    filterList = (ArrayList<Data>) SuperVisorHome.mm.stream().filter(x -> x.getStatue().equals("placed") && Integer.parseInt(x.getGMoney()) <= filterValue && x.getmPRegion().equals(spRegion) && x.getmDRegion().equals(sdRegion)).collect(Collectors.toList());
                }
            }
            updateNone(filterList.size());
            MyAdapter filterAdapter = new MyAdapter(Filters.this, filterList);
            recyclerView.setAdapter(filterAdapter);
            txtOrderCount.setText("هناك " + filterList.size() + " شحنة في خط سيرك");
        }
    }

    private void tsferAdapter() {
        filterList.clear();
        filterList.trimToSize();
        recyclerView.setAdapter(null);
    }

    private void updateNone(int listSize) {
        if(listSize > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }

    public boolean isNumb (String value) {
        return !Pattern.matches("[a-zA-Z]+", value);
    }


}

