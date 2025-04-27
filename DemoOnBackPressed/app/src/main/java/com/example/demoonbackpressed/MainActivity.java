package com.example.demoonbackpressed;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demoonbackpressed.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        mBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mBinding.btnActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AndroidActivity.class);
            startActivity(intent);
        });

        mBinding.btnCompatibleActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CompatibleActivity.class);
            startActivity(intent);
        });

        mBinding.btnAndroidxActivity.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AndroidXActivity.class);
            startActivity(intent);
        });
    }
}