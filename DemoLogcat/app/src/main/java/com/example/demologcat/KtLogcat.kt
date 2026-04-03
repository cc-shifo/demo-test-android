package com.example.demologcat

import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.lang.StringBuilder

class KtLogcat {
    private val TAG = "LogcatCapture"
    // companion object {
    //     private const val TAG = "LogcatCapture"
    // }
    private var logcatProcess: Process? = null
    private var isCapturing = false
    private var outputFilePath: String? = null

    /**
     * 启动 logcat 输出到指定文件（会清空之前的文件内容，然后持续追加）
     * @param filePath 文件绝对路径，需确保父目录存在且有写权限
     * @param logFormat logcat 格式，如 "time"、"threadtime" 等，推荐 "threadtime"
     * @param filterSpec 过滤表达式，如 "*:V" 或 "MyTag:D *:S"，可为 null
     * @return 是否成功启动
     */
    fun start(filePath: String, logFormat: String?, filterSpec: String?): Boolean {
        if (isCapturing) {
            Log.w(TAG, "Already capturing logcat, stop first.")
            return false
        }
        return try {
            // 准备命令：logcat -v <format> [-s <filter>] -f <file>
            // 注意：-f 参数会直接让 logcat 写入文件，不需要重定向，但必须保证目标文件可写
            val cmd = StringBuilder("logcat -v").apply {
                append(" ").append(logFormat ?: "threadtime").append(" uid")

                if (!filterSpec.isNullOrEmpty()) {
                    append(" ").append(filterSpec)
                }
                append(" -f ").append(filePath)
            }
            // 也可以使用 -r <kbytes> -n <count> 来循环日志，此处简化
            Log.d(TAG, "Executing: $cmd")
            logcatProcess = Runtime.getRuntime().exec(cmd.toString())
            outputFilePath = filePath
            isCapturing = true

            // 可选：启动一个线程监控 logcat 进程的错误流，以便发现异常
            monitorErrorStream()
            true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to start logcat", e)
            false
        }
    }

    /**
     * 启动 logcat 并输出到文件（使用默认格式 threadtime，无过滤）
     */
    fun start(filePath: String): Boolean {
        return start(filePath, null, null)
    }

    /**
     * 停止 logcat 进程
     */
    fun stop() {
        if (logcatProcess != null && isCapturing) {
            logcatProcess?.destroy()
            try {
                logcatProcess?.waitFor()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
            logcatProcess = null
            isCapturing = false
            Log.i(TAG, "Logcat capture stopped. Output file: $outputFilePath")
        }
    }

    /**
     * 获取当前捕获状态
     */
    fun isCapturing(): Boolean {
        return isCapturing
    }

    /**
     * 监控 logcat 进程的错误输出，以便检测权限不足等问题
     */
    private fun monitorErrorStream() {
        if (logcatProcess == null) return
        Thread {
            try {
                BufferedReader(InputStreamReader(logcatProcess!!.errorStream)).use { errorReader ->
                    var line: String?
                    while (errorReader.readLine().also { line = it } != null) {
                        Log.e(TAG, "logcat error: $line")
                        // 如果错误信息包含 "Permission denied"，则自动停止捕获
                        if (line!!.contains("Permission denied") || line!!.contains("not allowed")) {
                            Log.e(TAG, "Permission denied for logcat. Stopping capture.")
                            stop()
                            break
                        }
                    }
                }
            } catch (e: IOException) {
                Log.e(TAG, "Error reading logcat error stream", e)
            }
        }.start()
    }

}