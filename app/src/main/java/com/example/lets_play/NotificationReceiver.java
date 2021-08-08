package com.example.lets_play;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import static com.example.lets_play.App.ACTION_NEXT;
import static com.example.lets_play.App.ACTION_PAUSE;
import static com.example.lets_play.App.ACTION_PREVIOUS;
import static com.example.lets_play.App.GET_TOAST;

// Step 6 "Notification"
public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1=new Intent(context,MusicService.class);
        if(intent.getAction()!=null){
            switch (intent.getAction()){
                case ACTION_NEXT:
                    Toast.makeText(context,"Playing Next",Toast.LENGTH_SHORT).show();
                    intent1.putExtra("myActionName",intent.getAction());
                    context.startService(intent1);
                    break;
                case ACTION_PREVIOUS:
                    Toast.makeText(context,"Playing Previous",Toast.LENGTH_SHORT).show();
                    intent1.putExtra("myActionName",intent.getAction());
                    context.startService(intent1);
                    break;
                case ACTION_PAUSE:
                    Toast.makeText(context,"Done",Toast.LENGTH_SHORT).show();
                    intent1.putExtra("myActionName",intent.getAction());
                    context.startService(intent1);
                    break;
                    // Following is a part of Jugard
                case GET_TOAST:
                    Toast.makeText(context,"Tap True",Toast.LENGTH_SHORT).show();
                    intent1.putExtra("myActionName",intent.getAction());
                    context.startService(intent1);
                    break;
            }
        }
    }
}
