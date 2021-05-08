package com.armjld.rayashipping;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.Models.LocationDataType;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CopyingData {

    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya");
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    Context mContext;

    public CopyingData(Context mContext) {
        this.mContext = mContext;
    }

    public void getPickUp(Order orderData) {
        uDatabase.child(orderData.getuId()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user = snapshot.getValue(UserData.class);
                assert user != null;

                String clip = "الاسم : " + orderData.getOwner() + "\n";
                clip = clip + "التلفون : " + user.getPhone() + "\n";
                clip = clip + "العنوان : " + orderData.getTxtPState() + " - " + orderData.getmPRegion() + " - " + orderData.getmPAddress();

                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("PickUp", clip);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(mContext, "تم نسخ بيانات الراسل", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void getReciverData(Order orderData) {
        String clip = "الاسم : " + orderData.getDName() + "\n";
        clip = clip + "التلفون : " + orderData.getDPhone() + "\n";
        clip = clip + "العنوان : " + orderData.getTxtDState() + " - " + orderData.getmDRegion() + " - " + orderData.getDAddress();

        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("PickUp", clip);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext, "تم نسخ بيانات المرسل له", Toast.LENGTH_LONG).show();
    }

    public void getSupplierData(UserData user) {
        uDatabase.child(user.getId()).child("locations").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String clip = "الاسم : " + user.getName() + "\n";
                clip = clip + "التلفون : " + user.getPhone() + "\n";

                if(snapshot.exists()) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        LocationDataType loc = ds.getValue(LocationDataType.class);
                        assert loc != null;
                        clip = clip + "العنوان : " + loc.getState() + " - " + loc.getRegion() + " - " + loc.getAddress() + "\n";
                        clip = clip + "اللوكيشن : " + "https://www.google.com/maps/search/?api=1&query=" + loc.getLattude() + "," + loc.getLontude();
                        break;
                    }
                }

                ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("PickUp", clip);
                clipboard.setPrimaryClip(clipData);
                Toast.makeText(mContext, "تم نسخ بيانات العميل", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public String geMyUser(UserData user) {
        String clip = "الاسم : " + user.getName() + "\n";
        clip = clip + "رقم الحساب : " + user.getCode() + "\n";

        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("PickUp", clip);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext, "تم نسخ بيانات الحساب", Toast.LENGTH_LONG).show();

        return clip;
    }

    public String getOrderData(Order orderData) {
        String clip = "اوردر رقم : " + orderData.getTrackid() + "\n";
        clip = clip + "للمستلم : " + orderData.getDName() + "\n";
        clip = clip + "رقم الهاتف : " + orderData.getDPhone() + "\n";
        clip = clip + "العنوان : " + orderData.getTxtDState() + " - " + orderData.getmDRegion() + " - " + orderData.getDAddress();

        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("PickUp", clip);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext, "تم نسخ بيانات الشحنه", Toast.LENGTH_LONG).show();

        return clip;
    }

    public String copyID(Order orderData) {
        String clip = orderData.getId();
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Order Id", clip);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext, "تم نسخ الـKey", Toast.LENGTH_LONG).show();
        return clip;
    }

    public String getOrderDataToReciver(Order orderData) {
        String clip = "Seal Express" + "\n";
        clip = clip + "اوردر رقم : " + orderData.getTrackid() + "\n";
        clip = clip + "من : " + orderData.getOwner() + "\n";
        clip = clip + "المحتوي : " + orderData.getPackType() + "\n";
        clip = clip + "إجمالي التحصيل : " + orderData.getGMoney() + " ج" + "\n";
        clip = clip + "الي عنوان : " + orderData.getTxtDState() + " - " + orderData.getmDRegion() + " - " + orderData.getDAddress();

        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("PickUp", clip);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext, "تم نسخ بيانات الشحنه", Toast.LENGTH_LONG).show();

        return clip;
    }

    public String copyUserData(UserData user) {
        String userClip = "Email : " + user.getEmail() + "\n";
        userClip = userClip + "Password : " + user.getMpass();

        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("User", userClip);
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext, "تم نسخ بيانات المستخدم", Toast.LENGTH_LONG).show();

        return userClip;
    }

    public String copyPickUpPhone(Order order) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("User", order.getpPhone());
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext, "تم نسخ رقم الراسل", Toast.LENGTH_LONG).show();

        return order.getpPhone();
    }

    public String copyDropPhone(Order order) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("User", order.getDPhone());
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext, "تم نسخ رقم المستلم", Toast.LENGTH_LONG).show();

        return order.getDPhone();
    }

    public String copyTrack(Order order) {
        ClipboardManager clipboard = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("User", order.getTrackid());
        clipboard.setPrimaryClip(clipData);
        Toast.makeText(mContext, "تم نسخ رقم التتبع", Toast.LENGTH_LONG).show();

        return order.getDPhone();
    }
}
