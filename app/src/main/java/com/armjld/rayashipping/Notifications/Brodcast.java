package com.armjld.rayashipping.Notifications;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.notiData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class Brodcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String orderID = intent.getStringExtra("orderid");

        assert orderID != null;
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Data data = snapshot.getValue(Data.class);
                assert data != null;
                if (data == null) {
                    Toast.makeText(context, "Data is Null", Toast.LENGTH_SHORT).show();
                    return;
                }
                String owner = data.getuId();
                if (data.getStatue().equals("recived")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
                    String datee = sdf.format(new Date());

                    FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderID).child("statue").setValue("recived2");
                    FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderID).child(orderID).child("recived2Time").setValue(datee);
                    String message = "قام " + UserInFormation.getUserName() + " بتأكد استلام الاوردر";
                    notiData Noti = new notiData(UserInFormation.getId(), owner, orderID, message, datee, "false", "profile", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
                    FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests").child(owner).push().setValue(Noti);
                    Toast.makeText(context, "تم تأكيد استلام الشحنة", Toast.LENGTH_SHORT).show();
                }

                NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }


}
