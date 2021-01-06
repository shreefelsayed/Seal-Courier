package com.armjld.rayashipping;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class Wallet {

    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    public void addRecivedMoney(String id) {
        uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("walletmoney").exists()) {
                    float currentValue = Float.parseFloat(snapshot.child("walletmoney").getValue().toString());
                    uDatabase.child(id).child("walletmoney").setValue(currentValue + 7.5);
                } else {
                    uDatabase.child(id).child("walletmoney").setValue(7.5);
                }
                Timber.i("Added Money to Wallet");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void addDelvMoney(String id) {
        uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("walletmoney").exists()) {
                    float currentValue = Float.parseFloat(snapshot.child("walletmoney").getValue().toString());
                    uDatabase.child(id).child("walletmoney").setValue(currentValue + 15);
                } else {
                    uDatabase.child(id).child("walletmoney").setValue(15);
                }
                Timber.i("Added Money to Wallet");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void addMoneyToShreef(String orderId) {
        DatabaseReference walletRef = FirebaseDatabase.getInstance().getReference().child("Pickly").child("MyWallet");
        walletRef.child("Raya").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    if(snapshot.child("totalMoney").exists()) {
                        float currentValue = Float.parseFloat(snapshot.child("totalMoney").getValue().toString());
                        walletRef.child("Raya").child("totalMoney").setValue(currentValue + 5);
                    } else {
                        walletRef.child("Raya").child("totalMoney").setValue(5);
                    }
                } else {
                    walletRef.child("Raya").child("totalMoney").setValue(5);
                }

                walletRef.child("Raya").child("orders").child(orderId).child("id").setValue(orderId);
                walletRef.child("Raya").child("orders").child(orderId).child("date").setValue(datee);

                Timber.i("Added Money to Wallet");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
