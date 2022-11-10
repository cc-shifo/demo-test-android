package com.example.demoscreenorientation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;

public class DefaultChangeMainActivity extends AppCompatActivity {
    private static final String TAG = "DefaultChangeMain";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate1: " + getOriString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default_change_main);
        Log.d(TAG, "onCreate2: ");
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged1: " + getOriString());
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "onConfigurationChanged2: " + getOriString());
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart1: " + getOriString());
        super.onStart();
        Log.d(TAG, "onStart1: " + getOriString());
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume1: " + getOriString());
        super.onResume();
        Log.d(TAG, "onResume2: " + getOriString());
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop1: " + getOriString());
        super.onStop();
        Log.d(TAG, "onStop2: " + getOriString());
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy1: " + getOriString());
        super.onDestroy();
        Log.d(TAG, "onDestroy2: " + getOriString());
    }

    private String getOriString() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ?
                "[landscape]" : "[portrait]";
    }
}