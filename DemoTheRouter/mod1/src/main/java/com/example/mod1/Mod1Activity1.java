package com.example.mod1;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mod0.RouterPath;
import com.therouter.router.Route;

@Route(path = RouterPath.MOD1_ACTIVITY1)
public class Mod1Activity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1_mod1);
    }
}