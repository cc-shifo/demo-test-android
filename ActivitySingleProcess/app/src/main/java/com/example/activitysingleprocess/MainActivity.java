package com.example.activitysingleprocess;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.activitysingleprocess.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
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

        mBinding.textView.setText("MainActivity");
        mBinding.button1.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(getPackageName(),
                    "com.example.activitysingleprocess.MainActivity1"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        mBinding.button2.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setComponent(new ComponentName(getPackageName(),
                    "com.example.activitysingleprocess.CheckCustomTabActivity"));
            startActivity(intent);
        });

        Log.d(TAG, "onCreate: ---------------------------MainActivity");

        if (isCustomTabsAvailable(this)) {
            Log.d(TAG, "onCreate: CustomTabs is available");
        } else {
            Log.d(TAG, "onCreate: CustomTabs is not available");
        }
    }

    public static boolean isCustomTabsAvailable(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent serviceIntent = new Intent("android.support.customtabs.action.CustomTabsService");
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(serviceIntent, 0);
        return resolveInfos != null && !resolveInfos.isEmpty();
    }
}