package com.armjld.rayashipping.Adapters;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Chat.Messages;
import com.armjld.rayashipping.Chat.chatListclass;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Ratings.Ratings;
import com.armjld.rayashipping.SuperVisor.AsignOrder;
import com.armjld.rayashipping.SuperVisor.SuperAvillable;
import com.armjld.rayashipping.SuperVisor.SuperRecived;
import com.armjld.rayashipping.models.Captins;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.userData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class SuperAdapter extends RecyclerView.Adapter<SuperAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {
    Context mContext;
    ArrayList<Captins> captinList;
    String from;
    private static final int PHONE_CALL_CODE = 100;

    public SuperAdapter(Context mContext, ArrayList<Captins> captinList, String from) {
        this.mContext = mContext;
        this.captinList = captinList;
        this.from = from;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_my_captins, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuperAdapter.MyViewHolder holder, int position) {
        Captins captin = captinList.get(position);
        holder.txtName.setText(captin.getName());

        if(from.equals("info")) {
            holder.btnCall.setVisibility(View.VISIBLE);
            holder.btnMessage.setVisibility(View.VISIBLE);
            holder.btnAsign.setVisibility(View.GONE);
        } else if(from.equals("asign")) {
            holder.btnCall.setVisibility(View.GONE);
            holder.btnMessage.setVisibility(View.GONE);
            holder.btnAsign.setVisibility(View.VISIBLE);
        }

        // ------- Show Captin Orders and Data
        holder.linCaptin.setOnClickListener(v-> {
        });

        // ------- Chat with Captin
        holder.btnMessage.setOnClickListener(v-> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(UserInFormation.getId(), captin.getId(), mContext);
            Messages.cameFrom = "Profile";
        });

        // ------- Asign to Captin
        holder.btnAsign.setOnClickListener(v-> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل تريد تسليم الشحنه للمندوب ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                OrdersClass ordersClass = new OrdersClass(mContext);
                if(AsignOrder.type.equals("accept")) {
                    ordersClass.assignToCaptin(captin.getId(), AsignOrder.orderId, AsignOrder.orderOwner, AsignOrder.orderProvider);
                    SuperAvillable.orderAdapter.notifyItemChanged(AsignOrder.position);
                } else if (AsignOrder.type.equals("bid")) {
                    ordersClass.bidOnOrder(AsignOrder.orderOwner, AsignOrder.orderId, AsignOrder.dName, captin.getId(), AsignOrder.orderProvider);
                    SuperAvillable.orderAdapter.notifyItemChanged(AsignOrder.position);
                } else if(AsignOrder.type.equals("delv")) {
                    ordersClass.asignDelv(captin.getId(), AsignOrder.orderId, AsignOrder.orderOwner, AsignOrder.orderProvider);
                    SuperRecived.orderAdapter.notifyItemChanged(AsignOrder.position);
                }
                ((Activity) mContext).finish();
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // ------- Call Intent
        holder.btnCall.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل تريد الاتصال بالمندوب ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {
                checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + captin.getPhone()));
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                mContext.startActivity(callIntent);
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // -------- Get Captin Data -----
        DatabaseReference capRef = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(captin.getId());
        capRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userData user = snapshot.getValue(userData.class);
                assert user != null;

                holder.setData(user);

                // ------------ Ratings ----------- \\
                String one = "0";
                String two = "0";
                String three = "0";
                String four = "0";
                String five = "0";
                if(snapshot.child("rating").child("one").exists()) {
                    one = Objects.requireNonNull(snapshot.child("rating").child("one").getValue()).toString();
                }
                if(snapshot.child("rating").child("two").exists()) {
                    two = Objects.requireNonNull(snapshot.child("rating").child("two").getValue()).toString();
                }
                if(snapshot.child("rating").child("three").exists()) {
                    three = Objects.requireNonNull(snapshot.child("rating").child("three").getValue()).toString();
                }
                if(snapshot.child("rating").child("four").exists()) {
                    four = Objects.requireNonNull(snapshot.child("rating").child("four").getValue()).toString();
                }
                if(snapshot.child("rating").child("five").exists()) {
                    five = Objects.requireNonNull(snapshot.child("rating").child("five").getValue()).toString();
                }
                Ratings _rat = new Ratings();
                holder.captinRatings.setRating(_rat.calculateRating(one,two,three,four,five));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    @Override
    public int getItemCount() {
        return captinList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public TextView txtName;
        public ImageView imgCaptin;
        public RatingBar captinRatings;
        public ImageView btnCall, btnMessage, btnAsign;
        public LinearLayout linCaptin;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;

            imgCaptin = myview.findViewById(R.id.imgCaptin);
            captinRatings = myview.findViewById(R.id.captinRatings);
            btnCall = myview.findViewById(R.id.btnCall);
            btnMessage = myview.findViewById(R.id.btnMessage);
            txtName = myview.findViewById(R.id.txtName);
            btnAsign = myview.findViewById(R.id.btnAsign);
            linCaptin = myview.findViewById(R.id.linCaptin);
        }

        public void setData(userData user) {
            Picasso.get().load(Uri.parse(user.getPpURL())).into(imgCaptin);
            txtName.setText(user.getName());
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
