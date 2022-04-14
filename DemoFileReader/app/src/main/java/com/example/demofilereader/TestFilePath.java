package com.example.demofilereader;

import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestFilePath {

    /**
     * internal storage——利用内部存储器，用于存储应用持久化文件的
     * @param context 应用的上下文
     * @return /data/user/0/包名/files/
     */
    File getInternalPriFile(@NonNull Context context) {
        return context.getFilesDir();
    }

    /**
     * external storage——利用存储器，用于存储应用持久化文件的
     * @param context 应用的上下文
     * @return /sdcard/Android/data/package name/
     */
    File getExtPriFile(@NonNull Context context) {
        return context.getExternalFilesDir(null);
    }


    List<String> getFileList(@NonNull Context context) {
        String[] files = context.fileList();

        return Arrays.asList(files);
    }
}
