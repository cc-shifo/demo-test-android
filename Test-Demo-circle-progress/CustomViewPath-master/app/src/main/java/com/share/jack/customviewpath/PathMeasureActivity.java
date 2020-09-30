package com.share.jack.customviewpath;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.share.jack.customviewpath.widget.CustomPathMeasure;

public class PathMeasureActivity extends AppCompatActivity {
    CustomPathMeasure mCustomPathMeasure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_measure);
        mCustomPathMeasure = findViewById(R.id.as_PathMeasure);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCustomPathMeasure.startAnim();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCustomPathMeasure.stopAnim();
    }
}