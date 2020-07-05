package com.example.demo.demobottomsheetdialogfragment.home;

import android.graphics.drawable.Drawable;

public class HomeMenuItemData {
    /**
     * item icon resource id
     */
    private int mImageResID;
    /**
     * item label
     */
    private String mLabel;
    /**
     * the Activity will be started when clicking on an item.
     */
    private String mComponentClass;

    public HomeMenuItemData(int imageResID, String label, String componentClass) {
        mImageResID = imageResID;
        mLabel = label;
        mComponentClass = componentClass;
    }

    public int getImageResID() {
        return mImageResID;
    }

    public void setImageResID(int imageResID) {
        mImageResID = imageResID;
    }

    public String getLabel() {
        return mLabel;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public String getComponentClass() {
        return mComponentClass;
    }

    public void setComponentClass(String componentClass) {
        mComponentClass = componentClass;
    }
}
