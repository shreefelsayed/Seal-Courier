package com.armjld.rayashipping;

import android.content.Context;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.armjld.rayashipping.models.CaptinMoney;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.notiData;
import com.armjld.rayashipping.models.userData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EnvioMoney {

    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    DatabaseReference Envio = FirebaseDatabase.getInstance().getReference().child("Envio");
    DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    Context mContext;

    public EnvioMoney(Context mContext) {
        this.mContext = mContext;
    }

    // --------- Pay Pack Money For Captin by Super Visor --
    public void payPackMoney(userData user, String walletMoney) {
        uDatabase.child(user.getId()).child("packMoney").setValue("0");

        // ---- Set Payment as Paid
        uDatabase.child(user.getId()).child("payments").orderByChild("isPaid").equalTo("false").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CaptinMoney captinMoney = ds.getValue(CaptinMoney.class);
                        String key = ds.getKey();
                        assert captinMoney != null;

                        if (captinMoney.getTransType().equals("ourmoney")) {
                            int finalPack = Integer.parseInt(UserInFormation.getPackMoney()) + Integer.parseInt(captinMoney.getMoney());
                            UserInFormation.setPackMoney(finalPack + "");
                            // --- Add Money To Supplier Wallet
                            captinMoney.setTransType("pack");
                            captinMoney.setDate(datee);
                            uDatabase.child(UserInFormation.getId()).child("payments").push().setValue(captinMoney);
                            uDatabase.child(UserInFormation.getId()).child("packMoney").setValue(finalPack + "");

                            // ---- Set as Paid in Captins Wallet
                            assert key != null;
                            uDatabase.child(user.getId()).child("payments").child(key).child("isPaid").setValue("true");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // ---- Send noti to Captin
        String message = "تم استلام المستحقات و قيمتها " + walletMoney + " بنجاح";
        notiData Noti = new notiData(UserInFormation.getId(), "", user.getId(), message, datee, "false", "wallet", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(user.getId()).push().setValue(Noti);

        Toast.makeText(mContext, "تم دفع المستحقات بنجاح", Toast.LENGTH_SHORT).show();
    }

    // --------- Pay Bouns For Captin by Super visor --
    public void payBouns(userData user, String walletMoney) {
        uDatabase.child(user.getId()).child("walletmoney").setValue(0);

        // ---- Set Payment as Paid
        uDatabase.child(user.getId()).child("payments").orderByChild("isPaid").equalTo("false").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        CaptinMoney captinMoney = ds.getValue(CaptinMoney.class);
                        String key = ds.getKey();
                        assert captinMoney != null;
                        if (!captinMoney.getTransType().equals("ourmoney")) {
                            assert key != null;

                            // --- Add Money To Supplier Wallet
                            int finalWallet = Integer.parseInt(UserInFormation.getWalletmoney()) + Integer.parseInt(captinMoney.getMoney());
                            UserInFormation.setWalletmoney(finalWallet + "");

                            captinMoney.setTransType("captin");
                            captinMoney.setDate(datee);
                            uDatabase.child(UserInFormation.getId()).child("payments").child(key).setValue(captinMoney);
                            uDatabase.child(UserInFormation.getId()).child("walletmoney").setValue(finalWallet);

                            // --- Mark as Paid in Captins Wallet
                            uDatabase.child(user.getId()).child("payments").child(key).child("isPaid").setValue("true");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        // ---- Send noti to Captin
        String message = "تم تسليمك البونص و قيمته " + walletMoney + " بنجاح";
        notiData Noti = new notiData(UserInFormation.getId(), "", user.getId(), message, datee, "false", "wallet", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(user.getId()).push().setValue(Noti);
    }
}
