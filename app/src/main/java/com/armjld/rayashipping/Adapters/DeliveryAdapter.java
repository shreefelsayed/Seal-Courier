package com.armjld.rayashipping.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
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
import com.armjld.rayashipping.Captin.PartDeliver;
import com.armjld.rayashipping.Captin.captinRecived;
import com.armjld.rayashipping.DeniedReasons;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.OrdersClass;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.AsignOrder;
import com.armjld.rayashipping.SuperVisor.OrderInfo;
import com.armjld.rayashipping.SuperVisor.SuperAvillable;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.util.ArrayList;
import java.util.Objects;

public class DeliveryAdapter extends RecyclerView.Adapter<DeliveryAdapter.MyViewHolder> implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PHONE_CALL_CODE = 100;
    public static caculateTime _cacu = new caculateTime();
    Context mContext;
    ArrayList<Data> filtersData;
    String from;

    public DeliveryAdapter(Context mContext, ArrayList<Data> filtersData, String from) {
        this.mContext = mContext;
        this.filtersData = filtersData;
        this.from = from;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_new_captin, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint({"SetTextI18n", "NonConstantResourceId"})
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        Data data = filtersData.get(position);

        // Get Post Date
        holder.setData(data);
        holder.setStatue(data);
        holder.checkDeleted(data.getRemoved());
        holder.setViewer(UserInFormation.getAccountType(), data);

        holder.txtOrderTo.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Address", holder.txtOrderTo.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "تم نسخ العنوان", Toast.LENGTH_LONG).show();
        });

        holder.txtTrackId.setOnClickListener(v-> {
            ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("TrackId", holder.txtTrackId.getText().toString().trim());
            clipboard.setPrimaryClip(clip);
            Toast.makeText(mContext, "تم نسخ رقم الشحنه", Toast.LENGTH_LONG).show();
        });

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
            int oCount = 0;

            for(int i = 0; i < Home.captinAvillable.size(); i ++) {
                Data oData = Home.captinAvillable.get(i);
                if(oData.getuId().equals(data.getuId())) {
                    if(oData.getStatue().equals("recived") || oData.getStatue().equals("accepted")) {
                        oCount ++;
                    }
                }
            }

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("تأكيد استلام الشحنه").setCancelable(true).setPositiveButton("استلام " + oCount + " شحنه", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                for(int i = 0; i < Home.captinAvillable.size(); i ++) {
                    Data oData = Home.captinAvillable.get(i);
                    if(oData.getuId().equals(data.getuId())) {
                        if(oData.getStatue().equals("recived") || oData.getStatue().equals("accepted")) {
                            // ------- Update Database ------
                            OrdersClass ordersClass = new OrdersClass(mContext);
                            ordersClass.orderRecived(oData);

                            // -------- Update Adapter
                            Home.captinAvillable.get(i).setStatue("recived2");
                            //holder.setStatue(filtersData.get(position));
                        }

                    }
                }
                dialogInterface.dismiss();
            }).setNegativeButton("استلام هذه الشحنه فقط", R.drawable.ic_close, (dialogInterface, which) -> {
                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(mContext);
                ordersClass.orderRecived(data);

                // -------- Update Adapter
                filtersData.get(position).setStatue("recived2");
                holder.setStatue(filtersData.get(position));
                dialogInterface.dismiss();
            }).build();
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
            DeniedReasons.orderData = data;
            DeniedReasons.pos = position;
            mContext.startActivity(new Intent(mContext, DeniedReasons.class));
        });

        // ----  Set ORDER as Delivered
        holder.btnDelivered.setOnClickListener(v -> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل قمت بتسليم الشحنة تسليم كامل ام جزئي ؟").setCancelable(true).setPositiveButton("تسليم كامل", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // --- Full Deliver (Update Order, Add Money)

                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(mContext);
                ordersClass.orderDelvered(data);
                // -------- Update Adapter
                captinRecived.filterList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, filtersData.size());

                dialogInterface.dismiss();
            }).setNegativeButton("تسليم جزئي", R.drawable.ic_close, (dialogInterface, which) -> {
                // --- Partial Deliver (Update Money and Send Notification)
                PartDeliver.orderData = data;
                mContext.startActivity(new Intent(mContext, PartDeliver.class));
                dialogInterface.dismiss();
            }).build();
            mBottomSheetDialog.show();
        });

        // ----- Asign To Another by SuperVisor
        holder.btnAsignOther.setOnClickListener(v-> {
            Intent intent = new Intent(mContext, AsignOrder.class);
            AsignOrder.assignToCaptin.clear();
            AsignOrder.assignToCaptin.add(filtersData.get(position));
            ((Activity) mContext).startActivityForResult(intent, 1);
        });

        // ----- Mark as Recived as a Supplier By Supper Visor
        holder.btnCapRecived.setOnClickListener(v-> {
            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل قام المندوب باستلام تلك الشحنه من الراسل ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                // ------- Update Database ------
                OrdersClass ordersClass = new OrdersClass(mContext);
                ordersClass.setRecived(data);

                // ---- Update Adatper
                filtersData.get(position).setStatue("recived");
                holder.setStatue(filtersData.get(position));
                notifyItemChanged(position);

                dialogInterface.dismiss();
            }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
            mBottomSheetDialog.show();
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

        public Button btnDelivered, btnRecived, btnOrderBack, btnDenied;
        public Button btnCapRecived, btnAsignOther;

        ImageView btnInfo;

        public TextView txtTrackId, txtGetStat, txtgMoney, txtUsername, txtOrderTo, txtOwner;
        public LinearLayout linerAll, linSuperVisor;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myview = itemView;

            btnDelivered = myview.findViewById(R.id.btnDelivered);
            btnInfo = myview.findViewById(R.id.btnInfo);
            txtGetStat = myview.findViewById(R.id.txtStatue);
            linerAll = myview.findViewById(R.id.linerAll);
            btnRecived = myview.findViewById(R.id.btnRecived);
            txtgMoney = myview.findViewById(R.id.ordercash);
            txtUsername = myview.findViewById(R.id.txtUsername);
            txtOrderTo = myview.findViewById(R.id.orderto);
            btnOrderBack = myview.findViewById(R.id.btnOrderBack);
            btnDenied = myview.findViewById(R.id.btnDenied);
            txtTrackId = myview.findViewById(R.id.txtTrackId);
            linSuperVisor = myview.findViewById(R.id.linSuperVisor);
            btnAsignOther = myview.findViewById(R.id.btnAsignOther);
            btnCapRecived = myview.findViewById(R.id.btnCapRecived);
            txtOwner = myview.findViewById(R.id.txtOwner);
        }

        @SuppressLint("SetTextI18n")
        public void setData(Data data) {
            txtTrackId.setText(data.getTrackid());
            if(data.getStatue().equals("accepted") || data.getStatue().equals("recived") || data.getStatue().equals("recived2") || data.getStatue().equals("capDenied")) {
                txtOrderTo.setText(data.reStateP() + " - " + data.getmPAddress());
            } else if(data.getStatue().equals("readyD") || data.getStatue().equals("denied")) {
                txtOrderTo.setText(data.reStateD() + " - " + data.getDAddress());
            }
            txtgMoney.setText(data.getGMoney() + " ج");
            txtOwner.setText(data.getOwner());
            txtUsername.setText(data.getDName());
        }


        public void setStatue(Data orderData) {
            switch (orderData.getStatue()) {
                case "accepted":
                    btnDelivered.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.VISIBLE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                    txtGetStat.setText("قيد الاستلام");
                    txtGetStat.setVisibility(View.VISIBLE);
                    if(!orderData.getpHubName().equals("")) txtOrderTo.setText(orderData.getpHubName());
                    break;
                case "recived":
                    btnDelivered.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.VISIBLE);
                    txtGetStat.setText("في انتظار تأكيد الاستلام");
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    if(!orderData.getpHubName().equals("")) txtOrderTo.setText(orderData.getpHubName());
                    break;
                case "recived2":
                    btnDelivered.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.VISIBLE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.GREEN));
                    txtGetStat.setText(" تم استلام الشحنه");
                    if(!orderData.getpHubName().equals("")) txtOrderTo.setText(orderData.getpHubName());
                    break;
                case "readyD":
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnDenied.setVisibility(View.VISIBLE);
                    btnDelivered.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.YELLOW));
                    txtGetStat.setText("قيد التسليم");
                    txtGetStat.setVisibility(View.VISIBLE);
                    break;
                case "denied":
                    btnDelivered.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                    txtGetStat.setText("مرتجع يسلم للمخزن");
                    break;
                case "capDenied":
                    btnDelivered.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnOrderBack.setVisibility(View.VISIBLE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setText("تسليم للتاجر");
                    txtGetStat.setBackgroundTintList(ColorStateList.valueOf(Color.MAGENTA));
                    txtOrderTo.setText(orderData.reStateP());
                    break;
                default:
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

        public void checkDeleted(String removed) {
            if (removed.equals("true")) {
                linerAll.setVisibility(View.GONE);
            } else {
                linerAll.setVisibility(View.VISIBLE);
            }
        }

        public void setViewer(String accountType, Data data) {
            if (accountType.equals("Supervisor")) {
                btnRecived.setVisibility(View.GONE);
                btnDelivered.setVisibility(View.GONE);
                btnInfo.setVisibility(View.VISIBLE);
                btnOrderBack.setVisibility(View.GONE);
                btnDenied.setVisibility(View.GONE);
                if(data.getStatue().equals("readyD") || data.getStatue().equals("accepted")) {
                    linSuperVisor.setVisibility(View.VISIBLE);
                    if(data.getStatue().equals("readyD")) {
                        btnCapRecived.setVisibility(View.GONE);
                    }
                }
            } else {
                linSuperVisor.setVisibility(View.GONE);
            }
        }
    }
}
