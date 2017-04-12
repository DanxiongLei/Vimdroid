package com.damonlei.utils.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toolbar;

import static com.damonlei.utils.Config.VERBOSE;

/**
 * @author damonlei
 * @time 2017/3/1
 * @email danxionglei@foxmail.com
 */
public class BaseActivity extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        if (VERBOSE) {
            Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "], persistentState = [" + persistentState + "].\nYou might call the wrong onCreate func.");
        }
        super.onCreate(savedInstanceState, persistentState);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (VERBOSE) {
            Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        if (VERBOSE) {
            Log.d(TAG, "onStart() called");
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        if (VERBOSE) {
            Log.d(TAG, "onResume() called");
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (VERBOSE) {
            Log.d(TAG, "onPause() called");
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        if (VERBOSE) {
            Log.d(TAG, "onStop() called");
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (VERBOSE) {
            Log.d(TAG, "onDestroy() called");
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (VERBOSE) {
            Log.d(TAG, "onBackPressed() called");
        }
        super.onBackPressed();
    }

    @Override
    public Resources getResources() {
        return super.getResources();
    }

    @Override
    public void setActionBar(Toolbar toolbar) {
        super.setActionBar(toolbar);
    }

    @Override
    public void setSupportActionBar(@Nullable android.support.v7.widget.Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
    }
}
