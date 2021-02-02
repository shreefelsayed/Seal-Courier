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
        View view = inflater.inflate(R.layout.card_captin, parent, false);
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
        holder.setIcon(UserInFormation.getTrans());
        holder.setProvider(data.getProvider());
        holder.setViewer(UserInFormation.getAccountType(), data);


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
                if(oData.getuId().equals(data.getuId()) && oData.getStatue().equals("recived")) {
                   oCount ++;
                }
            }

            BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("تأكيد استلام الشحنه").setCancelable(true).setPositiveButton("استلام " + oCount + " شحنه", R.drawable.ic_tick_green, (dialogInterface, which) -> {
                for(int i = 0; i < Home.captinAvillable.size(); i ++) {
                    Data oData = Home.captinAvillable.get(i);
                    if(oData.getuId().equals(data.getuId()) && oData.getStatue().equals("recived")) {
                        // ------- Update Database ------
                        OrdersClass ordersClass = new OrdersClass(mContext);
                        ordersClass.orderRecived(oData);

                        // -------- Update Adapter
                        Home.captinAvillable.get(i).setStatue("recived2");
                        //holder.setStatue(filtersData.get(position));
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
        public Button btnInfo, btnDelivered, btnRecived, btnOrderBack, btnDenied;
        public Button btnCapRecived, btnAsignOther;
        public TextView txtTrackId,txtProvider, txtGetStat, txtgGet, txtgMoney, txtDate, txtUsername, txtOrderFrom, txtOrderTo, txtPostDate, pickDate;
        public LinearLayout linerDate, linerAll, linSuperVisor;
        public ImageButton mImageButton;
        public ImageView icTrans;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            myview = itemView;

            btnDelivered = myview.findViewById(R.id.btnDelivered);
            btnInfo = myview.findViewById(R.id.btnInfo);
            txtGetStat = myview.findViewById(R.id.txtStatue);
            linerAll = myview.findViewById(R.id.linerAll);
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
            txtTrackId = myview.findViewById(R.id.txtTrackId);

            linSuperVisor = myview.findViewById(R.id.linSuperVisor);
            btnAsignOther = myview.findViewById(R.id.btnAsignOther);
            btnCapRecived = myview.findViewById(R.id.btnCapRecived);
        }

        @SuppressLint("SetTextI18n")
        public void setData(Data data) {
            txtTrackId.setText(data.getTrackid());
            txtPostDate.setText(_cacu.setPostDate(data.getDate()));
            txtOrderTo.setText(data.reStateD());
            txtDate.setText("تاريخ التسليم : " + data.getDDate());
            pickDate.setText("تاريخ الاستلام : " + data.getpDate());
            txtgGet.setText("مصاريف الشحن  " + data.getGGet() + " ج");
            txtgMoney.setText("اجمالي التحصيل : " + data.getGMoney() + " ج");
            txtOrderFrom.setText(data.reStateP());
            txtUsername.setText(data.getOwner());
        }


        public void setStatue(Data orderData) {
            switch (orderData.getStatue()) {
                case "accepted":
                    btnDelivered.setVisibility(View.GONE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    btnInfo.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundColor(Color.YELLOW);
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
                    txtGetStat.setBackgroundColor(Color.RED);
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
                    txtGetStat.setBackgroundColor(Color.GREEN);
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
                    txtGetStat.setBackgroundColor(Color.YELLOW);
                    txtGetStat.setText("قيد التسليم");
                    txtGetStat.setVisibility(View.VISIBLE);
                    if(!orderData.getdHubName().equals("")) txtOrderFrom.setText(orderData.getdHubName());
                    break;
                case "denied":
                    btnDelivered.setVisibility(View.VISIBLE);
                    btnInfo.setVisibility(View.VISIBLE);
                    btnRecived.setVisibility(View.GONE);
                    btnOrderBack.setVisibility(View.GONE);
                    txtgMoney.setVisibility(View.GONE);
                    btnDenied.setVisibility(View.GONE);
                    txtGetStat.setVisibility(View.VISIBLE);
                    txtGetStat.setBackgroundColor(Color.RED);
                    txtGetStat.setText("مرتجع يسلم للمخزن");
                    if(!orderData.getdHubName().equals("")) txtOrderFrom.setText(orderData.getdHubName());
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
                    txtGetStat.setBackgroundColor(Color.MAGENTA);
                    if(!orderData.getpHubName().equals("")) txtOrderFrom.setText(orderData.getpHubName());
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
                btnDelivered.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                btnRecived.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                txtgGet.setBackgroundColor(mContext.getResources().getColor(R.color.yellow));
                icTrans.setColorFilter(mContext.getResources().getColor(R.color.yellow));
            } else if (provider.equals("Raya")) {
                txtgGet.setVisibility(View.GONE);
                txtProvider.setVisibility(View.VISIBLE);
                txtProvider.setText("Envio");
                txtProvider.setBackgroundColor(mContext.getResources().getColor(R.color.ic_profile_background));
                btnDelivered.setBackgroundColor(mContext.getResources().getColor(R.color.ic_profile_background));
                btnRecived.setBackgroundColor(mContext.getResources().getColor(R.color.ic_profile_background));
                txtgGet.setBackgroundColor(mContext.getResources().getColor(R.color.ic_profile_background));
                icTrans.setColorFilter(mContext.getResources().getColor(R.color.ic_profile_background));
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
