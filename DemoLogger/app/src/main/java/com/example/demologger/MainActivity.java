package com.example.demologger;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.demologger.databinding.ActivityMainBinding;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import pub.devrel.easypermissions.EasyPermissions;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {
    private ActivityMainBinding mBinding;
    private String mPath;
    private long mCounter;
    private AtomicBoolean mAtomicBoolean;
    private static final int PERMISSION_REQUEST_CODE = 0x01;
    private final String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mPath = getExternalCacheDir().getAbsolutePath() + "/logsample/xlog";

        // mPath = getExternalFilesDir("cache").getAbsolutePath();
        // mPath = "/storage/sdcard0/Documents/log";
        // mPath = Environment.getDownloadCacheDirectory().getAbsolutePath();
        LogUtil.openStoragePolicy(mPath, 0);
        LogUtil.enableStorage(true);
        mAtomicBoolean = new AtomicBoolean(true);
        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "请开启权限",
                    PERMISSION_REQUEST_CODE, permissions);
        } else {
            initBtn();
        }
    }

    private void initMarsXLog() {
        final String SDCARD = Environment.getDownloadCacheDirectory().getAbsolutePath();
        final String logPath = SDCARD + "/marssample/log";
        // this is necessary, or may crash for SIGBUS
        final String cachePath = this.getFilesDir() + "/xlog";
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        initBtn();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        EasyPermissions.requestPermissions(this, "请开启权限", PERMISSION_REQUEST_CODE, permissions);
    }

    private void initBtn() {
        mPath = mPath + ": ";
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                mCounter = 0;
                mAtomicBoolean.set(true);
                while (mAtomicBoolean.get()) {
                    Timber.i("%s: %d", mPath, mCounter);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBinding.tvHelloWorld.setText(mPath + mCounter);
                        }
                    });
                    try {
                        Thread.sleep(500);
                        mCounter++;
                    } catch (InterruptedException e) {
                        Timber.e(e);
                    }
                }

            }
        });
        mBinding.btnCreateData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!thread.isAlive()) {
                    thread.start();
                }
            }
        });

        mBinding.btnDestroyData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!thread.isInterrupted()) {
                    mAtomicBoolean.set(false);
                    thread.interrupt();
                }
            }
        });
    }
}