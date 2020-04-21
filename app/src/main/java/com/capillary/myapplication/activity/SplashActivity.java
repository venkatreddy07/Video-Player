package com.capillary.myapplication.activity;

import android.content.Intent;
import android.os.Handler;

import com.capillary.myapplication.R;
import com.capillary.myapplication.activity.main.MainActivity;
import com.capillary.myapplication.base.BaseActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected int getContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void init() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        },2000);
    }
}
