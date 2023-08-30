/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20230107 	         LiuJian                  Create
 */

package com.example.demoscreensplithelper;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

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
    private static final String TAG = "ScreenSplitHelper";
    private static final int NO_ID = -1;
    /**
     * the representation of a view.
     */
    private final HashMap<String, WindowState> mWindowStates;
    private final HashMap<String, OnChangeToFull> mFullStateCallBacks;
    private final HashMap<String, OnChangeToHalf> mHalfStateCallBacks;
    private final HashMap<String, OnChangeToQuarter> mQuarterStateCallBacks;
    private OnWindowSizeChanged mOnWindowSizeChanged;
    /**
     * 由于窗口大小的变化添加了动画，动画播放需要时间，所以用此变量表示变化是否完成。
     */
    private boolean mIsSizeChanging;
    private int mSwapId;

    public ScreenSplitHelper() {
        mWindowStates = new HashMap<>(4, 1);
        mFullStateCallBacks = new HashMap<>(4, 1);
        mQuarterStateCallBacks = new HashMap<>(4, 1);
        mHalfStateCallBacks = new HashMap<>(4, 1);
        mSwapId = NO_ID;
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
     * <p>
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
     * <p>
     * remove references of every view from {@link ScreenSplitHelper}.
     */
    public void clearView() {
        for (WindowState state : mWindowStates.values()) {
            state.setView(null);
        }
        mFullStateCallBacks.clear();
        mQuarterStateCallBacks.clear();
        mHalfStateCallBacks.clear();
        mOnWindowSizeChanged = null;
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
    public void setOnWindowSizeChanged(@Nullable OnWindowSizeChanged onWindowSizeChanged) {
        mOnWindowSizeChanged = onWindowSizeChanged;
    }

    /**
     * 改变窗口大小及布局位置，改变过程中会产生150ms的过度动画。
     *
     * @param id the touched view.
     */
    public void changeWindowSize(@IdRes int id) {
        if (mIsSizeChanging) {
            return;
        }

        String key = String.valueOf(id);
        WindowState windowState = mWindowStates.get(key);
        if (windowState == null || windowState.getState() == WindowState.FULL_SCREEN) {
            return;
        }

        mIsSizeChanging = true;
        if (windowState.getState() == WindowState.HALF_SCREEN) {
            halfToFullHalfToQuarter(key, windowState);
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
    }

    private int getViewIndex(View view) {
        ViewParent parent = view.getParent();
        if (parent instanceof ViewGroup) {
            return ((ViewGroup) parent).indexOfChild(view);
        }

        return -1;
    }

    /**
     * 半屏转全屏，全屏动画里对原半屏进行四分屏处理。
     *
     * @param key   current view id
     * @param state current clicked view
     */
    private void halfToFullHalfToQuarter(@NonNull String key, @NonNull WindowState state) {
        halfToFull(key, state);
    }

    /**
     * 半屏转全屏
     *
     * @param key   current view id
     * @param state current clicked view
     */
    private void halfToFull(@NonNull String key, @NonNull WindowState state) {
        TransitionHelper helper = new TransitionHelper(state);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "halfToFull onFinish: ");
            halfToFullOnChanged(state);
            halfToQuarter();
        });
        helper.beginDelayedTransition();
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
        state.setState(WindowState.FULL_SCREEN);
        mWindowStates.put(key, state);
    }

    /**
     * 被点击的半屏转全屏完成后会回调此接口。
     * @param view 被点击的半屏
     */
    private void halfToFullOnChanged(@NonNull WindowState view) {
        OnChangeToFull changeToFull = mFullStateCallBacks.get(String.valueOf(view.getView()
                .getId()));
        if (changeToFull != null) {
            changeToFull.onFull();
        }
    }

    /**
     * 半屏转四分屏幕。
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
            Log.d(TAG, "halfToQuarter: halfScreen == null || fullScreen == null");
            mIsSizeChanging = false;
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
    }

    /**
     * 被点击的半屏的另外一个半屏转换四分屏完成后调用。
     * @param view 被点击的半屏的另外一个半屏
     */
    private void halfToQuarterOnChanged(@NonNull WindowState view) {
        OnChangeToQuarter changeToQuarter = mQuarterStateCallBacks.get(String.valueOf(view
                .getView().getId()));
        if (changeToQuarter != null) {
            changeToQuarter.onQuarter();
        }
    }

    /**
     * 从布局参数的start位置开始将该位置的view与其他view进行分屏操作
     *
     * @param halfScreen    此时的半屏，将要转成四分屏
     * @param lpStart       start位置的view布局参数
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromStart(@NonNull WindowState halfScreen,
            @NonNull ConstraintLayout.LayoutParams lpStart,
            @NonNull final ConstraintLayout.LayoutParams lpEnd,
            @Nullable final WindowState quarterScreen) {
        TransitionHelper helper = new TransitionHelper(halfScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: halfToQuarterFromStart");
            halfToQuarterOnChanged(halfScreen);
            if (quarterScreen != null) {
                // it's already been in quarter state, align it to end.
                if (quarterScreen.getView().getId() == mSwapId) {//固定位置，右对齐
                    lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                    lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    quarterScreen.getView().setLayoutParams(lpEnd);
                }
                quarterScreen.getView().bringToFront();
            }

            mIsSizeChanging = false;
            if (mOnWindowSizeChanged != null) {
                mOnWindowSizeChanged.onSizeChanged();
            }
        });
        helper.beginDelayedTransition();
        // convert it to quarter start screen.
        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        halfScreen.getView().setLayoutParams(lpStart);
        halfScreen.getView().bringToFront();
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);
    }

    /**
     * 从布局参数的end位置开始将该位置的view与其他view进行分屏操作
     *
     * @param halfScreen    此时的半屏，将要转成四分屏
     * @param lpStart       start位置的view布局参数
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromEnd(@NonNull WindowState halfScreen,
            @NonNull ConstraintLayout.LayoutParams lpStart,
            @NonNull ConstraintLayout.LayoutParams lpEnd,
            @Nullable WindowState quarterScreen) {
        TransitionHelper helper = new TransitionHelper(halfScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: halfToQuarterFromEnd");
            halfToQuarterOnChanged(halfScreen);
            if (quarterScreen != null) {// quarter start screen.
                if (quarterScreen.getView().getId() == mSwapId) {
                    lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    //两个右下角半屏，地图位于右下角，其中一个半屏变全屏，另外一个半屏移动到右下角，地图被覆盖
                    lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    quarterScreen.getView().setLayoutParams(lpStart);
                }
                quarterScreen.getView().bringToFront();
            }
            mIsSizeChanging = false;
            if (mOnWindowSizeChanged != null) {
                mOnWindowSizeChanged.onSizeChanged();
            }
        });
        helper.beginDelayedTransition();

        // convert it to quarter end screen.
        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        halfScreen.getView().setLayoutParams(lpEnd);
        halfScreen.getView().bringToFront();
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);
    }

    /**
     * 从布局参数的center位置开始将该位置的view与其他view进行分屏操作
     *
     * @param halfScreen    此时的半屏，将要转成四分屏
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromCenter(@NonNull WindowState halfScreen,
            @NonNull ConstraintLayout.LayoutParams lpEnd,
            @Nullable WindowState quarterScreen,
            @NonNull WindowState fullScreen) {
        TransitionHelper helper = new TransitionHelper(halfScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: halfToQuarterFromCenter");
            halfToQuarterOnChanged(halfScreen);
            if (quarterScreen != null) {
                quarterScreen.getView().bringToFront();
            }

            mIsSizeChanging = false;
            if (mOnWindowSizeChanged != null) {
                mOnWindowSizeChanged.onSizeChanged();
            }
        });
        helper.beginDelayedTransition();

        int qG = quarterScreen != null ? quarterScreen.getGravity() : fullScreen.getGravity();
        if (qG == WindowState.END) {
            lpEnd.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        } else {
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        }

        lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        halfScreen.getView().setLayoutParams(lpEnd);
        halfScreen.getView().bringToFront();
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);
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
            Log.d(TAG, "quarterToHalf: fullScreen == null");
            mIsSizeChanging = false;
            return; // 在半屏状态下点击小屏的场景
        }

        ConstraintLayout.LayoutParams lpStart = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        lpStart.matchConstraintPercentWidth = 0.5f;
        lpStart.matchConstraintDefaultWidth =
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        lpStart.verticalWeight = 1;
        lpStart.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        ConstraintLayout.LayoutParams lpEnd = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        lpEnd.matchConstraintPercentWidth = 0.5f;
        lpEnd.matchConstraintDefaultWidth = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        lpEnd.verticalWeight = 1;
        lpEnd.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        int gravity = view.getGravity();
        if (gravity == WindowState.START) {
            quarterToHalfFromStart(view, lpStart, lpEnd, fullScreen, quarter);
        } else if (gravity == WindowState.END) {
            quarterToHalfFromEnd(view, lpStart, lpEnd, fullScreen, quarter);
        } else {
            quarterToHalfFromCenter(view, lpStart, lpEnd, fullScreen, quarter);
        }
    }

    /**
     * 被点击的四分屏转半屏完成后会回调此接口。
     * @param quarter 被点击的四分屏幕
     */
    private void quarterToHalfOnChanged(@NonNull WindowState quarter) {
        OnChangeToHalf qState = mHalfStateCallBacks.get(String.valueOf(quarter.getView()
                .getId()));
        if (qState != null) {
            qState.onHalf();
        }
    }

    /**
     * 四分屏被点击，转半屏完成后，全屏将转半屏，全屏转半屏完成后会回调此接口。
     * @param fullScreen 要转换的全屏
     */
    private void fullToHalfOnChanged(@NonNull WindowState fullScreen) {
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
     * @param theOtherQuarter 另外一个四分屏
     */
    private void quarterToHalfFromStart(@NonNull final WindowState view,
            @NonNull final ConstraintLayout.LayoutParams lpStart,
            @NonNull final ConstraintLayout.LayoutParams lpEnd,
            @NonNull final WindowState fullScreen, @Nullable final WindowState theOtherQuarter) {
        TransitionHelper helper = new TransitionHelper(view);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: quarterToHalfFromStart: ");
            quarterToHalfOnChanged(view);
            onEndQuarterToHalfFromStart(view, lpEnd, fullScreen, theOtherQuarter);
        });
        helper.beginDelayedTransition();

        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        view.getView().setLayoutParams(lpStart);
        view.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(view.getView().getId()), view);
    }

    /**
     * 四分屏转半屏情形下后半部分流程——执行全屏转半屏。
     *
     * @param view       the clicked quarter view
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void onEndQuarterToHalfFromStart(@NonNull WindowState view,
            @NonNull ConstraintLayout.LayoutParams lpEnd,
            @NonNull final WindowState fullScreen, @Nullable final WindowState theOtherQuarter) {
        TransitionHelper helper = new TransitionHelper(fullScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: onEndQuarterToHalfFromStart: ");
            fullToHalfOnChanged(fullScreen);
            if (theOtherQuarter != null) {
                theOtherQuarter.getView().bringToFront();
            }
            mIsSizeChanging = false;
            if (mOnWindowSizeChanged != null) {
                mOnWindowSizeChanged.onSizeChanged();
            }
        });
        helper.beginDelayedTransition();
        lpEnd.startToEnd = view.getView().getId();
        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
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
     * @param theOtherQuarter 另外一个四分屏
     */
    private void quarterToHalfFromEnd(@NonNull WindowState view,
            @NonNull final ConstraintLayout.LayoutParams lpStart,
            @NonNull final ConstraintLayout.LayoutParams lpEnd,
            @NonNull final WindowState fullScreen, @Nullable WindowState theOtherQuarter) {
        TransitionHelper helper = new TransitionHelper(view);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: quarterToHalfFromEnd");
            quarterToHalfOnChanged(view);
            onEndQuarterToHalfFromEnd(view, lpStart, fullScreen, theOtherQuarter);
        });
        helper.beginDelayedTransition();

        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        view.getView().setLayoutParams(lpEnd);
        view.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(view.getView().getId()), view);
    }

    /**
     * 四分屏转半屏情形下后半部分流程——执行全屏转半屏。
     *
     * @param view       the clicked quarter view
     * @param lpStart    start位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void onEndQuarterToHalfFromEnd(@NonNull WindowState view,
            @NonNull ConstraintLayout.LayoutParams lpStart,
            @NonNull final WindowState fullScreen, @Nullable final WindowState theOtherQuarter) {
        TransitionHelper helper = new TransitionHelper(fullScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: onEndQuarterToHalfFromEnd");
            fullToHalfOnChanged(fullScreen);
            if (theOtherQuarter != null) {
                theOtherQuarter.getView().bringToFront();
            }
            mIsSizeChanging = false;
            if (mOnWindowSizeChanged != null) {
                mOnWindowSizeChanged.onSizeChanged();
            }
        });
        helper.beginDelayedTransition();
        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        lpStart.endToStart = view.getView().getId();
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
     * @param theOtherQuarter 另外一个四分屏
     */
    private void quarterToHalfFromCenter(@NonNull final WindowState view,
            @NonNull final ConstraintLayout.LayoutParams lpStart,
            @NonNull final ConstraintLayout.LayoutParams lpEnd,
            @NonNull final WindowState fullScreen, @Nullable WindowState theOtherQuarter) {
        if (fullScreen.getGravity() == WindowState.END) {
            quarterToHalfFromCenterWithFullEnd(view, lpStart, lpEnd, fullScreen, theOtherQuarter);
        } else {
            quarterToHalfFromCenterWithFullStart(view, lpStart, lpEnd, fullScreen, theOtherQuarter);
        }
    }

    /**
     * 被点击的四分屏是交互区域的原中心分屏，当前的全屏是原end区域的。
     *
     * @param view       the clicked quarter view
     * @param lpStart    start位置的view布局参数
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     * @param theOtherQuarter 另外一个四分屏
     */
    private void quarterToHalfFromCenterWithFullEnd(@NonNull final WindowState view,
          @NonNull final ConstraintLayout.LayoutParams lpStart,
          @NonNull final ConstraintLayout.LayoutParams lpEnd,
          @NonNull final WindowState fullScreen, @Nullable WindowState theOtherQuarter) {
        TransitionHelper helper = new TransitionHelper(view);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: quarterToHalfFromCenterWithFullEnd");
            quarterToHalfOnChanged(view);
            onQuarterToHalfFromCenterEnd(lpEnd, fullScreen, theOtherQuarter);
        });
        helper.beginDelayedTransition();

        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        view.getView().setLayoutParams(lpStart);
        view.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(view.getView().getId()), view);
    }

    /**
     * 被点击的四分屏是交互区域的原中心分屏，当前的全屏是原end区域的。
     *
     * @param view       the clicked quarter view
     * @param lpStart    start位置的view布局参数
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     * @param theOtherQuarter 另外一个四分屏
     */
    private void quarterToHalfFromCenterWithFullStart(@NonNull final WindowState view,
            @NonNull final ConstraintLayout.LayoutParams lpStart,
            @NonNull final ConstraintLayout.LayoutParams lpEnd,
            @NonNull final WindowState fullScreen, @Nullable final WindowState theOtherQuarter) {
        TransitionHelper helper = new TransitionHelper(view);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: quarterToHalfFromCenterWithFullStart");
            quarterToHalfOnChanged(view);
            onQuarterToHalfFromCenterStart(lpStart, fullScreen, theOtherQuarter);
        });
        helper.beginDelayedTransition();

        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        view.getView().setLayoutParams(lpEnd);
        view.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(view.getView().getId()), view);
    }

    /**
     * 从布局参数的center位置开始将该位置的view与其他view进行分屏操作的后半部分流程——全屏属于原始end位置的view，
     * 将该View复原到end位置，并且半屏。
     *
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void onQuarterToHalfFromCenterEnd(@NonNull ConstraintLayout.LayoutParams lpEnd,
            @NonNull final WindowState fullScreen, @Nullable final WindowState theOtherQuarter) {
        TransitionHelper helper = new TransitionHelper(fullScreen);
        helper.setOnFinishListener(() -> {
            fullToHalfOnChanged(fullScreen);
            if (theOtherQuarter != null) {
                theOtherQuarter.getView().bringToFront();
            }
            mIsSizeChanging = false;
            if (mOnWindowSizeChanged != null) {
                mOnWindowSizeChanged.onSizeChanged();
            }
        });
        helper.beginDelayedTransition();
        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        fullScreen.getView().setLayoutParams(lpEnd);
        fullScreen.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);

    }

    /**
     * 从布局参数的center位置开始将该位置的view与其他view进行分屏操作的后半部分流程——全屏属于原始end位置的view，
     * 将该View复原到start位置，并且半屏。
     *
     * @param lpStart    start位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void onQuarterToHalfFromCenterStart(@NonNull ConstraintLayout.LayoutParams lpStart,
            @NonNull final WindowState fullScreen, @Nullable final WindowState theOtherQuarter) {
        TransitionHelper helper = new TransitionHelper(fullScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: onQuarterToHalfFromCenterStart");
            fullToHalfOnChanged(fullScreen);
            if (theOtherQuarter != null) {
                theOtherQuarter.getView().bringToFront();
            }
            mIsSizeChanging = false;
            if (mOnWindowSizeChanged != null) {
                mOnWindowSizeChanged.onSizeChanged();
            }
        });
        helper.beginDelayedTransition();

        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        fullScreen.getView().setLayoutParams(lpStart);
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
    public interface OnWindowSizeChanged {
        /**
         * called after setting an state for every view's size has finished changed.
         */
        void onSizeChanged();
    }
}
