package com.example.lets_play;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import static com.example.lets_play.App.GET_TOAST;


// Add this service in manifest too and enable it true
// step 2 for  creating notification bar for play music
public class MusicService extends Service {
    public static final String ACTION_NEXT ="NEXT";
    public static final String ACTION_PREVIOUS="PREVIOUS";
    public static final String ACTION_PAUSE="PAUSE";
    ActionPlaying actionPlaying;
    private IBinder mBinder=new MyBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    // Inner Customized class
    public class MyBinder extends Binder{
        // method to get the MusicService
        MusicService getService(){
            return  MusicService.this;
        }
    }

    // invoke when we'll start service using any intent from any activity
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            String actionName = intent.getStringExtra("myActionName");
            if (actionName != null) {
                switch (actionName) {
                    case ACTION_NEXT:
                        if (actionPlaying != null) {
                            actionPlaying.clickNext();
                        }
                        break;
                    case ACTION_PREVIOUS:
                        if (actionPlaying != null) {
                            actionPlaying.clickPrevious();
                        }
                        break;
                    case ACTION_PAUSE:
                        if (actionPlaying != null) {
                            actionPlaying.clickPause();
                        }
                        break;
                        // following is the part Jugard
                    case GET_TOAST:

                        break;

                    default:
                        throw new IllegalStateException("Unexpected value: " + actionName);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
           // Toast.makeText(PlaySong.class,"Can't Perform Action",Toast.LENGTH_SHORT).show();
        }
        // once service stops it restarted itself
        return START_STICKY;
    }
    public void setCallBack(ActionPlaying actionPlaying){
        this.actionPlaying=actionPlaying;
    }


}
