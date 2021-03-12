package com.armjld.rayashipping;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.models.CaptinMoney;
import com.armjld.rayashipping.models.Data;
import com.armjld.rayashipping.models.UserInFormation;
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
    public void addRecivedMoney(String id, Data orderData) {
        if(UserInFormation.getReciveMoney() != 0) {
            int onRecivMoney = UserInFormation.getReciveMoney();
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("walletmoney").exists()) {
                        int currentValue = Integer.parseInt(Objects.requireNonNull(snapshot.child("walletmoney").getValue()).toString());
                        uDatabase.child(id).child("walletmoney").setValue(currentValue + onRecivMoney);
                        if (UserInFormation.getId().equals(id))
                            UserInFormation.setWalletmoney(String.valueOf((currentValue + onRecivMoney)));
                    } else {
                        uDatabase.child(id).child("walletmoney").setValue(onRecivMoney);
                        if (UserInFormation.getId().equals(id))
                            UserInFormation.setWalletmoney(String.valueOf((onRecivMoney)));
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
    public void addDeniedMoney(String id, Data orderData) {
        if(UserInFormation.getDeniedMoney() != 0) {
            int onDeniedMoney = UserInFormation.getDeniedMoney();
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("walletmoney").exists()) {
                        int currentValue = Integer.parseInt(Objects.requireNonNull(snapshot.child("walletmoney").getValue()).toString());
                        uDatabase.child(id).child("walletmoney").setValue(currentValue + onDeniedMoney);
                        if (UserInFormation.getId().equals(id))
                            UserInFormation.setWalletmoney(String.valueOf((currentValue + onDeniedMoney)));
                    } else {
                        uDatabase.child(id).child("walletmoney").setValue(onDeniedMoney);
                        if (UserInFormation.getId().equals(id))
                            UserInFormation.setWalletmoney(String.valueOf((onDeniedMoney)));
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

                    if (UserInFormation.getId().equals(id)) UserInFormation.setWalletmoney(String.valueOf((currentValue + money)));
                } else {
                    uDatabase.child(id).child("walletmoney").setValue(money);
                    if (UserInFormation.getId().equals(id)) UserInFormation.setWalletmoney(String.valueOf((money)));
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

    public void addToUser(String id, int money, Data orderData, String Action) {
        CaptinMoney captinMoney = new CaptinMoney(orderData.getId(), Action, datee, "false", orderData.getTrackid(), String.valueOf(money));
        uDatabase.child(id).child("payments").push().setValue(captinMoney);
        Timber.i("Add Money in Logs");
    }

    // ----- Add Money to Delv
    public void addDelvMoney(String id, Data orderData) {
        if(UserInFormation.getDelvMoney() != 0) {
            int onDelvMoney = UserInFormation.getDelvMoney();
            uDatabase.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.child("walletmoney").exists()) {
                        int currentValue = Integer.parseInt(Objects.requireNonNull(snapshot.child("walletmoney").getValue()).toString());
                        uDatabase.child(id).child("walletmoney").setValue(currentValue + onDelvMoney);
                        if (UserInFormation.getId().equals(id)) UserInFormation.setWalletmoney(String.valueOf(currentValue + onDelvMoney));
                    } else {
                        uDatabase.child(id).child("walletmoney").setValue(onDelvMoney);
                        if (UserInFormation.getId().equals(id))
                            UserInFormation.setWalletmoney(String.valueOf((onDelvMoney)));
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
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    // ------ Add Package Money into Supplier Account
    public void addToSupplier(String gMoney, String uId, Data orderData) {
        int money = Integer.parseInt(gMoney);

        uDatabase.child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                Timber.i("Added Money To Supplier %s", Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                addToUser(uId, Integer.parseInt(gMoney), orderData, "ourmoney");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}