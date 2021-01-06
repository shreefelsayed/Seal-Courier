package com.armjld.rayashipping.Captin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.armjld.rayashipping.Chat.Messages;
import com.armjld.rayashipping.Chat.chatListclass;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.SuperVisorHome;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    Context context;
    ArrayList<Data>filtersData;
    private final DatabaseReference uDatabase;
    private static final int PHONE_CALL_CODE = 100;
    public static caculateTime _cacu = new caculateTime();

    public DeliveryAdapter(Context context, ArrayList<Data> filtersData) {
        this.context = context;
        this.filtersData = filtersData;

        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view  = inflater.inflate(R.layout.card_captin,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int position) {
        Vibrator vibe = (Vibrator) Objects.requireNonNull(context).getSystemService(Context.VIBRATOR_SERVICE);
        Data data = filtersData.get(position);
        String owner = data.getuId();

        // Get Post Date
        String startDate = Objects.requireNonNull(data.getDate());

        holder.setDate(data.getDDate(), data.getpDate());
        holder.setUsername(data.getuId(), data.getOwner());
        holder.setOrdercash(data.getGMoney());
        holder.setOrderFrom(data.reStateP());
        holder.setOrderto(data.reStateD());
        holder.setFee(data.getGGet());
        holder.setPostDate(startDate);
        holder.setDilveredButton(data.getStatue());
        holder.checkDeleted(data.getRemoved());
        holder.setIcon(UserInFormation.getTrans());


        // ------------------------------------   Order info
        holder.btnInfo.setOnClickListener(v -> {
            Intent intent = new Intent(context, CaptinOrderInfo.class);
            CaptinOrderInfo.orderData = filtersData.get(position);
            ((Activity) context).startActivityForResult(intent, 1);
        });

        // ---------------- Set order to Recived
        holder.btnRecived.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context).setMessage("هل قمت باستلام الشحنة من التاجر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(context);
                ordersClass.orderRecived(data.getProvider(), data.getId(), data.getuId());

                // -------- Update Adapter
                filtersData.get(position).setStatue("recived2");
                holder.setDilveredButton("recived2");
                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // -----------------------   Set ORDER as Delivered
        holder.btnDelivered.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) context).setMessage("هل قمت بتسليم الشحنة ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(context);
                ordersClass.orderDelvered(data.getProvider(), data.getId(),data.getDName(), data.getuId(), data.getuAccepted());

                // -------- Update Adapter

                //filtersData.get(position).setStatue("delivered");
                //holder.setDilveredButton("delivered");

                chatListclass _ch = new chatListclass();
                _ch.dlevarychat(owner);

                SuperVisorHome.captinDelv.remove(position);
                notifyItemRemoved(position);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        holder.btnChat.setOnClickListener(v-> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(UserInFormation.getId(), data.getuId(), context);
            Messages.cameFrom = "Profile";
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        ((SuperVisorHome)context).onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(context, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public Button btnInfo,btnDelivered,btnRate,btnChat,btnRecived,btnOrderBack;
        public TextView txtRate,txtGetStat,txtgGet, txtgMoney,txtDate, txtUsername, txtOrderFrom, txtOrderTo,txtPostDate,pickDate;
        public LinearLayout linerDate, linerAll;
        public ImageButton mImageButton;
        public ImageView icTrans;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myview = itemView;

            btnDelivered = myview.findViewById(R.id.btnDelivered);
            btnInfo = myview.findViewById(R.id.btnInfo);
            btnRate = myview.findViewById(R.id.btnRate);
            txtRate = myview.findViewById(R.id.drComment);
            txtGetStat = myview.findViewById(R.id.txtStatue);
            linerAll = myview.findViewById(R.id.linerAll);
            btnChat = myview.findViewById(R.id.btnChat);
            btnRecived = myview.findViewById(R.id.btnRecived);
            linerDate = myview.findViewById(R.id.linerDate);
            txtgGet = myview.findViewById(R.id.fees);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtDate = myview.findViewById(R.id.date);
            mImageButton = myview.findViewById(R.id.imageButton);
            txtUsername = myview.findViewById(R.id.txtUsername);
            txtOrderFrom = myview.findViewById(R.id.OrderFrom);
            txtOrderTo = myview.findViewById(R.id.orderto);
            txtPostDate = myview.findViewById(R.id.txtPostDate);
            btnOrderBack = myview.findViewById(R.id.btnOrderBack);
            pickDate = myview.findViewById(R.id.pickDate);
            icTrans = myview.findViewById(R.id.icTrans);
        }


        void setUsername(String uid, String owner){
            if(!owner.equals("")) {
                txtUsername.setText(owner);
                return;
            }

            uDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()) {
                        txtUsername.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) { }
            });
        }


        public void setOrderFrom(String orderFrom){
            txtOrderFrom.setText(orderFrom);
        }

        public void setDilveredButton(String state) {
            btnRate.setText("تقييم التاجر");
            switch (state) {
                case "accepted" : {
                    btnDelivered.setVisibility(View.GONE);
                    btnChat.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    break;
                }
                case "recived" : {
                    btnChat.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.VISIBLE);
                    btnDelivered.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    break;
                }

                case "recived2" : {
                    btnChat.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.VISIBLE);
                    txtGetStat.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    break;
                }

                case "readyD" : {
                    btnChat.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.VISIBLE);
                    txtGetStat.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    break;
                }

                case "capDenied" : {
                    btnChat.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.VISIBLE);
                    txtGetStat.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.VISIBLE);
                    break;
                }

                default: {
                    btnChat.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    break;
                }

                // -------- I don't know what to do HERE ..
                /* case "delivered" :
                case "deniedback" : {
                    btnDelivered.setVisibility(View.GONE);
                    btnChat.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    break;
                }

                case "denied": {
                    btnDelivered.setVisibility(View.GONE);
                    btnChat.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setText("مرتجع لم يتم تسلمية للتاجر");
                    btnOrderBack.setVisibility(View.GONE);
                    break;
                } */
            }
        }

        public void setOrderto(String orderto){
            txtOrderTo.setText(orderto);
        }

        public void setDate (String date, String pDate){
            txtDate.setText(date);
            pickDate.setText(pDate);
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash){
            txtgMoney.setText("ثمن الرسالة  " + ordercash + " ج");
        }

        @SuppressLint("SetTextI18n")
        public void setFee(String fees) {
            txtgGet.setText("مصاريف الشحن  " + fees + " ج");
        }

        public void setPostDate(String startDate) {
            txtPostDate.setText(_cacu.setPostDate(startDate));
        }

        public void checkDeleted(String removed) {
            if(removed.equals("true")) {
                linerAll.setVisibility(View.GONE);
            } else {
                linerAll.setVisibility(View.VISIBLE);
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setIcon(String trans) {
            switch (trans) {
                case "Trans" :
                    icTrans.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_run));
                    break;
                case "Car" :
                    icTrans.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_car));
                    break;
                case "Bike" :
                    icTrans.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_bicycle));
                    break;
                case "Motor" :
                    icTrans.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_vespa));
                    break;
            }
        }
    }

    private String getDate () {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(new Date());
    }
}
