/*
 * = COPYRIGHT
 *          xxxx
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                    Author                  Action
 * 20221021                LiuJian                 Create
 */

package com.example.demologger;

import android.util.Log;

import androidx.annotation.NonNull;

import timber.log.Timber;

/**
 * Output messages into terminal or files. Initialize this util by calling
 * {@link LogUtil#plant(boolean)}.
 */
public final class LogUtil {
    /**
     * all message sent to {@code FileReportingTree} will be written into file if {@link LogUtil}
     * works in file log mode.
     */
    private static boolean mStorageEnabled = false;
    private static boolean mTerminalEnabled = true;

    private LogUtil() {
        // nothing
    }

    /**
     * @param isDebugBuildType true all message will be outputted into terminal, it's usually
     *                         used in debug build version of APK. Otherwise set it false, then
     *                         file log mode will be open, all message will be sent to {@code
     *                         FileReportingTree}
     */
    public static void plant(boolean isDebugBuildType) {
        if (isDebugBuildType) {
            mTerminalEnabled = true;
            mStorageEnabled = false;
        }
        Timber.plant(new ReportingTree());
    }

    public static void openStoragePolicy(@NonNull String logDir, int cacheDays) {
        /*init xlog*/
        FileLog.open(logDir, cacheDays);
    }

    public static void close() {
        FileLog.close();
    }

    /**
     * @param enabled true all messages sent to {@code ReportingTree} will be outputted into
     *                terminal.
     */
    public static void enableTerminal(boolean enabled) {
        mTerminalEnabled = enabled;
    }

    /**
     * @param enabled true all messages sent to {@code ReportingTree} will be written into
     *                file.
     */
    public static void enableStorage(boolean enabled) {
        mStorageEnabled = enabled;
    }

    /**
     * A tree which logs important information into a file. In default, only {@link Log#WARN} and
     * {@link Log#ERROR} will be written into file, which is saved under the subdirectory of
     * {@link android.os.Environment#DIRECTORY_DOWNLOADS}, the application package name will be
     * the subdirectory name.
     */
    private static class ReportingTree extends Timber.DebugTree {
        @Override
        protected void log(int priority, String tag, @NonNull String message, Throwable t) {
            StackTraceElement[] stackTrace = new Throwable().getStackTrace();
            if (stackTrace.length <= 5) {
                throw new IllegalStateException(
                        "Synthetic stacktrace didn't have enough elements: are you using " +
                                "proguard?");
            }
            message = message + "(line:" + stackTrace[5].getLineNumber() + ")";

            if (mTerminalEnabled) {
                super.log(priority, tag, message, t);
            }

            if (mStorageEnabled) {
                if (priority == Log.VERBOSE) {
                    priority = com.tencent.mars.xlog.Log.LEVEL_VERBOSE;
                } else if (priority == Log.DEBUG) {
                    priority = com.tencent.mars.xlog.Log.LEVEL_DEBUG;
                } else if (priority == Log.INFO) {
                    priority = com.tencent.mars.xlog.Log.LEVEL_INFO;
                } else if (priority == Log.WARN) {
                    priority = com.tencent.mars.xlog.Log.LEVEL_WARNING;
                } else if (priority == Log.ERROR) {
                    priority = com.tencent.mars.xlog.Log.LEVEL_ERROR;
                } else if (priority == Log.ASSERT) {
                    priority = com.tencent.mars.xlog.Log.LEVEL_FATAL;
                } else {
                    priority = com.tencent.mars.xlog.Log.LEVEL_NONE;
                }

                FileLog.log(priority, tag, message, t);
            }
        }
    }
}
