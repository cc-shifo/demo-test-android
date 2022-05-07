package com.example.demoh5jsnativecomm.utils;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.demoh5jsnativecomm.R;

import pub.devrel.easypermissions.EasyPermissions;

public class PermissionUtil {
    public static final int MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 1;
    public static final int WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST = 2;
    private static final String[] STORAGE_PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PermissionUtil() {
        // nothing
    }

    public static boolean checkManagerStoragePermission(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return checkStoragePermissionApi30(activity);
        } else {
            return checkStoragePermissionApi19(activity);
        }
    }

    public static void requestManagerStoragePermission(AppCompatActivity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            requestStoragePermissionApi30(activity);
        }
        // If you want to see the default storage behavior on Android Q once the permission is
        // granted
        // Set the "requestLegacyExternalStorage" flag in the AndroidManifest.xml file to false
        else {
            requestStoragePermissionApi19(activity);
        }
    }

    @RequiresApi(30)
    private static boolean checkStoragePermissionApi30(AppCompatActivity activity) {
        AppOpsManager appOps = activity.getSystemService(AppOpsManager.class);
        int mode = appOps.unsafeCheckOpNoThrow(
                "android:manage_external_storage",
                activity.getApplicationInfo().uid,
                activity.getPackageName()
        );

        return mode == AppOpsManager.MODE_ALLOWED;
    }

    @RequiresApi(19)
    private static boolean checkStoragePermissionApi19(AppCompatActivity activity) {
        return EasyPermissions.hasPermissions(activity, STORAGE_PERMISSIONS);
    }


    @RequiresApi(30)
    private static void requestStoragePermissionApi30(AppCompatActivity activity) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);

        activity.startActivityForResult(intent, MANAGE_EXTERNAL_STORAGE_PERMISSION_REQUEST);
    }

    @RequiresApi(19)
    private static void requestStoragePermissionApi19(AppCompatActivity activity) {
        EasyPermissions.requestPermissions(activity,
                activity.getString(R.string.request_storage_rationale),
                WRITE_EXTERNAL_STORAGE_PERMISSION_REQUEST, STORAGE_PERMISSIONS);
    }
}
