package com.example.demoonbackpressed;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class CompatibleActivity extends AppCompatActivity {
    private static final String TAG = "CompatibleActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_compatible);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getOnBackInvokedDispatcher().registerOnBackInvokedCallback(OnBackInvokedDispatcher.PRIORITY_DEFAULT,
                    new OnBackInvokedCallback() {
                        @Override
                        public void onBackInvoked() {
                            Log.d(TAG, "onBackInvoked: ");
                            Toast.makeText(CompatibleActivity.this, "Hello back pressed", Toast.LENGTH_LONG)
                                 .show();
                            // 如果添加了toast，没有效果。导航栏的一直处于按下状态效果。不区分PRIORITY_DEFAULT，PRIORITY_DEFAULT
                            // getOnBackPressedDispatcher().onBackPressed();
                            // 如果添加了toast，没有效果。导航栏的一直处于按下状态效果。不区分PRIORITY_DEFAULT，PRIORITY_DEFAULT
                            onBackPressed();
                        }
                    });
        }
    }
}