package com.armjld.rayashipping;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.userData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UsersClass {

    Context mContext;
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    public UsersClass(Context mContext) {
        this.mContext = mContext;
    }

    public void unActiveCaptin(userData user) {
        if(!user.getAccountType().equals("Delivery Worker")) {
            Toast.makeText(mContext, "لا يوجد لديك صلاحيه لاغلاق هذا الحساب", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog mDialog = new ProgressDialog(mContext);

        mDialog.setMessage("جاري ايقاف حساب المندوب ..");
        mDialog.show();
        mDialog.setCancelable(false);

        String supId = user.getMySuperId();

        // --- Update data of in Captin's DB
        uDatabase.child(user.getId()).child("active").setValue("false");
        uDatabase.child(user.getId()).child("mySuper").setValue("");
        uDatabase.child(user.getId()).child("mySuperId").setValue("");
        uDatabase.child(user.getId()).child("trackId").setValue("");

        // --- Update date in Supervisor's DB
        uDatabase.child(supId).child("captins").child(user.getId()).removeValue();

        // --- get all Orders from Captin --
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef("Raya");

        // --- Add To Wallet
        EnvioMoney envioMoney = new EnvioMoney(mContext);
        envioMoney.payPackMoney(user, user.getPackMoney());

        // --- Loop Order
        mDatabase.orderByChild("uAccepted").equalTo(user.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Data orderData = ds.getValue(Data.class);
                        assert orderData != null;
                        switch (orderData.getStatue()) {
                            case "accepted": // if order set to pick up by a captin, reutrn it to supervisor
                                mDatabase.child(orderData.getId()).child("statue").setValue("placed");
                                mDatabase.child(orderData.getId()).child("uAccepted").setValue("");
                                break;
                            case "readyD": // if order set to be droped by captin, return it to supervisor
                                mDatabase.child(orderData.getId()).child("statue").setValue("supD");
                                mDatabase.child(orderData.getId()).child("uAccepted").setValue("");
                                break;
                            case "capDenied": // if order set to return to supplier, return it to supervisor
                                mDatabase.child(orderData.getId()).child("statue").setValue("supDenied");
                                mDatabase.child(orderData.getId()).child("uAccepted").setValue("");
                                break;
                        }
                    }
                }

                mDialog.dismiss();
                Toast.makeText(mContext, "تم إيقاف حساب الكابتن", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
