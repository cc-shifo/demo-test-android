package com.demo.demosafeonclicklistener;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        final int INTERVAL = 200;
        TextView tv = findViewById(R.id.tv_click);
        Button btn = findViewById(R.id.btn_click);
        btn.setOnClickListener(new OnSafeClickListener(INTERVAL) {
            @Override
            public void onSafeClick(View v) {
                String text = SystemClock.uptimeMillis() + "";
                Snackbar.make(v, text, BaseTransientBottomBar.LENGTH_SHORT)
                        .show();
                tv.setText(text);
            }

            @Override
            public void onQuickClick(View v) {
                tv.setText("Not Safe click");
            }
        });
    }

    private abstract static class OnSafeClickListener implements View.OnClickListener {
        private static final String TAG = "OnSafeClickListener";
        private final int mInterval;
        private long mMilliseconds;

        public OnSafeClickListener() {
            this(500);
        }

        /**
         * @param interval the interval milliseconds between two clicking actions.
         */
        public OnSafeClickListener(int interval) {
            mInterval = interval;
            mMilliseconds = 0;
        }

        @Override
        public void onClick(View v) {
            final long ms = SystemClock.uptimeMillis();
            Log.d(TAG, "onClick: " + ms);
            long interval = ms - mMilliseconds;
            if (ms - mMilliseconds > mInterval) {
                Log.d(TAG, "SafeClick: " + ms + ", interval=" + interval);
                mMilliseconds = ms;
                onSafeClick(v);
            } else {
                Log.d(TAG, "QuickClick: " + ms + ", interval=" + interval);
                onQuickClick(v);
            }
        }

        public abstract void onSafeClick(View v);
        public void onQuickClick(View v) {
            //nothing
        }
    }
}