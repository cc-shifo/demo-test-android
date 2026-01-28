package com.example.mod2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mod0.RouterPath;
import com.therouter.router.Route;

@Route(path = RouterPath.MOD2_ACTIVITY1)
public class Mod2Activity1 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mod21);
    }
}