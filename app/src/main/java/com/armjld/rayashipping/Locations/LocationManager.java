package com.armjld.rayashipping.Locations;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.armjld.rayashipping.Models.LocationDataType;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class LocationManager {
    public static ArrayList<LocationDataType> locHolder = new ArrayList<LocationDataType>();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference Bdatabase;
    private int index = 0;

    public ArrayList<LocationDataType> getLocHolder() {
        return locHolder;
    }

    public void setLocHolder(ArrayList<LocationDataType> locHolder) {
        LocationManager.locHolder = locHolder;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean add(LocationDataType loc, String id) {

        if (loc == null) {
            return false;
        } else {
            Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getUser().getId()).child("locations").child(id);
            Bdatabase.child("lattude").setValue(loc.getLattude());
            Bdatabase.child("lontude").setValue(loc.getLontude());
            Bdatabase.child("address").setValue(loc.getAddress());
            Bdatabase.child("region").child(loc.getRegion());
            Bdatabase.child("state").child(loc.getState());
            Bdatabase.child("title").child(loc.getTitle());
            Bdatabase.child("id").setValue(loc.getId());
            Log.i("Location Manager", "Added Location Successfully");
            return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean Remove(String id) {
        LocationDataType loc = null;
        loc = locHolder.stream().filter(x -> x.getId().equals(id)).findFirst().get();

        locHolder.remove(loc);
        String uId = UserInFormation.getUser().getId();

        if (uId == null || uId.isEmpty()) {
            return false;
        }

        Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("locations").child(id);
        Bdatabase.removeValue();
        return true;

    }

    public void ImportLocation() {
        locHolder.clear();
        locHolder.trimToSize();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            return;
        }
        Bdatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(user.getUid()).child("locations");
        Bdatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        LocationDataType loc = ds.getValue(LocationDataType.class);
                        locHolder.add(loc);
                        Log.i("Location Manager", " Added New Location");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}
