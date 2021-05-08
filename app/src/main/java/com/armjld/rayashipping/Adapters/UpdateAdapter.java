package com.armjld.rayashipping.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.Models.Update;

import java.util.ArrayList;


public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.MyViewHolder>{

    Context mContext;
    ArrayList<Update> updatesList;


    public UpdateAdapter(Context mContext, ArrayList<Update> updatesList) {
        this.mContext = mContext;
        this.updatesList = updatesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_update, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Update update = updatesList.get(position);
        holder.setData(update);
    }


    @Override
    public int getItemCount() { return updatesList.size(); }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        TextView txtTitle, txtTime;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;

            txtTitle = myview.findViewById(R.id.txtTitle);
            txtTime = myview.findViewById(R.id.txtTime);
        }

        public void setData (Update update) {
            caculateTime cacu = new caculateTime();

            txtTitle.setText(update.getMsg());
            txtTime.setText(cacu.setPostDate(update.getDate()));

        }

    }
}