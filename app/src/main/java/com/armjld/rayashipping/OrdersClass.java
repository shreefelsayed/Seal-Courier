package com.armjld.rayashipping;

import android.content.Context;
import android.widget.Toast;

import com.armjld.rayashipping.Chat.chatListclass;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.notiData;
import com.armjld.rayashipping.models.requestsData;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

public class OrdersClass {

    Context mContext;
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    DatabaseReference nDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests");

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    Long tsLong = System.currentTimeMillis()/1000;
    String logC = tsLong.toString();

    public OrdersClass(Context mContext) {
        this.mContext = mContext;
    }

    public void assignToCaptin(String captinId, String orderId, String owner, String provider) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(provider);
        // -------- Set changes in Order Data
        mDatabase.child(orderId).child("uAccepted").setValue(captinId);
        mDatabase.child(orderId).child("pSupervisor").setValue(UserInFormation.getSup_code());
        mDatabase.child(orderId).child("statue").setValue("accepted");
        mDatabase.child(orderId).child("acceptedTime").setValue(datee);

        // --------- Set Changes in User Data
        uDatabase.child(UserInFormation.getId()).child("orders").child(orderId).child("captin").setValue(captinId);
        uDatabase.child(captinId).child("orders").child(orderId).child("statue").setValue("accepted");


        // ------------------ Send Notification To Captin ------------------ //
        String msg = "قام " + UserInFormation.getUserName() + " بتسليمك شحنه جديده لاستلامها.";
        notiData Noti = new notiData(UserInFormation.getId(), captinId, orderId,msg,datee,"false","orderinfo",UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(captinId).push().setValue(Noti);

        // ------------------ Send Notification To Supplier
        msg = "تمت مراجعه شحنتك و سيتم استلامها في اقرب وقت";
        Noti = new notiData(UserInFormation.getId(), owner, orderId,msg,datee,"false","orderinfo",UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(owner).push().setValue(Noti);

        // ---------------- Add Log
        String Log = "تم تسليم الشحنه من المشرف " + UserInFormation.getSup_code() + " الي المندوب" + captinId;
        mDatabase.child(orderId).child("logs").child(logC).setValue(Log);

        Toast.makeText(mContext, "تم تسليم الاوردر للمندوب", Toast.LENGTH_SHORT).show();
        Timber.i("Order Assigned Succefully");
    }

    public void bidOnOrder (String owner, String orderID, String dName, String capId, String provider) {
        // ------------------- Send Request -------------------- //
        String notiKey = nDatabase.child(owner).push().getKey();

        requestsData _reqData = new requestsData(capId, datee, "N/A", "",owner, notiKey, orderID, UserInFormation.getId());
        rquests _rquests = new rquests(mContext);
        _rquests.addrequest(_reqData, capId, provider);

        // ------------------ Notificatiom ------------------ //
        String message = "قامت شركه الرايه بطلب لتوصيل شحنه " + dName;
        notiData Noti = new notiData(capId, owner,orderID,message,datee,"false","orderinfo", "الرايه", UserInFormation.getUserURL());
        assert notiKey != null;
        nDatabase.child(owner).child(notiKey).setValue(Noti);
        Toast.makeText(mContext, "تم تقديم طلب التوصيل", Toast.LENGTH_SHORT).show();
        Timber.i("Order Requested Sent Succefully");
    }

    public void orderRecived (String provider, String orderId, String owner) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(provider);

        mDatabase.child(orderId).child("statue").setValue("recived2");
        mDatabase.child(orderId).child("recived2Time").setValue(datee);

        /*String message = "قام " + UserInFormation.getUserName() + " بتأكيد استلام شحنه";
        notiData Noti = new notiData(UserInFormation.getId(), UserInFormation.getSupId(), orderID,message,datee,"false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(owner).push().setValue(Noti);*/

        // -------- in Case of Esh7nly Add money to Esh7nly Wallet
        if(provider.equals("Esh7nly")) {
            Wallet wallet = new Wallet();
            wallet.addMoneyToShreef(orderId);
        }

        // ---------------- Add Log
        String Log = "قام المندوب " + UserInFormation.getId() + " بتأكيد استلام الشحنه من التاجر " + owner;
        mDatabase.child(orderId).child("logs").child(logC).setValue(Log);

        Toast.makeText(mContext, "تم تأكيد استلام الشحنة", Toast.LENGTH_SHORT).show();
    }

    public void orderDelvered(String provider, String orderId, String dName, String owner, String delvId) {
        getRefrence ref = new getRefrence();
        Wallet wallet = new Wallet();
        DatabaseReference mDatabase = ref.getRef(provider);

        mDatabase.child(orderId).child("statue").setValue("delivered");
        mDatabase.child(orderId).child("dilverTime").setValue(datee);

        // --------------------------- Add Money to Both Delivery & Esh7nly Wallets
        wallet.addDelvMoney(delvId);
        wallet.addMoneyToShreef(orderId);

        // --------------------------- Send Notifications ---------------------//
        String message =  "تم تسليم شحنه " + dName + " بنجاح";
        notiData Noti = new notiData(delvId, owner,orderId,message,datee,"false","orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(owner).push().setValue(Noti);

        // ---------------- Add Log
        String Log = "قام المندوب " + delvId + " بتسليم الشحنه للعميل " + dName + " و تم اضافه ١٥ جنيه في حسابه" + " و تم تحويل ٥ جنيه في حساب شركه إشحنلي";
        mDatabase.child(orderId).child("logs").child(logC).setValue(Log);

        Toast.makeText(mContext, "تم توصيل الشحنة", Toast.LENGTH_SHORT).show();
    }


    public void asignDelv(String captinId, String orderId, String owner, String provider) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(provider);

        // -------- Set changes in Order Data
        mDatabase.child(orderId).child("uAccepted").setValue(captinId);
        mDatabase.child(orderId).child("statue").setValue("readyD");
        mDatabase.child(orderId).child("readyDTime").setValue(datee);

        // --------- Set Changes in User Data
        uDatabase.child(UserInFormation.getId()).child("orders").child(orderId).child("captin").setValue(captinId);
        uDatabase.child(captinId).child("orders").child(orderId).child("statue").setValue("readyD");

        // ------------------ Send Notification To Captin ------------------ //
        String msg = "قام " + UserInFormation.getUserName() + " بتسليمك شحنه جديده لتسليمها.";
        notiData Noti = new notiData(UserInFormation.getId(), captinId, orderId,msg,datee,"false","orderinfo",UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(captinId).push().setValue(Noti);

        // --------- Add Log
        String Log = "قام المشرف بتسليم الشحنه للكابتن " + captinId + " لتسليمها للعميل";
        mDatabase.child(orderId).child("logs").child(logC).setValue(Log);

        Toast.makeText(mContext, "تم تسليم الشحنه للمندوب", Toast.LENGTH_SHORT).show();
        Timber.i("Order Assigned Succefully");
    }
}
