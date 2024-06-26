package com.example.scopedstoragedemo

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.scopedstoragedemo.databinding.ActivityMainBinding
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

const val PICK_FILE = 1
const val PICK_IMAGES = 2
const val CREATE_WRITE_REQUEST = 3
const val ALL_FILES_ACCESS_PERMISSION = 4

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val permissionsToRequire = ArrayList<String>()
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequire.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionsToRequire.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
        if (permissionsToRequire.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequire.toTypedArray(), 0)
        }
        binding.browseAlbum.setOnClickListener {
            val intent = Intent(this, BrowseAlbumActivity::class.java)
            startActivity(intent)
        }
        binding.addImageToAlbum.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)
            val displayName = "${System.currentTimeMillis()}.jpg"
            val mimeType = "image/jpeg"
            val compressFormat = Bitmap.CompressFormat.JPEG
            addBitmapToAlbum(bitmap, displayName, mimeType, compressFormat)
        }
        binding.downloadFile.setOnClickListener {
            val fileUrl = "http://guolin.tech/android.txt"
            val fileName = "android.txt"
            downloadFile(fileUrl, fileName)
        }
        binding.pickFile.setOnClickListener {
            pickFileAndCopyUriToExternalFilesDir()
        }
        binding.writeRequest.setOnClickListener {
            val intent = Intent(this, BrowseAlbumActivity::class.java)
            intent.putExtra("pick_files", true)
            startActivityForResult(intent, PICK_IMAGES)
        }
        binding.manageExternalStorage.setOnClickListener {
            requestAllFilesAccessPermission()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0) {
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "You must allow all the permissions.", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                }
            }
        }
    }

    private fun addBitmapToAlbum(
        bitmap: Bitmap,
        displayName: String,
        mimeType: String,
        compressFormat: Bitmap.CompressFormat
    ) {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        } else {
            values.put(
                MediaStore.MediaColumns.DATA,
                "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
            )
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(compressFormat, 100, outputStream)
                outputStream.close()
                Toast.makeText(this, "Add bitmap to album succeeded.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadFile(fileUrl: String, fileName: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(
                this,
                "You must use device running Android 10 or higher",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        thread {
            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                val inputStream = connection.inputStream
                val bis = BufferedInputStream(inputStream)
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOWNLOADS + "/abcd"
                )
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    val outputStream = contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        val bos = BufferedOutputStream(outputStream)
                        val buffer = ByteArray(1024)
                        var bytes = bis.read(buffer)
                        while (bytes >= 0) {
                            bos.write(buffer, 0, bytes)
                            bos.flush()
                            bytes = bis.read(buffer)
                        }
                        bos.close()
                        runOnUiThread {
                            Toast.makeText(
                                this,
                                "$fileName is in Download directory now.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                bis.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun pickFileAndCopyUriToExternalFilesDir() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        startActivityForResult(intent, PICK_FILE)
    }

    private fun requestAllFilesAccessPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R || Environment.isExternalStorageManager()) {
            Toast.makeText(
                this,
                "We can access all files on external storage now",
                Toast.LENGTH_SHORT
            ).show()
        } else {
            val builder = AlertDialog.Builder(this)
                .setTitle("Tip")
                .setMessage("We need permission to access all files on external storage")
                .setPositiveButton("OK") { _, _ ->
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivityForResult(intent, ALL_FILES_ACCESS_PERMISSION)
                }
                .setNegativeButton("Cancel", null)
            builder.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PICK_FILE -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    val uri = data.data
                    if (uri != null) {
                        val fileName = getFileNameByUri(uri)
                        createDirInDownload(uri, fileName)
                        copyUriToExternalFilesDir(uri, fileName)
                    }
                }
            }
            PICK_IMAGES -> {
                if (resultCode == Activity.RESULT_OK && data != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        val urisToModify =
                            data.getSerializableExtra("checked_uris") as ArrayList<Uri>
                        val editPendingIntent =
                            MediaStore.createWriteRequest(contentResolver, urisToModify)
                        startIntentSenderForResult(
                            editPendingIntent.intentSender, CREATE_WRITE_REQUEST,
                            null, 0, 0, 0
                        )
                    } else {
                        Toast.makeText(this, "Write permissions are granted", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            CREATE_WRITE_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "Write permissions are granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Write permissions are denied", Toast.LENGTH_SHORT).show()
                }
            }
            ALL_FILES_ACCESS_PERMISSION -> {
                requestAllFilesAccessPermission()
            }
        }
    }

    private fun getFileNameByUri(uri: Uri): String {
        var fileName = System.currentTimeMillis().toString()
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            fileName =
                cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME))
            cursor.close()
        }
        return fileName
    }

    private fun copyUriToExternalFilesDir(uri: Uri, fileName: String) {
        thread {
            val inputStream = contentResolver.openInputStream(uri)
            val tempDir = getExternalFilesDir("temp")
            if (inputStream != null && tempDir != null) {
                val file = File("$tempDir/$fileName")
                val fos = FileOutputStream(file)
                val bis = BufferedInputStream(inputStream)
                val bos = BufferedOutputStream(fos)
                val byteArray = ByteArray(1024)
                var bytes = bis.read(byteArray)
                while (bytes > 0) {
                    bos.write(byteArray, 0, bytes)
                    bos.flush()
                    bytes = bis.read(byteArray)
                }
                bos.close()
                fos.close()
                runOnUiThread {
                    Toast.makeText(this, "Copy file into $tempDir succeeded.", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun createDirInDownload(uri: Uri, fileName: String) {
//        var downloadPath : String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
//        var downloadPath2 : String = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).absolutePath
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
//            Toast.makeText(this, "You must use device running Android 10 or higher", Toast.LENGTH_SHORT).show()
//            return
//        }
//        thread {
//            try {
//                val values = ContentValues()
////                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
//                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
//                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(
                this,
                "You must use device running Android 10 or higher",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        thread {
            val values = ContentValues()
//            values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                val outputStream = contentResolver.openOutputStream(uri)
                if (outputStream != null) {
//                    val bos = BufferedOutputStream(outputStream)
//                    val buffer = ByteArray(1024)
//                    var bytes = bis.read(buffer)
//                    while (bytes >= 0) {
//                        bos.write(buffer, 0 , bytes)
//                        bos.flush()
//                        bytes = bis.read(buffer)
//                    }
//                    bos.close()
                    runOnUiThread {
                        Toast.makeText(
                            this,
                            "$fileName is in Download directory now.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

    }


}
