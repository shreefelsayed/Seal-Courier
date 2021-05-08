package com.armjld.rayashipping;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.Models.CaptinMoney;
import com.armjld.rayashipping.Models.Order;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import timber.log.Timber;

public class Wallet {

    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());


    // ----- Add Money to Delv on Recived
    public void addRecivedMoney(String id, Order orderData) {
        if(UserInFormation.getUser().getPickUpMoney() != 0) {
            int onRecivMoney = UserInFormation.getUser().getPickUpMoney();
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("walletmoney").exists()) {
                        int currentValue = Integer.parseInt(Objects.requireNonNull(snapshot.child("walletmoney").getValue()).toString());
                        uDatabase.child(id).child("walletmoney").setValue(currentValue + onRecivMoney);
                        if (UserInFormation.getUser().getId().equals(id))
                            UserInFormation.getUser().setWalletmoney((currentValue + onRecivMoney));
                    } else {
                        uDatabase.child(id).child("walletmoney").setValue(onRecivMoney);
                        if (UserInFormation.getUser().getId().equals(id))
                            UserInFormation.getUser().setWalletmoney(onRecivMoney);
                    }

                    addToUser(id, onRecivMoney, orderData, "recived");
                    Timber.i("Added Money to Wallet");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    // ----- Add Money to Delv on Denied
    public void addDeniedMoney(String id, Order orderData) {
        if(UserInFormation.getUser().getDeniedMoney() != 0) {
            int onDeniedMoney = UserInFormation.getUser().getDeniedMoney();
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("walletmoney").exists()) {
                        int currentValue = Integer.parseInt(Objects.requireNonNull(snapshot.child("walletmoney").getValue()).toString());
                        uDatabase.child(id).child("walletmoney").setValue(currentValue + onDeniedMoney);
                        if (UserInFormation.getUser().getId().equals(id))
                            UserInFormation.getUser().setWalletmoney((currentValue + onDeniedMoney));
                    } else {
                        uDatabase.child(id).child("walletmoney").setValue(onDeniedMoney);
                        if (UserInFormation.getUser().getId().equals(id))
                            UserInFormation.getUser().setWalletmoney(onDeniedMoney);
                    }

                    addToUser(id, onDeniedMoney, orderData, "denied");
                    Timber.i("Added Money to Wallet");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

    }

    public void addBouns(String id, int money) {
        uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child("walletmoney").exists()) {
                    int currentValue = Integer.parseInt(Objects.requireNonNull(snapshot.child("walletmoney").getValue()).toString());
                    uDatabase.child(id).child("walletmoney").setValue(currentValue + money);

                    if (UserInFormation.getUser().getId().equals(id)) UserInFormation.getUser().setWalletmoney((currentValue + money));
                } else {
                    uDatabase.child(id).child("walletmoney").setValue(money);
                    if (UserInFormation.getUser().getId().equals(id)) UserInFormation.getUser().setWalletmoney(money);
                }

                addBounsToUser(id, money, "bouns");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void addBounsToUser(String id, int money, String Action) {
        CaptinMoney captinMoney = new CaptinMoney("", Action, datee, "false", "", String.valueOf(money));
        uDatabase.child(id).child("payments").push().setValue(captinMoney);
        Timber.i("Add Money to Bouns");
    }

    public void addToUser(String id, int money, Order orderData, String Action) {
        CaptinMoney captinMoney = new CaptinMoney(orderData.getId(), Action, datee, "false", orderData.getTrackid(), String.valueOf(money));
        uDatabase.child(id).child("payments").push().setValue(captinMoney);
        Timber.i("Add Money in Logs");
    }

    // ----- Add Money to Delv
    public void addDelvMoney(String id, Order orderData) {
        if(UserInFormation.getUser().getDeliverMoney() != 0) {
            int onDelvMoney = UserInFormation.getUser().getDeliverMoney();
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("walletmoney").exists()) {
                        int currentValue = Integer.parseInt(Objects.requireNonNull(snapshot.child("walletmoney").getValue()).toString());
                        uDatabase.child(id).child("walletmoney").setValue(currentValue + onDelvMoney);
                        if (UserInFormation.getUser().getId().equals(id)) UserInFormation.getUser().setWalletmoney((currentValue + onDelvMoney));
                    } else {
                        uDatabase.child(id).child("walletmoney").setValue(onDelvMoney);
                        if (UserInFormation.getUser().getId().equals(id))
                            UserInFormation.getUser().setWalletmoney(onDelvMoney);
                    }

                    Timber.i("Added Money to Wallet");

                    addToUser(id, onDelvMoney, orderData, "delivered");
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }

    // ---- Add Money to Esh7nly Account
    public void addMoneyToShreef(String orderId, String trackID) {
        DatabaseReference walletRef = FirebaseDatabase.getInstance().getReference().child("Pickly").child("MyWallet");
        walletRef.child("Envio").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.child("totalMoney").exists()) {
                        float currentValue = Float.parseFloat(snapshot.child("totalMoney").getValue().toString());
                        walletRef.child("Raya").child("totalMoney").setValue(currentValue + 5);
                    } else {
                        walletRef.child("Raya").child("totalMoney").setValue(5);
                    }
                } else {
                    walletRef.child("Raya").child("totalMoney").setValue(5);
                }

                walletRef.child("Raya").child("orders").child(orderId).child("id").setValue(orderId);
                walletRef.child("Raya").child("orders").child(orderId).child("date").setValue(datee);
                walletRef.child("Raya").child("orders").child(orderId).child("trackid").setValue(trackID);

                Timber.i("Added Money to Wallet");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    // ------ Add Package Money into Supplier Account
    public void addToSupplier(String gMoney, String uId, Order orderData) {
        int money = Integer.parseInt(gMoney);

        uDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserData user = snapshot.getValue(UserData.class);

                if (snapshot.child("packMoney").exists()) {
                    int myMoney = Integer.parseInt(Objects.requireNonNull(snapshot.child("packMoney").getValue()).toString());
                    uDatabase.child(uId).child("packMoney").setValue((myMoney + money) + "");
                } else {
                    uDatabase.child(uId).child("packMoney").setValue((money) + "");
                }

                if (snapshot.child("totalMoney").exists()) {
                    int myMoney = Integer.parseInt(Objects.requireNonNull(snapshot.child("totalMoney").getValue()).toString());
                    uDatabase.child(uId).child("totalMoney").setValue((myMoney + money) + "");
                } else {
                    uDatabase.child(uId).child("totalMoney").setValue((money) + "");
                }

                // --- If User is Fixed Price also add the
                assert user != null;

                if(!user.getPaymentType().equals("dynamic")) {
                    if (snapshot.child("walletmoney").exists()) {
                        int myMoney = Integer.parseInt(Objects.requireNonNull(snapshot.child("walletmoney").getValue()).toString());
                        uDatabase.child(uId).child("walletmoney").setValue((myMoney + Integer.parseInt(orderData.getReturnMoney())));
                    } else {
                        uDatabase.child(uId).child("walletmoney").setValue(Integer.parseInt(orderData.getReturnMoney()));
                    }

                    FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya").child(orderData.getId()).child("gget").setValue(orderData.getReturnMoney());
                    FirebaseDatabase.getInstance().getReference().child("Pickly").child("raya").child(orderData.getId()).child("paid").setValue("true");

                    addToUser(uId, Integer.parseInt(orderData.getReturnMoney()), orderData, "shippingFees");
                }

                Timber.i("Added Money To Supplier %s", Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                addToUser(uId, Integer.parseInt(gMoney), orderData, "ourmoney");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}