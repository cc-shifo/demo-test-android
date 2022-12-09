package com.example.demonodragswitchcompat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextView;
    private SwitchCompat mSwitchCompat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTextView = findViewById(R.id.test_tv);
        mSwitchCompat = findViewById(R.id.test_switch);
        mSwitchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            String v = isChecked ? "true" : "false";
            mTextView.setText(v);
        });
    }
}