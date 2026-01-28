package com.example.demoudisk

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class ShareTestAPPFileUri {
    fun getShareableUri(context: Context, file: File): Uri {
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    // 创建分享Intent
    fun createShareIntent(context: Context, file: File): Intent {
        val uri = getShareableUri(context, file)
        return Intent(Intent.ACTION_SEND).apply {
            type = context.contentResolver.getType(uri)
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
    }
}