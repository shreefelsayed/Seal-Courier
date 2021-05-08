package com.armjld.rayashipping.Notifications;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Filters;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.caculateTime;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.notiData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class NotiAdaptere extends RecyclerView.Adapter<NotiAdaptere.MyViewHolder> {

    public static caculateTime _cacu = new caculateTime();
    Context context, context1;
    long count;
    ArrayList<notiData> notiData;


    public NotiAdaptere(Context context, ArrayList<notiData> notiData, Context context1, long count) {
        this.count = count;
        this.context = context;
        this.notiData = notiData;
        this.context1 = context1;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_notification, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        String Datee = notiData.get(position).getDatee();
        String Statue = notiData.get(position).getStatue();
        String OrderID = notiData.get(position).getOrderid();
        String uName = notiData.get(position).getuName();
        String action = notiData.get(position).getAction();
        String ppURL = notiData.get(position).getPpURL();

        holder.setNoti(uName, Statue, Datee, ppURL);

        holder.myview.setOnClickListener(v -> {
            switch (action) {
                case "noting": {
                    // ------------ Do Nothing
                    break;
                }
                case "recived":
                case "profile": {
                    whichProfile();
                    break;
                }
                /*case "order": {
                    if(UserInFormation.getAccountType().equals("Delivery Worker")) {
                        // ---------- Check For Order Avillabilty -------- //
                        Data orderData = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            orderData = SuperVisorHome.mm.stream().filter(x -> x.getId().equals(OrderID)).findFirst().orElse(null);
                        } else {
                            for(int i = 0; i<SuperVisorHome.mm.size(); i++) {
                                if(SuperVisorHome.mm.get(i).getId().equals(OrderID)) {
                                    orderData = SuperVisorHome.mm.get(i);
                                }
                            }
                        }
                        if(orderData != null) {
                            OneOrder.orderData = orderData;
                            Intent OneOrder = new Intent(context, OneOrder.class);
                            context.startActivity(OneOrder);
                        } else {
                            Toast.makeText(context, "تم قبول الاوردر من مندوب اخر", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }*/
                case "home": {
                    context.startActivity(new Intent(context, Home.class));
                    break;
                }
                case "facebook": {
                    String fbLink = "https://www.facebook.com/esh7nlyy/";
                    Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(fbLink));
                    context.startActivity(browse);
                    break;
                }
                case "playstore": {
                    String psLink = "https://play.google.com/store/apps/details?id=com.armjld.shipply";
                    Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(psLink));
                    context.startActivity(browse);
                    break;
                }
                /*case "map" : {
                    context.startActivity(new Intent(context, MapsActivity.class));
                    break;
                }

                case "wallet" : {
                    if(UserInFormation.getAccountType().equals("Supervisor")) {
                        context.startActivity(new Intent(context, MyWallet.class));
                    }
                    break;
                }

                case "ticket" : {
                    context.startActivity(new Intent(context, Tickets.class));
                    break;
                }*/

                case "filter": {
                    if (UserInFormation.getAccountType().equals("Delivery Worker")) {
                        context.startActivity(new Intent(context, Filters.class));
                    }
                    break;
                }

                case "fbgroup": {
                    if (UserInFormation.getAccountType().equals("Delivery Worker")) {
                        openWebURL("https://www.facebook.com/groups/3563137860431456/");
                    }
                    break;
                }

                /*case"orderinfo" : {
                    for(int i = 0; i<SuperVisorHome.delvList.size(); i++) {
                        if(SuperVisorHome.delvList.get(i).getId().equals(OrderID)) {
                            Intent capInfo = new Intent(context, CaptinOrderInfo.class);
                            capInfo.putExtra("orderid", OrderID);
                            context.startActivity(capInfo);
                            break;
                        }
                    }
                    break;
                }*/

                default: {
                    whichProfile();
                }
            }
        });


    }

    private void whichProfile() {
        Home.whichFrag = "Profile";
        context.startActivity(new Intent(context, Home.class));
    }


    @Override
    public int getItemCount() {
        return (int) count;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void openWebURL(String inURL) {
        Intent browse = new Intent(Intent.ACTION_VIEW, Uri.parse(inURL));
        context.startActivity(browse);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public View myview;
        public TextView txtBody, txtNotidate, txtName;
        public ImageView imgEditPhoto;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myview = itemView;

            txtBody = myview.findViewById(R.id.txtBody);
            txtNotidate = myview.findViewById(R.id.txtNotidate);
            imgEditPhoto = myview.findViewById(R.id.imgEditPhoto);
            txtName = myview.findViewById(R.id.txtName);
        }

        public void setNoti(String uName, String statue, String datee, String ppURL) {
            txtName.setText(uName);
            txtBody.setText(statue);
            txtNotidate.setText(_cacu.setPostDate(datee));
            if (!ppURL.equals("")) {
                Picasso.get().load(Uri.parse(ppURL)).into(imgEditPhoto);
            }
        }

    }
}