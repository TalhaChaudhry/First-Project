package com.example.lets_play;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.text.html.ImageView;

import static com.example.lets_play.App.ACTION_NEXT;
import static com.example.lets_play.App.ACTION_PAUSE;
import static com.example.lets_play.App.ACTION_PREVIOUS;
import static com.example.lets_play.App.CHANNEL_ID_1;
import static com.example.lets_play.App.GET_TOAST;

public class PlaySong extends AppCompatActivity implements ActionPlaying, ServiceConnection {
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.setLooping(true);
        mediaPlayer.stop();
        mediaPlayer.pause();
        updateSeek.interrupt();
        updateTime.interrupt();
        mNotificationManagerCompat.cancelAll();
    }

    TextView textView;
    ImageView previous,pause,next,opt,mainImg;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    TextView textView2,textView3;
    Thread updateTime;
    int position;
    String totaltime;
    SeekBar seekBar;
    Thread updateSeek;
    private NotificationManagerCompat mNotificationManagerCompat;
    MusicService musicService;
    MediaSessionCompat mediaSession;

 //   boolean forOpt=true;


   // @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        textView=findViewById(R.id.textView);
        pause=findViewById(R.id.pause);
        previous=findViewById(R.id.previous);
        mainImg=findViewById(R.id.imageView);
        opt=findViewById(R.id.replay);
        textView2=findViewById(R.id.textView5);
        next=findViewById(R.id.next);
        textView3=findViewById(R.id.textView6);
        seekBar=findViewById(R.id.seekBar);
        Intent intent=getIntent();
        Bundle bundle= intent.getExtras();
        songs=(ArrayList)bundle.getParcelableArrayList("songList");
        textContent=intent.getStringExtra("currentSong");
        textView.setText(textContent);
        textView.setSelected(true);
        position=intent.getIntExtra("position",0);
        Uri uri= Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(this,uri);
        totaltime= setTimer(mediaPlayer.getDuration());


        textView2.setText(totaltime);
        try {
            setImage();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        mNotificationManagerCompat=NotificationManagerCompat.from(this);
        mediaSession=new MediaSessionCompat(this,"Let's Play Music");
        mediaPlayer.setLooping(true);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
              //  updateSeek.start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek= new Thread(){
            @Override
            public  void run(){
                int currentPosition =0;
                try{
                   // while (currentPosition<mediaPlayer.getDuration()){
                   while (mediaPlayer != null) {
                   // while(mediaPlayer.getCurrentPosition()<mediaPlayer.getDuration()){
                        if (mediaPlayer.isPlaying()) {
                            currentPosition = mediaPlayer.getCurrentPosition();
                            seekBar.setProgress(currentPosition);
                            //  textView2.setText(setTimer(currentPosition));
                            sleep(800);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();
        updateTime=new Thread(){
            @Override
            public void run() {

               while (mediaPlayer != null) {
               // while(mediaPlayer.getCurrentPosition()<mediaPlayer.getDuration()){
                    try {
//                        Log.i("Thread ", "Thread Called");
                        // create new message to send to handler
                        if (mediaPlayer.isPlaying()) {
                            Message msg = new Message();
                            msg.what = mediaPlayer.getCurrentPosition();
                            textView3.setText(setTimer(msg.what));
                            sleep(1000);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        };updateTime.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onOptClick(mp);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickPause();
        }
    });
        previous.setOnClickListener(new  View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickPrevious();
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            clickNext();
        }
    });
        opt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // if(forOpt==true){
                if(mediaPlayer.isLooping()){
                    mediaPlayer.setLooping(false);
                    opt.setImageResource(R.drawable.nextplay);
                   // forOpt=false;
                }
                else {
                    mediaPlayer.setLooping(true);
                    opt.setImageResource(R.drawable.replay);
                 //   forOpt=true;
                }

            }
        });

    }

    public String setTimer(int duration) {
        String timelable="";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        timelable+=min+":";
        if (sec<10) timelable+="0";
        timelable+=sec;
        return timelable;

    }
    // Step 5 "Notification"

    public void showNotification(int playPauseBtn){
        // Following contentIntent is not Good approach it's Jugard
        Intent intent=new Intent(this,NotificationReceiver.class)
                .setAction(GET_TOAST);
        PendingIntent contentIntent=PendingIntent.getActivity(this,0,intent,0);
       // Till here Jugard
        Intent prevIntent=new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_PREVIOUS);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this,0,prevIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent playIntent=new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_PAUSE);
        PendingIntent playPendingIntent = PendingIntent.getBroadcast(this,0,playIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Intent nextIntent=new Intent(this,NotificationReceiver.class)
                .setAction(ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this,0,nextIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Bitmap picture=BitmapFactory.decodeResource(getResources(),R.drawable.icons);
        Notification notification= new androidx.core.app.NotificationCompat.Builder(this,CHANNEL_ID_1)
                    .setLargeIcon(picture)
                    .setContentText(textContent)
                    .setSmallIcon(R.drawable.icons)
                    .setColor(getResources().getColor(R.color.design_default_color_secondary))
                    .addAction(R.drawable.previous,"Previous",prevPendingIntent)
                    .addAction(playPauseBtn,"Play",playPendingIntent)
                    .addAction(R.drawable.next,"Next",nextPendingIntent)
                    .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                    .setMediaSession(mediaSession.getSessionToken()))
                    .setPriority(androidx.core.app.NotificationCompat.PRIORITY_LOW)
                    .setContentIntent(contentIntent)
                    .setOnlyAlertOnce(true)
                    .build();
        NotificationManager notificationManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0,notification);

    }
    // for displaying Image of mp3 song
    public void setImage() throws FileNotFoundException {
        // for getting metadata of media
        android.media.MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(songs.get(position).getPath());
        byte [] data = mmr.getEmbeddedPicture();
        //coverart is an Imageview object

        // convert the byte array to a bitmap
        if(data != null)
        {
            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
            mainImg.setImageBitmap(bitmap); //associated cover art in bitmap
        }
        else
        {
            mainImg.setImageResource(R.drawable.icons); //any default cover resourse folder
        }

      //  mainImg.setAdjustViewBounds(true);
      //  mainImg.setLayoutParams(new LinearLayout.LayoutParams(500, 500));
    } // till here (Image of mp3)

    @Override
    protected void onResume() {
        super.onResume();
        // For binding Service (Step 4 "Notification")
        Intent intent1=new Intent(this,MusicService.class);
        // it will call onServiceConnected method which is OverRide below
        bindService(intent1,this,BIND_AUTO_CREATE);
    }

    // For unbinding the service following is the method (Related to step 4 "Notification")
    @Override
    protected void onPause() {
        super.onPause();
        // this will call onServiceDisconnected Method
        unbindService(this);
    }

    // Implementing these three methods due to ActionPlaying interface
    // this interface is to safe the actions playing on three buttons i.e previous, pause, next
    @Override
    public void clickNext() {
        seekBar.setProgress(0);
        mediaPlayer.pause();
        mediaPlayer.release();
        if(position!=songs.size()-1){
            position+=1;
        }
        else{
            position=0;
        }
        Uri uri= Uri.parse(songs.get(position).toString());
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        try {
            setImage();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        // mediaPlayer.setLooping(true);
        pause.setImageResource(R.drawable.pause);
        seekBar.setMax(mediaPlayer.getDuration());
        textContent = songs.get(position).getName().toString();
        totaltime= setTimer(mediaPlayer.getDuration());
        textView2.setText(totaltime);
        textView.setText(textContent);
        showNotification(R.drawable.pause);
    }

    @Override
    public void clickPause() {
        if(mediaPlayer.isPlaying()){
            pause.setImageResource(R.drawable.play);
            mediaPlayer.pause();
            showNotification(R.drawable.play);
        }
        else{
            pause.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            showNotification(R.drawable.pause);
            mediaPlayer.setLooping(true);
        }
    }

    @Override
    public void clickPrevious() {
        seekBar.setProgress(0);
        mediaPlayer.pause();
        mediaPlayer.release();
        if (position != 0) {
            position -= 1;
        } else {
            position = songs.size() - 1;
        }
        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        try {
            setImage();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
        // mediaPlayer.setLooping(true);
        pause.setImageResource(R.drawable.pause);
        seekBar.setMax(mediaPlayer.getDuration());
        textContent = songs.get(position).getName().toString();
        totaltime = setTimer(mediaPlayer.getDuration());
        textView2.setText(totaltime);
        textView.setText(textContent);
        showNotification(R.drawable.pause);
    }
    public void onOptClick(MediaPlayer mp){
        //if(forOpt==false){
        if(!mediaPlayer.isLooping()){
           clickNext();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    onOptClick(mp);
                }
            });
        }
       // else {
           // mp.start();
       // }

        //return loc;
    }

    // implementing these two methods due to ServiceConnection interface
    // Step 3 "Notification"
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
   // For getting the service and refer it to the obj of MusicService
    MusicService.MyBinder binder=(MusicService.MyBinder)service;
    musicService=binder.getService();
    musicService.setCallBack(PlaySong.this);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    musicService=null;
    }
}
