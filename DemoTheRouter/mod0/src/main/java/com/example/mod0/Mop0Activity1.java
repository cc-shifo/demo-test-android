package com.example.mod0;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.therouter.router.Route;

@Route(path = RouterPath.MOD0_ACTIVITY1)
public class Mop0Activity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity0_mop0);
    }
}