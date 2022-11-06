package com.example.demoh5jsnativecomm.utils;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FileExploreUtil {

    private FileExploreUtil() {
        // nothing
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @param comparator  The comparator to determine the order of the list.
     * @return the files that satisfy the filter in directory
     */
    public static List<File> listFilesInDirWithFilter(@NonNull final File dir,
                                                      @Nullable final FileFilter filter,
                                                      final boolean isRecursive,
                                                      @Nullable final Comparator<File> comparator) {
        List<File> files = listFilesInDirWithFilterInner(dir, filter, isRecursive);
        if (comparator != null) {
            Collections.sort(files, comparator);
        }
        return files;
    }

    /**
     * Return the files that satisfy the filter in directory.
     *
     * @param dir         The directory.
     * @param filter      The filter.
     * @param isRecursive True to traverse subdirectories, false otherwise.
     * @return the files that satisfy the filter in directory
     */
    public static List<File> listFilesInDirWithFilterInner(@NonNull final File dir,
                                                           @Nullable final FileFilter filter,
                                                           final boolean isRecursive) {
        List<File> list = new ArrayList<>();
        if (!FileUtil.isDir(dir)) {
            return list;
        }
        File[] files = dir.listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (filter != null && filter.accept(file)) {
                    list.add(file);
                }
                if (isRecursive && file.isDirectory()) {
                    list.addAll(listFilesInDirWithFilterInner(file, filter, true));
                }
            }
        }
        return list;
    }
}
