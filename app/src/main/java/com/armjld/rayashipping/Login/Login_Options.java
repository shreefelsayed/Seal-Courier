package com.armjld.rayashipping.Login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.armjld.rayashipping.R;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import java.util.Objects;
import java.util.regex.Pattern;

public class Login_Options extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    private FirebaseAuth mAuth;
    private ProgressDialog mdialog;
    private EditText email,pass;
    private TextInputLayout tlEmail, tlPass;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__options);

        email = findViewById(R.id.txtEditName);
        pass = findViewById(R.id.txtEditPassword);
        Button btnlogin = findViewById(R.id.btnEditInfo);
        TextView txtForgetPass = findViewById(R.id.txtForgetPass);
        tlEmail = findViewById(R.id.tlEmail);
        tlPass = findViewById(R.id.tlPass);
        TextView signup = findViewById(R.id.signup_text);
        mdialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();

        signup.setOnClickListener(v -> startActivity(new Intent(this, New_SignUp.class)));
        txtForgetPass.setOnClickListener(v -> startActivity(new Intent(this, Forget_Password.class)));

        String uMail = getIntent().getStringExtra("umail");
        String uPass = getIntent().getStringExtra("upass");

        btnlogin.setOnClickListener(v -> {
            String memail = email.getText().toString().trim();
            String mpass = pass.getText().toString().trim();

            // --- Check how to login (Email Or Phone)
            if(memail.length() == 11 && isNumb(memail)) {
                loginWithPhone(memail, mpass);
            } else {
                login(memail, mpass);
            }

        });

        textWatchers();

        if(uMail != null && uPass != null) {
            email.setText(uMail);
            pass.setText(uPass);
            login(uMail,uPass);
        }

    }

    private void textWatchers() {
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tlEmail.setErrorEnabled(false);
            }
        });

        pass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tlPass.setErrorEnabled(false);
            }
        });
    }

    private void loginWithPhone(String phone, String mpass) {
        if (TextUtils.isEmpty(mpass)) {
            tlPass.setError("يجب ادخال كلمه المرور");
            pass.requestFocus();
            return;
        }

        mdialog.setMessage("جاري التاكد من رقم الهاتف ..");
        mdialog.show();

        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").orderByChild("phone").equalTo(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot user : snapshot.getChildren()) {
                        String email = Objects.requireNonNull(user.child("email").getValue()).toString();
                        String realPass = Objects.requireNonNull(user.child("mpass").getValue()).toString();
                        if(!mpass.equals(realPass)) {
                            MaterialDialog materialDialog = new MaterialDialog.Builder(Login_Options.this).setTitle("فشل في تسجيل الدخول").setMessage("تأكد من البريد الالكتروني و كلمة المرور الخاصة بك").setCancelable(true).setPositiveButton("حاول مجددا", (dialogInterface, which) -> dialogInterface.dismiss()).setNegativeButton("استرداد كلمة السر", (dialogInterface, which) -> {
                                dialogInterface.dismiss();
                                startActivity(new Intent(Login_Options.this, Forget_Password.class));
                            }).build();
                            materialDialog.show();
                            mdialog.dismiss();
                        } else {
                            login(email, mpass);
                        }
                        break;
                    }
                } else {
                    mdialog.dismiss();
                    Toast.makeText(Login_Options.this, "رقم الهاتف غير مسجل", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void login(String memail, String mpass) {
        if (TextUtils.isEmpty(memail)) {
            tlEmail.setError("يجب ادخال اسم المستخدم");
            email.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mpass)) {
            tlPass.setError("يجب ادخال كلمه المرور");
            pass.requestFocus();
            return;
        }
        mdialog.setMessage("جاري تسجيل الدخول..");
        mdialog.show();

        mAuth.signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(task -> {
            if (task.isSuccessful() && FirebaseAuth.getInstance().getCurrentUser() != null) {
                mdialog.dismiss();
                LoginManager _lgnMn = new LoginManager();
                _lgnMn.setMyInfo(Login_Options.this);
            } else {
                mdialog.dismiss();
                MaterialDialog materialDialog = new MaterialDialog.Builder(Login_Options.this).setTitle("فشل في تسجيل الدخول").setMessage("تأكد من البريد الالكتروني و كلمة المرور الخاصة بك").setCancelable(true).setPositiveButton("حاول مجددا", (dialogInterface, which) -> dialogInterface.dismiss()).setNegativeButton("استرداد كلمة السر", (dialogInterface, which) -> {
                    dialogInterface.dismiss();
                    startActivity(new Intent(Login_Options.this, Forget_Password.class));
                }).build();
                materialDialog.show();
            }
        });
    }

    public boolean isNumb (String value) {
        return !Pattern.matches("[a-zA-Z]+", value);
    }


}