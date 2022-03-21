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
    /**
     * 用于为屏幕中间view的过度动画
     */
    private final Transition mTransitionFull;
    /**
     * 用于为屏幕左边view的过度动画
     */
    private final Transition mTransitionLayoutStart;
    /**
     * 用于为屏幕右边边view的过度动画
     */
    private final Transition mTransitionLayoutEnd;
    private OnChangedComplete mOnChangedComplete;
    private int mSwapId;

    public ScreenSplitHelper() {
        mWindowStates = new HashMap<>(4, 1);
        mFullStateCallBacks = new HashMap<>(4, 1);
        mQuarterStateCallBacks = new HashMap<>(4, 1);
        mHalfStateCallBacks = new HashMap<>(4, 1);
        mSwapId = NO_ID;

        final int TIME = 200;
        mTransitionFull = new ChangeBounds();
        mTransitionLayoutStart = new ChangeBounds();
        mTransitionLayoutEnd = new ChangeBounds();
        mTransitionFull.setDuration(TIME);
        mTransitionLayoutStart.setDuration(TIME);
        mTransitionLayoutEnd.setDuration(TIME);
    }


    /**
     * 添加一个{@link WindowState}对象
     * <p>
     * add a view represented by {@link WindowState}
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
                mWindowStates.remove(String.valueOf(oldId));
            }

            mWindowStates.put(key, view);
        }
    }

    /**
     * 删除指定id的{@link WindowState}对象
     *
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
        mWindowStates.remove(key);
    }

    /**
     * 清除所有{@link WindowState}对象
     *
     * remove references of every view from {@link ScreenSplitHelper}.
     */
    public void clearView() {
        for (WindowState state : mWindowStates.values()) {
            state.setView(null);
        }
        mFullStateCallBacks.clear();
        mQuarterStateCallBacks.clear();
        mHalfStateCallBacks.clear();
        mOnChangedComplete = null;
        mWindowStates.clear();
    }

    /**
     * 为指定id的view设置转变成全屏布局后的回调{@link OnChangeToFull}
     * the callback is used by the bound view after the state of the bound view is changed.
     * the callback is used by the bound view if this view can give an valuable response to an
     * touch event. It's an state that an full screen, half screen or quarter screen event will
     * happen.
     */
    public void setOnChangeToFull(@IdRes int id, @Nullable OnChangeToFull onChangeToFull) {
        mFullStateCallBacks.put(String.valueOf(id), onChangeToFull);
    }

    /**
     * 为指定id的view设置转变成半屏布局后的回调{@link OnChangeToHalf}
     * called after the state of the bound view is set to {@link WindowState#HALF_SCREEN}
     */
    public void setOnChangeToHalf(@IdRes int id, @Nullable OnChangeToHalf onChangeToHalf) {
        mHalfStateCallBacks.put(String.valueOf(id), onChangeToHalf);
    }

    /**
     * 为指定id的view设置转变成四分屏布局后的回调{@link OnChangeToQuarter}
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
    public void setOnChangeComplete(@Nullable OnChangedComplete onChangedComplete) {
        mOnChangedComplete = onChangedComplete;
    }

    /**
     * 改变窗口大小及布局位置，改变过程中会产生150ms的过度动画。
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

        if (mOnChangedComplete != null) {
            mOnChangedComplete.onComplete();
        }
    }

    /**
     * 半屏转全屏
     *
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
                mTransitionFull);
        state.getView().setLayoutParams(layoutParams);
        state.setState(WindowState.FULL_SCREEN);
        mWindowStates.put(key, state);
        OnChangeToFull changeToFull = mFullStateCallBacks.get(key);
        if (changeToFull != null) {
            changeToFull.onFull();
        }
    }

    /**
     * 半屏转四分屏幕
     *
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
            halfToQuarterFromStart(halfScreen, lpStart, lpEnd, quarterScreen);
        } else if (gravity == WindowState.END) {//固定位置，右对齐
            halfToQuarterFromEnd(halfScreen, lpStart, lpEnd, quarterScreen);
        } else {// this half screen is swappable.当前半屏view是gravity等于center的view
            halfToQuarterFromCenter(halfScreen, lpEnd, quarterScreen, fullScreen);
        }

        OnChangeToQuarter changeToQuarter = mQuarterStateCallBacks.get(String.valueOf(halfScreen
                .getView().getId()));
        if (changeToQuarter != null) {
            changeToQuarter.onQuarter();
        }
    }

    /**
     * 从布局参数的start位置开始将该位置的view与其他view进行分屏操作
     *
     * @param halfScreen    the clicked quarter view
     * @param lpStart       start位置的view布局参数
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromStart(@NonNull WindowState halfScreen,
                                        @NonNull ConstraintLayout.LayoutParams lpStart,
                                        @NonNull ConstraintLayout.LayoutParams lpEnd,
                                        @Nullable WindowState quarterScreen) {
        // convert it to quarter start screen.
        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                mTransitionLayoutStart);
        halfScreen.getView().setLayoutParams(lpStart);
        halfScreen.getView().bringToFront();
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);

        if (quarterScreen != null) {
            TransitionManager.beginDelayedTransition((ViewGroup) quarterScreen.getView().getParent(),
                    mTransitionLayoutEnd);
            // it's already been in quarter state, align it to end.
            if (quarterScreen.getView().getId() == mSwapId) {//固定位置，右对齐
                lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                quarterScreen.getView().setLayoutParams(lpEnd);
            }

            quarterScreen.getView().bringToFront();
        }
    }

    /**
     * 从布局参数的end位置开始将该位置的view与其他view进行分屏操作
     *
     * @param halfScreen    the clicked quarter view
     * @param lpStart       start位置的view布局参数
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromEnd(@NonNull WindowState halfScreen,
                                      @NonNull ConstraintLayout.LayoutParams lpStart,
                                      @NonNull ConstraintLayout.LayoutParams lpEnd,
                                      @Nullable WindowState quarterScreen) {
        // convert it to quarter end screen.
        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                mTransitionLayoutEnd);
        halfScreen.getView().setLayoutParams(lpEnd);
        halfScreen.getView().bringToFront();
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);

        if (quarterScreen != null) {// quarter start screen.
            TransitionManager.beginDelayedTransition((ViewGroup) quarterScreen.getView().getParent(),
                    mTransitionLayoutStart);
            if (quarterScreen.getView().getId() == mSwapId) {
                lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                //两个右下角半屏，地图位于右下角，其中一个半屏变全屏，另外一个半屏移动到右下角，地图被覆盖
                lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                quarterScreen.getView().setLayoutParams(lpStart);
            }
            quarterScreen.getView().bringToFront();
        }
    }

    /**
     * 从布局参数的center位置开始将该位置的view与其他view进行分屏操作
     *
     * @param halfScreen    the clicked quarter view
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromCenter(@NonNull WindowState halfScreen,
                                         @NonNull ConstraintLayout.LayoutParams lpEnd,
                                         @Nullable WindowState quarterScreen,
                                         @NonNull WindowState fullScreen) {
        int qG = quarterScreen != null ? quarterScreen.getGravity() : fullScreen.getGravity();
        if (qG == WindowState.END) {
            lpEnd.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        } else {
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        }

        lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                mTransitionLayoutStart);
        halfScreen.getView().setLayoutParams(lpEnd);
        halfScreen.getView().bringToFront();
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);
        if (quarterScreen != null) {
            TransitionManager.beginDelayedTransition((ViewGroup) quarterScreen.getView().getParent(),
                    mTransitionLayoutEnd);
            quarterScreen.getView().bringToFront();
        }
    }

    /**
     * 四分屏转半屏
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
            quarterToHalfFromStart(view, lpStart, lpEnd, fullScreen);
        } else if (gravity == WindowState.END) {
            quarterToHalfFromEnd(view, lpStart, lpEnd, fullScreen);
        } else {
            quarterToHalfFromCenter(view, lpStart, lpEnd, fullScreen);
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
     * 从布局参数的start位置开始将该位置的view与其他view进行分屏操作
     *
     * @param view       the clicked quarter view
     * @param lpStart    start位置的view布局参数
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void quarterToHalfFromStart(@NonNull WindowState view,
                                        @NonNull ConstraintLayout.LayoutParams lpStart,
                                        @NonNull ConstraintLayout.LayoutParams lpEnd,
                                        @NonNull WindowState fullScreen) {
        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        lpStart.endToStart = fullScreen.getView().getId();
        TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                mTransitionLayoutStart);
        view.getView().setLayoutParams(lpStart);
        view.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(view.getView().getId()), view);

        lpEnd.startToEnd = view.getView().getId();
        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView().getParent(),
                mTransitionLayoutEnd);
        fullScreen.getView().setLayoutParams(lpEnd);
        fullScreen.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
    }

    /**
     * 从布局参数的end位置开始将该位置的view与其他view进行分屏操作
     *
     * @param view       the clicked quarter view
     * @param lpStart    start位置的view布局参数
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void quarterToHalfFromEnd(@NonNull WindowState view,
                                      @NonNull ConstraintLayout.LayoutParams lpStart,
                                      @NonNull ConstraintLayout.LayoutParams lpEnd,
                                      @NonNull WindowState fullScreen) {
        lpEnd.startToEnd = fullScreen.getView().getId();
        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                mTransitionLayoutEnd);
        view.getView().setLayoutParams(lpEnd);
        view.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(view.getView().getId()), view);

        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        lpStart.endToStart = view.getView().getId();
        TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView().getParent(),
                mTransitionLayoutStart);
        fullScreen.getView().setLayoutParams(lpStart);
        fullScreen.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
    }

    /**
     * 从布局参数的center位置开始将该位置的view与其他view进行分屏操作
     *
     * @param view       the clicked quarter view
     * @param lpStart    start位置的view布局参数
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void quarterToHalfFromCenter(@NonNull WindowState view,
                                         @NonNull ConstraintLayout.LayoutParams lpStart,
                                         @NonNull ConstraintLayout.LayoutParams lpEnd,
                                         @NonNull WindowState fullScreen) {
        int fG = fullScreen.getGravity();
        if (fG == WindowState.END) {
            lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            lpStart.endToStart = fullScreen.getView().getId();
            TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                    mTransitionLayoutStart);
            view.getView().setLayoutParams(lpStart);
            view.setState(WindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(view.getView().getId()), view);

            lpEnd.startToEnd = view.getView().getId();
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView().getParent(),
                    mTransitionLayoutEnd);
            fullScreen.getView().setLayoutParams(lpEnd);
        } else {
            lpEnd.startToEnd = fullScreen.getView().getId();
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                    mTransitionLayoutEnd);
            view.getView().setLayoutParams(lpEnd);
            view.setState(WindowState.HALF_SCREEN);
            mWindowStates.put(String.valueOf(view.getView().getId()), view);

            lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            lpStart.endToStart = view.getView().getId();
            TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView().getParent(),
                    mTransitionLayoutStart);
            fullScreen.getView().setLayoutParams(lpStart);
        }
        fullScreen.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
    }

    /**
     * 当与该回调对应的view布局参数变成全屏后，该回调将被调用。
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
     * 当与该回调对应的view布局参数变成四分屏后，该回调将被调用。
     * the callback is used by the bound view after the state of the bound view is changed
     */
    public interface OnChangeToQuarter {
        /**
         * called after the state of the bound view is set to {@link WindowState#QUARTER_SCREEN}
         */
        void onQuarter();
    }

    /**
     * 当与该回调对应的view布局参数变成半屏后，该回调将被调用。
     * the callback is used by the bound view after the state of the bound view is changed
     */
    public interface OnChangeToHalf {
        /**
         * called after the state of the bound view is set to {@link WindowState#HALF_SCREEN}
         */
        void onHalf();
    }

    /**
     * 当设置完所有view的布局参数之后将调用该接口。该回调发生在{@link OnChangeToFull},
     * {@link OnChangeToQuarter}和{@link OnChangeToHalf}之后。
     * <p>
     * the callback is used by all view if this view can give an valuable response to an
     * touch event. It's an state that an full screen, half screen or quarter screen event will
     * happen.
     */
    public interface OnChangedComplete {
        /**
         * called after setting an state for every view's size has finished changed.
         */
        void onComplete();
    }
}
