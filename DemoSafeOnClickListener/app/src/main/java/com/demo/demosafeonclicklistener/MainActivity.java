package com.demo.demosafeonclicklistener;

import android.os.Bundle;
import android.os.SystemClock;
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
        final int INTERVAL = 500;
        TextView tv = findViewById(R.id.tv_click);
        Button btn = findViewById(R.id.btn_click);
        btn.setOnClickListener(new OnSafeClickListener(R.id.btn_click, INTERVAL) {
            @Override
            public void onSafeClick(View v) {
                String text = SystemClock.uptimeMillis() + "";
                Snackbar.make(v, text, BaseTransientBottomBar.LENGTH_LONG)
                        .show();
                tv.setText(text);
            }
        });
    }

    private abstract static class OnSafeClickListener implements View.OnClickListener {
        private final int mInterval;
        private long mMilliseconds;
        @IdRes
        private final int mId;

        public OnSafeClickListener(@IdRes int id) {
            this(id, 500);
        }

        /**
         * @param interval the interval milliseconds between two clicking actions.
         */
        public OnSafeClickListener(@IdRes int id, int interval) {
            mId = id;
            mInterval = interval;
        }

        @Override
        public void onClick(View v) {
            mMilliseconds = v.getTag(mId) == null ? 0 : (long) v.getTag(mId);
            if (SystemClock.uptimeMillis() - mMilliseconds > mInterval) {
                mMilliseconds = SystemClock.uptimeMillis();
                v.setTag(mId, mMilliseconds);
                onSafeClick(v);
            }
        }

        public abstract void onSafeClick(View v);

        public boolean isSafetyClicked() {
            return SystemClock.uptimeMillis() - mMilliseconds > mInterval;
        }
    }
}