package com.example.demoh5jsnativecomm.fileexplorer;

import java.io.File;

public class ItemData {
    private File mFile;
    private String mItemName;

    public ItemData(File file, String itemName) {
        mFile = file;
        mItemName = itemName;
    }

    public File getFile() {
        return mFile;
    }

    public void setFile(File file) {
        mFile = file;
    }

    public String getItemName() {
        return mItemName;
    }

    public void setItemName(String itemName) {
        mItemName = itemName;
    }
}
