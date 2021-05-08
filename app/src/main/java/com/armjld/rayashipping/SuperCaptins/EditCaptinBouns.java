package com.armjld.rayashipping.SuperCaptins;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditCaptinBouns extends AppCompatActivity {

    public static UserData user;
    ImageView btnBack;
    Button btnEdit;
    EditText txtDeliver, txtPickUp,txtDenied;
    DatabaseReference uDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_captin_bouns);

        if(user == null) {
            finish();
            return;
        }

        // --- Toolbar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("العمولات");

        btnBack = findViewById(R.id.btnBack);
        btnEdit = findViewById(R.id.btnEdit);
        txtDeliver = findViewById(R.id.txtDeliver);
        txtPickUp = findViewById(R.id.txtPickUp);
        txtDenied = findViewById(R.id.txtDenied);

        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getId());

        btnBack.setOnClickListener(v-> finish());

        btnEdit.setOnClickListener(v-> {
            if(txtDeliver.getText().toString().isEmpty() ||
                    txtPickUp.getText().toString().isEmpty()
                    || txtDenied.getText().toString().isEmpty()) {
                return;
            }

            int delv = Integer.parseInt(txtDeliver.getText().toString().trim());
            int pick = Integer.parseInt(txtPickUp.getText().toString().trim());
            int deny = Integer.parseInt(txtDenied.getText().toString().trim());

            user.setDeliverMoney(delv);
            user.setPickUpMoney(pick);
            user.setDeniedMoney(deny);

            uDatabase.child("pickUpMoney").setValue(pick);
            uDatabase.child("deniedMoney").setValue(deny);
            uDatabase.child("deliverMoney").setValue(delv);

            Toast.makeText(this, "تم تعديل العموله للمندوب", Toast.LENGTH_SHORT).show();
            finish();
        });

        setData();
    }

    private void setData() {
        txtDeliver.setText(user.getDeliverMoney() + "");
        txtPickUp.setText(user.getPickUpMoney() + "");
        txtDenied.setText(user.getDeniedMoney() + "");
    }
}