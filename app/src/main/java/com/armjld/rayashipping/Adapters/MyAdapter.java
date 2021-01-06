package com.armjld.rayashipping.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.AsignOrder;
import com.armjld.rayashipping.SuperVisor.OrderInfo;
import com.armjld.rayashipping.SuperVisor.SuperVisorHome;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.getRefrence;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.rquests;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

     Context context;
     ArrayList<Data>filtersData;
     public static caculateTime _cacu = new caculateTime();

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    public MyAdapter(Context context, ArrayList<Data> filtersData) {
        this.context = context;
        this.filtersData =filtersData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view  = LayoutInflater.from(context).inflate(R.layout.card_main,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(filtersData.get(position).getProvider());

        // Get Post Date
        String orderID = filtersData.get(position).getId().trim();
        String owner = filtersData.get(position).getuId();
        String type = filtersData.get(position).getType();
        holder.setDate(filtersData.get(position).getDDate(), filtersData.get(position).getpDate());
        holder.setOrdercash(filtersData.get(position).getGMoney());
        holder.setOrderFrom(filtersData.get(position).reStateP());
        holder.setOrderto(filtersData.get(position).reStateD());
        holder.setFee(filtersData.get(position).getGGet());
        holder.setPostDate(filtersData.get(position).getDate());
        holder.setBid(type);

        holder.switchLayout(filtersData.get(position).getProvider());

        // ----------- Listener to Hide Buttons when order deleted or became accepted ------------ //
        mDatabase.child(orderID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Data orderData = snapshot.getValue(Data.class);

                // ------- if some other supervisor accepted the order
                if (!orderData.getStatue().equals("placed") && !orderData.getStatue().equals("supD")) {
                    holder.btnBid.setVisibility(View.GONE);
                    holder.btnAccept.setVisibility(View.GONE);
                    holder.btnMore.setVisibility(View.GONE);
                    holder.txtWarning.setVisibility(View.VISIBLE);
                    holder.txtWarning.setBackgroundColor(Color.RED);
                    holder.txtWarning.setText("الاوردر تم قبولة بالفعل من مشرف اخر");

                    if(snapshot.child("dSupervisor").exists() && orderData.getStatue().equals("supD")) {
                        if(snapshot.child("dSupervisor").getValue().toString().equals(UserInFormation.getId())) {
                            holder.btnAccept.setVisibility(View.VISIBLE);
                            holder.btnMore.setVisibility(View.VISIBLE);
                            holder.txtWarning.setVisibility(View.GONE);
                        }
                    }
                    // ------- if i accepted the order but didn't refresh
                    if(SuperVisorHome.mCaptinsIDS.contains(Objects.requireNonNull(snapshot.child("uAccepted").getValue()).toString())) {
                        holder.txtWarning.setText("تم قبول الاوردر");
                        holder.txtWarning.setBackgroundColor(Color.GREEN);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        holder.isBid = rquests.getRequests().stream().anyMatch(x -> x.getOrderId().equals(orderID));
        if(!holder.isBid) {
            holder.setBid("false");
        } else {
            holder.setBid("true");
        }


        holder.btnMore.setOnClickListener(v -> {
            Intent intent = new Intent(context, OrderInfo.class);
            OrderInfo.orderData = filtersData.get(position);
            intent.putExtra("position", position);
            OrderInfo.cameFrom = "Home Activity";
            ((Activity) context).startActivityForResult(intent, 1);
        });


        holder.btnAccept.setOnClickListener(v-> {
            Intent intent = new Intent(context, AsignOrder.class);
            intent.putExtra("orderId", orderID);
            intent.putExtra("orderOwner", owner);
            intent.putExtra("dName", filtersData.get(position).getDName());
            intent.putExtra("provider", filtersData.get(position).getProvider());

            // ------- Check what action to take --------- \\
            if(filtersData.get(position).getStatue().equals("supD")) {
                intent.putExtra("type", "delv");

            } else if (filtersData.get(position).getStatue().equals("placed")) {
                intent.putExtra("type", "accept");
            }

            ((Activity) context).startActivityForResult(intent, 1);

        });

        holder.btnBid.setOnClickListener(v1 -> {
        if(holder.isBid) {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context).setMessage("هل انت متأكد من انك تريد الغاء التقديم علي هذه الشحنه ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------------------- Send Request -------------------- //
                rquests _rquests = new rquests(context);
                _rquests.deleteReq(orderID, owner, filtersData.get(position).getProvider());
                holder.isBid = false;
                // ------------------ Notificatiom ------------------ //
                holder.setBid("false");
                Toast.makeText(context, "تم الغاء التقديم", Toast.LENGTH_SHORT).show();
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        } else {
            Intent intent = new Intent(context, AsignOrder.class);
            intent.putExtra("orderId", orderID);
            intent.putExtra("orderOwner", owner);
            intent.putExtra("type", "bid");
            intent.putExtra("dName", filtersData.get(position).getDName());
            intent.putExtra("position", position);
            intent.putExtra("provider", filtersData.get(position).getProvider());

            ((Activity) context).startActivityForResult(intent, 1);
            }
        });
    }

    @Override
    public int getItemCount() { return filtersData.size(); }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        public View myview;
        public Button btnMore,btnBid,btnAccept;
        public TextView txtWarning,txtgGet, txtgMoney,txtDate, txtOrderFrom,txtOrderTo,txtPostDate, txtDate2,txtProvider;
        public LinearLayout linerDate,linAgree;
        public ImageView icTrans;
        public boolean isBid = false;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview=itemView;
            btnMore = myview.findViewById(R.id.btnMore);
            txtWarning = myview.findViewById(R.id.txtWarning);
            linerDate = myview.findViewById(R.id.linerDate);
            txtgGet = myview.findViewById(R.id.fees);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtDate = myview.findViewById(R.id.date);
            txtDate2 = myview.findViewById(R.id.date3);
            txtOrderFrom = myview.findViewById(R.id.OrderFrom);
            txtOrderTo = myview.findViewById(R.id.orderto);
            txtPostDate = myview.findViewById(R.id.txtPostDate);
            btnBid = myview.findViewById(R.id.btnBid);
            icTrans = myview.findViewById(R.id.icTrans);
            btnAccept = myview.findViewById(R.id.btnAccept);
            linAgree = myview.findViewById(R.id.linAgree);
            txtProvider = myview.findViewById(R.id.txtProvider);
        }

        private void setBid(String type) {
            if(type.equals("true")) {
                btnBid.setText("الغاء الطلب");
                btnBid.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_bad_square));
            } else {
                btnBid.setBackgroundColor(ContextCompat.getColor(context,R.color.yellow));
                btnBid.setText("تقديم طلب توصيل");
            }
        }

        public void setOrderFrom(String orderFrom){
            txtOrderFrom.setText(orderFrom);
        }

        public void setOrderto(String orderto){
            txtOrderTo.setText(orderto);
        }

        public void setDate (String dDate, String pDate){
            txtDate.setText(dDate);
            txtDate2.setText(pDate);
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash){
            txtgMoney.setText("سعر الشحنة  " + ordercash + " ج");
        }

        @SuppressLint("SetTextI18n")
        public void setFee(String fees) {
            txtgGet.setText(fees);
        }


        public void setPostDate(String startDate) {
            txtPostDate.setText(_cacu.setPostDate(startDate));
        }

        public void switchLayout(String provider) {
            if(provider.equals("Esh7nly")) {
                linAgree.setVisibility(View.VISIBLE);
                btnAccept.setVisibility(View.GONE);
                txtgMoney.setVisibility(View.VISIBLE);
                txtProvider.setVisibility(View.VISIBLE);
                txtProvider.setText("Esh7nly");
                txtProvider.setBackgroundColor(context.getResources().getColor(R.color.yellow));
            } else if (provider.equals("Raya")) {
                linAgree.setVisibility(View.GONE);
                btnAccept.setVisibility(View.VISIBLE);
                txtgMoney.setVisibility(View.GONE);
                txtProvider.setVisibility(View.VISIBLE);
                txtProvider.setText("Raya");
                txtProvider.setBackgroundColor(context.getResources().getColor(R.color.ic_profile_background));

            }
        }

    }

}