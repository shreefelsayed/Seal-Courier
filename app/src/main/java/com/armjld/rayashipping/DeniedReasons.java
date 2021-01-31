package com.armjld.rayashipping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.rayashipping.Captin.captinRecived;
import com.armjld.rayashipping.Login.LoginManager;
import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.models.Data;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.google.firebase.database.FirebaseDatabase.getInstance;

public class DeniedReasons extends AppCompatActivity {

    public static int pos;
    private RadioButton rd1,rd2,rd3,rd4;
    private String Msg = "";
    private EditText txtContact;
    FirebaseAuth mAuth;
    Button btnSend;
    public static Data orderData;
    CheckBox chkRecive;
    EditText txtDDate;

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya");
    @Override
    protected void onResume() {
        super.onResume();
        if(!LoginManager.dataset) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_denied_reasons);

        rd1 = findViewById(R.id.rd1);
        rd2 = findViewById(R.id.rd2);
        rd3 = findViewById(R.id.rd3);
        rd4 = findViewById(R.id.rd4);
        txtDDate = findViewById(R.id.txtDDate);
        chkRecive = findViewById(R.id.chkRecive);



        txtContact = findViewById(R.id.txtContact);
        RadioGroup rdGroup = findViewById(R.id.rdGroup);
        mAuth = FirebaseAuth.getInstance();
        btnSend = findViewById(R.id.btnSend);

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("سبب المرتجع");

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v-> finish());

        rdGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if(checkedId == R.id.rd3) {
                txtContact.setVisibility(View.VISIBLE);
            } else {
                txtContact.setVisibility(View.GONE);
            }
        });

        txtDDate.setText(orderData.getDDate());

        if(Integer.parseInt(orderData.getTries()) == 1) {
            txtDDate.setVisibility(View.GONE);
        }

        // ------------------ Set Pick Date --------------------- //

        final DatePickerDialog.OnDateSetListener dDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker dateView, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(Calendar.YEAR, year);
                newDate.set(Calendar.MONTH, monthOfYear);
                newDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(newDate);
            }

            private void updateLabel(Calendar newDate) {
                String dFormat = "yyyy-MM-dd";
                SimpleDateFormat sDF = new SimpleDateFormat(dFormat, Locale.ENGLISH);
                txtDDate.setText(sDF.format(newDate.getTime()));
            }
        };

        txtDDate.setOnClickListener(v -> {
            DatePickerDialog dpd = new DatePickerDialog(DeniedReasons.this, dDate, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
            DatePicker dp = dpd.getDatePicker();
            long now = Calendar.getInstance().getTimeInMillis() - 100;
            dp.setMinDate(now);
            dp.setMaxDate((now + (1000*60*60*24*4)));
            dpd.show();
        });

        btnSend.setOnClickListener(v -> {
            if(rd1.isChecked()) {
                Msg = rd1.getText().toString();
            } else if (rd2.isChecked()) {
                Msg = rd2.getText().toString();
            } else if(rd4.isChecked()) {
                Msg = rd4.getText().toString();
            } else if(rd3.isChecked()) {
                if(txtContact.getText().toString().isEmpty()) {
                    Toast.makeText(this, "الرجاء توضيح سبب عدم الاستلام", Toast.LENGTH_SHORT).show();
                    return;
                }
                Msg = txtContact.getText().toString();
            }

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(this).setMessage("هل انت متاكد من انك تريد حذف الاوردر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_delete_white, (dialogInterface, which) -> {
                OrdersClass ordersClass = new OrdersClass(this);
                ordersClass.orderDenied(orderData, Msg);

                setNewData();
                // ----- Update Adapter
                orderData.setStatue("denied");
                captinRecived.getOrders();

                finish();
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });
    }

    private void setNewData() {
        // -- Set New Date
        mDatabase.child(orderData.getId()).child("ddate").setValue(txtDDate.getText().toString().trim());

        if(chkRecive.isChecked()) {
            //--- Make order as Paid and check if it's paid already and remove the money
            OrdersClass ordersClass = new OrdersClass(this);
            ordersClass.orderPaid(orderData);

        }

    }
}