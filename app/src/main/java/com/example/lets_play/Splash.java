package com.example.lets_play;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;


public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        Thread thread =new Thread(){
            @Override
           public void  run(){
                try{
                    sleep(10000);

                }
                catch (Exception e){
                    e.printStackTrace();
                }
                finally {
                    Intent intent= new Intent(Splash.this,MainActivity.class);
                    
                    startActivity(intent);
                }
            }

        };
        thread.start();

    }
}
