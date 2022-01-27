package com.example.demo_framelayout_layout_weight;

import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.ViewGroup;

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
public class ScreenSplitHelper {
    private static final int NO_ID = -1;
    /**
     * the representation of a view.
     */
    private final HashMap<String, WindowState> mWindowStates;
    private final HashMap<String, OnChangeToFull> mFullStateCallBacks;
    private final HashMap<String, OnChangeToHalf> mHalfStateCallBacks;
    private final HashMap<String, OnChangeToQuarter> mQuarterStateCallBacks;
    private final HashMap<String, OnChangeComplete> mCompleteCallBacks;
    private final Transition mTransition1;
    private final Transition mTransition2;
    private final Transition mTransition3;
    private int mSwapId;

    public ScreenSplitHelper() {
        mWindowStates = new HashMap<>(4, 1);
        mFullStateCallBacks = new HashMap<>(4, 1);
        mQuarterStateCallBacks = new HashMap<>(4, 1);
        mHalfStateCallBacks = new HashMap<>(4, 1);
        mCompleteCallBacks = new HashMap<>(4, 1);
        mSwapId = NO_ID;

        mTransition1 = new ChangeBounds();
        mTransition2 = new ChangeBounds();
        mTransition3 = new ChangeBounds();
        mTransition1.setDuration(300);
        mTransition2.setDuration(300);
        mTransition3.setDuration(300);
    }


    /**
     * add a callback for a view represented by {@link WindowState}
     *
     * @param view the representation of a view
     */
    public void addView(@NonNull WindowState view) {
        if (view.getView() != null) {
            int gravity = view.getGravity();
            if (gravity == WindowState.CENTER) {
                mSwapId = view.getView().getId();
            }

            String key = String.valueOf(view.getView().getId());
            int oldId = NO_ID;
            for (WindowState state : mWindowStates.values()) {
                if (state.getGravity() == gravity) {
                    oldId = state.getView().getId();
                    break;
                }
            }

            if (oldId != NO_ID) {
                mFullStateCallBacks.remove(String.valueOf(oldId));
                mHalfStateCallBacks.remove(String.valueOf(oldId));
                mQuarterStateCallBacks.remove(String.valueOf(oldId));
                mCompleteCallBacks.remove(String.valueOf(oldId));
                mWindowStates.remove(String.valueOf(oldId));
            }

            mWindowStates.put(key, view);
        }
    }

    /**
     * remove references of every view from {@link ScreenSplitHelper}.
     *
     * @param id the view to be remove from {@link ScreenSplitHelper}.
     */
    public void removeView(@IdRes int id) {
        WindowState windowState = mWindowStates.get(String.valueOf(id));
        if (windowState != null) {
            windowState.setView(null);
        }
        String key = String.valueOf(id);
        mFullStateCallBacks.remove(key);
        mQuarterStateCallBacks.remove(key);
        mHalfStateCallBacks.remove(key);
        mCompleteCallBacks.remove(key);
        mWindowStates.remove(key);
    }

    /**
     * remove references of every view from {@link ScreenSplitHelper}.
     */
    public void clearView() {
        for (WindowState state : mWindowStates.values()) {
            state.setView(null);
        }
        mFullStateCallBacks.clear();
        mQuarterStateCallBacks.clear();
        mHalfStateCallBacks.clear();
        mCompleteCallBacks.clear();
        mWindowStates.clear();
    }

    /**
     * the callback is used by the bound view after the state of the bound view is changed.
     * the callback is used by the bound view if this view can give an valuable response to an
     * touch event. It's an state that an full screen, half screen or quarter screen event will
     * happen.
     */
    public void setOnChangeToFull(@IdRes int id, @Nullable OnChangeToFull onChangeToFull) {
        mFullStateCallBacks.put(String.valueOf(id), onChangeToFull);
    }

    /**
     * called after the state of the bound view is set to {@link WindowState#HALF_SCREEN}
     */
    public void setOnChangeToHalf(@IdRes int id, @Nullable OnChangeToHalf onChangeToHalf) {
        mHalfStateCallBacks.put(String.valueOf(id), onChangeToHalf);
    }

    /**
     * the callback is used by the bound view after the state of the bound view is changed
     */
    public void setOnChangeToQuarter(@IdRes int id, @Nullable OnChangeToQuarter onChangeToQuarter) {
        mQuarterStateCallBacks.put(String.valueOf(id), onChangeToQuarter);
    }

    /**
     * the callback is used by the bound view if this view can give an valuable response to an
     * touch event. It's an state that an full screen, half screen or quarter screen event will
     * happen.
     */
    public void setOnChangeComplete(@IdRes int id, @Nullable OnChangeComplete onChangeComplete) {
        mCompleteCallBacks.put(String.valueOf(id), onChangeComplete);
    }

    /**
     * zoom in or out
     *
     * @param id the touched view.
     */
    public void zoomInOut(@IdRes int id) {
        String key = String.valueOf(id);
        WindowState windowState = mWindowStates.get(key);
        if (windowState == null || windowState.getState() == WindowState.FULL_SCREEN) {
            return;
        }

        if (windowState.getState() == WindowState.HALF_SCREEN) {
            halfToFull(key, windowState);
            halfToQuarter();
            // 1.find the half screen, convert it to quarter, set align to start or end, and
            //  bring to front
            // 2.set align to end or start for the other quarter screen, and bring it to front
            // 3.bring other views to front
        } else {
            // 1.find the full screen
            // 2.convert the selected quarter screen to half, convert the previous full screen
            //  to half, set these two half screens' alignment, and bring to front
            // 3.bring other views to front
            quarterToHalf(windowState);
        }

        OnChangeComplete callBack = mCompleteCallBacks.get(key);
        if (callBack != null) {
            callBack.onComplete();
        }
    }

    /**
     * @param key   current view id
     * @param state current clicked view
     */
    private void halfToFull(@NonNull String key, @NonNull WindowState state) {

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        layoutParams.horizontalWeight = 1;
        layoutParams.verticalWeight = 1;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        TransitionManager.beginDelayedTransition((ViewGroup) state.getView().getParent(),
                mTransition1);
        state.getView().setLayoutParams(layoutParams);
        state.setState(WindowState.FULL_SCREEN);
        mWindowStates.put(key, state);
        OnChangeToFull changeToFull = mFullStateCallBacks.get(key);
        if (changeToFull != null) {
            changeToFull.onFull();
        }
    }

    /**
     * find the half screen, then convert it to quarter screen and bring it to front.
     * find the quarter screen, then adjust its alignment and bring it to front.
     */
    private void halfToQuarter() {
        WindowState fullScreen = null;
        WindowState halfScreen = null;
        WindowState quarterScreen = null;
        // find half screen
        // find quarter screen
        for (WindowState win : mWindowStates.values()) {
            int state = win.getState();
            if (state == WindowState.HALF_SCREEN) {
                halfScreen = win;
            } else if (state == WindowState.QUARTER_SCREEN) {
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
        if (gravity == WindowState.START) {//固定位置，左对齐
            // convert it to quarter start screen.
            lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                    mTransition2);
            halfScreen.getView().setLayoutParams(lpStart);
            halfScreen.getView().bringToFront();
            halfScreen.setState(WindowState.QUARTER_SCREEN);
            mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);

            if (quarterScreen != null) {
                TransitionManager.beginDelayedTransition((ViewGroup) quarterScreen.getView().getParent(),
                        mTransition3);
                // it's already been in quarter state, align it to end.
                if (quarterScreen.getView().getId() == mSwapId) {//固定位置，右对齐
                    lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                    lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    quarterScreen.getView().setLayoutParams(lpEnd);
                }

                quarterScreen.getView().bringToFront();
            }
        } else if (gravity == WindowState.END) {//固定位置，右对齐
            // convert it to quarter end screen.
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                    mTransition2);
            halfScreen.getView().setLayoutParams(lpEnd);
            halfScreen.getView().bringToFront();
            halfScreen.setState(WindowState.QUARTER_SCREEN);
            mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);

            if (quarterScreen != null) {// quarter start screen.
                TransitionManager.beginDelayedTransition((ViewGroup) quarterScreen.getView().getParent(),
                        mTransition3);
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
                if (qG == WindowState.END) {
                    lpEnd.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                } else {
                    lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                }
                lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                        mTransition2);
                halfScreen.getView().setLayoutParams(lpEnd);
                halfScreen.getView().bringToFront();
                halfScreen.setState(WindowState.QUARTER_SCREEN);
                mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);
                TransitionManager.beginDelayedTransition((ViewGroup) quarterScreen.getView().getParent(),
                        mTransition3);
                quarterScreen.getView().bringToFront();
            }
        }

        OnChangeToQuarter changeToQuarter = mQuarterStateCallBacks.get(String.valueOf(halfScreen
                .getView().getId()));
        if (changeToQuarter != null) {
            changeToQuarter.onQuarter();
        }
    }

    /**
     * change the comparing list.
     *
     * @param view the clicked quarter view
     */
    private void quarterToHalf(@NonNull WindowState view) {
        WindowState fullScreen = null;
        WindowState quarter = null;

        for (WindowState win : mWindowStates.values()) {
            int state = win.getState();
            if (state == WindowState.FULL_SCREEN) {
                fullScreen = win;
            } else {
                if (win != view) {
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
        int gravity = view.getGravity();
        if (gravity == WindowState.START) {
            lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            lpStart.endToStart = fullScreen.getView().getId();
            TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                    mTransition2);
            view.getView().setLayoutParams(lpStart);
            view.setState(WindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(view.getView().getId()), view);

            lpEnd.startToEnd = view.getView().getId();
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView().getParent(),
                    mTransition3);
            fullScreen.getView().setLayoutParams(lpEnd);
            fullScreen.setState(WindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
        } else if (gravity == WindowState.END) {
            lpEnd.startToEnd = fullScreen.getView().getId();
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                    mTransition2);
            view.getView().setLayoutParams(lpEnd);
            view.setState(WindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(view.getView().getId()), view);

            lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            lpStart.endToStart = view.getView().getId();
            TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView().getParent(),
                    mTransition3);
            fullScreen.getView().setLayoutParams(lpStart);
            fullScreen.setState(WindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
        } else {
            int fG = fullScreen.getGravity();
            if (fG == WindowState.END) {
                lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                lpStart.endToStart = fullScreen.getView().getId();
                TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                        mTransition2);
                view.getView().setLayoutParams(lpStart);
                view.setState(WindowState.HALF_SCREEN);
                mWindowStates.put(String.valueOf(view.getView().getId()), view);

                lpEnd.startToEnd = view.getView().getId();
                lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView().getParent(),
                        mTransition3);
                fullScreen.getView().setLayoutParams(lpEnd);
            } else {
                lpEnd.startToEnd = fullScreen.getView().getId();
                lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                        mTransition2);
                view.getView().setLayoutParams(lpEnd);
                view.setState(WindowState.HALF_SCREEN);
                mWindowStates.put(String.valueOf(view.getView().getId()), view);

                lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                lpStart.endToStart = view.getView().getId();
                TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView().getParent(),
                        mTransition3);
                fullScreen.getView().setLayoutParams(lpStart);
            }
            fullScreen.setState(WindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
        }
        if (quarter != null) {
            quarter.getView().bringToFront();
        }

        OnChangeToHalf qState = mHalfStateCallBacks.get(String.valueOf(view.getView()
                .getId()));
        if (qState != null) {
            qState.onHalf();
        }
        OnChangeToHalf fState = mHalfStateCallBacks.get(String.valueOf(fullScreen.getView()
                .getId()));
        if (fState != null) {
            fState.onHalf();
        }
    }

    /**
     * the callback is used by the bound view after the state of the bound view is changed.
     * the callback is used by the bound view if this view can give an valuable response to an
     * touch event. It's an state that an full screen, half screen or quarter screen event will
     * happen.
     */
    public interface OnChangeToFull {
        /**
         * called after the state of the bound view is set to {@link WindowState#FULL_SCREEN}
         */
        void onFull();
    }

    /**
     * the callback is used by the bound view after the state of the bound view is changed
     */
    public interface OnChangeToQuarter {
        /**
         * called after the state of the bound view is set to {@link WindowState#QUARTER_SCREEN}
         */
        void onQuarter();
    }

    /**
     * the callback is used by the bound view after the state of the bound view is changed
     */
    public interface OnChangeToHalf {
        /**
         * called after the state of the bound view is set to {@link WindowState#HALF_SCREEN}
         */
        void onHalf();
    }

    /**
     * the callback is used by the bound view if this view can give an valuable response to an
     * touch event. It's an state that an full screen, half screen or quarter screen event will
     * happen.
     */
    public interface OnChangeComplete {
        /**
         * called after setting an state for every view is complete.
         */
        void onComplete();
    }
}
