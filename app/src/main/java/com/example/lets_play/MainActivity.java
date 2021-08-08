package com.example.lets_play;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {
    private final int SPLASH_DISPLAY_LENGHT = 1000;
    private  ArrayList<File> mySongs = fetchSongs(Environment.getExternalStorageDirectory());

    private String [] items = new String[mySongs.size()];

    // all done for setting an item click listener

    private View.OnClickListener onItemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            // confirm to play the audio from use
            AlertDialog.Builder alertDialogBuilder=new AlertDialog.Builder(MainActivity.this);
            alertDialogBuilder.setMessage("Do you want to play this audio ");
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //TODO: Step 4 of 4: Finally call getTag() on the view.
                    // This viewHolder will have all required values.
                    RecyclerView.ViewHolder viewHolder = (RecyclerView.ViewHolder) view.getTag();
                    int position = viewHolder.getAdapterPosition();
                    // viewHolder.getItemId();
                    // viewHolder.getItemViewType();
                    // viewHolder.itemView;
                    Intent intent = new Intent(MainActivity.this, PlaySong.class);
                    String currentSong = items[position];
                    intent.putExtra("songList", mySongs);
                    intent.putExtra("currentSong", currentSong);
                    intent.putExtra("position", position);
                    startActivity(intent);
                }
            });
            alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ;
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


        }

    }; // till here for item click listener



// starting of the main Activity methods
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView recyclerView;
     
        super.onCreate(savedInstanceState);
       // ProgressDialog.show(this, "Loading", "Wait while loading...");
        setContentView(R.layout.activity_main);

         recyclerView= findViewById(R.id.recyclerView);
        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {


                        for(int i=0;i<mySongs.size();i++){
                            items[i] = mySongs.get(i).getName().replace(".mp3", "");
                        }
                        // making object of CustomAAdapter
                        CustomAdapter adapter= new CustomAdapter(items);
                        //setting LayoutManager
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
                        // toast
                        Toast.makeText(MainActivity.this, "Total Songs  "+mySongs.size(), Toast.LENGTH_SHORT).show();
                        // setting adapter to recycleView
                        recyclerView.setAdapter(adapter);
                        // passing for click listener above defined at the starting of class
                       adapter.setOnItemClickListener(onItemClickListener);


                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();
        // for notification from firebase
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(
                    "FireBaseNotification","FireBaseNotification", NotificationManager.IMPORTANCE_HIGH
            );
            // show even at ock screen too
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.enableLights(true);
            NotificationManager manager=getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);

        }
        FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Successful";
                        if (!task.isSuccessful()){
                            msg = "failed";
                        }
                       // Log.d(TAG, msg);
                       // Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


    }
    public ArrayList<File> fetchSongs(File file){
        ArrayList<File> arrayList = new ArrayList<File>();
        File [] songs = file.listFiles();
        if(songs !=null){
            for(File myFile: songs){
                if(!myFile.isHidden() && myFile.isDirectory()){

                    arrayList.addAll(fetchSongs(myFile));
                }
                else{
                    if(myFile.getName().endsWith(".mp3") && !myFile.getName().startsWith(".")){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}