package com.example.demokmlparser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.demokmlparser.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private KmlParser mKmlParser;
    private List<KmlEntry> mKmlEntries;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mKmlParser = new KmlParser();
        // setContentView(R.layout.activity_main);
        mBinding.demoPolygon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mKmlEntries = null;
                try (InputStream inputStream = getResources().getAssets().open("1_polygon.kml")){
                    mKmlEntries = mKmlParser.pull2xml(inputStream);
                    mBinding.txDemo.setText(mKmlEntries.toString());
                } catch (Exception e) {
                    Log.e(TAG, "onClick: ", e);
                }
            }
        });
    }
}