package com.example.demoudisk;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

import androidx.annotation.NonNull;

public class DocUtil {
    private static final String TAG = "DocUtil";
    public static void createDirectory(@NonNull Context context,  Uri uri, String directoryName) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri docUri = DocumentsContract.buildDocumentUriUsingTree(uri,
                DocumentsContract.getTreeDocumentId(uri));
        Uri directoryUri = null;
        try {
            directoryUri = DocumentsContract
                    .createDocument(contentResolver, docUri, DocumentsContract.Document.MIME_TYPE_DIR, directoryName);
        } catch (Exception e) {
            Log.w(TAG, "IOException", e);
        }
        if (directoryUri != null) {
            Log.i(TAG, String.format(
                    "Created directory : %s, Document Uri : %s, Created directory Uri : %s",
                    directoryName, docUri, directoryUri));

        } else {
            Log.w(TAG, String.format("Failed to create a directory : %s, Uri %s", directoryName,
                    docUri));
        }

    }
}
