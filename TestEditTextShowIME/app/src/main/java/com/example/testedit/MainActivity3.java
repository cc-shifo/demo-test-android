package com.example.testedit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity3 extends AppCompatActivity {
    private static final String TAG = "MainActivity3";
    private EditText mEditText1;
    private EditText mEditText2;
    /**
     * 计算点击次数。
     */
    private long mClickedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        mEditText1 = findViewById(R.id.activity3_test_ed1);
        mEditText1.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "mEditText1 onFocusChange: " + hasFocus);
            }
        });
        mEditText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "mEditText1 beforeTextChanged: s=" + s.toString() + ", start=" + start +
                        ", count=" + count + ", after=" + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "mEditText1 onTextChanged: s=" + s.toString() + ", start=" + start +
                        ", before=" + before + ", count=" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "mEditText1 afterTextChanged: s=" + s.toString());
            }
        });
        mEditText1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "mEditText1 onEditorAction: " + v.getText().toString());
                }
                return false;
            }
        });
        mEditText2 = findViewById(R.id.activity3_test_ed2);
        mEditText2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                Log.d(TAG, "mEditText2 onFocusChange: " + hasFocus);
            }
        });
        mEditText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                Log.d(TAG, "mEditText2 beforeTextChanged: s=" + s.toString() + ", start=" + start +
                        ", count=" + count + ", after=" + after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.d(TAG, "mEditText2 onTextChanged: s=" + s.toString() + ", start=" + start +
                        ", before=" + before + ", count=" + count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d(TAG, "mEditText2 afterTextChanged: s=" + s.toString());
            }
        });
        mEditText2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Log.d(TAG, "mEditText2 onEditorAction: " + v.getText().toString());
                }
                return false;
            }
        });

        Button button = findViewById(R.id.activity3_test_set_ed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickedCount++;
                String s = "onClick: " + mClickedCount;
                Log.d(TAG, s);
                mEditText1.setText(s);
                if (mEditText2.getText() == null) {
                    Log.d(TAG, "mEditText2 text== null");
                } else {
                    Log.d(TAG, "mEditText2 text!= null, just empty");
                }
            }
        });
    }
}