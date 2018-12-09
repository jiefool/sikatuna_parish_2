package com.tanginan.www.sikatuna_parish;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();


                } finally {
                    Intent openClass = new Intent(SplashActivity.this, LoginActivity.class);
                    finish();
                    startActivity(openClass);
                }
            }
        };

        timer.start();

    }
}

