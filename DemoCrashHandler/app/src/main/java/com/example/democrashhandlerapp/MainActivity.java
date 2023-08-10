package com.example.democrashhandlerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Application;
import android.os.Bundle;


import com.example.democrashhandlerapp.databinding.ActivityMainBinding;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.helloWorldApp.setText(getApplication().toString());
        mBinding.btnGetApp.setOnClickListener(v -> {
            Application app = AppUtil.getApplication1();
            if (app != null) {
                mBinding.helloWorld.setText(app.toString());
            } else {

                mBinding.helloWorld.setText("null application");
            }
        });
        mBinding.btnTestCrashThread.setOnClickListener(v -> testThread());
    }

    private void testThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String txt = null;
                int n = 1;
                int b = txt.length() + n;
                System.out.printf(Locale.ENGLISH, "%d%n", b);
            }
        }).start();
    }
}