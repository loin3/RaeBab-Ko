package com.example.third;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

public class FireBaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        Log.d("qwer", s);
        SharedPreferences sharedPreferences = getSharedPreferences("member", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", s);
        editor.commit();

        SharedPreferences sharedPreferences1 = getSharedPreferences("member", MODE_PRIVATE);
        String token = sharedPreferences1.getString("token", null);
        Log.d("qwer2", token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "shit: " + remoteMessage.getData().toString());
        if (remoteMessage.getData().size() > 0) {
            sendNotification(remoteMessage.getData().get("description"));
        }
    }

    private void sendNotification(String description) {

        Log.d("zxcv", description);

        Intent intent = new Intent(this, DescriptionActivity.class);
        intent.putExtra("description", description);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, "10001")
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setContentTitle("FCM Message")
                        .setContentText("일어나")
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelName = "defaultChannel";
            NotificationChannel channel = new NotificationChannel("10001", channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }
}