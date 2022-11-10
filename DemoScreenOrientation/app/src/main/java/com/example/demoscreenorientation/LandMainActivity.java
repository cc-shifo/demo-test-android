package com.example.demoscreenorientation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class LandMainActivity extends AppCompatActivity {
    private static final String TAG = "LandMainActivity";

    protected void onCreate(Bundle savedInstanceState) {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        Log.d(TAG, "onCreate1: " + getOriString() + ", config: " + getConfigOri());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_land_main);
        Log.d(TAG, "onCreate2: " + getOriString() + ", config: " + getConfigOri());
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged1: " + getOriString() + ", config: " + getConfigOri());
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged2: " + getOriString() + ", config: " + getConfigOri());
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart1: " + getOriString() + ", config: " + getConfigOri());
        super.onStart();
        Log.d(TAG, "onStart1: " + getOriString() + ", config: " + getConfigOri());
    }

    @Override
    protected void onResume() {
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        Log.d(TAG, "onResume1: " + getOriString() + ", config: " + getConfigOri());
        super.onResume();
        Log.d(TAG, "onResume2: " + getOriString() + ", config: " + getConfigOri());
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop1: " + getOriString() + ", config: " + getConfigOri());
        super.onStop();
        Log.d(TAG, "onStop2: " + getOriString() + ", config: " + getConfigOri());
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy1: " + getOriString() + ", config: " + getConfigOri());
        super.onDestroy();
        Log.d(TAG, "onDestroy2: " + getOriString() + ", config: " + getConfigOri());
    }

    private String getOriString() {
        return getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ?
                "[landscape]" : "[portrait]";
    }

    private String getConfigOri() {
        return  getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?
                "[landscape]" : "[portrait]";
    }
}