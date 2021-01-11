package com.armjld.rayashipping;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.models.CaptinMoney;
import com.armjld.rayashipping.models.userData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EnvioMoney {

    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    DatabaseReference Envio = FirebaseDatabase.getInstance().getReference().child("Envio");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    Context mContext;

    public EnvioMoney(Context mContext) {
        this.mContext = mContext;
    }

    // --------- Pay Pack Money For Captin by Super Visor --
    public void payPackMoney(userData user, String walletMoney) {
        uDatabase.child(user.getId()).child("packMoney").setValue("0");

        // ---- Set Payment as Paid
        uDatabase.child(user.getId()).child("payments").orderByChild("isPaid").equalTo("false").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CaptinMoney captinMoney = ds.getValue(CaptinMoney.class);
                        String key = ds.getKey();
                        if (captinMoney.getTransType().equals("ourmoney")) {
                            uDatabase.child(user.getId()).child("payments").child(key).child("isPaid").setValue("true");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        Toast.makeText(mContext, "تم دفع المستحقات بنجاح", Toast.LENGTH_SHORT).show();
    }

    // --------- Pay Bouns For Captin by Super visor --
    public void payBouns(userData user, String walletMoney) {
        uDatabase.child(user.getId()).child("walletmoney").setValue(0);

        // ---- Set Payment as Paid
        uDatabase.child(user.getId()).child("payments").orderByChild("isPaid").equalTo("false").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CaptinMoney captinMoney = ds.getValue(CaptinMoney.class);
                        String key = ds.getKey();
                        assert captinMoney != null;
                        if (!captinMoney.getTransType().equals("ourmoney")) {
                            assert key != null;
                            uDatabase.child(user.getId()).child("payments").child(key).child("isPaid").setValue("true");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        /*// ---- Add Money to Outcome
        int minus = Integer.parseInt(walletMoney) * -1;
        Envio.child("outcome").child("totalOutcome").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    int already = Integer.parseInt(snapshot.getValue().toString());
                    int final_ = already + minus;
                    Envio.child("outcome").child("totalOutcome").setValue(final_);
                } else {
                    Envio.child("outcome").child("totalOutcome").setValue(minus);
                }

                Envio.child("outcome").
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });*/
    }
}
