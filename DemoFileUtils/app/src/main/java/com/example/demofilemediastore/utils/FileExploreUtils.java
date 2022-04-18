package com.example.demofilemediastore.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileExploreUtils {
    public static List<File> getFilesList(File file) {
        List<File> list = new ArrayList<>(0);
        File[] rawFilesList = file.listFiles();
        if (rawFilesList != null) {
            for (File f : rawFilesList) {
                if (!f.isHidden()) {
                    list.add(f);
                }
            }
        }

        return list;
    }
}
