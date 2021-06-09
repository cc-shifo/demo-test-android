package com.example.demoroundcornerprogressview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private float mProgress = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ProgressView progressView = findViewById(R.id.progress_view);

        Button btnInit = findViewById(R.id.btn_init);
        btnInit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.setState(IntDefDownloadStatus.INIT);
            }
        });

        Button btnDownload= findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.setState(IntDefDownloadStatus.DOWNLOADING);
                mProgress += 2;
                progressView.setText( mProgress +"MB");
                progressView.setProgress(mProgress);
            }
        });

        Button btnStopped= findViewById(R.id.btn_stopped);
        btnStopped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.setText("");
                progressView.setState(IntDefDownloadStatus.STOPPED);
            }
        });

        Button btnSuccess= findViewById(R.id.btn_success);
        btnSuccess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.setText("");
                progressView.setState(IntDefDownloadStatus.SUCCESS);
            }
        });

        Button btnFailed= findViewById(R.id.btn_failed);
        btnFailed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.setText("");
                progressView.setState(IntDefDownloadStatus.FAILED);
            }
        });

        Button btnReady= findViewById(R.id.btn_ready);
        btnReady.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressView.setText("");
                progressView.setState(IntDefDownloadStatus.READY);
            }
        });
    }
}