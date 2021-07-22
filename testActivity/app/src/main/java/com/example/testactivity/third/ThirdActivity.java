package com.example.testactivity.third;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import com.example.testactivity.R;
import com.example.testactivity.databinding.ActivityThirdBinding;

/**
 * activity and fragment lifecycle state
 */
public class ThirdActivity extends AppCompatActivity {
    private static final String TAG = "ThirdActivity";
    private ActivityThirdBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + getLifecycle().getCurrentState());
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_third);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(mBinding.thirdActivityContent.getId(),
                new ThirdActivityBlankFragment());
        transaction.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + getLifecycle().getCurrentState());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d(TAG, "onSaveInstanceState: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + getLifecycle().getCurrentState());
    }
}