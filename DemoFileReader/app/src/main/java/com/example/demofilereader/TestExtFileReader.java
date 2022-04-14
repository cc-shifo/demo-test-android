package com.example.demofilereader;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;

public class TestExtFileReader {

    // Checks if a volume containing external storage is available
    // for read and write.
    public boolean isExternalStorageWritable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    // Checks if a volume containing external storage is available to at least read.
    public boolean isExternalStorageReadable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) ||
                Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED_READ_ONLY);
    }
}
