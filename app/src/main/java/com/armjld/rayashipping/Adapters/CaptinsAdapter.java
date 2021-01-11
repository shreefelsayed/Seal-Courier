package com.armjld.rayashipping.Adapters;

import android.Manifest;
import android.annotation.SuppressLint;
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
import com.armjld.rayashipping.SuperCaptins.MapCaptinTrack;
import com.armjld.rayashipping.SuperCaptins.MyCaptinInfo;
import com.armjld.rayashipping.SuperVisor.AsignOrder;
import com.armjld.rayashipping.SuperVisor.SuperAvillable;
import com.armjld.rayashipping.SuperVisor.SuperRecived;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.userData;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CaptinsAdapter extends RecyclerView.Adapter<CaptinsAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {
    private static final int PHONE_CALL_CODE = 100;
    static Context mContext;
    ArrayList<userData> captinList;
    String from;

    public CaptinsAdapter(Context mContext, ArrayList<userData> captinList, String from) {
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CaptinsAdapter.MyViewHolder holder, int position) {
        userData captin = captinList.get(position);
        holder.txtName.setText(captin.getName());
        holder.txtAddress.setText(captin.getUserCity());

        holder.setData(captin);
        holder.setIcon(captin);

        if (from.equals("info")) {
            holder.btnCall.setVisibility(View.VISIBLE);
            holder.btnMessage.setVisibility(View.VISIBLE);
            holder.btnAsign.setVisibility(View.GONE);
        } else if (from.equals("asign")) {
            holder.btnCall.setVisibility(View.GONE);
            holder.btnMessage.setVisibility(View.GONE);
            holder.btnAsign.setVisibility(View.VISIBLE);
        }

        // ------- Show Captin Orders and Data
        holder.linCaptin.setOnClickListener(v -> {
            MyCaptinInfo.user = captin;
            mContext.startActivity(new Intent(mContext, MyCaptinInfo.class));
        });

        // ------- Chat with Captin
        holder.btnMessage.setOnClickListener(v -> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(UserInFormation.getId(), captin.getId(), mContext);
            Messages.cameFrom = "Profile";
        });

        // ---- Track Captin
        holder.btnTrack.setOnClickListener(v -> {
            if (captin.getTrackId().equals("")) return;
            MapCaptinTrack.user = captin;
            MapCaptinTrack.DEVICE_ID = captin.getTrackId();
            mContext.startActivity(new Intent(mContext, MapCaptinTrack.class));
        });

        // ------- Asign to Captin
        holder.btnAsign.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل تريد تسليم الشحنه للمندوب ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                AssignToCaptin(captin.getId());
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
    }

    private void AssignToCaptin(String capId) {
        OrdersClass ordersClass = new OrdersClass(mContext);
        for (int i = 0; i < AsignOrder.assignToCaptin.size(); i++) {
            Data orderData = AsignOrder.assignToCaptin.get(i);
            switch (orderData.getStatue()) {
                case "placed":
                    if (!orderData.getProvider().equals("Esh7nly")) {
                        ordersClass.assignToCaptin(orderData, capId);
                    } else {
                        ordersClass.bidOnOrder(orderData, capId);
                    }
                    SuperAvillable.orderAdapter.notifyItemChanged(AsignOrder.position);
                    break;
                case "supD":
                    ordersClass.asignDelv(orderData, capId);
                    SuperRecived.orderAdapter.notifyItemChanged(AsignOrder.position);
                    break;
                case "supDenied":
                    ordersClass.asignDenied(orderData, capId);
                    SuperRecived.orderAdapter.notifyItemChanged(AsignOrder.position);
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return captinList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{permission}, requestCode);
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

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public TextView txtName, txtAddress;
        public ImageView imgCaptin, icTrans;
        public ImageView btnCall, btnMessage, btnAsign, btnTrack;
        public LinearLayout linCaptin;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            imgCaptin = myview.findViewById(R.id.imgCaptin);
            btnCall = myview.findViewById(R.id.btnCall);
            btnMessage = myview.findViewById(R.id.btnMessage);
            txtName = myview.findViewById(R.id.txtName);
            btnAsign = myview.findViewById(R.id.btnAsign);
            linCaptin = myview.findViewById(R.id.linCaptin);
            btnTrack = myview.findViewById(R.id.btnTrack);
            txtAddress = myview.findViewById(R.id.txtAddress);
            icTrans = myview.findViewById(R.id.icTrans);
        }

        public void setData(userData user) {
            Picasso.get().load(Uri.parse(user.getPpURL())).into(imgCaptin);
            txtName.setText(user.getName());
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setIcon(userData captin) {
            switch (captin.getTransType()) {
                case "Motor":
                    icTrans.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_vespa));
                    break;
                case "Car":
                    icTrans.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_car));
                    break;
                default:
                    icTrans.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_run));
                    break;
            }
        }
    }
}
