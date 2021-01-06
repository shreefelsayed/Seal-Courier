package com.armjld.rayashipping.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.armjld.rayashipping.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.Timer;
import java.util.TimerTask;

public class StartUp extends AppCompatActivity {

    public static double rate = 1;
    SharedPreferences sharedPreferences = null;
    private final String TAG = "StartUp";
    int codee = 10001;
    static DatabaseReference uDatabase;
    boolean doubleBackToExitPressedOnce = false;
    public static double minVersion;
    public static double lastVersion;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "اضغط مرة اخري للخروج من التطبيق", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.activity_startup);

        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");

        checkMode();
        PackageInfo pInfo = null;
        try {
            pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        assert pInfo != null;
        String version = pInfo.versionName;
        double dVersion = Double.parseDouble(version);
        Log.i("StartUp", "Current Version : " + dVersion);

        // -------------- Check for Updates --------------- //
        FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder().setMinimumFetchIntervalInSeconds(1200).build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                minVersion = mFirebaseRemoteConfig.getDouble("min_version_rayah");
                lastVersion = mFirebaseRemoteConfig.getDouble("last_version_rayah");
                sharedPreferences = getSharedPreferences("com.armjld.rayashipping", MODE_PRIVATE);

                if(dVersion >= minVersion) {
                    if(dVersion >= lastVersion) {
                        whatToDo();
                    } else {
                        MaterialDialog materialDialog = new MaterialDialog.Builder(StartUp.this).setTitle("يوجد تحديث جديد").setMessage("هل ترغب في التحديث الأن ؟").setCancelable(false).setPositiveButton("تحديث الان", (dialogInterface, which) -> {
                            openWebURL("https://play.google.com/store/apps/details?id=com.armjld.rayashipping");
                            dialogInterface.dismiss();
                            finish();
                        }).setNegativeButton("لاحقا", (dialogInterface, which) -> {
                            dialogInterface.dismiss();
                            whatToDo();
                        }).build();
                        materialDialog.show();
                    }
                } else {
                    MaterialDialog materialDialog = new MaterialDialog.Builder(StartUp.this).setTitle("يوجد تحديث جديد").setMessage("متوفر الأن تحديث جديد للبرنامج, يجب تحديث البرنامج للاستمرار").setCancelable(false).setPositiveButton("تحديث", (dialogInterface, which) -> {
                        openWebURL("https://play.google.com/store/apps/details?id=com.armjld.rayashipping");
                        dialogInterface.dismiss();
                        finish();
                    }).build();
                    materialDialog.show();
                }
            }
        });

    }

    private void checkMode() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void openWebURL(String inURL) {
        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse(inURL) );
        startActivity(browse);
    }

    private void whatToDo() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("com.armjld.rayashipping", MODE_PRIVATE);
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    LoginManager _lgnMn = new LoginManager();
                    _lgnMn.setMyInfo(StartUp.this);
                    FirebaseCrashlytics.getInstance().setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                } else {
                    finish();
                    startActivity(new Intent(StartUp.this, Login_Options.class));
                }
            }
        }, 2500);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == codee) {
            if (resultCode != RESULT_OK) {
                whatToDo();
                Toast.makeText(this, "لم يتم تحديث التطبيق", Toast.LENGTH_SHORT).show();
            }
        }
    }





}
