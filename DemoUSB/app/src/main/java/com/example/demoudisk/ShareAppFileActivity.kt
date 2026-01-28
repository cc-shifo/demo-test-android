package com.example.demoudisk

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.io.FileOutputStream
import android.os.Process

class ShareAppFileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent?.action == Intent.ACTION_VIEW) {
            handleIncomingFile(intent.data)
        }
        setContentView(R.layout.activity_share_app_file)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun handleIncomingFile(uri: Uri?) {
        uri ?: return

        // 检查权限有效期
        val modeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        if (checkUriPermission(uri, Process.myPid(), Process.myUid(),
                modeFlags) != PackageManager.PERMISSION_GRANTED) {
            // 请求临时权限
            contentResolver.takePersistableUriPermission(uri, modeFlags)
        }

        // 复制到安全位置
        val inputStream = contentResolver.openInputStream(uri)
        val outputDir = getExternalFilesDir(null)
        val outputFile = File(outputDir, "received_file_${System.currentTimeMillis()}")

        inputStream?.use { input ->
            FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }

        // 处理文件
        // processReceivedFile(outputFile)
    }
}