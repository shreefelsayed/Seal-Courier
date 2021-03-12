package com.armjld.rayashipping.Captin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.notiData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

public class PartDeliver extends AppCompatActivity {

    Button btnReturnOrders;
    EditText txtOldMoney, txtNewMoney,txtReturns;
    ImageView btnBack;
    public static Data orderData;

    DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_part_deliver);

        if(orderData == null) {
            finish();
            return;
        }

        btnBack = findViewById(R.id.btnBack);
        txtOldMoney = findViewById(R.id.txtOldMoney);
        txtNewMoney = findViewById(R.id.txtNewMoney);
        txtReturns = findViewById(R.id.txtReturns);
        btnReturnOrders = findViewById(R.id.btnReturnOrders);

        btnBack.setOnClickListener(v-> finish());


        txtOldMoney.setText(orderData.getGMoney());

        btnReturnOrders.setOnClickListener(v-> check());
    }

    private void check() {
        String strMoney = txtNewMoney.getText().toString().trim();
        String strNotes = txtReturns.getText().toString().trim();

        if(strMoney.isEmpty()) {
            Toast.makeText(this, "Can't be Empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if(isNumb(strMoney)) {
            Toast.makeText(this, "Not Number", Toast.LENGTH_SHORT).show();
            return;
        }

        int newMoney = Integer.parseInt(strMoney);

        if(newMoney <= 0) {
            Toast.makeText(this, "Can't be Zero", Toast.LENGTH_SHORT).show();
            return;
        }

        if(strNotes.length() == 0) {
            Toast.makeText(this, "Must Type Notes", Toast.LENGTH_SHORT).show();
            return;
        }

        setNewUpdates(strMoney, strNotes);
    }

    private void setNewUpdates(String strMoney, String strNotes) {
        // -- Message
        String message = "تم تسليم شحنه " + orderData.getDName() + " كتسليم جزئي و تم استلام مبلغ " + strMoney + " جنيه من " + orderData.getGMoney() + " جنيه";
        message = message + " و المرتجعات هي : " + strNotes;

        // --- Update Money
        orderData.setGMoney(strMoney);
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");
        mDatabase.child(orderData.getId()).child("gmoney").setValue(strMoney);

        // ------- Update Database ------
        OrdersClass ordersClass = new OrdersClass(this);
        ordersClass.orderDelvered(orderData);

        // ---- Add 15 LE to Fees
        captinRecived.getOrders();

        // ---- Send Notifications to Supplier
        notiData Noti = new notiData(orderData.getuAccepted(), orderData.getuId(), orderData.getId(), message, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
        nDatabase.child(orderData.getuId()).push().setValue(Noti);

        Toast.makeText(this, "تم تسجيل الاوردر كمرتجع جزئي", Toast.LENGTH_SHORT).show();
        finish();
    }

    public boolean isNumb (String value) { return Pattern.matches("[a-zA-Z]+", value); }
}