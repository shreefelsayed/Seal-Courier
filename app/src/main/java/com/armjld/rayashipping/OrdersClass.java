package com.armjld.rayashipping;

import android.content.Context;
import android.widget.Toast;

import com.armjld.rayashipping.Chat.chatListclass;
import com.armjld.rayashipping.models.Data;
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

    public OrdersClass(Context mContext) {
        this.mContext = mContext;
    }

    // ----------------------------- Captins Actions ------------------------- \\

    // ------- When Captin Recived an Order from a Supplier
    public void orderRecived(Data orderData) {
        getRefrence ref = new getRefrence();
        Wallet wallet = new Wallet();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // ---- Update Database Value
        mDatabase.child(orderData.getId()).child("statue").setValue("recived2");
        mDatabase.child(orderData.getId()).child("recived2Time").setValue(datee);

        // ---- Send Notification to Supervisor
        String message = "قام " + UserInFormation.getUserName() + " بتأكيد استلام شحنه " + orderData.getOwner();
        notiData Noti = new notiData(UserInFormation.getId(), UserInFormation.getSupId(), orderData.getId(), message, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(orderData.getuId()).push().setValue(Noti);

        // ---- in Case of Esh7nly Add money to Esh7nly Wallet
        if (orderData.getProvider().equals("Esh7nly")) {
            wallet.addMoneyToShreef(orderData.getId(), orderData.getTrackid());
        }


        // --- Add Money to Captin 
        boolean toPay = true;
        for (int i = 0; i < Home.captinAvillable.size(); i++) {
            Data oData = Home.captinAvillable.get(i);
            if (oData.getStatue().equals("recived2") && oData.getuId().equals(orderData.getuId())) {
                toPay = false;
                break;
            }
        }

        if (toPay) wallet.addRecivedMoney(UserInFormation.getId(), orderData);

        // --- Add Log
        String Log = "قام المندوب " + UserInFormation.getId() + " بتأكيد استلام الشحنه من التاجر " + orderData.getuId();
        logOrder(mDatabase, orderData.getId(), Log);

        // -- Toast
        Toast.makeText(mContext, "تم تأكيد استلام الشحنة", Toast.LENGTH_SHORT).show();
    }

    // ----- When Captin say the client didn't deliver the order
    public void orderDenied(Data orderData) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        int tries = Integer.parseInt(orderData.getTries()) + 1;

        // -- Set Action
        mDatabase.child(orderData.getId()).child("statue").setValue("denied");
        mDatabase.child(orderData.getId()).child("tries").setValue(tries + "");
        mDatabase.child(orderData.getId()).child("deniedTime").setValue(datee);

        // --- Send notification to Supervisor
        String message = "قام " + UserInFormation.getUserName() + " بفشل في تسليم شحنه " + orderData.getDName();
        notiData Noti = new notiData(UserInFormation.getId(), UserInFormation.getSupId(), orderData.getId(), message, datee, "false", "nothing", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(UserInFormation.getSupId()).push().setValue(Noti);

        // ---- Add Log
        String Log = "قام المندوب " + UserInFormation.getUserName() + " بفشل تسليم الشحنه " + orderData.getuId();
        logOrder(mDatabase, orderData.getId(), Log);

        // --- Toast
        Toast.makeText(mContext, "تم تسجيل الشحنه كمرتجع الرجاء تسليمها للمخزن", Toast.LENGTH_SHORT).show();
    }

    // ------ When Captin deliver a denied order back to Supplier
    public void returnOrder(Data orderData) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // ---- Set Action
        mDatabase.child(orderData.getId()).child("statue").setValue("deniedback");
        mDatabase.child(orderData.getId()).child("deniedbackTime").setValue(datee);

        // --- Send notification to Supervisor
        String message = "قام " + UserInFormation.getUserName() + " بتسليم المرتجع الي " + orderData.getOwner();
        notiData Noti = new notiData(UserInFormation.getId(), UserInFormation.getSupId(), orderData.getId(), message, datee, "false", "nothing", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(UserInFormation.getSupId()).push().setValue(Noti);

        // ---- Add Log
        String Log = "قام المندوب " + UserInFormation.getId() + " بتسليم المرتجع الي " + orderData.getOwner();
        logOrder(mDatabase, orderData.getId(), Log);

        // --- Add Money on Deliver Denied


        // --- Update Chat Data
        chatListclass _ch = new chatListclass();
        _ch.dlevarychat(orderData.getuId());

        // --- Toast
        Toast.makeText(mContext, "تم تأكيد تسليم المرتجع", Toast.LENGTH_SHORT).show();
    }

    // ----- When a Captin Deliver an Order
    public void orderDelvered(Data orderData) {
        getRefrence ref = new getRefrence();
        Wallet wallet = new Wallet();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        mDatabase.child(orderData.getId()).child("statue").setValue("delivered");
        mDatabase.child(orderData.getId()).child("dilverTime").setValue(datee);

        // -- Add Money to Delivery
        wallet.addDelvMoney(orderData.getuAccepted(), orderData);

        // ---- Send Notifications to Supplier
        String message = "تم تسليم شحنه " + orderData.getDName() + " بنجاح";
        notiData Noti = new notiData(orderData.getuAccepted(), orderData.getuId(), orderData.getId(), message, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(orderData.getuId()).push().setValue(Noti);

        // ---- Send Notifications to Supervisor
        message = "تم تسليم شحنه " + orderData.getDName() + " بنجاح";
        Noti = new notiData(orderData.getuAccepted(), UserInFormation.getSupId(), orderData.getId(), message, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(UserInFormation.getSupId()).push().setValue(Noti);

        // --- Add Money to Supplier Wallet
        if (!orderData.getProvider().equals("Esh7nly"))
            wallet.addToSupplier(orderData.getGMoney(), orderData.getuId(), orderData);

        // --- Add Money to Captins PackMoney
        int CurrentPackMoney = Integer.parseInt(UserInFormation.getPackMoney());
        int finalMoney = CurrentPackMoney + Integer.parseInt(orderData.getGMoney());
        wallet.addToUser(UserInFormation.getId(), CurrentPackMoney,orderData, "ourmoney");
        uDatabase.child(UserInFormation.getId()).child("packMoney").setValue(finalMoney + "");
        UserInFormation.setPackMoney(finalMoney + "");

        // -- Update Chat Data
        chatListclass _ch = new chatListclass();
        _ch.dlevarychat(orderData.getuId());

        // -- Add Log
        String Log = "قام المندوب " + orderData.getuAccepted() + " بتسليم الشحنه للعميل " + orderData.getDName() + " و تم اضافه ١٥ جنيه في حسابه" + " و تم تحويل ٥ جنيه في حساب شركه إشحنلي";
        logOrder(mDatabase, orderData.getId(), Log);

        // -- Toast
        Toast.makeText(mContext, "تم توصيل الشحنة", Toast.LENGTH_SHORT).show();
    }

    // ----------------------------- SuperVisors Actions ------------------------- \\

    // ---- When a Supervisor asign Captin to Pick ORder
    public void assignToCaptin(Data orderData, String capID) {
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
        notiData Noti = new notiData(UserInFormation.getId(), capID, orderData.getId(), msg, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(capID).push().setValue(Noti);

        // -- Send Notification To Supplier
        msg = "تمت مراجعه شحنتك و سيتم استلامها في اقرب وقت";
        Noti = new notiData(UserInFormation.getId(), orderData.getuId(), orderData.getId(), msg, datee, "false", "orderinfo", "Envio", UserInFormation.getUserURL());
        nDatabase.child(orderData.getuId()).push().setValue(Noti);

        // ----- Add Log
        String Log = "تم تسليم الشحنه من المشرف " + UserInFormation.getSup_code() + " الي المندوب" + capID;
        logOrder(mDatabase, orderData.getId(), Log);

        Toast.makeText(mContext, "تم تسليم الاوردر للمندوب", Toast.LENGTH_SHORT).show();
    }

    // ----- When a Supervsior Asign Captin on Esh7nly Order to Pick
    public void bidOnOrder(Data orderData, String capID) {
        // ----Send Request
        String notiKey = nDatabase.child(orderData.getuId()).push().getKey();
        requestsData _reqData = new requestsData(capID, datee, "N/A", "", orderData.getuId(), notiKey, orderData.getId(), UserInFormation.getId());
        rquests _rquests = new rquests(mContext);
        _rquests.addrequest(_reqData, capID, orderData.getProvider());

        // --- Notification to Supplier
        String message = "قامت شركه Envio بطلب لتوصيل شحنه " + orderData.getDName();
        notiData Noti = new notiData(capID, orderData.getuId(), orderData.getId(), message, datee, "false", "orderinfo", "Envio", UserInFormation.getUserURL());
        assert notiKey != null;
        nDatabase.child(orderData.getuId()).child(notiKey).setValue(Noti);

        // --- Toast
        Toast.makeText(mContext, "تم تقديم طلب التوصيل", Toast.LENGTH_SHORT).show();
    }

    // ----- When Supervisor Asign a returned order to a Captin to Deliver to Supplier
    public void asignDenied(Data orderData, String captinId) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // ----- Set Action
        mDatabase.child(orderData.getId()).child("statue").setValue("capDenied");
        mDatabase.child(orderData.getId()).child("uAccepted").setValue(captinId);

        // ---- Add Log
        String Log = "قام المشرف " + UserInFormation.getUserName() + " بتسليم المرتجع الي " + captinId;
        logOrder(mDatabase, orderData.getId(), Log);

        // ---- Toast
        Toast.makeText(mContext, "تم اختيار المندوب لتسليم المرتجع", Toast.LENGTH_SHORT).show();
    }

    // ----- When SuperVisor Asign an order to Captin to Deliver
    public void asignDelv(Data orderData, String capID) {
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());

        // --- Set changes in Order Data
        mDatabase.child(orderData.getId()).child("uAccepted").setValue(capID);
        mDatabase.child(orderData.getId()).child("statue").setValue("readyD");
        mDatabase.child(orderData.getId()).child("readyDTime").setValue(datee);

        // ----- Set Changes in User Data
        uDatabase.child(UserInFormation.getId()).child("orders").child(orderData.getId()).child("captin").setValue(capID);
        uDatabase.child(capID).child("orders").child(orderData.getId()).child("statue").setValue("readyD");

        // --- Send Notification To Captin
        String msg = "قام " + UserInFormation.getUserName() + " بتسليمك شحنه جديده لتسليمها.";
        notiData Noti = new notiData(UserInFormation.getId(), capID, orderData.getId(), msg, datee, "false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(capID).push().setValue(Noti);

        // --- Add Log
        String Log = "قام المشرف بتسليم الشحنه للكابتن " + capID + " لتسليمها للعميل";
        logOrder(mDatabase, orderData.getId(), Log);

        // ---- Toast
        Toast.makeText(mContext, "تم تسليم الشحنه للمندوب", Toast.LENGTH_SHORT).show();
        Timber.i("Order Assigned Succefully");
    }

    // ----------- Assign As Recived By SuperVisor
    public void setRecived(Data orderData) {
        // --- Update Databse
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(orderData.getProvider());
        mDatabase.child(orderData.getId()).child("statue").setValue("recived");
        mDatabase.child(orderData.getId()).child("recivedTime").setValue(datee);

        // ---- Send noti to Delivery Worker
        String message = "قام " + UserInFormation.getUserName() + " بتسليمك الشحنةاضغط هنا لتأكيد استلام الشحنه.";
        notiData Noti = new notiData(UserInFormation.getId() ,orderData.getuAccepted() , orderData.getId(),message,datee,"false", "orderinfo", UserInFormation.getUserName(), UserInFormation.getUserURL());
        nDatabase.child(orderData.getuAccepted()).push().setValue(Noti);

        // --- Toast
        Toast.makeText(mContext, "تم تسليم الشحنه للمندوب، في انظار تأكيد الاستلام", Toast.LENGTH_SHORT).show();

        // --- Logs
        String Log = "قام المشرف " + UserInFormation.getUserName() + " بتسليم الشحنه رقم : " + orderData.getTrackid() + " للمندوب " + orderData.getuAccepted() + " بدلا من التاجر " + orderData.getOwner();
        logOrder(mDatabase, orderData.getId(), Log);
    }

    // ------- Add Logs to Order
    public void logOrder(DatabaseReference mDatabase, String id, String message) {
        long tsLong = System.currentTimeMillis() / 1000;
        String logC = Long.toString(tsLong);
        mDatabase.child(id).child("logs").child(logC).setValue(message);
    }
}
