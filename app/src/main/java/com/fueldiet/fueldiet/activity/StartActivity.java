package com.fueldiet.fueldiet.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;

import com.fueldiet.fueldiet.R;

public class StartActivity extends BaseActivity {

    Handler handler = null;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        initViews();
        initHandler();
    }

    void initHandler(){
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(this::startIconAnimation, 100);
        handler.postDelayed(this::startMainActivity, 1200);
    }

    void initViews(){
        logo = findViewById(R.id.logo);
    }

    void startIconAnimation(){
        logo.animate()
                .scaleXBy(1f)
                .scaleYBy(1f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(500L)
                .start();
    }

    /**
     * Loads MainActivity
     */
    void startMainActivity(){
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        //So app doesn't start if user cancels the animation
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
        super.onPause();
    }
}
