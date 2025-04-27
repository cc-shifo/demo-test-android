package com.example.demoonbackpressed;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.window.OnBackAnimationCallback;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AndroidActivity extends Activity {
    private static final String TAG = "AndroidActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    new OnBackInvokedCallback() {
                        @Override
                        public void onBackInvoked() {
                            Log.d(TAG, "onBackInvoked: ");
                            Toast.makeText(AndroidActivity.this, "Hello back pressed", Toast.LENGTH_LONG)
                                    .show();
                            // 如果添加了toast，
                            // 调用back，导航栏的并不会一直处于按下状态效果。不区分PRIORITY_DEFAULT，PRIORITY_DEFAULT
                            // 不调用back，导航栏会一直处于按下状态效果。不区分PRIORITY_DEFAULT，PRIORITY_DEFAULT
                            onBackPressed();
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");
    }
}