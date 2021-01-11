package com.armjld.rayashipping;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class getRefrence {

    public DatabaseReference getRef(String provider) {
        if (provider.equals("Raya")) {
            return FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya");
        } else {
            return FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders");
        }
    }

}
