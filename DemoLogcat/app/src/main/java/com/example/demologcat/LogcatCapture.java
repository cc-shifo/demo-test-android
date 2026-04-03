package com.example.demologcat;

import android.util.Log;

import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * 通过执行 logcat 命令将系统日志写入文件
 * 注意：需要 READ_LOGS 权限，普通应用不可用（仅系统应用或 root 设备）
 */
public class LogcatCapture {
    private static final String TAG = "LogcatCapture";
    private Process logcatProcess;
    private boolean isCapturing = false;
    private String outputFilePath;

    /**
     * 启动 logcat 输出到指定文件（会清空之前的文件内容，然后持续追加）
     * @param filePath 文件绝对路径，需确保父目录存在且有写权限
     * @param logFormat logcat 格式，如 "time"、"threadtime" 等，推荐 "threadtime"
     * @param filterSpec 过滤表达式，如 "*:V" 或 "MyTag:D *:S"，可为 null
     * @return 是否成功启动
     */
    public boolean start(String filePath, @Nullable String logFormat, @Nullable String filterSpec) {
        if (isCapturing) {
            Log.w(TAG, "Already capturing logcat, stop first.");
            return false;
        }
        try {
            // 准备命令：logcat -v <format> [-s <filter>] -f <file>
            // 注意：-f 参数会直接让 logcat 写入文件，不需要重定向，但必须保证目标文件可写
            StringBuilder cmd = new StringBuilder("logcat -v ").append(logFormat);
            if (filterSpec != null && !filterSpec.isEmpty()) {
                cmd.append(" ").append(filterSpec);
            }
            cmd.append(" -f ").append(filePath);
            // 也可以使用 -r <kbytes> -n <count> 来循环日志，此处简化
            Log.d(TAG, "Executing: " + cmd.toString());
            logcatProcess = Runtime.getRuntime().exec(cmd.toString());
            outputFilePath = filePath;
            isCapturing = true;

            // 可选：启动一个线程监控 logcat 进程的错误流，以便发现异常
            monitorErrorStream();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Failed to start logcat", e);
            return false;
        }
    }

    /**
     * 启动 logcat 并输出到文件（使用默认格式 threadtime，无过滤）
     */
    public boolean start(String filePath) {
        return start(filePath, "threadtime", null);
    }

    /**
     * 停止 logcat 进程
     */
    public void stop() {
        if (logcatProcess != null && isCapturing) {
            logcatProcess.destroy();
            try {
                logcatProcess.waitFor();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logcatProcess = null;
            isCapturing = false;
            Log.i(TAG, "Logcat capture stopped. Output file: " + outputFilePath);
        }
    }

    /**
     * 获取当前捕获状态
     */
    public boolean isCapturing() {
        return isCapturing;
    }

    /**
     * 监控 logcat 进程的错误输出，以便检测权限不足等问题
     */
    private void monitorErrorStream() {
        if (logcatProcess == null) return;
        new Thread(() -> {
            // try (BufferedReader errorReader = new BufferedReader(
            //         new InputStreamReader(logcatProcess.getErrorStream()))) {
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(logcatProcess.getInputStream()))) {
                String line;
                while ((line = errorReader.readLine()) != null) {
                    Log.e(TAG, "logcat error: " + line);
                    // 如果错误信息包含 "Permission denied"，则自动停止捕获
                    if (line.contains("Permission denied") || line.contains("not allowed")) {
                        Log.e(TAG, "Permission denied for logcat. Stopping capture.");
                        stop();
                        break;
                    }
                }
            } catch (IOException e) {
                Log.e(TAG, "Error reading logcat error stream", e);
            }
        }).start();
    }
}