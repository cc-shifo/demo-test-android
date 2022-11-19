/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.demoapiseekbar;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Random;

// import com.example.android.apis.R;


/**
 * Demonstrates how to use a seek bar
 */
public class SeekBar1 extends Activity implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "SeekBar1";
    SeekBar mSeekBar;
    SeekBar mSeekBarMin;
    TextView mProgressText;
    TextView mTrackingText;

    private int startP;
    private int endP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.seekbar_1);

        mSeekBar = (SeekBar) findViewById(R.id.seek);
        mSeekBarMin = (SeekBar) findViewById(R.id.seekMin);
        mSeekBar.setOnSeekBarChangeListener(this);
        mProgressText = (TextView) findViewById(R.id.progress);
        mTrackingText = (TextView) findViewById(R.id.tracking);


        mSeekBarMin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.d(TAG, "onProgressChanged: " + progress + ", fromUser: " + fromUser);
                mProgressText.setText("onProgressChanged: " + progress + ", fromUser: " + fromUser);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                startP = seekBar.getProgress();
                Log.d(TAG, "onStartTrackingTouch: " + startP);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                endP = seekBar.getProgress();
                Log.d(TAG, "onStopTrackingTouch: " + endP);
                mProgressText.setText("onStopTrackingTouch: " + endP);

            }
        });
        ((CheckBox) findViewById(R.id.enabled)).setOnCheckedChangeListener(
                new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        // findViewById(R.id.seekMin).setEnabled(isChecked);
                        // findViewById(R.id.seekMax).setEnabled(isChecked);
                        // mSeekBar.setEnabled(isChecked);
                        int val = new Random().nextInt(90);
                        mSeekBarMin.setProgress(val);
                        mTrackingText.setText(String.valueOf(val));
                    }
                });
    }

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {
        mProgressText.setText(progress + " " +
                getString(R.string.seekbar_from_touch) + "=" + fromTouch);
    }

    public void onStartTrackingTouch(SeekBar seekBar) {
        mTrackingText.setText(getString(R.string.seekbar_tracking_on));
    }

    public void onStopTrackingTouch(SeekBar seekBar) {
        mTrackingText.setText(getString(R.string.seekbar_tracking_off));
    }
}
