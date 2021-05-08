package com.armjld.rayashipping.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.rateData;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class ratingAdapter extends RecyclerView.Adapter<ratingAdapter.MyViewHolder> {

    Context context;
    ArrayList<rateData> rating;

    public ratingAdapter(Context context, ArrayList<rateData> rating) {
        this.context = context;
        this.rating = rating;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.card_ratings,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        rateData theRate = rating.get(position);

        holder.txtComment.setText(rating.get(position).getComment());
        holder.rbRate.setRating(theRate.getRate());


        // ------- Get Commented User Data ------ \\
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(theRate.getsId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user = snapshot.getValue(UserData.class);

                assert user != null;

                holder.txtName.setText(user.getName());
                Picasso.get().load(user.getPpURL()).into(holder.imgUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    @Override
    public int getItemCount() { return rating.size(); }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View view;
        ImageView imgUser;
        TextView txtName,txtComment;
        RatingBar rbRate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            imgUser = view.findViewById(R.id.imgUser);
            txtName = view.findViewById(R.id.txtName);
            txtComment = view.findViewById(R.id.txtComment);
            rbRate = view.findViewById(R.id.rbRate);

        }
    }
}


