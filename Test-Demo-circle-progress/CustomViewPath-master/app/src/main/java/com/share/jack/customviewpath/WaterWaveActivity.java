package com.share.jack.customviewpath;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.share.jack.customviewpath.widget.WaterWaveProgress;

public class WaterWaveActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water_wave);
        WaterWaveProgress waterWaveProgress =  findViewById(R.id.as_WaterWaveProgress);
        waterWaveProgress.animateWave();
    }
}