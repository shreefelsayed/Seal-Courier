package com.armjld.rayashipping.SuperCaptins;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Adapters.ratingAdapter;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.rateData;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CaptinsReviews extends AppCompatActivity {

    public static UserData user;
    DatabaseReference uDatabase,mDatabase;
    ArrayList<rateData> allRatings = new ArrayList<>();
    ratingAdapter _ratingAdapter;
    ImageView btnBack;
    RecyclerView ratingRecycler;
    TextView txtEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captins_reviews);

        ratingRecycler = findViewById(R.id.ratingRecycler);
        btnBack = findViewById(R.id.btnBack);
        txtEmpty = findViewById(R.id.txtEmpty);

        //Recycler
        ratingRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        ratingRecycler.setLayoutManager(layoutManager);

        // ----- Set toolbar Title ------- \\
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("التعليقات");

        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya");

        btnBack.setOnClickListener(v-> finish());

        getRatings();
    }

    private void getRatings() {
        uDatabase.child(user.getId()).child("comments").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allRatings.clear();
                allRatings.trimToSize();

                if(snapshot.exists()) {
                    for(DataSnapshot comments : snapshot.getChildren()) {
                        rateData ratings = comments.getValue(rateData.class);
                        assert ratings != null;

                        // -- Only Comments
                        if(!ratings.getComment().trim().equals("")) {
                            allRatings.add(ratings);
                        }

                    }
                }

                // --- Set Adapter
                _ratingAdapter = new ratingAdapter(CaptinsReviews.this, allRatings);
                ratingRecycler.setAdapter(_ratingAdapter);
                UpdateUI();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void UpdateUI() {
        //Toast.makeText(this, "" + allRatings.size(), Toast.LENGTH_SHORT).show();
        if(allRatings.size() > 0) {
            txtEmpty.setVisibility(View.GONE);
        } else {
            txtEmpty.setVisibility(View.VISIBLE);
        }
    }
}