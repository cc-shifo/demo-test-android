package com.pax.helloworld;


import androidx.annotation.NonNull;

public class AppInfo {
    /**
     * the other APKs installed in the device.
     */
    private String mPackageName;
    private LauncherActivity mLauncherActivity;
    private byte mPageNum;
    private byte mSeqInPage;

    /**
     * @param packageName application package name
     * @param launcherActivity   if it is not null, start the activity in current application
     */
    public AppInfo(String packageName, LauncherActivity launcherActivity) {
        this(packageName, launcherActivity, (byte)0, (byte)0);
    }

    public AppInfo(String packageName, LauncherActivity launcherActivity, byte pageNum, byte seqInPage) {
        this.mPackageName = packageName;
        this.mLauncherActivity = launcherActivity;
        this.mPageNum = pageNum;
        this.mSeqInPage = seqInPage;
    }

    @NonNull
    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public LauncherActivity getLauncherActivity() {
        return mLauncherActivity;
    }

    public void setLauncherActivity(LauncherActivity launcherActivity) {
        this.mLauncherActivity = launcherActivity;
    }

    public byte getPageNum() {
        return mPageNum;
    }

    public void setPageNum(byte pageNum) {
        mPageNum = pageNum;
    }

    public byte getSeqInPage() {
        return mSeqInPage;
    }

    public void setSeqInPage(byte seqInPage) {
        mSeqInPage = seqInPage;
    }

    public static class LauncherActivity{
        /**
         * the activity class in current application
         */
        private String className;
        /**
         * the activity icon in current application
         */
        private int iconRes;
        /**
         * the activity label in current application
         */
        private String label;

        public LauncherActivity(@NonNull String className, int iconRes, String label) {
            this.className = className;
            this.iconRes = iconRes;
            this.label = label;
        }

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public int getIconRes() {
            return iconRes;
        }

        public void setIconRes(int iconRes) {
            this.iconRes = iconRes;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }
}