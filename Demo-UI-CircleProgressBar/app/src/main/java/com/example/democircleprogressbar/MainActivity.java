package com.example.democircleprogressbar;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import com.example.democircleprogressbar.dialog.CustomProgressBar;
import com.example.democircleprogressbar.dialog.SecondActivity;
import com.example.mylibrary.HorizontalProgressBarWithNumber;
import com.example.mylibrary.RoundProgressBarWidthNumber;

import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private RoundProgressBarWidthNumber mRoundProgressBar;

    private HorizontalProgressBarWithNumber mProgressBar;
    private static final int MSG_PROGRESS_UPDATE = 0x110;
    private CustomProgressBar mCustomProgressBar;
    private AtomicBoolean mClick = new AtomicBoolean(false);
    private CountDownTimer mTimer;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int progress = mProgressBar.getProgress();
            int roundProgress = mRoundProgressBar.getProgress();
            mProgressBar.setProgress(++progress);
            mRoundProgressBar.setProgress(++roundProgress);
            if (progress >= 100) {
                mHandler.removeMessages(MSG_PROGRESS_UPDATE);
            }
            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mProgressBar = (HorizontalProgressBarWithNumber) findViewById(R.id.id_progressbar01);
        mRoundProgressBar = (RoundProgressBarWidthNumber) findViewById(R.id.id_progress02);
        mCustomProgressBar = findViewById(R.id.custom_progress_bar);
        Button button = findViewById(R.id.btn_start_count_down);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mClick.getAndSet(true)) {
                    countDownProgress();
                }
            }
        });
        Button button2 = findViewById(R.id.btn_start_activity2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    private void countDownProgress() {
        mTimer = new CountDownTimer(60000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mCustomProgressBar.setProgress((int)(60000 - millisUntilFinished) / 1000);
            }

            @Override
            public void onFinish() {
                mCustomProgressBar.setProgress(0);
                mClick.set(false);
            }
        };
        mTimer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
    }
}