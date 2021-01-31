package com.armjld.rayashipping.SuperCaptins;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.rayashipping.EnvioMoney;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Wallet;
import com.armjld.rayashipping.models.userData;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

public class MyCaptinAddBouns extends AppCompatActivity {

    EditText txtAddMoney;
    Button btnAddBouns;
    TextInputLayout tlAddMoney;
    ImageView btnBack;
    public static userData user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_captin_add_bouns);

        btnAddBouns = findViewById(R.id.btnAddBouns);
        txtAddMoney = findViewById(R.id.txtAddMoney);
        tlAddMoney = findViewById(R.id.tlAddMoney);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v-> finish());

        // --- Toolbar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("إضافه بونص");

        // --- Add Money
        btnAddBouns.setOnClickListener(v-> {
            String money = txtAddMoney.getText().toString();

            if(isNumb(money)) {
                tlAddMoney.setError("ادخل مبلغ صحيح");
                return;
            }

            int intMoney = Integer.parseInt(money);

            if(intMoney < 1) {
                tlAddMoney.setError("اقل مبلغ يمكن اضافته هو ١ جنيه");
                return;
            }

            Wallet wallet = new Wallet();
            wallet.addBouns(user.getId(), intMoney);
            Toast.makeText(this, "تم اضافه المبلغ الي المندوب", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    public boolean isNumb (String value) {
        return Pattern.matches("[a-zA-Z]+", value);
    }

}