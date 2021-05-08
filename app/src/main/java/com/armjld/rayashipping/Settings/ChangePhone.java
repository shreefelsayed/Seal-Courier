package com.armjld.rayashipping.Settings;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Models.UserInFormation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mukesh.OtpView;

import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class ChangePhone extends AppCompatActivity {

    public String mVerificationId = "";
    EditText txtPhone;
    OtpView txtOTP;
    FloatingActionButton btnNext;
    ViewFlipper viewFlipper;
    String strPhone, strOTP;
    ImageView btnBack;
    CountDownTimer resend;
    TextView txtResend;
    TextInputLayout tlPhone;
    String oldPhone;
    ProgressDialog mdialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_phone);

        mdialog = new ProgressDialog(this);

        //Title Bar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("تغيير رقم الهاتف");

        txtPhone = findViewById(R.id.txtPhone);
        txtOTP = findViewById(R.id.txtCode);
        txtResend = findViewById(R.id.txtResend);
        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        viewFlipper = findViewById(R.id.viewFlipper);
        tlPhone = findViewById(R.id.tlPhone);
        txtPhone.setText(UserInFormation.getuPhone());

        oldPhone = UserInFormation.getuPhone();

        txtOTP.setOtpCompletionListener(otp -> verifyPhoneNumberWithCode(mVerificationId, Objects.requireNonNull(txtOTP.getText()).toString()));
        viewFlipper.setDisplayedChild(0);

        btnBack.setOnClickListener(v -> showPrev());
        btnNext.setOnClickListener(v -> showNext());

        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txtPhone.getText().toString().trim().length() == 11 && !txtPhone.getText().toString().equals(oldPhone)) {
                    tlPhone.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        txtResend.setOnClickListener(v -> {
            if (strPhone == null) {
                viewFlipper.setDisplayedChild(0);
                btnBack.setVisibility(View.VISIBLE);
                return;
            }
            Toast.makeText(this, "اعادة ارسال الكود", Toast.LENGTH_SHORT).show();
            mdialog.setMessage("اعادة ارسال كود التفعيل");
            mdialog.show();
            mCallBack();
            sendCode(strPhone);
        });
    }

    private void showNext() {
        switch (viewFlipper.getDisplayedChild()) {
            case 0:
                if (txtPhone.getText().toString().length() != 11) {
                    tlPhone.setError("ادخل رقم هاتف مكون من ١١ رقم");
                    return;
                }

                if (txtPhone.getText().toString().equals(oldPhone)) {
                    tlPhone.setError("لا يمكنك ادخال رقمك القديم");
                    return;
                }
                check(txtPhone.getText().toString().trim());
                break;
            case 1:
                strOTP = Objects.requireNonNull(txtOTP.getText()).toString();
                verifyPhoneNumberWithCode(mVerificationId, strOTP);
                break;
        }
    }

    private void showPrev() {
        switch (viewFlipper.getDisplayedChild()) {
            case 0:
                finish();
                break;
            case 1:
                strOTP = "";
                strPhone = "";
                txtOTP.setText("");
                viewFlipper.showPrevious();
                break;
        }
    }

    private void check(String trim) {
        mdialog.setMessage("جاري التأكد من رقم الهاتف ..");
        mdialog.setCancelable(false);
        mdialog.show();
        FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").orderByChild("phone").equalTo(trim).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(ChangePhone.this, "رقم الهاتف مسجل في حساب أخر", Toast.LENGTH_SHORT).show();
                    mdialog.dismiss();
                } else {
                    strPhone = trim;
                    mCallBack();
                    sendCode(strPhone);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void sendCode(String uPhone) {
        mdialog.setMessage("جاري ارسال الكود ..");
        mdialog.setCancelable(false);
        mdialog.show();
        if (mCallbacks == null) {
            mCallBack();
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+2" + uPhone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        mdialog.setMessage("جاري التاكد من الكود ..");
        mdialog.setCancelable(false);
        mdialog.show();

        if (verificationId.equals("")) {
            Toast.makeText(this, "حدث خطأ في لتاكد من الرمز الرجاء الابلاغ عنه", Toast.LENGTH_LONG).show();
            mdialog.dismiss();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        updatePhone(credential);
    }

    private void updatePhone(PhoneAuthCredential phoneAuthCredential) {
        mdialog.setMessage("جاري تحديث رقم الهاتف ..");
        mdialog.setCancelable(false);
        mdialog.show();
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).updatePhoneNumber(phoneAuthCredential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                changeDatabase();
            } else {
                if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    txtOTP.setText("");
                    Toast.makeText(this, "كود التفعيل غير صحيح", Toast.LENGTH_SHORT).show();
                }
            }
            mdialog.dismiss();
        });
    }

    private void changeDatabase() {
        DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId());
        DatabaseReference pDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("fawrypayments");

        uDatabase.child("phone").setValue(strPhone);

        pDatabase.orderByChild("phone").equalTo(UserInFormation.getuPhone()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot payments : snapshot.getChildren()) {
                        String id = Objects.requireNonNull(payments.child("id").getValue()).toString();
                        pDatabase.child(id).child("phone").setValue(strPhone);
                    }
                }

                UserInFormation.setuPhone(strPhone);
                Toast.makeText(ChangePhone.this, "تم تغيير رقم الهاتف بنجاح", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });


    }

    private void mCallBack() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                mdialog.dismiss();
                strOTP = credential.getSmsCode();
                txtOTP.setText(credential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mdialog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(ChangePhone.this, "رقم هاتف غير صحيح", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "لقد تخطيت عدد المحاولات المسموح به, حاول لاحقا", Snackbar.LENGTH_LONG).show();
                }
                Toast.makeText(ChangePhone.this, "Send to IT : " + e.toString(), Toast.LENGTH_LONG).show();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                mdialog.dismiss();
                super.onCodeSent(verificationId, token);
                txtOTP.setText("");
                Toast.makeText(ChangePhone.this, "تم ارسال الرمز", Toast.LENGTH_SHORT).show();
                mdialog.dismiss();
                mVerificationId = verificationId;
                viewFlipper.setDisplayedChild(1);
                startResendTimer();
            }
        };
    }

    private void startResendTimer() {
        resend = new CountDownTimer(60000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long l) {
                txtResend.setEnabled(false);
                txtResend.setText("اضغط هنا لاعادة ارسال كود التفعيل بعد " + l / 1000 + " ثانية");
            }

            @Override
            public void onFinish() {
                txtResend.setEnabled(true);
                txtResend.setText("اضغط لاعادة ارسال الرمز");
            }
        }.start();
    }

}