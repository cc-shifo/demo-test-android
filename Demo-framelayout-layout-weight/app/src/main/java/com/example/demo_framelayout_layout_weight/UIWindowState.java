package com.example.demo_framelayout_layout_weight;

public class UIWindowState {
    public static final int QUARTER_SCREEN = 1;
    public static final int FULL_SCREEN = 2;
    public static final int HALF_SCREEN = 3;

    public static final int START = 1;
    public static final int END = 2;


    /**
     * Only valid when {@code mWindowState} is in {@link #FULL_SCREEN}.
     */
    public static final int CENTER = 3;


    private int mWindowState;
    private int mGravity;

    public UIWindowState() {
        mWindowState = FULL_SCREEN;
        mGravity = CENTER;
    }

    public UIWindowState(int windowState, int gravity) {
        mWindowState = windowState;
        mGravity = gravity;
    }

    public int getWindowState() {
        return mWindowState;
    }

    public void setWindowState(int windowState) {
        mWindowState = windowState;
    }

    public int getGravity() {
        return mGravity;
    }

    public void setGravity(int gravity) {
        mGravity = gravity;
    }
}
