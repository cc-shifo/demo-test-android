/*
 * Copyright (c) 2022.
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 *
 * Revision History:
 *
 *     name                    action                  time
 *     liujian                 creation                20220424 13:53:34
 *
 */

package com.example.demoh5jsnativecomm.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.Okio;
import okio.Sink;
import okio.Source;

public class FileUtil {
    private static final String TAG = "FileUtil";
    private FileUtil() {
        // nothing
    }

    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    public static File getFileByPath(final String filePath) {
        return new File(filePath);
    }

    /**
     * Return whether it is a directory.
     *
     * @param dirPath The path of directory.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isDir(final String dirPath) {
        return isDir(getFileByPath(dirPath));
    }

    /**
     * Return whether it is a directory.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isDir(final File file) {
        return file != null && file.exists() && file.isDirectory();
    }

    /**
     * Return whether it is a file.
     *
     * @param filePath The path of file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFile(final String filePath) {
        return isFile(getFileByPath(filePath));
    }

    /**
     * Return whether it is a file.
     *
     * @param file The file.
     * @return {@code true}: yes<br>{@code false}: no
     */
    public static boolean isFile(final File file) {
        return file != null && file.exists() && file.isFile();
    }


    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param dirPath The path of directory.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return {@code true}: exists or creates successfully<br>{@code false}: otherwise
     */
    public static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * Write the data source of inputStream into the destination of fileOutput.
     *
     * @param fileOutput  the outputted destination.
     * @param inputStream the inputted data source.
     */
    public static void writeFile(File fileOutput, InputStream inputStream) {
        try (Sink fileSink = Okio.sink(fileOutput);
             BufferedSink bufferedSink = Okio.buffer(fileSink);
             Source fileSrc = Okio.source(inputStream)) {
            bufferedSink.writeAll(fileSrc);
            bufferedSink.flush();
        } catch (IOException e) {
            Log.e(TAG, "writeFile: ", e);
        }
    }
}
