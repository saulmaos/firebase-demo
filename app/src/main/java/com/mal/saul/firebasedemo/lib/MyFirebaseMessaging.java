package com.mal.saul.firebasedemo.lib;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mal.saul.firebasedemo.MainActivity;
import com.mal.saul.firebasedemo.R;

import java.util.Map;

import androidx.annotation.NonNull;

/**
 * Created by SAUL on 06/04/2020.
 */
public class MyFirebaseMessaging extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "onNewToken: " + token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "Mensaje recibido");
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.d(TAG, "data: " + data);
            String title = data.get("titulo");
            String msg = data.get("mensaje");
            sendNotification(title, msg);

        } else{
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            String title = notification.getTitle();
            String msg = notification.getBody();

            sendNotification(title, msg);
        }

    }

    private void sendNotification(String title, String msg) {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, MyNotification.NOTIFICATION_ID, intent, PendingIntent.FLAG_ONE_SHOT);

        MyNotification notification = new MyNotification(this, MyNotification.CHANNEL_ID_NOTIFICATIONS);
        notification.build(R.drawable.ic_launcher_foreground, title, msg, pendingIntent);
        notification.addChannel("Notificaciones", NotificationManager.IMPORTANCE_DEFAULT);
        notification.createChannelGroup(MyNotification.CHANNEL_GROUP_GENERAL, R.string.notification_channel_group_general);
        notification.show(MyNotification.NOTIFICATION_ID);
    }
}
