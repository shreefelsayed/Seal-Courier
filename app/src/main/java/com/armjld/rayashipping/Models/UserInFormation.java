package com.armjld.rayashipping.Models;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserInFormation {

    public static UserData user;

    public static UserData getUser() {
        return user;
    }

    public static void setUser(UserData user) {
        UserInFormation.user = user;
    }


    public static void getUserInformation() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if(mAuth.getCurrentUser() == null) {
            clearUser();
        }

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("user")
                .child(mAuth.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user = snapshot.getValue(UserData.class);
                setUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }


    public static void clearUser() {
        setUser(null);
    }

}