package com.example.demo_framelayout_layout_weight;


import android.view.View;

/**
 * Describe an view's position related to its parent and the width and height size of this view.
 * the width and height size will be represented by {@link WindowState#QUARTER_SCREEN},
 * {@link WindowState#FULL_SCREEN} and {@link WindowState#HALF_SCREEN}
 */
public class WindowState {
    /**
     * Quarter screen state
     */
    public static final int QUARTER_SCREEN = 1;
    /**
     * Full screen state
     */
    public static final int FULL_SCREEN = 2;
    /**
     * Half screen state
     */
    public static final int HALF_SCREEN = 3;

    /**
     * the window's position related to its parent. Only valid when the state is
     * {@link WindowState#HALF_SCREEN} or {@link WindowState#QUARTER_SCREEN}
     */
    public static final int START = 1;
    /**
     * the window's position related to its parent. Only valid when the state is
     * {@link WindowState#HALF_SCREEN} or {@link WindowState#QUARTER_SCREEN}
     */
    public static final int END = 2;

    /**
     * Only valid when {@code mWindowState} is in {@link #FULL_SCREEN}.
     */
    public static final int CENTER = 3;

    /**
     * the view which the {@link WindowState} will be bound to
     */
    private View mView;
    /**
     * the view which the {@link WindowState} will be bound to
     */
    private int mState;
    /**
     * the current position of the bound view. {@link WindowState#START},
     * {@link WindowState#END} or {@link WindowState#CENTER}
     */
    private final int mGravity;

    public WindowState(int state, int gravity) {
        mState = state;
        mGravity = gravity;
    }

    public int getState() {
        return mState;
    }

    public void setState(int state) {
        mState = state;
    }

    /**
     *
     * @return the current position of the bound view. {@link WindowState#START},
     * {@link WindowState#END} or {@link WindowState#CENTER}
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * @return the view which the {@link WindowState} is bound to
     */
    public View getView() {
        return mView;
    }

    /**
     * bind this {@link WindowState} object to an view.
     * @param view the view which the {@link WindowState} is bound to.
     */
    public void setView(View view) {
        mView = view;
    }
}
