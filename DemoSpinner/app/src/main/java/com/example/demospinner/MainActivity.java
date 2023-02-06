package com.example.demospinner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private int mCount;
    private int mZeroCount;
    private Spinner mSpinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // test0();
        test1();
    }

    private void test0() {
        mSpinner = findViewById(R.id.demo_spinner);
        String[] a = getResources().getStringArray(R.array.demo_str_array);
        final DemoAdapter0 adapter = new DemoAdapter0(this, R.layout.spinner_item,
                Arrays.asList(a));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + mCount);
                mSpinner.getSelectedView().setSelected(true);
                adapter.select(position);
                mCount++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected: "+ mZeroCount);
                mZeroCount++;
            }
        });
        mSpinner.setAdapter(adapter);
    }

    private void test1() {
        mSpinner = findViewById(R.id.demo_spinner);
        String[] a = getResources().getStringArray(R.array.demo_str_array);
        final DemoAdapter1 adapter1 = new DemoAdapter1(this, R.layout.spinner_item,
                Arrays.asList(a));

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected: " + mCount);
                TextView textView = (TextView)mSpinner.getSelectedView();
                textView.setTextColor(getResources().getColor(R.color.selected));
                adapter1.select(position);
                mCount++;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onNothingSelected: "+ mZeroCount);
                mZeroCount++;
            }
        });
        mSpinner.setAdapter(adapter1);
    }
}