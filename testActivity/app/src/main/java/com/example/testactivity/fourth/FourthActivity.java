package com.example.testactivity.fourth;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.testactivity.R;
import com.example.testactivity.databinding.ActivityFourthBinding;

/**
 * FourthActivityViewModel send a data every 10 seconds.
 * <p>
 * Once FourthActivity receives data, then immediately click Home or recently key in order
 * FourthActivity move to inactive state, waiting FourthActivityViewModel for sending a few data.
 * Assuming that FourthActivityViewModel has sent 4 data, once the 4th data was sent, user waits
 * 1 or 2 seconds and then go back to FourthActivity immediately. Finally FourthActivity will
 * display the 4th value. This means that LiveData will keep data, and then send this data to
 * FourthActivity at the appropriate time.
 */
public class FourthActivity extends AppCompatActivity {
    private static final String TAG = "FourthActivity";
    private ActivityFourthBinding mBinding;
    private FourthActivityViewModel mViewModel;
    private boolean mOnRestarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: " + getLifecycle().getCurrentState());
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_fourth);
        mViewModel = new ViewModelProvider.NewInstanceFactory().create(
                FourthActivityViewModel.class);
        mViewModel.getData().observe(this, aLong -> {
            String txt = aLong.toString();
            Log.d(TAG, "observe: " + txt);
            mBinding.fourthActivityTv.setText(txt);
        });
        mOnRestarted = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mOnRestarted = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: " + getLifecycle().getCurrentState());
        super.onResume();
        if (!mOnRestarted) {
            mViewModel.initSource();
            mOnRestarted = false;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState,
                                    @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Log.d(TAG, "onSaveInstanceState: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent: " + getLifecycle().getCurrentState());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: " + getLifecycle().getCurrentState());
    }
}