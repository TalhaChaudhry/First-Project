package com.example.lets_play;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.params.BlackLevelPattern;
import android.media.session.MediaSession;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        showNotification(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }


    public void showNotification(String title,String message){
        Bitmap picture= BitmapFactory.decodeResource(getResources(),R.drawable.icons);
        // building Notification
        NotificationCompat.Builder notification=new NotificationCompat.Builder(this,"FireBaseNotification")
                .setContentText(title)
                .setSmallIcon(R.drawable.icons)
                .setAutoCancel(true)
                .setContentText(message)
                .setLargeIcon(picture);

        // managing Notification
        NotificationManagerCompat notificationManager= NotificationManagerCompat.from(this);
        notificationManager.notify(30,notification.build());

    }



}
