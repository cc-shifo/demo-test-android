package com.example.demosensor;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.demosensor.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private SensorManager mSensorManager;
    private final float[] mAccelerometerReading = new float[3];
    private final float[] mMagnetometerReading = new float[3];

    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationAngles = new float[3];
    private SensorEventListener mSensorEventListener;
    private MutableLiveData<String> mLiveData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initDats();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterSensorListener();
    }

    private void initDats() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mLiveData = new MutableLiveData<>();
        mLiveData.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                mBinding.tvMsg.setText(s);
            }
        });
        mSensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                onSensorDataChange(sensorEvent);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
                // nothing
            }
        };
    }

    private void registerSensorListener() {
        // Get updates from the accelerometer and magnetometer at a constant rate.
        // To make batch operations more efficient and reduce power consumption,
        // provide support for delaying updates to the application.
        //
        // In this example, the sensor reporting delay is small enough such that
        // the application receives an update before the system checks the sensor
        // readings again.
        Sensor accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer != null) {
            mSensorManager.registerListener(mSensorEventListener, accelerometer,
                    5000000/*SensorManager.SENSOR_DELAY_NORMAL*/, 10000000);
        }
        Sensor magneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magneticField != null) {
            mSensorManager.registerListener(mSensorEventListener, magneticField,
                    5000000/*SensorManager.SENSOR_DELAY_NORMAL*/, 10000000);
        }
    }

    private void unregisterSensorListener() {
        mSensorManager.unregisterListener(mSensorEventListener);
    }

    private void onSensorDataChange(@NonNull SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mAccelerometerReading,
                    0, mAccelerometerReading.length);
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mMagnetometerReading,
                    0, mMagnetometerReading.length);
            calculateOrientation();
        }
    }

    private void calculateOrientation() {
        float[] rotationMatrix = new float[9];
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(rotationMatrix, null, mAccelerometerReading,
                mMagnetometerReading);
        // "mRotationMatrix" now has up-to-date information.
        SensorManager.getOrientation(rotationMatrix, mOrientationAngles);
        // "mOrientationAngles" now has up-to-date information.

        calculateHeading();
    }

    /**
     * 正北方向0度，从正北沿顺时针方向转动，偏移角为正，范围0~180;从正北沿逆时针方向转动，偏移角为负，范围0~-180
     */
    private void calculateHeading() {
        float heading = (float) Math.toDegrees(mOrientationAngles[0]);
        Log.d(TAG, "calculateHeading: ");
        mLiveData.postValue(String.valueOf(heading));
    }
}