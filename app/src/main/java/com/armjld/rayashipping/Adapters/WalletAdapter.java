package com.armjld.rayashipping.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.models.CaptinMoney;

import java.util.ArrayList;

public class WalletAdapter extends RecyclerView.Adapter<WalletAdapter.ViewHolder> {

    public static caculateTime _cacu = new caculateTime();
    Context context;
    ArrayList<CaptinMoney> orderData;

    public WalletAdapter(ArrayList<CaptinMoney> orderData, Context context) {
        this.context = context;
        this.orderData = orderData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_wallet, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CaptinMoney captinMoney = orderData.get(position);

        holder.txtTrackId.setText(captinMoney.getTrackId());
        holder.txtMoney.setText(captinMoney.getMoney() + " ج");
        holder.setState(captinMoney.getTransType());
        holder.txtDate.setText(_cacu.setPostDate(captinMoney.getDate()));

    }

    @Override
    public int getItemCount() {
        return this.orderData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public TextView txtDate, txtMoney, txtStatue, txtTrackId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;
            txtDate = myview.findViewById(R.id.txtDate);
            txtMoney = myview.findViewById(R.id.txtMoney);
            txtStatue = myview.findViewById(R.id.txtStatue);
            txtTrackId = myview.findViewById(R.id.txtTrackId);
        }

        public void setState(String transType) {
            switch (transType) {
                case "delivered":
                    txtMoney.setTextColor(Color.GREEN);
                    txtStatue.setText("تسليم شحنه");
                    break;
                case "recived":
                    txtMoney.setTextColor(Color.GREEN);
                    txtStatue.setText("استلام شحنه");
                    break;
                case "denied":
                    txtMoney.setTextColor(Color.GREEN);
                    txtStatue.setText("محاوله تسليم");
                    break;
                case "deniedback":
                    txtMoney.setTextColor(Color.GREEN);
                    txtStatue.setText("تسليم مرتجع للعميل");
                    break;
                case "ourmoney":
                    txtMoney.setTextColor(Color.RED);
                    txtStatue.setText("مستحقات شحنه");
                    break;
                case "pack" :
                    txtMoney.setTextColor(Color.RED);
                    txtStatue.setText("مستحقات شحنه");
                    break;
                case "bouns":
                    txtMoney.setTextColor(Color.GREEN);
                    txtStatue.setText("بونص");
                    break;
                case "captin" :
                    txtMoney.setTextColor(Color.YELLOW);
                    txtStatue.setText("مستحقات مندوب شحن");
                    break;
            }
        }
    }
}
