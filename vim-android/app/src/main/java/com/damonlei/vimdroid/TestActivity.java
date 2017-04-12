package com.damonlei.vimdroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import timber.log.Timber;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    public void start(View v) {
        Timber.d("start(17) ensureClick");
        startService(new Intent(this, AppService.class));
    }

    public void stop(View v) {
        stopService(new Intent(this, AppService.class));
    }

    public void attach(View v) {
        Intent intent = new Intent(this, AppService.class);
        intent.putExtra("test", true);
        intent.putExtra("cmdId", Global.CMD_ID_KEYBOARD);
        intent.putExtra("data", "{\"code\": \"F\"}");
        startService(intent);
    }

    public void detach(View v) {
        Intent intent = new Intent(this, AppService.class);
        intent.putExtra("test", true);
        intent.putExtra("cmdId", Global.CMD_ID_KEYBOARD);
        intent.putExtra("data", "{\"code\": \"Esc\"}");
        startService(intent);
    }

    public void analysis(View v) {
        Intent intent = new Intent(this, AppService.class);
        intent.putExtra("test", true);
        intent.putExtra("cmdId", 0);

    }
}
