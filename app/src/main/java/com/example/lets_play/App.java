package com.example.lets_play;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;


// step 1 for creating notification bar for play music

public class App extends Application {
    public static final String CHANNEL_ID_1="channal1";
    public static final String CHANNEL_NAME_1="FIRSTCHANNEL";
    public static final String ACTION_NEXT ="NEXT";
    public static final String ACTION_PREVIOUS="PREVIOUS";
    public static final String ACTION_PAUSE="PAUSE";
    public static final String GET_TOAST="SET_TOAST";
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel1();
    }

    private void createNotificationChannel1() {
        // run on these SDKd
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel1=new NotificationChannel(
                    CHANNEL_ID_1,CHANNEL_NAME_1, NotificationManager.IMPORTANCE_HIGH
            );
            // show even at ock screen too
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel1.enableLights(true);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);

        }
    }


}
