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
    private OnWindowSizeChanged mOnWindowSizeChanged;
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
    /**
     * 转场动画监听
     */
    private Transition.TransitionListener mTransitionListener;
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
        String key = String.valueOf(id);
        WindowState windowState = mWindowStates.get(key);
        if (windowState == null || windowState.getState() == WindowState.FULL_SCREEN) {
            return;
        }

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

        if (mOnWindowSizeChanged != null) {
            mOnWindowSizeChanged.onSizeChanged();
        }
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
        onHalfToFullListening(state);
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
        OnChangeToFull changeToFull = mFullStateCallBacks.get(key);
        if (changeToFull != null) {
            changeToFull.onFull();
        }
    }

    /**
     * 半屏转全屏场景，位全屏添加动画监听，然后在动画监听里处理四分屏事件。
     *
     * @param state current clicked view
     */
    private void onHalfToFullListening(@NonNull WindowState state) {
        mTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                // nothing
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mTransitionFull.removeListener(mTransitionListener);
                halfToQuarter(true);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                mTransitionFull.removeListener(mTransitionListener);
                halfToQuarter(false);
            }

            @Override
            public void onTransitionPause(Transition transition) {
                // nothing
            }

            @Override
            public void onTransitionResume(Transition transition) {
                // nothing
            }
        };
        mTransitionFull.addListener(mTransitionListener);
        TransitionManager.beginDelayedTransition((ViewGroup) state.getView().getParent(),
                mTransitionFull);
    }

    /**
     * 半屏转四分屏幕。
     *
     * @param activeAnimation 动画是否有有效。
     *                        <p>
     *                        find the half screen, then convert it to quarter screen and bring
     *                        it to front.
     *                        find the quarter screen, then adjust its alignment and bring it to
     *                        front.
     */
    private void halfToQuarter(boolean activeAnimation) {
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
            halfToQuarterFromStart(activeAnimation, halfScreen, lpStart, lpEnd, quarterScreen);
        } else if (gravity == WindowState.END) {//固定位置，右对齐
            halfToQuarterFromEnd(activeAnimation, halfScreen, lpStart, lpEnd, quarterScreen);
        } else {// this half screen is swappable.当前半屏view是gravity等于center的view
            halfToQuarterFromCenter(activeAnimation, halfScreen, lpEnd, quarterScreen, fullScreen);
        }

        OnChangeToQuarter changeToQuarter = mQuarterStateCallBacks.get(String.valueOf(halfScreen
                .getView().getId()));
        if (changeToQuarter != null) {
            changeToQuarter.onQuarter();
        }
    }

    /**
     * 在半分屏转四分屏场景中，处理半屏转四分屏延迟动画。
     *
     * @param halfScreen 代表半屏的对象。
     */
    private void onHalfToQuarterBeginDelayedTransition(WindowState halfScreen) {
        int gravity = halfScreen.getGravity();
        if (gravity == WindowState.START) {//固定位置，左对齐
            TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                    mTransitionLayoutStart);
        } else if (gravity == WindowState.END) {//固定位置，右对齐
            TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                    mTransitionLayoutEnd);
        } else {// this half screen is swappable.当前半屏view是gravity等于center的view
            TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                    mTransitionLayoutStart);
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
    private void halfToQuarterFromStart(boolean activeAnimation, @NonNull WindowState halfScreen,
            @NonNull ConstraintLayout.LayoutParams lpStart,
            @NonNull ConstraintLayout.LayoutParams lpEnd,
            @Nullable WindowState quarterScreen) {
        // convert it to quarter start screen.
        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        if (activeAnimation) {
            TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                    mTransitionLayoutStart);
        }
        halfScreen.getView().setLayoutParams(lpStart);
        halfScreen.getView().bringToFront();
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);

        if (quarterScreen != null) {
            if (activeAnimation) {
                TransitionManager.beginDelayedTransition((ViewGroup) quarterScreen.getView()
                        .getParent(), mTransitionLayoutEnd);
            }
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
     * @param halfScreen    此时的半屏，将要转成四分屏
     * @param lpStart       start位置的view布局参数
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromEnd(boolean activeAnimation, @NonNull WindowState halfScreen,
            @NonNull ConstraintLayout.LayoutParams lpStart,
            @NonNull ConstraintLayout.LayoutParams lpEnd,
            @Nullable WindowState quarterScreen) {
        // convert it to quarter end screen.
        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        if (activeAnimation) {
            TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                    mTransitionLayoutEnd);
        }
        halfScreen.getView().setLayoutParams(lpEnd);
        halfScreen.getView().bringToFront();
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);

        if (quarterScreen != null) {// quarter start screen.
            if (activeAnimation) {
                TransitionManager.beginDelayedTransition((ViewGroup) quarterScreen.getView()
                        .getParent(), mTransitionLayoutStart);
            }
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
     * @param halfScreen    此时的半屏，将要转成四分屏
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromCenter(boolean activeAnimation, @NonNull WindowState halfScreen,
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
        if (activeAnimation) {
            TransitionManager.beginDelayedTransition((ViewGroup) halfScreen.getView().getParent(),
                    mTransitionLayoutStart);
        }
        halfScreen.getView().setLayoutParams(lpEnd);
        halfScreen.getView().bringToFront();
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);

        if (quarterScreen != null) {
            if (activeAnimation) {
                TransitionManager.beginDelayedTransition((ViewGroup) quarterScreen.getView()
                        .getParent(), mTransitionLayoutEnd);
            }
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
     * @param theOtherQuarter 另外一个四分屏
     */
    private void quarterToHalfFromStart(@NonNull WindowState view,
            @NonNull final ConstraintLayout.LayoutParams lpStart,
            @NonNull final ConstraintLayout.LayoutParams lpEnd,
            @NonNull final WindowState fullScreen, @Nullable WindowState theOtherQuarter) {
        mTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                // nothing
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mTransitionLayoutStart.removeListener(mTransitionListener);
                TransitionManager
                        .beginDelayedTransition((ViewGroup) fullScreen.getView().getParent(),
                                mTransitionLayoutEnd);
                onEndQuarterToHalfFromStart(view, lpEnd, fullScreen, theOtherQuarter);

            }

            @Override
            public void onTransitionCancel(Transition transition) {
                mTransitionLayoutStart.removeListener(mTransitionListener);
                onEndQuarterToHalfFromStart(view, lpEnd, fullScreen, theOtherQuarter);
            }

            @Override
            public void onTransitionPause(Transition transition) {
                // nothing
            }

            @Override
            public void onTransitionResume(Transition transition) {
                // nothing
            }
        };
        mTransitionLayoutStart.addListener(mTransitionListener);
        TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                mTransitionLayoutStart);
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
            @NonNull WindowState fullScreen, @Nullable WindowState theOtherQuarter) {
        lpEnd.startToEnd = view.getView().getId();
        lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        fullScreen.getView().setLayoutParams(lpEnd);
        fullScreen.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
        if (theOtherQuarter != null) {
            theOtherQuarter.getView().bringToFront();
        }
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
        mTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                // nothing
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                mTransitionLayoutEnd.removeListener(mTransitionListener);
                TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView()
                        .getParent(), mTransitionLayoutStart);
                onEndQuarterToHalfFromEnd(view, lpStart, fullScreen, theOtherQuarter);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
                mTransitionLayoutEnd.removeListener(mTransitionListener);
                onEndQuarterToHalfFromEnd(view, lpStart, fullScreen, theOtherQuarter);
            }

            @Override
            public void onTransitionPause(Transition transition) {
                // nothing
            }

            @Override
            public void onTransitionResume(Transition transition) {
                // nothing
            }
        };
        mTransitionLayoutEnd.addListener(mTransitionListener);
        TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                mTransitionLayoutEnd);
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
            @NonNull WindowState fullScreen, @Nullable WindowState theOtherQuarter) {
        lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        lpStart.endToStart = view.getView().getId();
        fullScreen.getView().setLayoutParams(lpStart);
        fullScreen.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(fullScreen.getView().getId()), fullScreen);
        if (theOtherQuarter != null) {
            theOtherQuarter.getView().bringToFront();
        }
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
        onQuarterToHalfFromCenterListening(lpStart, lpEnd, fullScreen);
        int fG = fullScreen.getGravity();
        if (fG == WindowState.END) {
            TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                    mTransitionLayoutStart);
            lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            view.getView().setLayoutParams(lpStart);
        } else {
            TransitionManager.beginDelayedTransition((ViewGroup) view.getView().getParent(),
                    mTransitionLayoutEnd);
            lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            view.getView().setLayoutParams(lpEnd);
        }
        view.setState(WindowState.HALF_SCREEN);
        mWindowStates.put(String.valueOf(view.getView().getId()), view);

        if (theOtherQuarter != null) {
            theOtherQuarter.getView().bringToFront();
        }
    }

    /**
     * 从布局参数的center位置开始将该位置的view与其他view进行分屏操作的后半部分流程——全屏属于原始end位置的view，
     * 将该View复原到end位置，将原始属于start位置位置的view该View复原到start位置，并且半屏。
     *
     * @param lpStart    start位置的view布局参数
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void onQuarterToHalfFromCenterListening(
            @NonNull final ConstraintLayout.LayoutParams lpStart,
            @NonNull final ConstraintLayout.LayoutParams lpEnd,
            @NonNull final WindowState fullScreen) {
        Transition transition;
        int fG = fullScreen.getGravity();
        if (fG == WindowState.END) {
            transition = mTransitionLayoutStart;
            mTransitionListener = new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    // nothing
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    mTransitionLayoutStart.removeListener(mTransitionListener);
                    TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView()
                            .getParent(), mTransitionLayoutEnd);
                    onQuarterToHalfFromCenterEnd(lpEnd, fullScreen);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    mTransitionLayoutStart.removeListener(mTransitionListener);
                    onQuarterToHalfFromCenterEnd(lpEnd, fullScreen);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // nothing
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // nothing
                }
            };
        } else {
            transition = mTransitionLayoutEnd;
            mTransitionListener = new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {
                    // nothing
                }

                @Override
                public void onTransitionEnd(Transition transition) {
                    mTransitionLayoutEnd.removeListener(mTransitionListener);
                    TransitionManager.beginDelayedTransition((ViewGroup) fullScreen.getView()
                            .getParent(), mTransitionLayoutStart);
                    onQuarterToHalfFromCenterStart(lpStart, fullScreen);
                }

                @Override
                public void onTransitionCancel(Transition transition) {
                    mTransitionLayoutEnd.removeListener(mTransitionListener);
                    onQuarterToHalfFromCenterStart(lpStart, fullScreen);
                }

                @Override
                public void onTransitionPause(Transition transition) {
                    // nothing
                }

                @Override
                public void onTransitionResume(Transition transition) {
                    // nothing
                }
            };
        }
        transition.addListener(mTransitionListener);

    }

    /**
     * 从布局参数的center位置开始将该位置的view与其他view进行分屏操作的后半部分流程——全屏属于原始end位置的view，
     * 将该View复原到end位置，并且半屏。
     *
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void onQuarterToHalfFromCenterEnd(
            @NonNull ConstraintLayout.LayoutParams lpEnd, @NonNull WindowState fullScreen) {
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
            @NonNull WindowState fullScreen) {
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
