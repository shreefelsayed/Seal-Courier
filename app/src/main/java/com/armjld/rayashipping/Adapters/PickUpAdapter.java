package com.armjld.rayashipping.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Models.UserData;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.caculateTime;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PickUpAdapter extends RecyclerView.Adapter<PickUpAdapter.MyViewHolder>  {
    Context mContext;
    ArrayList<UserData> pickUps;

    public PickUpAdapter(Context mContext, ArrayList<UserData> pickUps) {
        this.mContext = mContext;
        this.pickUps = pickUps;
    }

    @NonNull
    @Override
    public PickUpAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view  = inflater.inflate(R.layout.card_pickups,parent,false);
        return new PickUpAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PickUpAdapter.MyViewHolder holder, int position) {
        UserData pick = pickUps.get(position);

        holder.setData(pick);

    }

    @Override
    public int getItemCount() { return pickUps.size(); }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        View view;

        ImageView imgUser;
        TextView txtName, txtDate, txtOrdersCount;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            imgUser = view.findViewById(R.id.img_user);
            txtName = view.findViewById(R.id.txtName);
            txtDate = view.findViewById(R.id.txtDate);
            txtOrdersCount = view.findViewById(R.id.txtOrdersCount);
        }

        @SuppressLint("SetTextI18n")
        public void setData(UserData pick) {
            caculateTime _cac = new caculateTime();
            Picasso.get().load(pick.getPpURL()).into(imgUser);
            txtName.setText(pick.getName());
            txtOrdersCount.setText(pick.getOrdersCount() + "");
        }
    }
}
