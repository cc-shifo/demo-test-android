package com.example.testactivity.second;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.testactivity.R;

/**
 * the MainActivity lifecycle state when returning back from SecondActivity
 * from MainActivity to SecondActivity, then back to MainActivity
 */
public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }
}