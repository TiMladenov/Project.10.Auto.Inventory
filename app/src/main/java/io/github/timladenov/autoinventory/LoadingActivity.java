package io.github.timladenov.autoinventory;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getSupportActionBar().hide();

        Thread logo = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(2500);
                    Intent afterSplash = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(afterSplash);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        logo.start();
    }
}
