package com.application.homedinein;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.CursorTreeAdapter;
import android.widget.ProgressBar;

public class Splash extends AppCompatActivity {
    ProgressBar progressBar;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        setContentView(R.layout.activity_splash);
        progressBar=findViewById(R.id.progressBar);
        progressBar.setProgressTintList(ColorStateList.valueOf(Color.BLUE));
        Handler h=new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i=0;
                while(i<10)
                {
                    i++;
                    int finalI = i;
                    h.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(finalI*10);
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                //Intent intent = new Intent(Splash.this, MainActivity.class);
                //Change MainActivity to Home
                Intent intent = new Intent(Splash.this, MainActivity.class);
                startActivity(intent);
                finish();


            }
        }).start();
    }
}