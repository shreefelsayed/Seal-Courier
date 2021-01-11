package com.armjld.rayashipping.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.armjld.rayashipping.Captin.CaptinOrderInfo;
import com.armjld.rayashipping.Captin.captinRecived;
import com.armjld.rayashipping.Chat.Messages;
import com.armjld.rayashipping.Chat.chatListclass;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.OrderInfo;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;
import java.util.Objects;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PHONE_CALL_CODE = 100;
    public static caculateTime _cacu = new caculateTime();
    private final DatabaseReference uDatabase;
    Context mContext;
    ArrayList<Data> filtersData;
    String from;

    public DeliveryAdapter(Context mContext, ArrayList<Data> filtersData, String from) {
        this.mContext = mContext;
        this.filtersData = filtersData;
        this.from = from;
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_captin, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Vibrator vibe = (Vibrator) Objects.requireNonNull(mContext).getSystemService(Context.VIBRATOR_SERVICE);
        Data data = filtersData.get(position);

        // Get Post Date
        holder.setDate(data.getDDate(), data.getpDate());
        holder.setUsername(data.getuId(), data.getOwner());
        holder.setOrdercash(data.getGMoney());
        holder.setOrderFrom(data.reStateP());
        holder.setOrderto(data.reStateD());
        holder.setFee(data.getGGet());
        holder.setPostDate(data.getDate());
        holder.setDilveredButton(data.getStatue());
        holder.checkDeleted(data.getRemoved());
        holder.setIcon(UserInFormation.getTrans());
        holder.setProvider(data.getProvider());
        holder.txtProvider.setText(data.getProvider());
        holder.setViewer(UserInFormation.getAccountType());


        // ------------------------------------   Order info
        holder.btnInfo.setOnClickListener(v -> {
            Intent intent;
            if (UserInFormation.getAccountType().equals("Delivery Worker")) {
                intent = new Intent(mContext, CaptinOrderInfo.class);
                intent.putExtra("from", from);
                CaptinOrderInfo.orderData = filtersData.get(position);
            } else {
                intent = new Intent(mContext, OrderInfo.class);
                intent.putExtra("from", from);
                OrderInfo.orderData = filtersData.get(position);
            }

            ((Activity) mContext).startActivityForResult(intent, 1);
        });

        // ---------------- Set order to Recived
        holder.btnRecived.setOnClickListener(v -> {
            assert vibe != null;
            vibe.vibrate(20);
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل قمت باستلام الشحنة من التاجر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(mContext);
                ordersClass.orderRecived(data);

                // -------- Update Adapter
                filtersData.get(position).setStatue("recived2");
                holder.setDilveredButton("recived2");

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // ----------------------- Return a Denied Order
        holder.btnOrderBack.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل قمت بتسليم الشحنة للتاجر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(mContext);
                ordersClass.returnOrder(data);

                // -------- Update Adapter
                captinRecived.filterList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, filtersData.size());

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();

        });

        // ----------------------- Set Order As Denied
        holder.btnDenied.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("لم يستلم العميل الشحنه ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(mContext);
                ordersClass.orderDenied(data);

                // ----- Update Adapter
                filtersData.get(position).setStatue("denied");
                holder.setDilveredButton("denied");
                notifyItemChanged(position);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // ----  Set ORDER as Delivered
        holder.btnDelivered.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل قمت بتسليم الشحنة ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(mContext);
                ordersClass.orderDelvered(data);

                // -------- Update Adapter
                captinRecived.filterList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, filtersData.size());

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
        });

        // ----- Chat with Supplier
        holder.btnChat.setOnClickListener(v -> {
            chatListclass _chatList = new chatListclass();
            _chatList.startChating(UserInFormation.getId(), data.getuId(), mContext);
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
        ((Home) mContext).onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public Button btnInfo, btnDelivered, btnChat, btnRecived, btnOrderBack, btnDenied;
        public TextView txtProvider, txtGetStat, txtgGet, txtgMoney, txtDate, txtUsername, txtOrderFrom, txtOrderTo, txtPostDate, pickDate;
        public LinearLayout linerDate, linerAll;
        public ImageButton mImageButton;
        public ImageView icTrans;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myview = itemView;

            btnDelivered = myview.findViewById(R.id.btnDelivered);
            btnInfo = myview.findViewById(R.id.btnInfo);
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
            txtProvider = myview.findViewById(R.id.txtProvider);
            btnDenied = myview.findViewById(R.id.btnDenied);
        }


        void setUsername(String uid, String owner) {
            if (!owner.equals("")) {
                txtUsername.setText(owner);
                return;
            }

            uDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        txtUsername.setText(Objects.requireNonNull(dataSnapshot.child("name").getValue()).toString());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }


        public void setOrderFrom(String orderFrom) {
            txtOrderFrom.setText(orderFrom);
        }

        public void setDilveredButton(String state) {
            switch (state) {
                case "accepted": {
                    btnDelivered.setVisibility(View.GONE);
                    btnChat.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);

                    btnInfo.setVisibility(View.VISIBLE);

                    txtGetStat.setBackgroundColor(Color.YELLOW);
                    txtGetStat.setText("قيد الاستلام");
                    txtGetStat.setVisibility(View.VISIBLE);
                    break;
                }
                case "recived": {
                    btnChat.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);

                    btnInfo.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.VISIBLE);

                    txtGetStat.setText("في انتظار تأكيد الاستلام");
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundColor(Color.RED);
                    break;
                }

                case "recived2": {
                    btnChat.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.GONE);

                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundColor(Color.GREEN);
                    txtGetStat.setText(" تم استلام الشحنه");
                    break;
                }

                case "readyD": {
                    btnChat.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);

                    txtgMoney.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnDenied.setVisibility(View.VISIBLE);
                    btnDelivered.setVisibility(View.VISIBLE);

                    txtGetStat.setBackgroundColor(Color.YELLOW);
                    txtGetStat.setText("قيد التسليم");
                    txtGetStat.setVisibility(View.VISIBLE);
                    break;
                }

                case "denied": {
                    btnChat.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);

                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundColor(Color.RED);
                    txtGetStat.setText("مرتجع يسلم للمخزن");
                    break;
                }

                case "capDenied": {
                    btnChat.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);

                    btnInfo.setVisibility(View.VISIBLE);
                    btnOrderBack.setVisibility(View.VISIBLE);

                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setText("تسليم للتاجر");
                    txtGetStat.setBackgroundColor(Color.MAGENTA);
                    break;
                }

                default: {
                    btnChat.setVisibility(View.GONE);
                    btnDelivered.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.GONE);
                    break;
                }
            }
        }

        public void setOrderto(String orderto) {
            txtOrderTo.setText(orderto);
        }

        public void setDate(String date, String pDate) {
            txtDate.setText(date);
            pickDate.setText(pDate);
        }

        @SuppressLint("SetTextI18n")
        public void setOrdercash(String ordercash) {
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
            if (removed.equals("true")) {
                linerAll.setVisibility(View.GONE);
            } else {
                linerAll.setVisibility(View.VISIBLE);
            }
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setIcon(String trans) {
            switch (trans) {
                case "Trans":
                    icTrans.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_run));
                    break;
                case "Car":
                    icTrans.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_car));
                    break;
                case "Bike":
                    icTrans.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_bicycle));
                    break;
                case "Motor":
                    icTrans.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_vespa));
                    break;
            }
        }

        public void setProvider(String provider) {
            if (provider.equals("Esh7nly")) {
                txtgGet.setVisibility(View.VISIBLE);
                txtProvider.setVisibility(View.VISIBLE);
                txtProvider.setText("Esh7nly");
                txtgMoney.setVisibility(View.VISIBLE);
                txtProvider.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                btnChat.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                btnDelivered.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                btnRecived.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                txtgGet.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                icTrans.setColorFilter(mContext.getResources().getColor(R.color.yellow));
            } else if (provider.equals("Raya")) {
                txtgGet.setVisibility(View.GONE);
                txtProvider.setVisibility(View.VISIBLE);
                txtProvider.setText("Envio");
                txtProvider.setBackgroundColor(mContext.getResources().getColor(R.color.ic_profile_background));
                btnChat.setBackgroundColor(mContext.getResources().getColor(R.color.ic_profile_background));
                btnDelivered.setBackgroundColor(mContext.getResources().getColor(R.color.ic_profile_background));
                btnRecived.setBackgroundColor(mContext.getResources().getColor(R.color.ic_profile_background));
                txtgGet.setBackgroundColor(mContext.getResources().getColor(R.color.ic_profile_background));
                icTrans.setColorFilter(mContext.getResources().getColor(R.color.ic_profile_background));
            }
        }

        public void setViewer(String accountType) {
            if (accountType.equals("Supervisor")) {
                btnRecived.setVisibility(View.GONE);
                btnDelivered.setVisibility(View.GONE);
                btnChat.setVisibility(View.GONE);
                btnInfo.setVisibility(View.VISIBLE);
                btnOrderBack.setVisibility(View.GONE);
                btnDenied.setVisibility(View.GONE);
            }
        }
    }
}
