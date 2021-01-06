package com.armjld.rayashipping;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserDatabase {
    private final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());


    public void setValue(String child, String value) {
        uDatabase.child(child).setValue(value);
    }
}
