package com.armjld.rayashipping.Chat;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.Chat;
import com.armjld.rayashipping.Models.UserInFormation;
import com.squareup.picasso.Picasso;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    int MSG_RIGHT = 1;
    int MSG_LEFT = 0;
    String TAG = "Message Adapter";

    Context context;
    List<Chat> chatData;
    String uId = UserInFormation.getId();


    public MessageAdapter(Context context, List<Chat> chatData) {
        this.context = context;
        this.chatData = chatData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MSG_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String strTime = chatData.get(position).getTimestamp().substring(11, 16);
        holder.txtTime.setText(strTime);

        if (chatData.get(position).getType().equals("pic")) {
            holder.txtMsg.setVisibility(View.GONE);
            holder.imgMsg.setVisibility(View.VISIBLE);
            Picasso.get().load(Uri.parse(chatData.get(position).getUrl())).into(holder.imgMsg);
        } else {
            holder.txtMsg.setVisibility(View.VISIBLE);
            holder.imgMsg.setVisibility(View.GONE);
            holder.txtMsg.setText(chatData.get(position).getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return this.chatData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (chatData.get(position).getSenderid().equals(uId)) {
            return MSG_RIGHT;
        } else {
            return MSG_LEFT;
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtMsg, txtTime;
        ImageView imgMsg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMsg = itemView.findViewById(R.id.txtMsg);
            txtTime = itemView.findViewById(R.id.txtTime);
            imgMsg = itemView.findViewById(R.id.imgMsg);
        }
    }
}