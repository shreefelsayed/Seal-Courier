package com.armjld.rayashipping.Chat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.rayashipping.Login.StartUp;
import com.armjld.rayashipping.R;
import com.armjld.rayashipping.Home;
import com.armjld.rayashipping.models.Chat;
import com.armjld.rayashipping.models.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Messages extends AppCompatActivity {

    private final DatabaseReference messageDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms");
    private final DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("Pickly").child("users");
    ImageView btnSend;
    private final String uId = UserInFormation.getId();
    private String rId = "";
    private String roomId ;
    ImageView btnBack,imgPPP, btnCall;
    TextView txtName, txtType;
    private static final int PHONE_CALL_CODE = 100;
    public static String cameFrom = "Chats";
    LinearLayout EmptyPanel;
    ImageView imgMsg;
    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    int TAKE_IMAGE_CODE = 10001;
    private Bitmap bitmap;
    private ProgressDialog mdialog;


    EditText editWriteMessage;
    RecyclerView recyclerMsg;

    MessageAdapter messageAdapter;
    List<Chat> mChat;
    String phoneNumb = "";

    @Override
    public void onBackPressed() {
        getBack();
    }


    @SuppressLint("QueryPermissionsNeeded")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        if(uId == null) {
            finish();
            startActivity(new Intent(this, StartUp.class));
        }

        roomId = getIntent().getStringExtra("roomid");
        rId = getIntent().getStringExtra("rid");

        btnSend = findViewById(R.id.btnSend);
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText("");
        editWriteMessage = findViewById(R.id.editWriteMessage);
        recyclerMsg = findViewById(R.id.recyclerMsg);
        btnBack = findViewById(R.id.btnBack);
        txtName = findViewById(R.id.txtName);
        txtType = findViewById(R.id.txtType);
        imgPPP = findViewById(R.id.imgPPP);
        btnCall = findViewById(R.id.btnCall);
        EmptyPanel = findViewById(R.id.EmptyPanel);
        EmptyPanel.setVisibility(View.GONE);
        imgMsg = findViewById(R.id.imgMsg);

        mdialog = new ProgressDialog(this);


        recyclerMsg.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerMsg.setLayoutManager(linearLayoutManager);


        btnBack.setOnClickListener(v-> getBack());

        btnCall.setOnClickListener(v-> {
            if(!phoneNumb.equals("")) {

                BottomSheetMaterialDialog mBottomSheetDialog = new BottomSheetMaterialDialog.Builder(Messages.this).setMessage("هل تريد الاتصال ؟").setCancelable(true).setPositiveButton("نعم", R.drawable.ic_add_phone, (dialogInterface, which) -> {
                    checkPermission(Manifest.permission.CALL_PHONE, PHONE_CALL_CODE);
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + phoneNumb));
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    startActivity(callIntent);

                    dialogInterface.dismiss();
                }).setNegativeButton("لا", R.drawable.ic_close, (dialogInterface, which) -> dialogInterface.dismiss()).build();
                mBottomSheetDialog.show();



            } else {
                Toast.makeText(this, "التاجر لم يضع رقم هاتف", Toast.LENGTH_SHORT).show();
            }
        });

        uDatabase.child(rId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtName.setText(Objects.requireNonNull(snapshot.child("name").getValue()).toString());
                String dataPhone = Objects.requireNonNull(snapshot.child("phone").getValue()).toString();

                if(dataPhone.length() == 11) {
                    phoneNumb = dataPhone;
                } else if (dataPhone.length() == 10) {
                    phoneNumb = "0" + dataPhone;
                } else {
                    phoneNumb = "";
                }

                if(Objects.requireNonNull(snapshot.child("accountType").getValue()).toString().equals("Supplier")) {
                    txtType.setText("تاجر");
                } else if(Objects.requireNonNull(snapshot.child("accountType").getValue()).toString().equals("Delivery Worker")){
                    txtType.setText("كابتن");
                } else {
                    txtType.setText("خدمة العملاء");
                }
                Picasso.get().load(Uri.parse(Objects.requireNonNull(snapshot.child("ppURL").getValue()).toString())).into(imgPPP);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

        imgMsg.setOnClickListener(v-> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });
        
        btnSend.setOnClickListener(v -> {
            String msg = editWriteMessage.getText().toString().trim();

            if(msg.length() == 0) {
                editWriteMessage.requestFocus();
                return;
            }

            String msgid =  FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).push().getKey();
            Chat chat = new Chat(uId,rId,msg,getDate(),"text","","true",msgid);

            FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).child(msgid).setValue(chat);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").child(rId).child("timestamp").setValue(getDate());
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(rId).child("chats").child(uId).child("timestamp").setValue(getDate());
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(rId).child("chats").child(uId).child("newMsg").setValue("true");

            editWriteMessage.setText("");
        });

        readMessage();
    }

    private void readMessage() {
        mChat = new ArrayList<>();
        messageDatabase.child(roomId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        Chat chat = ds.getValue(Chat.class);
                        mChat.add(chat);
                        messageAdapter = new MessageAdapter(Messages.this, mChat);
                        recyclerMsg.setAdapter(messageAdapter);
                        if(chat.getReciverid().equals(UserInFormation.getId())) {
                            messageDatabase.child(roomId).child(chat.getMsgid()).child("newMsg").setValue("false");
                        }
                    }
                }

                checkEmpty(mChat.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void checkEmpty(int size) {
        if(size > 0) {
            EmptyPanel.setVisibility(View.GONE);
        } else {
            EmptyPanel.setVisibility(View.VISIBLE);
        }
    }


    private String getDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
        return sdf.format(new Date());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                bitmap = resizeBitmap(source, 1000);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(this, photoUri));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(uri != null) {
                bitmap = rotateImage(bitmap , uri);
            }
            assert uri != null;

            mdialog.setMessage("جاري ارسال الصوره ..");
            mdialog.show();
            handleUpload(bitmap);
        }
    }

    private Bitmap rotateImage(Bitmap bitmap, Uri uri){
        ExifInterface exifInterface =null;
        try {
            if(uri==null){
                return bitmap;
            }
            exifInterface = new ExifInterface(String.valueOf(uri));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        if(exifInterface != null) {
            int orintation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION ,ExifInterface.ORIENTATION_UNDEFINED);
            if(orintation == 6 || orintation == 3 || orintation == 8) {
                Matrix matrix = new Matrix();
                if (orintation == 6) {
                    matrix.postRotate(90);
                }
                else if (orintation == 3) {
                    matrix.postRotate(180);
                } else {
                    matrix.postRotate(270);
                }
                return Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
            } else {
                return bitmap;
            }
        } else {
            return bitmap;
        }

    }

    private void handleUpload (Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);

        // MSG ID ---
        String msgid =  FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).push().getKey();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("chats").child(roomId).child(msgid + ".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> getDownUrl(msgid, reference)).addOnFailureListener(e -> Log.e("Upload Error: ", "Fail:", e.getCause()));
    }

    private void getDownUrl(String msgid, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            // ---- Send The MSG
            Chat chat = new Chat(UserInFormation.getId(),rId,"صوره",getDate(),"pic",uri.toString(),"true",msgid);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomId).child(msgid).setValue(chat);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(uId).child("chats").child(rId).child("timestamp").setValue(getDate());
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(rId).child("chats").child(uId).child("timestamp").setValue(getDate());
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(rId).child("chats").child(uId).child("newMsg").setValue("true");

            mdialog.dismiss();
        });
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


    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(Messages.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(Messages.this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(Messages.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(Messages.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }

        if (requestCode == PHONE_CALL_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Phone Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Phone Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                if (source.getHeight() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (maxLength * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, maxLength, false);
                return result;
            } else {
                if (source.getWidth() <= maxLength) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (maxLength * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, maxLength, targetHeight, false);
                return result;
            }
        }
        catch (Exception e) { return source; }
    }

    private void getBack() {
        switch (cameFrom) {
            case "Chats" : {
                Home.whichFrag = "Chats";
                startActivity(new Intent(this, Home.class));
                break;
            }
            case "Profile" : {
                //HomeActivity.whichFrag = "Profile";
                finish();
                break;
            }
        }
    }

}