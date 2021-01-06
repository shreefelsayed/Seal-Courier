package com.armjld.rayashipping;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.SuperVisor.SuperVisorHome;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.requestsData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;


public class rquests {

    private static final ArrayList<myCaptReqs>requests= new ArrayList<>();
    private DatabaseReference Bdatabase;
    int count;

    Context mContext;

    public rquests(Context context) {
        this.mContext = context;
    }


    public static ArrayList<myCaptReqs> getRequests() {
        return requests;
    }

    public void addrequest(requestsData _req, String captin, String provider){
        String uId = UserInFormation.getId();
        if(uId == null){ return; }
        // ------- Set data to Captin

        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(provider);

        mDatabase.child(_req.getOrderId()).child("requests").child(captin).setValue(_req);
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(captin).child("requests").child(_req.getOrderId()).setValue(_req);

        // ------- Set Data to SuperVisor
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("requests").child(_req.getOrderId()).setValue(_req);

        // -------- Add to App
        myCaptReqs reqs = new myCaptReqs(captin, _req.getOrderId());
        requests.add(reqs);
    }

    // -------------- Just Delete Current User Request for one Order ---------------- //
    public void deleteReq(String orderid, String owner, String provider){
        myCaptReqs _getReq = requests.stream().filter(x -> x.getOrderId().equals(orderid)).findFirst().orElse(null);
        assert _getReq != null;
        String captinId = _getReq.getCaptinId();
        requests.remove(_getReq);

        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(provider);

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(captinId).child("requests").child(orderid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("noti").exists()) {
                    FirebaseDatabase.getInstance().getReference().child("Pickly").child("notificationRequests").child(owner).child(Objects.requireNonNull(snapshot.child("noti").getValue()).toString()).removeValue();
                }
                mDatabase.child(orderid).child("requests").child(captinId).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(captinId).child("requests").child(orderid).removeValue();
                FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("requests").child(orderid).removeValue();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    // ------------------ Delete all the requests on and Order ---------------------- ///
    public void  deletedOrder(String orderid, String provider){
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(provider);

        count =0;
        mDatabase.child(orderid).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        requestsData rData = ds.getValue(requestsData.class);
                        assert rData != null;
                        String dlivaryId = rData.getId();
                        mDatabase.child(orderid).child("requests").child(dlivaryId).child("statue").setValue("declined");
                        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(dlivaryId).child("requests").child(orderid).child("statue").setValue("declined");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // -------------- Delete all expect for one ------------- \\
    public void  deleteButOne(String orderid, String one, String provider){
        getRefrence ref = new getRefrence();
        DatabaseReference mDatabase = ref.getRef(provider);

        count =0;
        mDatabase.child(orderid).child("requests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        requestsData rData = ds.getValue(requestsData.class);
                        assert rData != null;
                        String dlivaryId = rData.getId();
                        if(!dlivaryId.equals(one)) {
                            FirebaseDatabase.getInstance().getReference().child("Pickly").child("orders").child(orderid).child("requests").child(dlivaryId).child("statue").setValue("declined");
                            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(dlivaryId).child("requests").child(orderid).child("statue").setValue("declined");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
    // -------------------- Import the Requests for the Current User --------------------------- //
    public void ImportCuurentRequests() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        String datee = sdf.format(new Date());
        requests.clear();
        requests.trimToSize();

        if(SuperVisorHome.mCaptinsIDS.size() == 0) {
            return;
        }

        // ------- Get requests assigned to all of my captins
        for(int i = 0; i < SuperVisorHome.mCaptinsIDS.size(); i++) {
            String myCaptinID = SuperVisorHome.mCaptinsIDS.get(i);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(myCaptinID).child("requests").orderByChild("statue").equalTo("N/A").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()) {
                        for(DataSnapshot ds : snapshot.getChildren()) {
                            requestsData req = ds.getValue(requestsData.class);
                            assert req != null;
                            myCaptReqs reqs = new myCaptReqs(myCaptinID, req.getOrderId());
                            requests.add(reqs);

                            // ------ Delete Outdated Requests
                            if(!req.getDate().substring(0 , 10).equals(datee.substring(0 ,10)) && !SuperVisorHome.avillableIDS.contains(req.getOrderId())) {
                                String owner = req.getOwner();
                                deleteReq(req.getOrderId(), owner, "Esh7nly");
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }

        /*if(capAssigns.recyclerView != null) {
             capAssigns.getOrders();
        }*/

    }
}
