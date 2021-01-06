package com.armjld.rayashipping.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.app.RemoteInput;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.armjld.rayashipping.R;
import com.armjld.rayashipping.SuperVisor.SuperVisorHome;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();

            if(Objects.equals(data.get("type"), "normal")) {
                String nameFrom = data.get("title");
                String statue = data.get("body");
                String action = data.get("action");
                String OrderID = data.get("orderid");
                if(Objects.equals(action, "recived")) {
                    sendOrderRecived(statue, nameFrom, OrderID);
                } else {
                    sendNotification(statue, nameFrom,action, OrderID);
                }
            } else {
                String title = data.get("title");
                String body = data.get("body");
                String roomid = data.get("roomid");
                String to = data.get("to");
                chatNoti(title, body, roomid, to);
            }

            if (true) {
                scheduleJob();
            } else {
                handleNow();
            }
        }

    }

    @Override
    public void onNewToken(@NonNull String token) {
        sendRegistrationToServer(token);
    }

    private void scheduleJob() {
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class).build();
        WorkManager.getInstance().beginWith(work).enqueue();
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendRegistrationToServer(String token) { }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void chatNoti (String title, String body, String roomid, String to) {
        SuperVisorHome.getMessageCount();
        // ------------ Click Notification Event ------------------- //
        Intent chatintent = new Intent(this, SuperVisorHome.class);
        chatintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent2 = PendingIntent.getActivity(this, 1000 /* Request code */, chatintent, 0);


        // ----------------- Click Reply Action Intent ---------------- //
        Intent replyIntent = new Intent(this, DirectReplyReciver.class);


        Bundle params = new Bundle();
        params.putString("roomid", roomid);
        params.putString("to", to);
        replyIntent.putExtras(params);

        PendingIntent replyPendingIntent = PendingIntent.getActivity(this, 1000, replyIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        RemoteInput remoteInput = new RemoteInput.Builder("chatKey").setLabel("Aa..").build();
        NotificationCompat.Action replyAction = new NotificationCompat.Action.Builder(R.drawable.ic_chats, "دردشة", replyPendingIntent).addRemoteInput(remoteInput).build();
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(title).setConversationTitle("Quicker").addMessage(body, 123, title);
        String channelId = getString(R.string.default_notification_channel_id);

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.noti);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setStyle(messagingStyle)
                        .setContentTitle(title)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentText(body)
                        .setAutoCancel(true)
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .addAction(replyAction)
                        .setSound(soundUri)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                        .setContentIntent(pendingIntent2);

        // ---------------------------- Show Notification ----------------------- //
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }
        assert notificationManager != null;
        notificationManager.notify(1000 , notificationBuilder.build());
    }

    private void sendOrderRecived(String messageBody, String title, String OrderID) {
        SuperVisorHome.getNoti();
        Intent intent = new Intent(this, SuperVisorHome.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Intent brodcastIntent = new Intent(this, Brodcast.class);
        brodcastIntent.putExtra("orderid", OrderID);
        brodcastIntent.putExtra("owner", OrderID);
        PendingIntent actionIntent = PendingIntent.getBroadcast(this, 0, brodcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.noti);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setSound(soundUri)
                        .setContentIntent(pendingIntent)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .addAction(R.drawable.ic_logo, "تأكيد الاستلام", actionIntent)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(OrderID, createID(), notificationBuilder.build());

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(10 , notificationBuilder.build());
    }

    private void sendNotification(String messageBody, String title, String action, String OrderID) {
        SuperVisorHome.getNoti();
        Intent intent = new Intent(this, SuperVisorHome.class);
        intent.putExtra("action", action);
        intent.putExtra("order", OrderID);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+ getApplicationContext().getPackageName() + "/" + R.raw.noti);

        String channelId = getString(R.string.default_notification_channel_id);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo)
                        .setContentTitle(title)
                        .setContentText(messageBody)
                        .setSound(soundUri)
                        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentIntent(pendingIntent)
                        .setCategory(NotificationCompat.CATEGORY_STATUS)
                        .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(OrderID, createID(), notificationBuilder.build());

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Esh7nly", NotificationManager.IMPORTANCE_HIGH);

            AudioAttributes attributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT).build();

            // Configure the notification channel.
            channel.setDescription(messageBody);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setSound(soundUri, attributes); // This is IMPORTANT

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(1 , notificationBuilder.build());
    }

    public int createID() {
        Date now = new Date();
        return Integer.parseInt(new SimpleDateFormat("ddHHmmss", Locale.ENGLISH).format(now));
    }
}