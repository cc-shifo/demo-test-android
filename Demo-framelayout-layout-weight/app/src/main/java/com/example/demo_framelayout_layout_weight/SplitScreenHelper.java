
package com.example.demo_framelayout_layout_weight;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.HashMap;

/**
 * 初始状态的左边小窗口决定了以后该窗口都是在左边。
 * 初始状态的右边小窗口决定了以后该窗口都是在右边。
 * 初始状态中央区域为可交换区。
 * 初始状态右边小窗口允许存在列表，当存在列表时，最后添加的那个为当前窗口。
 */
public class SplitScreenHelper {
    private final static int NO_ID = -1;
    private final HashMap<String, UIWindowState> mWindowStates;
    /**
     * action from converting an quarter to half screen will change the comparing list.
     */
    private final HashMap<String, OtherCallBack> mCallBackHashMap;
    private int mSwapId;
    private int mStartId;
    private int mEndId;
    private UIWindowState mCurrentEndWindow;

    public SplitScreenHelper() {
        mWindowStates = new HashMap<>(4, 1);
        mCallBackHashMap = new HashMap<>(4, 1);
        mSwapId = NO_ID;
        mStartId = NO_ID;
        mEndId = NO_ID;
    }

    public void addView(@NonNull UIWindowState view, @Nullable OtherCallBack callBack) {
        if (view.getView() != null) {
            String key = String.valueOf(view.getView().getId());
            int gravity = view.getGravity();
            if (gravity == UIWindowState.CENTER) {
                if (mSwapId != NO_ID) {
                    String oldId = String.valueOf(mSwapId);
                    mWindowStates.remove(oldId);
                }
                mSwapId = view.getView().getId();
            } else if (gravity == UIWindowState.START) {
                if (mStartId != NO_ID) {
                    String oldId = String.valueOf(mStartId);
                    mWindowStates.remove(oldId);
                }
                mStartId = view.getView().getId();
            } else {
                if (mEndId != NO_ID) {
                    String oldId = String.valueOf(mEndId);
                    mWindowStates.remove(oldId);
                }
                mEndId = view.getView().getId();
            }

            if (callBack != null) {
                mCallBackHashMap.put(key, callBack);
            }
            mWindowStates.put(key, view);
        }
    }

    public void removeView(@IdRes int id) {
        UIWindowState windowState = mWindowStates.get(String.valueOf(id));
        if (windowState != null) {
            windowState.setView(null);
        }
        String key = String.valueOf(id);
        mCallBackHashMap.remove(key);
        mWindowStates.remove(key);
    }

    public void clearView() {
        for (UIWindowState state : mWindowStates.values()) {
            state.setView(null);
        }
        mCallBackHashMap.clear();
        mWindowStates.clear();
    }

    /**
     * @param id the touched view.
     */
    public void zoomInOut(@IdRes int id) {
        String key = String.valueOf(id);
        UIWindowState windowState = mWindowStates.get(key);
        if (windowState == null || windowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            return;
        }

        if (windowState.getWindowState() == UIWindowState.HALF_SCREEN) {
            fillFull(key, windowState);
            fillQuarter();
            // 1.find the half screen, convert it to quarter, set align to start or end, and
            //  bring to front
            // 2.set align to end or start for the other quarter screen, and bring it to front
            // 3.bring other views to front
        } else {
            // 1.find the full screen
            // 2.convert the selected quarter screen to half, convert the previous full screen
            //  to half, set these two half screens' alignment, and bring to front
            // 3.bring other views to front
            fillHalf(windowState);
        }

        OtherCallBack callBack = mCallBackHashMap.get(key);
        if (callBack != null) {
            callBack.bringToFront();
        }
    }

    /**
     * @param key   current view id
     * @param state current clicked view
     */
    private void fillFull(@NonNull String key, @NonNull UIWindowState state) {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        layoutParams.horizontalWeight = 1;
        layoutParams.verticalWeight = 1;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        state.getView().setLayoutParams(layoutParams);
        state.setWindowState(UIWindowState.FULL_SCREEN);
        mWindowStates.put(key, state);
    }

    /**
     * find the half screen, then convert it to quarter screen and bring it to front.
     * find the quarter screen, then adjust its alignment and bring it to front.
     */
    private void fillQuarter() {
        UIWindowState fullScreen = null;
        UIWindowState halfScreen = null;
        UIWindowState quarterScreen = null;
        // find half screen
        // find quarter screen
        for (UIWindowState win : mWindowStates.values()) {
            int state = win.getWindowState();
            if (state == UIWindowState.HALF_SCREEN) {
                halfScreen = win;
            } else if (state == UIWindowState.QUARTER_SCREEN) {
                quarterScreen = win;
            } else {
                fullScreen = win;
            }
        }

        if (halfScreen == null || fullScreen == null) {
            return;
        }

        ConstraintLayout.LayoutParams lpStart = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        lpStart.matchConstraintPercentWidth = 0.25f;
        lpStart.matchConstraintDefaultWidth =
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        lpStart.matchConstraintPercentHeight = 0.25f;
        lpStart.matchConstraintDefaultHeight =
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        ConstraintLayout.LayoutParams lpEnd = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        lpEnd.matchConstraintPercentWidth = 0.25f;
        lpEnd.matchConstraintDefaultWidth = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        lpEnd.matchConstraintPercentHeight = 0.25f;
        lpEnd.matchConstraintDefaultHeight = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        int gravity = halfScreen.getGravity();
        if (gravity == UIWindowState.START) {//固定位置，左对齐
            // convert it to quarter start screen.
            lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            halfScreen.getView().setLayoutParams(lpStart);
            halfScreen.getView().bringToFront();
            halfScreen.setWindowState(UIWindowState.QUARTER_SCREEN);
            mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);

            if (quarterScreen != null) {
                // it's already been in quarter state, align it to end.
                if (quarterScreen.getView().getId() == mSwapId) {//固定位置，右对齐
                    lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                    lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    quarterScreen.getView().setLayoutParams(lpEnd);
                }
                quarterScreen.getView().bringToFront();
            }
        } else if (gravity == UIWindowState.END) {//固定位置，右对齐
            // convert it to quarter end screen.
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            halfScreen.getView().setLayoutParams(lpEnd);
            halfScreen.getView().bringToFront();
            halfScreen.setWindowState(UIWindowState.QUARTER_SCREEN);
            mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);

            if (quarterScreen != null) {// quarter start screen.
                if (quarterScreen.getView().getId() == mSwapId) {
                    lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    //两个右下角半屏，地图位于右下角，其中一个半屏变全屏，另外一个半屏移动到右下角，地图被覆盖
                    lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    quarterScreen.getView().setLayoutParams(lpStart);
                }
                quarterScreen.getView().bringToFront();
            }
        } else {// this half screen is swappable.
            if (quarterScreen != null) {
                int qG = quarterScreen.getGravity();
                if (qG == UIWindowState.END) {
                    lpEnd.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                } else {
                    lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                }
                lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                halfScreen.getView().setLayoutParams(lpEnd);
                halfScreen.getView().bringToFront();
                halfScreen.setWindowState(UIWindowState.QUARTER_SCREEN);
                mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);
                quarterScreen.getView().bringToFront();
            }
        }
    }

    /**
     * change the comparing list.
     *
     * @param windowState the clicked quarter view
     */
    private void fillHalf(@NonNull UIWindowState windowState) {
        UIWindowState fullScreen = null;
        UIWindowState quarter = null;

        for (UIWindowState win : mWindowStates.values()) {
            int state = win.getWindowState();
            if (state == UIWindowState.FULL_SCREEN) {
                fullScreen = win;
            } else {
                if (win != windowState) {
                    quarter = win;
                }
            }
        }

        if (fullScreen == null) {
            return; // 在半屏状态下点击小屏的场景
        }

        ConstraintLayout.LayoutParams lpStart = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        lpStart.horizontalWeight = 1;
        lpStart.verticalWeight = 1;
        lpStart.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        ConstraintLayout.LayoutParams lpEnd = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        lpEnd.horizontalWeight = 1;
        lpEnd.verticalWeight = 1;
        lpEnd.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        int gravity = windowState.getGravity();
        if (gravity == UIWindowState.START) {
            lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            lpStart.endToStart = fullScreen.getView().getId();
            windowState.getView().setLayoutParams(lpStart);
            windowState.setWindowState(UIWindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(windowState.getView().getId()), windowState);

            lpEnd.startToEnd = windowState.getView().getId();
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            fullScreen.getView().setLayoutParams(lpEnd);
            fullScreen.setWindowState(UIWindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
        } else if (gravity == UIWindowState.END) {
            lpEnd.startToEnd = fullScreen.getView().getId();
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            windowState.getView().setLayoutParams(lpEnd);
            windowState.setWindowState(UIWindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(windowState.getView().getId()), windowState);

            lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            lpStart.endToStart = windowState.getView().getId();
            fullScreen.getView().setLayoutParams(lpStart);
            fullScreen.setWindowState(UIWindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
        } else {
            int fG = fullScreen.getGravity();
            if (fG == UIWindowState.END) {
                lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                lpStart.endToStart = fullScreen.getView().getId();
                windowState.getView().setLayoutParams(lpStart);
                windowState.setWindowState(UIWindowState.HALF_SCREEN);
                mWindowStates.put(String.valueOf(windowState.getView().getId()), windowState);

                lpEnd.startToEnd = windowState.getView().getId();
                lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                fullScreen.getView().setLayoutParams(lpEnd);
            } else {
                lpEnd.startToEnd = fullScreen.getView().getId();
                lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                windowState.getView().setLayoutParams(lpEnd);
                windowState.setWindowState(UIWindowState.HALF_SCREEN);
                mWindowStates.put(String.valueOf(windowState.getView().getId()), windowState);

                lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                lpStart.endToStart = windowState.getView().getId();
                fullScreen.getView().setLayoutParams(lpStart);
            }
            fullScreen.setWindowState(UIWindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
        }
        if (quarter != null) {
            quarter.getView().bringToFront();
        }
    }

    private void fillOther() {

    }

    /**
     *
     */
    public interface OtherCallBack {
        void bringToFront();
    }
}
