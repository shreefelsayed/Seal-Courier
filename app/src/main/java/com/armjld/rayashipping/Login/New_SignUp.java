package com.armjld.rayashipping.Login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.models.Captins;
import com.armjld.rayashipping.models.UserInFormation;
import com.armjld.rayashipping.models.userData;
import com.chivorn.smartmaterialspinner.SmartMaterialSpinner;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mukesh.OtpView;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class New_SignUp extends AppCompatActivity {

    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    public static String defultPP = "https://firebasestorage.googleapis.com/v0/b/shipply-fc7a0.appspot.com/o/Main%2FCaptin.jpg?alt=media&token=62b8df38-82de-4666-8b9c-9775c660e3e3";
    public static String newFirstName = "";
    public static String newLastName = "";
    public static String newEmail = "";
    public static String newPass = "";
    public String mVerificationId = "";
    FloatingActionButton btnNext;

    EditText txtFirstName, txtLastName, txtEmail, txtPass1, txtPass2, txtPhone, txtSuperVisor;
    OtpView txtCode;
    ImageView btnBack;
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    String acDate = sdf2.format(new Date());
    RadioButton rdMotor, rdCar, rdRun;
    String phoneNumb;
    TextView txtPrivacy;
    String newType = "Delivery Worker";
    CountryCodePicker ccp;
    int TAKE_IMAGE_CODE = 10001;
    String cCode = "+20";
    String isCar = "false";
    String isMotor = "false";
    String isTrans = "false";
    String strCity = "";
    String strGov = "";
    CountDownTimer resend;
    Button btnCar, btnMotor, btnRun;
    String trans;
    TextInputLayout tlFirstName, tlLastName, tlPass1, tlPass2, tlEmail, tlPhone, tlSuperVisor;
    SmartMaterialSpinner spnGov, spnCity;
    TextView txtResend;
    CheckBox chkPrivacy;
    TextView tbTitle;
    String supId = "";
    ArrayList<String> govs = new ArrayList<>();
    ArrayList<String> filterCityDrop = new ArrayList<>();
    private ProgressDialog mdialog;
    private ViewFlipper viewFlipper;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Bitmap bitmap;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    private ImageView imgSetPP;

    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                if (source.getHeight() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, targetWidth, maxLength, false);
            } else {
                if (source.getWidth() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (maxLength * aspectRatio);

                return Bitmap.createScaledBitmap(source, maxLength, targetHeight, false);
            }
        } catch (Exception e) {
            return source;
        }
    }

    @SuppressLint({"NewApi", "Recycle"})
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception ignored) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        showPrev();
    }

    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__sign_up);

        viewFlipper = findViewById(R.id.viewFlipper);
        mdialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
        tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("حساب جديد");

        btnNext = findViewById(R.id.btnNext);
        btnBack = findViewById(R.id.btnBack);
        txtPrivacy = findViewById(R.id.txtPrivacy);
        txtPrivacy.setPaintFlags(txtPrivacy.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        chkPrivacy = findViewById(R.id.chkPrivacy);
        tlPhone = findViewById(R.id.tlPhone);
        tlSuperVisor = findViewById(R.id.tlSuperVisor);


        btnCar = findViewById(R.id.btnCar);
        btnMotor = findViewById(R.id.btnMotor);
        btnRun = findViewById(R.id.btnRun);
        rdCar = findViewById(R.id.rdCar);
        rdRun = findViewById(R.id.rdRun);
        rdMotor = findViewById(R.id.rdMotor);

        spnGov = findViewById(R.id.spnGov);
        spnCity = findViewById(R.id.spnCity);


        txtFirstName = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass1 = findViewById(R.id.txtPass1);
        txtPass2 = findViewById(R.id.txtPass2);
        imgSetPP = findViewById(R.id.imgCaptin);
        txtPhone = findViewById(R.id.txtPhone);
        txtSuperVisor = findViewById(R.id.txtSuperVisor);
        ccp = findViewById(R.id.ccp);

        txtCode = findViewById(R.id.txtCode);
        txtResend = findViewById(R.id.txtResend);

        tlFirstName = findViewById(R.id.tlFirstName);
        tlLastName = findViewById(R.id.tlLastName);
        tlPass1 = findViewById(R.id.tlPass1);
        tlPass2 = findViewById(R.id.tlPass2);
        tlEmail = findViewById(R.id.tlEmail);


        viewFlipper.setDisplayedChild(0);

        txtEmail.setText(newEmail);
        txtLastName.setText(newLastName);
        txtFirstName.setText(newFirstName);

        String[] cities = getResources().getStringArray(R.array.arrayCities);
        for (String city : cities) {
            String[] filterSep = city.split(", ");
            String filterGov = filterSep[0].trim();
            govs.add(filterGov);
        }

        Set<String> set = new HashSet<>(govs);
        govs.clear();
        govs.addAll(set);
        Collections.sort(govs);


        spnCity.setEnabled(false);
        spnGov.setItem(govs);
        spnGov.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                strGov = govs.get(position);
                getCities();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        spnCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                strCity = filterCityDrop.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        textWatchers();

        txtResend.setOnClickListener(v -> {
            if (phoneNumb == null) {
                viewFlipper.setDisplayedChild(0);
                btnBack.setVisibility(View.VISIBLE);
                return;
            }
            Toast.makeText(this, "اعادة ارسال الكود", Toast.LENGTH_SHORT).show();
            mdialog.setMessage("اعادة ارسال كود التفعيل");
            mdialog.show();
            sendCode(phoneNumb);
        });

        txtCode.setOtpCompletionListener(otp -> verifyPhoneNumberWithCode(mVerificationId, otp));

        btnBack.setOnClickListener(v -> showPrev());

        //txtPrivacy.setOnClickListener(v-> startActivity(new Intent(this, Terms.class)));

        btnCar.setOnClickListener(v -> {

            isCar = "true";
            isMotor = "false";
            isTrans = "false";

            trans = "Car";

            btnCar.setBackgroundResource(R.drawable.btn_defult);
            btnMotor.setBackgroundResource(R.drawable.btn_bad);
            btnRun.setBackgroundResource(R.drawable.btn_bad);

            rdCar.setChecked(true);
            rdRun.setChecked(false);
            rdMotor.setChecked(false);
        });

        btnMotor.setOnClickListener(v -> {
            isCar = "false";
            isMotor = "true";
            isTrans = "false";

            trans = "Motor";


            btnMotor.setBackgroundResource(R.drawable.btn_defult);
            btnCar.setBackgroundResource(R.drawable.btn_bad);
            btnRun.setBackgroundResource(R.drawable.btn_bad);

            rdCar.setChecked(false);
            rdRun.setChecked(false);

            rdMotor.setChecked(true);
        });

        btnMotor.setOnClickListener(v -> {
            isCar = "false";
            isMotor = "false";
            isTrans = "true";

            trans = "Trans";


            btnMotor.setBackgroundResource(R.drawable.btn_bad);
            btnCar.setBackgroundResource(R.drawable.btn_bad);
            btnRun.setBackgroundResource(R.drawable.btn_defult);


            rdCar.setChecked(false);
            rdMotor.setChecked(false);
            rdRun.setChecked(true);

        });


        ccp.setOnCountryChangeListener(selectedCountry -> cCode = ccp.getSelectedCountryCodeWithPlus());

        btnNext.setOnClickListener(v -> showNext());


        //Set PP
        imgSetPP.setOnClickListener(v -> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(New_SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });

        Picasso.get().load(Uri.parse(defultPP)).into(imgSetPP);

    }

    private void textWatchers() {


        txtFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlFirstName.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlPhone.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlLastName.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtPass1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlPass1.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtPass2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlPass2.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tlEmail.setErrorEnabled(false);
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void getCities() {
        filterCityDrop.clear();
        filterCityDrop.trimToSize();
        String[] cities = getResources().getStringArray(R.array.arrayCities);
        for (String city : cities) {
            String[] filterSep = city.split(", ");
            String filterGov = filterSep[0].trim();
            String filterCity = filterSep[1].trim();
            if (filterGov.equals(strGov)) {
                filterCityDrop.add(filterCity);
            }
        }
        Collections.sort(filterCityDrop);
        spnCity.setItem(filterCityDrop);
        spnCity.setEnabled(true);
    }

    private void showPrev() {
        hideKeyboard(this);
        switch (viewFlipper.getDisplayedChild()) {
            case 0: {
                finish();
                break;
            }
            case 1: {
                clearTexts();
                viewFlipper.setDisplayedChild(0);
                break;
            }
            case 2: {
                txtCode.setText("");
                viewFlipper.setDisplayedChild(1);
                break;
            }
            default: {
                viewFlipper.showPrevious();
                break;
            }
        }
    }

    private void showNext() {
        hideKeyboard(this);
        switch (viewFlipper.getDisplayedChild()) {
            case 0: {
                if (isCar.equals("false") && isMotor.equals("false") && isTrans.equals("false")) {
                    Toast.makeText(this, "الرجاء اختيار وسيلة نقل واحدة", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!govs.contains(strGov) || !filterCityDrop.contains(strCity)) {
                    Toast.makeText(this, "الرجاء اختيار المحافظه و المدينه", Toast.LENGTH_SHORT).show();
                    return;
                }

                viewFlipper.showNext();
                break;
            }
            case 1: {
                String phone = txtPhone.getText().toString().trim();
                if (txtFirstName.getText().toString().isEmpty()) {
                    tlFirstName.setError("الرجاء ادخال الاسم الاول");
                    txtFirstName.requestFocus();
                    return;
                }

                if (txtLastName.getText().toString().isEmpty()) {
                    tlLastName.setError("الرجاء ادخال الاسم الاخير");
                    txtLastName.requestFocus();
                    return;
                }

                if (txtEmail.getText().toString().isEmpty()) {
                    tlEmail.setError("الرجاء ادخال البريد الالكتروني");
                    txtEmail.requestFocus();
                    return;
                }

                if (txtPhone.getText().toString().isEmpty()) {
                    tlPhone.setError("الرجاء ادخال رقم الهاتف");
                    txtPhone.requestFocus();
                    return;
                }

                if (phone.charAt(0) == '1' && phone.length() != 10) {
                    tlPhone.setError("رقم الهاتف غير صحيح");
                    txtPhone.requestFocus();
                    return;
                }

                if (phone.charAt(0) == '0' && phone.length() != 11) {
                    tlPhone.setError("رقم الهاتف غير صحيح");
                    txtPhone.requestFocus();
                    return;
                }

                if (txtPass1.getText().toString().isEmpty()) {
                    tlPass1.setError("الرجاء ادخال كلمه السر");
                    txtPass1.requestFocus();
                    return;
                }
                if (txtPass1.getText().toString().length() < 6) {
                    tlPass1.setError("الرجاء ادخال كلمه سر من 6 ارقام علي الاقل");
                    txtPass1.requestFocus();
                    return;
                }
                if (!txtPass1.getText().toString().equals(txtPass2.getText().toString())) {
                    tlPass2.setError("تاكد من تطابق كلمة السر");
                    txtPass2.requestFocus();
                    return;
                }

                if (txtSuperVisor.getText().toString().length() != 8) {
                    tlSuperVisor.setError("تأكد من كود المشرف");
                    txtSuperVisor.requestFocus();
                    return;
                }

                if (!chkPrivacy.isChecked()) {
                    Toast.makeText(this, "بالرجاء مراجعه الشروط و الاحكام.", Toast.LENGTH_LONG).show();
                    return;
                }

                newEmail = txtEmail.getText().toString();
                newPass = txtPass1.getText().toString();
                newFirstName = txtFirstName.getText().toString();
                newLastName = txtLastName.getText().toString();

                mdialog.setMessage("جاري التاكد من رقم الهاتف ..");
                mdialog.show();

                if (phone.length() == 10) {
                    phoneNumb = phone;
                } else {
                    phoneNumb = phone.substring(1, 11);
                }

                uDatabase.orderByChild("phone").equalTo("0" + phoneNumb).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            mdialog.dismiss();
                            Toast.makeText(New_SignUp.this, "رقم الهاتف مسجل مسبقا", Toast.LENGTH_LONG).show();
                        } else {
                            uDatabase.orderByChild("email").equalTo(txtEmail.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        mdialog.dismiss();
                                        txtEmail.requestFocus();
                                        tlEmail.setError("البريد الالكتروني مسجل مسبقا");
                                        Toast.makeText(New_SignUp.this, "البريد الالكتروني مسجل مسبقا", Toast.LENGTH_LONG).show();
                                    } else {
                                        mdialog.setMessage("جاري التأكد من رقم المشرف ..");
                                        checkSuperVisor(txtSuperVisor.getText().toString().trim());

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(New_SignUp.this, "حدث خطأ في التاكد من البيانات", Toast.LENGTH_SHORT).show();
                        mdialog.dismiss();
                    }
                });
                break;
            }

            case 2: {
                if (Objects.requireNonNull(txtCode.getText()).toString().length() != 6) {
                    Toast.makeText(this, "الكود الذي ادخلته خطأ", Toast.LENGTH_SHORT).show();
                    return;
                }

                mdialog.setMessage("جاري التأكد من الرمز ..");
                mdialog.show();
                verifyPhoneNumberWithCode(mVerificationId, txtCode.getText().toString().trim());
                break;
            }

        }
    }

    private void checkSuperVisor(String code) {
        uDatabase.orderByChild("supervisor_code").equalTo(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot superV : snapshot.getChildren()) {
                        mdialog.setMessage("جاري ارسال الكود ..");
                        mCallBack();
                        sendCode(phoneNumb);
                        supId = Objects.requireNonNull(superV.child("id").getValue()).toString();
                        break;
                    }
                } else {
                    mdialog.dismiss();
                    txtSuperVisor.requestFocus();
                    tlSuperVisor.setError("كود المشرف غير صحيح");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void sendCode(String uPhone) {

        if (mCallbacks == null) {
            mCallBack();
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                cCode + uPhone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        mdialog.setMessage("جاري التاكد من الكود ..");
        if (verificationId.equals("")) {
            Toast.makeText(this, "حدث خطأ في لتاكد من الرمز الرجاء الابلاغ عنه", Toast.LENGTH_LONG).show();
            mdialog.dismiss();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signUp(credential);
    }

    private void setUserData() {
        String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String memail = txtEmail.getText().toString().trim().toLowerCase();
        String mpass = txtPass1.getText().toString().trim();
        String muser = newFirstName + " " + newLastName;
        String mPhone = "0" + phoneNumb;
        String supCode = txtSuperVisor.getText().toString().trim();

        userData User = new userData(muser,
                mPhone,
                memail,
                acDate,
                id,
                newType,
                defultPP,
                "",
                mpass,
                "0",
                "",
                "",
                "",
                "",
                "true",
                "",
                strGov,
                strCity,
                "all",
                "none",
                "false",
                "false",
                0,
                "true",
                "Raya");

        uDatabase.child(id).setValue(User);

        // ------- set extra values
        uDatabase.child(id).child("transType").setValue(trans);
        uDatabase.child(id).child("isCar").setValue(isCar);
        uDatabase.child(id).child("isMotor").setValue(isMotor);
        uDatabase.child(id).child("isTrans").setValue(isTrans);

        uDatabase.child(id).child("isBike").setValue("false");

        uDatabase.child(id).child("mySuper").setValue(supCode);
        uDatabase.child(id).child("mySuperId").setValue(supId);
        uDatabase.child(id).child("supervisor_code").setValue("");

        // ------- add to the sup
        Captins captins = new Captins(muser, mPhone, id);
        uDatabase.child(supId).child("captins").child(id).setValue(captins);

        String locID = uDatabase.child(id).child("locations").push().getKey();
        assert locID != null;
        DatabaseReference Bdatabase = uDatabase.child(id).child("locations").child(locID);
        Bdatabase.child("state").setValue(strGov);
        Bdatabase.child("region").setValue(strCity);
        Bdatabase.child("title").setValue("عنوان 1");
        Bdatabase.child("id").setValue(locID);

        // ------------------ Set Device Token ----------------- //
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(New_SignUp.this, instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            uDatabase.child(id).child("device_token").setValue(deviceToken);
        });

        if (bitmap != null) {
            handleUpload(bitmap);
        } else {
            uDatabase.child(id).child("ppURL").setValue(defultPP);
            mdialog.dismiss();
        }

        // ------ add to today users
        SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String userDate = sdf0.format(new Date());

        DatabaseReference newUser = FirebaseDatabase.getInstance().getReference().child("Pickly").child("userByDay").child("Raya").child(userDate).child(id);
        newUser.child("phone").setValue(mPhone);
        newUser.child("name").setValue(muser);
        newUser.child("id").setValue(id);
        newUser.child("provider").setValue("Raya");

        Toast.makeText(getApplicationContext(), "تم انشاء حسابك بنجاح", Toast.LENGTH_LONG).show();
        mdialog.dismiss();

        finish();
        LoginManager _lgn = new LoginManager();
        _lgn.setMyInfo(this);
    }

    private void signUp(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(New_SignUp.this, taskPhone -> {
            if (taskPhone.isSuccessful()) {
                AuthCredential emailCred = EmailAuthProvider.getCredential(newEmail, newPass);
                Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(emailCred).addOnCompleteListener(New_SignUp.this, taskEmail -> {
                    if (taskEmail.isSuccessful()) {
                        setUserData();
                    } else {
                        txtCode.setText("");
                        mdialog.dismiss();
                        Toast.makeText(this, "حدث خطأ ما حاول لاحقا", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (taskPhone.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    txtCode.setText("");
                    mdialog.dismiss();
                    Toast.makeText(this, "كود التفعيل غير صحيح", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // --------------------------------- Phone Number Functions -------------------------- //
    private void mCallBack() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                mdialog.dismiss();
                txtCode.setText(credential.getSmsCode());

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mdialog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(New_SignUp.this, "رقم هاتف غير صحيح", Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), "لقد تخطيت عدد المحاولات المسموح به, حاول لاحقا", Snackbar.LENGTH_LONG).show();
                }
                Toast.makeText(New_SignUp.this, "Send to IT : " + e.toString(), Toast.LENGTH_LONG).show();
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                txtCode.setText("");
                Toast.makeText(New_SignUp.this, "تم ارسال الرمز", Toast.LENGTH_SHORT).show();
                mdialog.dismiss();
                mVerificationId = verificationId;
                viewFlipper.setDisplayedChild(2);
                btnBack.setVisibility(View.GONE);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                bitmap = resizeBitmap(source, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(New_SignUp.this, photoUri));
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if (uri != null) {
                bitmap = rotateImage(bitmap, uri);
            }
            assert uri != null;
            File f = new File("" + uri);
            imgSetPP.setImageBitmap(bitmap);
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, Uri uri) {
        ExifInterface exifInterface = null;
        try {
            if (uri == null) {
                return bitmap;
            }
            exifInterface = new ExifInterface(String.valueOf(uri));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (exifInterface != null) {
            int orintation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
            if (orintation == 6 || orintation == 3 || orintation == 8) {
                Matrix matrix = new Matrix();
                if (orintation == 6) {
                    matrix.postRotate(90);
                } else if (orintation == 3) {
                    matrix.postRotate(180);
                } else {
                    matrix.postRotate(270);
                }
                return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            } else {
                return bitmap;
            }
        } else {
            return bitmap;
        }
    }

    private void handleUpload(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        String uID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("ppUsers").child(uID + ".jpeg");
        final String did = uID;
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> getDownUrl(did, reference));
    }

    private void getDownUrl(final String uIDd, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            uDatabase.child(uIDd).child("ppURL").setValue(uri.toString());
            UserInFormation.setUserURL(uri.toString());
            mdialog.dismiss();
        });
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(New_SignUp.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(New_SignUp.this, new String[]{permission}, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(New_SignUp.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(New_SignUp.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void clearTexts() {
        txtFirstName.setText("");
        txtEmail.setText("");
        txtLastName.setText("");
        txtPhone.setText("");
        txtPass1.setText("");
        txtPass2.setText("");
        txtSuperVisor.setText("");
    }
}