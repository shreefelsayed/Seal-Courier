package com.armjld.rayashipping.Login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.armjld.rayashipping.Locations.LocationManager;
import com.armjld.rayashipping.Ratings.Ratings;
import com.armjld.rayashipping.UserDatabase;
import com.armjld.rayashipping.Models.UserInFormation;
import com.armjld.rayashipping.Models.UserData;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;


public class LoginManager {
    public static boolean dataset = false;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    UserDatabase userDatabase = new UserDatabase();

    SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
    String today = sdf0.format(new Date());

    public void setMyInfo(Context mContext) {
        mAuth = FirebaseAuth.getInstance();
        uDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).keepSynced(true);

        uDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    UserData User = snapshot.getValue(UserData.class);
                    assert User != null;

                    if (User.getActive().equals("false")) {
                        Toast.makeText(mContext, "تم اغلاق حسابك، الرجاء الاتصال بالدعم الفني", Toast.LENGTH_SHORT).show();
                        clearInfo(mContext);
                        return;
                    }

                    if (!User.getProvider().equals("Raya")) {
                        Toast.makeText(mContext, "لا يمكنك تسجيل الدخول", Toast.LENGTH_SHORT).show();
                        clearInfo(mContext);
                        return;
                    }

                    if (!User.getAccountType().equals("Delivery Worker") && !User.getAccountType().equals("Supervisor")) {
                        Toast.makeText(mContext, "لا يمكنك تسجيل الدخول", Toast.LENGTH_SHORT).show();
                        clearInfo(mContext);
                        return;
                    }

                    setSignInValues(mContext);
                    UserInFormation.setUser(User);

                    Ratings _ratings = new Ratings();
                    _ratings.setMyRating();
                    
                    if (UserInFormation.getUser().getAccountType().equals("Delivery Worker")) {
                        if (!snapshot.child("locations").exists() && snapshot.child("userCity").exists() && snapshot.child("userState").exists()) {
                            String locid = uDatabase.child(UserInFormation.getUser().getId()).child("locations").push().getKey();

                            HashMap<String, Object> newLocation = new HashMap<>();
                            newLocation.put("state", snapshot.child("userState").getValue().toString());
                            newLocation.put("region", snapshot.child("userCity").getValue().toString());
                            newLocation.put("title", "عنوان 1");
                            newLocation.put("id", locid);
                            uDatabase.child(UserInFormation.getUser().getId()).child("locations").child(locid).setValue(newLocation);
                        }

                        if (!snapshot.child("locations").exists() && !snapshot.child("userCity").exists()) {
                            String locid = uDatabase.child(UserInFormation.getUser().getId()).child("locations").push().getKey();
                            HashMap<String, Object> newLocation = new HashMap<>();
                            newLocation.put("state", "القاهرة");
                            newLocation.put("region", "الزمالك");
                            newLocation.put("title", "عنوان 1");
                            newLocation.put("id", locid);
                            uDatabase.child(UserInFormation.getUser().getId()).child("locations").child(locid).setValue(newLocation);
                            uDatabase.child(UserInFormation.getUser().getId()).child("userCity").setValue("الزمالك");
                            uDatabase.child(UserInFormation.getUser().getId()).child("userState").setValue("القاهرة");

                        }

                        if (snapshot.child("isCar").exists() && snapshot.child("isMotor").exists() && snapshot.child("isTrans").exists() && snapshot.child("isBike").exists()) {
                            if (snapshot.child("isTrans").getValue().toString().equals("true")) {
                                UserInFormation.getUser().setTransType("Trans");
                            }

                            if (snapshot.child("isCar").getValue().toString().equals("true")) {
                                UserInFormation.getUser().setTransType("Car");
                            }

                            if (snapshot.child("isBike").getValue().toString().equals("true")) {
                                UserInFormation.getUser().setTransType("Bike");
                            }

                            if (snapshot.child("isMotor").getValue().toString().equals("true")) {
                                UserInFormation.getUser().setTransType("Motor");
                            }
                        } else {
                            uDatabase.child(UserInFormation.getUser().getId()).child("isCar").setValue("false");
                            uDatabase.child(UserInFormation.getUser().getId()).child("isBike").setValue("false");
                            uDatabase.child(UserInFormation.getUser().getId()).child("isMotor").setValue("false");
                            uDatabase.child(UserInFormation.getUser().getId()).child("isTrans").setValue("true");
                            UserInFormation.getUser().setTransType("Trans");
                        }
                    }

                    NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();

                    dataset = true;
                    LocationManager _locMang = new LocationManager();
                    _locMang.ImportLocation();

                    mContext.startActivity(new Intent(mContext, LoadingScreen.class));
                    ((Activity) mContext).finish();
                } else {
                    mContext.startActivity(new Intent(mContext, Login_Options.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void setSignInValues(Context mContext) {
        // --- Set Device Name ---
        userDatabase.setValue("device", getDeviceName());

        // ---- Set App Version ---
        try {
            PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            String version = pInfo.versionName;
            userDatabase.setValue("app_version", version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // ------ Set IP Address ---
        new GetPublicIP().execute();
        @SuppressLint("HardwareIds") String android_id = Secure.getString(mContext.getContentResolver(), Secure.ANDROID_ID);
        userDatabase.setValue("unique_id", android_id);


        // ----- Set Device Token
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity) mContext, instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            userDatabase.setValue("device_token", deviceToken);
        });
    }

    public void clearInfo(Context mContext) {
        userDatabase.setValue("device_token", "");
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        mAuth.signOut();
        UserInFormation.clearUser();
        dataset = false;

        ((Activity) mContext).finish();
        mContext.startActivity(new Intent(mContext, Login_Options.class));
        Toast.makeText(mContext, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("StaticFieldLeak")
    public class GetPublicIP extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String publicIP = "";
            try  {
                java.util.Scanner s = new java.util.Scanner(new java.net.URL("https://api.ipify.org").openStream(), "UTF-8").useDelimiter("\\A");
                publicIP = s.next();
                System.out.println("My current IP address is " + publicIP);
            } catch (java.io.IOException e) {
                e.printStackTrace();
            }

            return publicIP;
        }

        @Override
        protected void onPostExecute(String publicIp) {
            super.onPostExecute(publicIp);
            userDatabase.setValue("ip", publicIp);
        }
    }

    /** Returns the consumer friendly device name */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

}
