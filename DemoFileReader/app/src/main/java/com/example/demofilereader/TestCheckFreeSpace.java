package com.example.demofilereader;

import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.storage.StorageManager;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.UUID;

import timber.log.Timber;

public class TestCheckFreeSpace {
    public void checkFreeSpace(@NonNull Context context) {
        // App needs 10 MB within internal storage.
        final long NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 10L;

        StorageManager storageManager =
                context.getApplicationContext().getSystemService(StorageManager.class);
        UUID appSpecificInternalDirUuid = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                appSpecificInternalDirUuid = storageManager.getUuidForPath(context.getFilesDir());
            } catch (IOException e) {
                Timber.e(e);
            }
        }
        long availableBytes = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                availableBytes = storageManager.getAllocatableBytes(appSpecificInternalDirUuid);
            } catch (IOException e) {
                Timber.e(e);
            }
        }
        if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    storageManager.allocateBytes(
                            appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP);
                } catch (IOException e) {
                    Timber.e(e);
                }
            }
        } else {
            // To request that the user remove all app cache files instead, set
            // "action" to ACTION_CLEAR_APP_CACHE.
            Intent storageIntent = new Intent();
            storageIntent.setAction(StorageManager.ACTION_MANAGE_STORAGE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            StorageStatsManager storageStatsManager =
                    context.getApplicationContext().getSystemService(StorageStatsManager.class);
            try {
                long f = storageStatsManager.getFreeBytes(appSpecificInternalDirUuid);
                long t = storageStatsManager.getTotalBytes(appSpecificInternalDirUuid);
                Timber.d("free/total: %f%100", f/t * 100f);
            } catch (IOException e) {
                Timber.e(e);
            }
        }
    }
}
