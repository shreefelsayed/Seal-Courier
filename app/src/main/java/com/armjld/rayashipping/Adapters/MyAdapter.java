package com.armjld.rayashipping.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.CopyingData;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.AsignOrder;
import com.armjld.rayashipping.SuperVisor.OrderInfo;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    public static caculateTime _cacu = new caculateTime();
    Context mContext;
    ArrayList<Order> filtersData;
    String from;

    public MyAdapter(Context mContext, ArrayList<Order> filtersData, String from) {
        this.mContext = mContext;
        this.filtersData = filtersData;
        this.from = from;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(mContext).inflate(R.layout.card_new_supervisor, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Order orderData = filtersData.get(position);
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(filtersData.get(position).getProvider());

        // Get Post Date
        String orderID = filtersData.get(position).getId().trim();
        String type = filtersData.get(position).getType();
        holder.setOrdercash(filtersData.get(position).getGMoney());
        holder.setOrderFrom(filtersData.get(position));
        holder.setOrderto(filtersData.get(position));
        holder.setPostDate(filtersData.get(position).getDate());
        holder.setState(filtersData.get(position));
        holder.setData(orderData);

        // ----------- Listener to Hide Buttons when order deleted or became accepted ------------ //
        mDatabase.child(orderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Order orderData = snapshot.getValue(Order.class);

                // ------- if some other supervisor accepted the order
                assert orderData != null;
                if (!orderData.getStatue().equals("placed") && !orderData.getStatue().equals("supD") && !orderData.getStatue().equals("supDenied")) {
                    holder.btnAccept.setVisibility(View.GONE);
                    holder.btnMore.setVisibility(View.GONE);

                    if (snapshot.child("dSupervisor").exists() && orderData.getStatue().equals("supD")) {
                        if (orderData.getdSupervisor().equals(UserInFormation.getUser().getId())) {
                            holder.btnAccept.setVisibility(View.VISIBLE);
                            holder.btnMore.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


        holder.txtOrderTo.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Address", holder.txtOrderTo.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "تم نسخ عنوان التسليم", Toast.LENGTH_LONG).show();
        });

        holder.txtOrderFrom.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Address", holder.txtOrderFrom.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "تم نسخ عنوان الاستلام", Toast.LENGTH_LONG).show();
        });

        holder.txtTrackId.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TrackId", holder.txtTrackId.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "تم نسخ رقم الشحنه", Toast.LENGTH_LONG).show();
        });

        holder.btnMore.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, OrderInfo.class);
            OrderInfo.orderData = filtersData.get(position);
            intent.putExtra("position", position);
            intent.putExtra("from", from);
            ((Activity) mContext).startActivityForResult(intent, 1);
        });

        holder.btnCopyData.setOnClickListener(v-> {
            PopupMenu popup = new PopupMenu(mContext, holder.btnCopyData);
            CopyingData copyingData = new CopyingData(mContext);
            popup.inflate(R.menu.copy_menu);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.reciver:
                        copyingData.getReciverData(orderData);
                        return true;
                    case R.id.Sender:
                        copyingData.getPickUp(orderData);
                        return true;
                    case R.id.order:
                        copyingData.getOrderData(orderData);
                        return true;
                    default:
                        return false;
                }
            });
            popup.show();
        });


        holder.btnAccept.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, AsignOrder.class);
            AsignOrder.assignToCaptin.clear();
            AsignOrder.assignToCaptin.add(filtersData.get(position));
            ((Activity) mContext).startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return filtersData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public View myview;
        public Button btnMore, btnAccept;
        public TextView txtgMoney, txtOrderFrom, txtOrderTo, txtPostDate, txtUsername, txtTrackId;
        public TextView txtStatue;
        public ImageView btnCopyData;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            btnMore = myview.findViewById(R.id.btnMore);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtOrderFrom = myview.findViewById(R.id.OrderFrom);
            txtOrderTo = myview.findViewById(R.id.orderto);
            txtPostDate = myview.findViewById(R.id.txtPostDate);
            btnAccept = myview.findViewById(R.id.btnAccept);
            txtUsername = myview.findViewById(R.id.txtUsername);
            txtTrackId = myview.findViewById(R.id.txtTrackId);
            txtStatue = myview.findViewById(R.id.txtStatue);
            btnCopyData = myview.findViewById(R.id.btnCopyData);
        }

        public void setOrderFrom(Order oData) {
            txtOrderFrom.setText(oData.getTxtPState() + " - " + oData.getmPRegion() + " - " + oData.getmPAddress());
        }

        public void setOrderto(Order oData) {
            txtOrderTo.setText(oData.getTxtDState() + " - " + oData.getmDRegion() + " - " + oData.getDAddress());
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash) {
            txtgMoney.setText(ordercash + " ج");
        }

        public void setPostDate(String startDate) {
            txtPostDate.setText(_cacu.setPostDate(startDate));
        }


        public void setState(Order orderData) {
            switch (orderData.getStatue()) {
                case "placed":
                    btnAccept.setVisibility(View.VISIBLE);
                    btnMore.setVisibility(View.VISIBLE);

                    txtStatue.setText("قيد الاستلام");
                    txtStatue.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));

                    if(!orderData.getpHubName().equals("")) txtOrderTo.setText(orderData.getmDRegion() + " - " + orderData.getDAddress() + " (" + orderData.getpHubName() + " )");
                    break;

                case "supD":
                    btnAccept.setVisibility(View.VISIBLE);
                    btnMore.setVisibility(View.VISIBLE);

                    txtStatue.setText("قيد التسليم");
                    txtStatue.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));

                    if(!orderData.getdHubName().equals("")) txtOrderFrom.setText(orderData.getdHubName());
                    break;
                case "supDenied":
                    btnAccept.setVisibility(View.VISIBLE);
                    btnMore.setVisibility(View.VISIBLE);

                    txtStatue.setText("مرتجع للتاجر");
                    txtStatue.setBackgroundTintList(ColorStateList.valueOf(Color.MAGENTA));

                    if(!orderData.getdHubName().equals("")) txtOrderFrom.setText(orderData.getpHubName());
                    txtOrderTo.setText(orderData.reStateP());
                    break;
                default:
                    txtStatue.setVisibility(View.GONE);
                    btnAccept.setVisibility(View.GONE);
                    btnMore.setVisibility(View.GONE);
                    break;
            }

        }

        public void setData(Order orderData) {
            txtTrackId.setText(orderData.getTrackid());
            txtUsername.setText(orderData.getOwner());
        }
    }

}