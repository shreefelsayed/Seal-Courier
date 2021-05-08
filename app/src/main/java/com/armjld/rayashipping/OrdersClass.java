package com.armjld.rayashipping;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.armjld.rayashipping.Captin.CaptinOrderInfo;
import com.armjld.rayashipping.Models.CaptinMoney;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.Update;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.notiData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class OrdersClass implements  ActivityCompat.OnRequestPermissionsResultCallback {

    Context mContext;
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    SimpleDateFormat dayFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
    String dayDate = dayFormat.format(new Date());
    private static final int PHONE_CALL_CODE = 100;


    public OrdersClass(Context mContext) {
        this.mContext = mContext;
    }

    // ----------------------------- Captins Actions ------------------------- \\

    // ------- When Captin Recived an Order from a Supplier
    public void orderRecived(Order orderData) {
        getRefrence ref = new getRefrence();
        Wallet wallet = new Wallet();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // ---- Update Database Value
        mDatabase.child(orderData.getId()).child("statue").setValue("recived2");
        mDatabase.child(orderData.getId()).child("recived2Time").setValue(datee);
        mDatabase.child(orderData.getId()).child("pDate").setValue(dayDate);

        // ---- Send Notification to Supervisor
        String message = "قام " + UserInFormation.getUserName() + " بتأكيد استلام شحنه " + orderData.getOwner();
        notiData Noti = new notiData(UserInFormation.getId(), UserInFormation.getSupId(), orderData.getId(), message, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
        nDatabase.child(UserInFormation.getSupId()).push().setValue(Noti);


        // --- Add Log
        String Log = "قام المندوب " + UserInFormation.getId() + " بتأكيد استلام الشحنه من التاجر " + orderData.getuId();
        logOrder(mDatabase, orderData.getId(), Log);
        setUpdate(orderData, "قام المندوب باستلام البيك اب", false);

        // -- Toast
        Toast.makeText(mContext, "تم تأكيد استلام الشحنة", Toast.LENGTH_SHORT).show();
    }

    // ----- When Captin say the client didn't deliver the order
    public void orderDenied(Order orderData, String Msg) {
        getRefrence ref = new getRefrence();
        Wallet wallet = new Wallet();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        int tries = Integer.parseInt(orderData.getTries()) + 1;

        // -- Set Action
        mDatabase.child(orderData.getId()).child("statue").setValue("denied");
        mDatabase.child(orderData.getId()).child("tries").setValue(tries + "");
        mDatabase.child(orderData.getId()).child("deniedTime").setValue(datee);
        mDatabase.child(orderData.getId()).child("denied_reason").setValue(Msg);

        // --- Send notification to Supervisor
        String message = "قام " + UserInFormation.getUserName() + " بفشل في تسليم شحنه " + orderData.getDName() + " بسبب " + Msg;
        notiData Noti = new notiData(UserInFormation.getId(), UserInFormation.getSupId(), orderData.getId(), message, datee, "false", "nothing", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
        nDatabase.child(UserInFormation.getSupId()).push().setValue(Noti);

        // --- Send notification to Supplier
        if(!orderData.getuId().equals("")) {
            message = "لم يتم تسليم الشحنه " + orderData.getDName() + " بسبب " + Msg;
            Noti = new notiData(UserInFormation.getId(), orderData.getuId(), orderData.getId(), message, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
            nDatabase.child(orderData.getuId()).push().setValue(Noti);
        }

        // ---- Add Money To Wallet
        wallet.addDeniedMoney(UserInFormation.getId(), orderData);

        // ---- Add Log
        String Log = "قام المندوب " + UserInFormation.getUserName() + " بفشل تسليم الشحنه بسبب : " + Msg;
        logOrder(mDatabase, orderData.getId(), Log);
        setUpdate(orderData, "فشل في تسليم الشحنه بسبب : " + Msg + ".", true);


        // --- Toast
        Toast.makeText(mContext, "تم تسجيل الشحنه كمرتجع الرجاء تسليمها للمخزن", Toast.LENGTH_SHORT).show();
    }

    // ------ When Captin deliver a denied order back to Supplier
    public void returnOrder(Order orderData) {
        // ---
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // ---- Set Action
        mDatabase.child(orderData.getId()).child("statue").setValue("deniedback");
        mDatabase.child(orderData.getId()).child("deniedbackTime").setValue(datee);

        // --- Send notification to Supervisor
        String message = "قام " + UserInFormation.getUserName() + " بتسليم المرتجع الي " + orderData.getOwner();
        notiData Noti = new notiData(UserInFormation.getId(), UserInFormation.getSupId(), orderData.getId(), message, datee, "false", "nothing", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
        nDatabase.child(UserInFormation.getSupId()).push().setValue(Noti);

        // ---- Add Log
        String Log = "قام المندوب " + UserInFormation.getId() + " بتسليم المرتجع الي " + orderData.getOwner();
        logOrder(mDatabase, orderData.getId(), Log);

        setUpdate(orderData, "تم ارسال المرتجع للراسل", false);

        // --- Toast
        Toast.makeText(mContext, "تم تأكيد تسليم المرتجع", Toast.LENGTH_SHORT).show();
    }

    // ----- When a Captin Deliver an Order
    public void orderDelvered(Order orderData) {
        if(orderData.getStatue().equals("delivered")) return;
        getRefrence ref = new getRefrence();
        Wallet wallet = new Wallet();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        mDatabase.child(orderData.getId()).child("statue").setValue("delivered");
        mDatabase.child(orderData.getId()).child("dilverTime").setValue(datee);
        mDatabase.child(orderData.getId()).child("ddate").setValue(dayDate);
        mDatabase.child(orderData.getId()).child("moneyStatue").setValue("courier");

        orderData.setMoneyStatue("courier");

        // -- Add Money to Delivery
        wallet.addDelvMoney(orderData.getuAccepted(), orderData);

        // ---- Send Notifications to Supervisor
        String messageS = "تم تسليم شحنه " + orderData.getDName() + " بنجاح";
        notiData NotiS = new notiData(orderData.getuAccepted(), UserInFormation.getSupId(), orderData.getId(), messageS, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
        nDatabase.child(UserInFormation.getSupId()).push().setValue(NotiS);

        // --- Add a Try
        int tries = Integer.parseInt(orderData.getTries()) + 1;
        mDatabase.child(orderData.getId()).child("tries").setValue(tries + "");

        if(!orderData.getuId().equals("")) {
            // ---- Send Notifications to Supplier
            String message = "تم تسليم شحنه " + orderData.getDName() + " بنجاح";
            notiData Noti = new notiData(orderData.getuAccepted(), orderData.getuId(), orderData.getId(), message, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
            nDatabase.child(orderData.getuId()).push().setValue(Noti);

            // --- Add Money to Supplier Wallet
            wallet.addToSupplier(orderData.getGMoney(), orderData.getuId(), orderData);
        }

        // --- Add Money to Captins PackMoney
        int CurrentPackMoney = Integer.parseInt(UserInFormation.getPackMoney());
        int finalMoney = CurrentPackMoney + Integer.parseInt(orderData.getGMoney());
        wallet.addToUser(UserInFormation.getId(), Integer.parseInt(orderData.getGMoney()),orderData, "ourmoney");

        uDatabase.child(UserInFormation.getId()).child("packMoney").setValue(finalMoney + "");
        UserInFormation.setPackMoney(finalMoney + "");

        // -- Add Log
        String Log = "قام المندوب " + orderData.getuAccepted() + " بتسليم الشحنه للعميل " + orderData.getDName() + " و تم اضافه ١٥ جنيه في حسابه" + " و تم تحويل ٥ جنيه في حساب شركه إشحنلي";
        logOrder(mDatabase, orderData.getId(), Log);

        setUpdate(orderData, "تم تسليم الشحنه", false);

        // -- Toast
        Toast.makeText(mContext, "تم توصيل الشحنة", Toast.LENGTH_SHORT).show();
    }

    // ----------------------------- SuperVisors Actions ------------------------- \\

    // ---- When a Supervisor asign Captin to Pick ORder
    public void assignToCaptin(Order orderData, String capID) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // --- Set changes in Order Data
        mDatabase.child(orderData.getId()).child("uAccepted").setValue(capID);
        mDatabase.child(orderData.getId()).child("pSupervisor").setValue(UserInFormation.getSup_code());
        mDatabase.child(orderData.getId()).child("statue").setValue("accepted");
        mDatabase.child(orderData.getId()).child("acceptedTime").setValue(datee);

        // --- Set Changes in User Data
        uDatabase.child(UserInFormation.getId()).child("orders").child(orderData.getId()).child("captin").setValue(capID);
        uDatabase.child(capID).child("orders").child(orderData.getId()).child("statue").setValue("assignToPick");

        // ----- Send Notification To Captin
        String msg = "قام " + UserInFormation.getUserName() + " بتسليمك شحنه جديده لاستلامها.";
        notiData Noti = new notiData(UserInFormation.getId(), capID, orderData.getId(), msg, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
        nDatabase.child(capID).push().setValue(Noti);

        // -- Send Notification To Supplier
        if(!orderData.getuId().equals("")) {
            msg = "تمت مراجعه شحنتك و سيتم استلامها في اقرب وقت";
            Noti = new notiData(UserInFormation.getId(), orderData.getuId(), orderData.getId(), msg, datee, "false", "orderinfo", "Envio", UserInFormation.getUserURL(), "Raya");
            nDatabase.child(orderData.getuId()).push().setValue(Noti);
        }

        // ----- Add Log
        String Log = "تم تسليم الشحنه من المشرف " + UserInFormation.getSup_code() + " الي المندوب" + capID;
        logOrder(mDatabase, orderData.getId(), Log);
        setUpdate(orderData, "الشحنه قيد الاستلام من مندوب الشحن", false);


        Toast.makeText(mContext, "تم تسليم البيك اب للمندوب", Toast.LENGTH_SHORT).show();
    }


    // ----- When Supervisor Asign a returned order to a Captin to Deliver to Supplier
    public void asignDenied(Order orderData, String captinId) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        String supId;
        if(UserInFormation.getAccountType().equals("Supervisor")) {
            supId = UserInFormation.getId();
        } else {
            supId = UserInFormation.getSupId();
        }

        // ----- Set Action
        mDatabase.child(orderData.getId()).child("statue").setValue("capDenied");
        mDatabase.child(orderData.getId()).child("uAccepted").setValue(captinId);

        // ---- Send Notification To Captin
        String message = "قام المشرف بتسليمك شحنه مرتجعه";
        notiData Noti = new notiData(supId ,captinId , orderData.getId(),message,datee,"false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
        nDatabase.child(captinId).push().setValue(Noti);

        // ---- Add Log
        String Log = "قام المشرف " + UserInFormation.getUserName() + " بتسليم المرتجع الي " + captinId;
        logOrder(mDatabase, orderData.getId(), Log);
        setUpdate(orderData, "تم تسليم المرتجع للمندوب", false);

        // ---- Toast
        Toast.makeText(mContext, "تم اختيار المندوب لتسليم المرتجع", Toast.LENGTH_SHORT).show();
    }

    // ----- When SuperVisor Asign an order to Captin to Deliver
    public void asignDelv(Order orderData, String capID) {

        if(orderData.getStatue().equals("deleted") || orderData.getStatue().equals("delivered") || orderData.getStatue().equals("deniedback")) return;

        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        String supId;

        if(UserInFormation.getAccountType().equals("Supervisor")) {
            supId = UserInFormation.getId();
        } else {
            supId = UserInFormation.getSupId();
        }

        // --- Set changes in Order Data
        mDatabase.child(orderData.getId()).child("uAccepted").setValue(capID);
        mDatabase.child(orderData.getId()).child("statue").setValue("readyD");
        mDatabase.child(orderData.getId()).child("readyDTime").setValue(datee);

        // ----- Set Changes in User Data
        uDatabase.child(supId).child("orders").child(orderData.getId()).child("captin").setValue(capID);
        uDatabase.child(capID).child("orders").child(orderData.getId()).child("statue").setValue("readyD");

        // --- Send Notification To Captin
        String msg = "قام " + UserInFormation.getUserName() + " بتسليمك شحنه جديده لتسليمها.";
        notiData Noti = new notiData(supId, capID, orderData.getId(), msg, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
        nDatabase.child(capID).push().setValue(Noti);

        // --- Add Log
        String Log = "قام المشرف بتسليم الشحنه للكابتن " + capID + " لتسليمها للعميل";
        logOrder(mDatabase, orderData.getId(), Log);
        setUpdate(orderData, "قيد التسليم - تم تسليم الاوردر للمندوب", false);

        // ---- Toast
        Toast.makeText(mContext, "تم تسليم الشحنه للمندوب", Toast.LENGTH_SHORT).show();
        Timber.i("Order Assigned Succefully");
    }

    // ----------- Assign As Recived By SuperVisor
    public void setRecived(Order orderData) {
        // --- Update Databse
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());
        mDatabase.child(orderData.getId()).child("statue").setValue("recived2");
        mDatabase.child(orderData.getId()).child("recived2Time").setValue(datee);

        // --- Toast
        Toast.makeText(mContext, "تم تسليم الشحنه للمندوب", Toast.LENGTH_SHORT).show();

        // --- Logs
        String Log = "قام المشرف " + UserInFormation.getUserName() + " بتسليم الشحنه رقم : " + orderData.getTrackid() + " للمندوب " + orderData.getuAccepted() + " بدلا من التاجر " + orderData.getOwner();
        logOrder(mDatabase, orderData.getId(), Log);

        setUpdate(orderData, "تم تسليم البيك اب للمندوب", false);
    }


    public void orderPaid(Order orderData) {
        // Refrence
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // --- Add Money to Captin Wallet
        Wallet wallet = new Wallet();
        int CurrentPackMoney = Integer.parseInt(UserInFormation.getPackMoney());
        int finalMoney = CurrentPackMoney + Integer.parseInt(orderData.getReturnMoney());
        wallet.addToUser(UserInFormation.getId(), Integer.parseInt(orderData.getReturnMoney()),orderData, "shippingFees");

        uDatabase.child(UserInFormation.getId()).child("packMoney").setValue(finalMoney + "");
        UserInFormation.setPackMoney(finalMoney + "");

        // Check if order is Paid Already --
        if(orderData.getPaid().equals("true") && !orderData.getuId().equals("")) {
            uDatabase.child(orderData.getuId()).child("payments").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        CaptinMoney captinMoney = ds.getValue(CaptinMoney.class);
                        if(captinMoney.getTrackId().equals(orderData.getTrackid()) && captinMoney.getTransType().equals("shippingFees")) {
                            if(captinMoney.getIsPaid().equals("false")) {
                                // --- If it's not paid but it's in the invoice
                                uDatabase.child(orderData.getuId()).child("payments").child(ds.getKey()).removeValue();
                            } else {
                                // -- If The Supplier Already Paid The Money
                                    CaptinMoney newWallet = new CaptinMoney(orderData.getId(), "ourmoney", datee, "false", orderData.getTrackid(), orderData.getReturnMoney());
                                    uDatabase.child(orderData.getuId()).child("payments").push().setValue(newWallet);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        // Set Order As Paid
        mDatabase.child(orderData.getId()).child("paid").setValue("true");
        mDatabase.child(orderData.getId()).child("gget").setValue(orderData.getReturnMoney());
    }

    // ------- Add Logs to Order
    public void logOrder(DatabaseReference mDatabase, String id, String message) {
        long tsLong = System.currentTimeMillis() / 1000;
        String logC = Long.toString(tsLong);
        mDatabase.child(id).child("logs").child(logC).setValue(message);
    }

    public void setUpdate(Order orderData, String msg, boolean isNoti) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // -- Set Data
        Update update = new Update(msg, "", datee, orderData.getId(), UserInFormation.getId());

        // -- Update Data
        mDatabase.child(orderData.getId()).child("updates").push().setValue(update);

        if(isNoti && !orderData.getuId().equals("")) {
            // -- Send Notification
            String message = "شحنه " + orderData.getDName() + " : " + msg;
            notiData Noti = new notiData("", orderData.getuId(), orderData.getId(), message, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL(), "Raya");
            nDatabase.child(orderData.getuId()).push().setValue(Noti);
        }

        Toast.makeText(mContext, "Update Added", Toast.LENGTH_SHORT).show();
    }

    public void callSender(Order orderData) {
        if(orderData.getpPhone().equals("")) return;

        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل تريد الاتصال بالتاجر ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {
            checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + orderData.getpPhone()));
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mContext.startActivity(callIntent);
            dialogInterface.dismiss();
        }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
        mBottomSheetDialog.show();
    }

    public void callConsigne(Order orderData) {
        if (orderData.getDPhone().equals("")) return;
        BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder((Activity) mContext).setMessage("هل تريد الاتصال بالعميل ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {
            checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + orderData.getDPhone()));
            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            mContext.startActivity(callIntent);

            dialogInterface.dismiss();
        }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
        mBottomSheetDialog.show();
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions((Activity) mContext, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(mContext, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(mContext, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
