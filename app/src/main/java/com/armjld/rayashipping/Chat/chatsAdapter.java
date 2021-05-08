package com.armjld.rayashipping.Chat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.Models.Chat;
import com.armjld.rayashipping.Models.ChatsData;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class chatsAdapter extends RecyclerView.Adapter<chatsAdapter.MyViewHolder> {

    public static caculateTime _cacu = new caculateTime();
    private final DatabaseReference messageDatabase, uDatabase;
    Context context;
    ArrayList<ChatsData> chatData;
    boolean unRead = false;
    String TAG = "Chat Adapter";

    public chatsAdapter(Context context, ArrayList<ChatsData> chatData) {
        this.context = context;
        this.chatData = chatData;
        messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_chat, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        ChatsData chat = chatData.get(position);
        String talkerID = chat.getUserId();
        Log.i(TAG, talkerID);

        holder.myview.setOnClickListener(v -> {
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getUser().getId()).child("chats").child(chat.getUserId()).child("newMsg").setValue("false");
            Intent intent = new Intent(context, Messages.class);
            intent.putExtra("roomid", chat.getRoomid());
            intent.putExtra("rid", chat.getUserId());
            context.startActivity(intent);
            Messages.cameFrom = "Chats";
            ChatFragmet.cameFrom = "Chat";
            if (unRead) {
                Home.msgCount--;
                if (Home.msgCount != 0) {
                    Home.chatsBadge.setNumber(Home.msgCount);
                } else {
                    Home.bottomNavigationView.removeBadge(R.id.chats);
                }
            }
        });

        uDatabase.child(talkerID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String imgURL = Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString();
                holder.txtName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                Picasso.get().load(Uri.parse(imgURL)).into(holder.imgEditPhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        messageDatabase.child(chat.getRoomid()).orderByChild("timestamp").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Chat chatS = snapshot.getValue(Chat.class);
                assert chatS != null;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    holder.txtBody.setText(Objects.requireNonNull(ds.child("msg").getValue()).toString());
                    String startDate = Objects.requireNonNull(ds.child("timestamp").getValue()).toString();
                    holder.txtNotidate.setText(_cacu.setPostDate(startDate));

                    if (Objects.requireNonNull(ds.child("newMsg").getValue()).toString().equals("true") && !ds.child("senderid").getValue().toString().equals(UserInFormation.getUser().getId())) {
                        unRead = true;
                        holder.indec.setVisibility(View.VISIBLE);
                    } else {
                        holder.indec.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }

    @Override
    public int getItemCount() {
        return chatData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtBody, txtNotidate;
        ImageView imgEditPhoto, indec;
        View myview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            indec = itemView.findViewById(R.id.indec);
            txtName = itemView.findViewById(R.id.txtName);
            txtBody = itemView.findViewById(R.id.txtBody);
            txtNotidate = itemView.findViewById(R.id.txtNotidate);
            imgEditPhoto = itemView.findViewById(R.id.imgEditPhoto);
        }
    }
}