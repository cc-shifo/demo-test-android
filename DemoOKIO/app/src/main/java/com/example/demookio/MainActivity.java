/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20230107 	         LiuJian                  Create
 */

package com.example.demookio;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.demoscreensplithelper.R;
import com.example.demoscreensplithelper.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding mBinding;
    private static final int REQ_CODE_30 = 10001;
    private static final int REQ_CODE_29 = 10002;

    private static final String[] API30_PERMISSION_LIST = new String[]{
            // Manifest.permission.WRITE_EXTERNAL_STORAGE, // files
            Manifest.permission.READ_EXTERNAL_STORAGE, // files
    };

    /**
     * Android 10及以下。Manifest中requestLegacyExternalStorage=true
     */
    private static final String[] API29_PERMISSION_LIST = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE, // files
            Manifest.permission.READ_EXTERNAL_STORAGE, // files
    };

    private OKIOUtil mOKIOUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission
                    .READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, API30_PERMISSION_LIST, REQ_CODE_30);
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, API29_PERMISSION_LIST, REQ_CODE_29);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQ_CODE_30) {
            if (permissions.length == 1 && grantResults.length == 1) {
                initOKIO();
            }
        } else if (requestCode == REQ_CODE_29) {
            if (permissions.length == 2 && grantResults.length == 2) {
                initOKIO();
            }
        }
    }

    private void initOKIO() {

    }

    private void initAppend() {
        boolean ret = OKIOUtil.getInstance().open(this, null, false, true);
        if (ret) {
            Log.d(TAG, "initAppend: false");
        }
    }

    private void initRead() {
        boolean ret = OKIOUtil.getInstance().open(this, null, true, false);
        if (ret) {
            Log.d(TAG, "initRead: false");
        }
    }
}