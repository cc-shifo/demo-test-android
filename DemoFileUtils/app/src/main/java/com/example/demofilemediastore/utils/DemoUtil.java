package com.example.demofilemediastore.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

public class DemoUtil {
    //
    // /**
    //  * 通过文件路径取得文件Uri
    //  * 通过MediaStore获取文件uri
    //  * @return 获取失败返回null
    //  */
    // @RequiresApi(Build.VERSION_CODES.Q)
    // @Nullable
    // Uri getFileUri(@NonNull Context context, @NonNull String path) {
    //     // projection代表数据库中需要检索出来的列，也可以不写，query的第二个参数传null，写了性能更好
    //     String[] projection = new String[] {
    //             MediaStore.Downloads._ID,
    //             MediaStore.Downloads.DISPLAY_NAME,
    //             MediaStore.Downloads.RELATIVE_PATH
    //     };
    //     // 从path解析出路径和文件名
    //     String directoryPath = path.substringBeforeLast("/")
    //     String fileName = path.substringAfterLast("/")
    //
    //     // SQL语句，路径匹配和文件名匹配
    //     val selection =
    //             "${MediaStore.Downloads.RELATIVE_PATH} LIKE ? AND ${MediaStore.Downloads.DISPLAY_NAME} = ?"
    //     // SQL语句参数
    //     val selectionArgs = arrayOf("%$directoryPath%", fileName)
    //
    //     val contentResolver: ContentResolver = context.contentResolver
    //     val uri = MediaStore.Downloads.EXTERNAL_CONTENT_URI
    //     // 使用ContentResolver查找，获得数据库指针
    //     val cursor = contentResolver.query(uri, projection, selection, selectionArgs, null)
    //
    //     var fileUri: Uri? = null
    //     if (cursor?.moveToFirst() == true) {
    //         val columnIndex = cursor.getColumnIndex(MediaStore.Downloads._ID)
    //         val fileId = cursor.getLong(columnIndex)
    //         fileUri = Uri.withAppendedPath(uri, fileId.toString())
    //         cursor.close()
    //     }
    //     return fileUri
    // }
    //
    // /**
    //  * {@link java.io.File#}
    //  * 向Download写文件
    //  * 这里的inputStream可以通过File#inputStream()获得，也可以是其他形式的inputStream，如okhttp下载文件。提供灵活性。
    //  * path支持直接嵌套目录。
    //  *
    //  * 作者：BlueSocks
    //  * 链接：https://www.jianshu.com/p/4cb19aae2b93
    //  * 来源：简书
    //  * 著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
    //  * @param path
    //  * @return
    //  */
    // /**
    //  * 写inputStream到公共目录Download
    //  * @param path 文件路径，必须以Download/开头，且不包含文件名
    //  */
    // @RequiresApi(Build.VERSION_CODES.Q)
    // fun writeToDownload(
    //         context: Context,
    //         path: String,
    //         fileName: String,
    //         inputStream: InputStream
    //                    ) {
    //     val contentValues = ContentValues()
    //     // 设置文件路径
    //     contentValues.put(MediaStore.Downloads.RELATIVE_PATH, path)
    //     // 设置文件名称
    //     contentValues.put(MediaStore.Downloads.DISPLAY_NAME, fileName)
    //     // ContentUri 表示操作哪个数据库, contentValues 表示要插入的数据内容
    //     val uri = context.contentResolver.insert(
    //             MediaStore.Downloads.EXTERNAL_CONTENT_URI,
    //             contentValues
    //                                             )!!
    //             // 向 path/filename 文件中插入数据
    //             val os: OutputStream = context.contentResolver.openOutputStream(uri)!!
    //             val bos = BufferedOutputStream(os)
    //     inputStream.use { istream ->
    //             bos.use { bos ->
    //             val buff = ByteArray(1024)
    //         var count: Int
    //         while (istream.read(buff).apply { count = this } != -1) {
    //             bos.write(buff, 0, count)
    //         }
    //     }
    //     }
    // }
    //
    //
    // // 通过path检查文件存在性
    // fun checkFileExistence(context: Context, path: String): Boolean {
    //     return getFileUri(context, path) != null
    // }
    //
    // // 通过Uri获取文件名
    // fun getFileName(context: Context, uri: Uri): String {
    //     var fileName = ""
    //     val contentResolver = context.contentResolver
    //     // 此处只需要取出文件名这一项
    //     val projection = arrayOf(OpenableColumns.DISPLAY_NAME)
    //     contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
    //             val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
    //         cursor.moveToFirst()
    //         fileName = cursor.getString(nameIndex)
    //         cursor.close()
    //     }
    //
    //     return fileName
    // }
    //
    // // 删除指定path文件
    // fun deleteFile(context: Context, path: String) {
    //     val uri = getFileUri(context, path)
    //     if (uri == null) {
    //         return
    //     }
    //     context.contentResolver.delete(uri, null, null)
    // }
    //
    // // 跳转其他程序打开文件
    // void openFile(Context context, String path) {
    //     Intent intent = new Intent();
    //     intent.setAction(Intent.ACTION_VIEW);
    //     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    //     intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    //     Uri uri = getFileUri(context, path)
    //     if (uri == null) {
    //         toastError("打开文件失败")
    //         return
    //     }
    //     intent.setDataAndType(uri, context.contentResolver.getType(uri) ?: "*/*")
    //     try {
    //         context.startActivity(intent)
    //     } catch (exception:ActivityNotFoundException) {
    //         // 对于设定的MIME没有对应程序可打开的情况
    //         intent.setDataAndType(uri, "*/*")
    //         context.startActivity(intent)
    //     }
    // }
    //
    // // 判断是否可以使用MediaStore的一个小函数
    // private boolean useMediaStore(String path) {
    //     return path.startsWith(Environment.DIRECTORY_DOWNLOADS) &&
    //             Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    // }

}
