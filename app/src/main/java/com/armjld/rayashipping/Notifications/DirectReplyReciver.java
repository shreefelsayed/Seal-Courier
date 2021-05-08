package com.armjld.rayashipping.Notifications;

import android.app.NotificationManager;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.armjld.rayashipping.Models.UserInFormation;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class DirectReplyReciver extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        final Bundle params = intent.getExtras();

        assert params != null;
        String to = params.getString("to");
        String roomid = params.getString("roomid");

        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);
        if (remoteInput != null) {
            String msg = (String) remoteInput.getCharSequence("chatKey");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
            String datee = sdf.format(new Date());

            assert roomid != null;
            String msgid = FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomid).push().getKey();
            HashMap<String, Object> newMsg = new HashMap<>();
            newMsg.put("reciverid", to);
            newMsg.put("senderid", UserInFormation.getId());
            newMsg.put("msg", msg);
            newMsg.put("timestamp", datee);
            newMsg.put("newMsg", "true");
            newMsg.put("msgid", msgid);

            assert msgid != null;
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("chatRooms").child(roomid).child(msgid).setValue(newMsg);

            assert to != null;
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(UserInFormation.getId()).child("chats").child(to).child("timestamp").setValue(datee);
            FirebaseDatabase.getInstance().getReference().child("Pickly").child("users").child(to).child("chats").child(UserInFormation.getId()).child("timestamp").setValue(datee);


            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(1000);
            finish();
        }

    }

}
