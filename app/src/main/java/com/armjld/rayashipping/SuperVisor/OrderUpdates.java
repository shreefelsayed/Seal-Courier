package com.armjld.rayashipping.SuperVisor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.rayashipping.Adapters.MyAdapter;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.Order;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;

import java.util.ArrayList;

public class OrderUpdates extends AppCompatActivity {

    SmartMaterialSpinner spnUpdate;
    EditText txtNotes;
    Button btnSave;
    ImageView btnBack;
    public static Order orderData;
    RecyclerView recyclerView;
    MyAdapter adminOrdersAdapter;

    ArrayList<Order> arr = new ArrayList<>();
    ArrayList<String> arrUpdates = new ArrayList<>();

    String theUpate = "";
    CheckBox chkNoti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_updates);

        // -- Initalize
        recyclerView = findViewById(R.id.recyclerView);
        spnUpdate = findViewById(R.id.spnUpdate);
        txtNotes = findViewById(R.id.txtNotes);
        btnSave = findViewById(R.id.btnSave);
        btnBack = findViewById(R.id.btnBack);
        chkNoti = findViewById(R.id.chkNoti);

        btnBack.setOnClickListener(v-> finish());

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("عمل ابديت");

        // -- Set the Recycler View
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        // -- Listener on Spiiner
        spnUpdate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                theUpate = arrUpdates.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // -- Save The Order
        btnSave.setOnClickListener(v-> {
            String strMsg = txtNotes.getText().toString().trim();

            if(theUpate.equals("")) {
                Toast.makeText(this, "Please choose an update", Toast.LENGTH_SHORT).show();
                return;
            }

            String finalUpate;
            if(!txtNotes.getText().toString().trim().equals("")) {
                finalUpate = theUpate + " - " + strMsg;
            } else {
                finalUpate = theUpate ;
            }

            sendUpdate(finalUpate);

            finish();
        });

        // -- Main Functions
        setRecycler();
        setSpinner();
    }

    private void sendUpdate(String update) {
        OrdersClass ordersClass = new OrdersClass(this);
        ordersClass.setUpdate(orderData, update, chkNoti.isChecked());
    }

    private void setSpinner() {
        arrUpdates.clear();

        arrUpdates.add("الهاتف مغلق");
        arrUpdates.add("الهاتف مشغول");
        arrUpdates.add("العميل لا يرد");
        arrUpdates.add("في انتظار تاكيد العميل");
        arrUpdates.add("لا يرغب في استلام الشحنه");
        arrUpdates.add("قام بتاجيل المعاد");
        arrUpdates.add("العميل غير موجود");
        arrUpdates.add("تم تحديد معاد للتسليم");

        spnUpdate.setItem(arrUpdates);
    }

    private void setRecycler() {
        arr.clear();
        arr.add(orderData);
        adminOrdersAdapter = new MyAdapter(this, arr, "");
        recyclerView.setAdapter(adminOrdersAdapter);
    }
}