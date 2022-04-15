package com.example.demofilemediastore;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class TestFileSelector {

    /**
     * 注意不能访问的目录。详情见{@link TestStorageAccessFramework#openDirectory(Activity, Uri)}
     */
    public static final int PICK_TREE = 30;

    public void openDirectory(@NonNull Activity activity, @Nullable Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
            // intent.addCategory(Intent.CATEGORY_OPENABLE);
            // intent.setType("*/*");
            // Optionally, specify a URI for the directory that should be opened in
            // the system file picker when it loads.
            // intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);

            activity.startActivityForResult(intent, PICK_TREE);
        }
    }

    // Request code for selecting a PDF document.
    private static final int PICK_PDF_FILE = 20;

    /**
     * android 11不能用ACTION_OPEN_DOCUMENT的intent去访问以下两个目录下的文件
     * The Android/data/ directory and all subdirectories.
     * The Android/obb/ directory and all subdirectories.
     *
     * @param pickerInitialUri 要打开的文件的uri
     */
    public void openFile(@NonNull Activity activity, @Nullable Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        // intent.setType("application/pdf");
        intent.setType("*/*");

        if (pickerInitialUri != null) {
            // Optionally, specify a URI for the file that should appear in the
            // system file picker when it loads.
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        }

        activity.startActivityForResult(intent, PICK_PDF_FILE);
    }
}
