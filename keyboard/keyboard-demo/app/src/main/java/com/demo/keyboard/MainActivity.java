package com.demo.keyboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        KeyboardView keyboardView = findViewById(R.id.keyboard_view);
        final Keyboard keyboard = new Keyboard(this, R.xml.keyboard_numbers);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(new KeyboardView.OnKeyboardActionListener() {
            @Override
            public void onPress(int primaryCode) {
                Log.d(TAG, "onPress: " + primaryCode);
            }

            @Override
            public void onRelease(int primaryCode) {
                Log.d(TAG, "onRelease: " + primaryCode);
            }

            @Override
            public void onKey(int primaryCode, int[] keyCodes) {
                Log.d(TAG, "onKey: primaryCode=" + primaryCode + ", keyCodes=" + keyCodes[0]);
            }

            @Override
            public void onText(CharSequence text) {
                Log.d(TAG, "onText: " + text);
            }

            @Override
            public void swipeLeft() {

            }

            @Override
            public void swipeRight() {

            }

            @Override
            public void swipeDown() {

            }

            @Override
            public void swipeUp() {

            }
        });
    }
}
