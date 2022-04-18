package com.example.demofilemediastore.utils;

import androidx.annotation.IntDef;

@IntDef(value = {MediaType.IMAGE, MediaType.VIDEO, MediaType.RECORD, MediaType.DOWNLOAD,
        MediaType.DOCUMENTS})
public @interface MediaType {
    /**
     * /sdcard/Pictures/
     */
    int IMAGE = 1;
    /**
     * external "/sdcard/Movies/"
     */
    int VIDEO = 2;
    /**
     * The recordings directory isn't available on Android 11 (API level 30) and lower
     * android 12 and higher: /sdcard/Recordings
     * android 11: external "/sdcard/DCIM/"
     * android 10 and lower: external "/sdcard/Sounds/"
     */
    int RECORD = 3;
    /**
     * external "/sdcard/Download" directory
     */
    int DOWNLOAD = 4;
    /**
     * external "/sdcard/Documents" directory
     */
    int DOCUMENTS = 5;
}
