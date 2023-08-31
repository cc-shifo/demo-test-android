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
 * 左，中，右view的parent是同一个parent。
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
    /**
     * 交换id。初次加载界面时，中间位置为交互区域，中间的这个view的id就是交互id。
     */
    private int mSwapId;

    /**
     * 描述start区域的界面和end区域的界面进行对比，且四分屏（最开始位于中间区域的界面）位于点击界面的对侧这种场景。
     * true表示是这种场景，否则false。
     *
     * start区域的界面和end区域的界面进行对比情况下，，被点击的界面和四分屏位于对侧时，半屏转四分屏前先移动四分屏
     * 到半屏的对侧，后处理半屏转四分屏业务效果会更好
     */
    private boolean isOppositeSideMoveCase;

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

        WindowState windowState = mWindowStates.get(String.valueOf(id));
        if (windowState == null || windowState.getState() == WindowState.FULL_SCREEN) {
            return;
        }

        mIsSizeChanging = true;
        if (windowState.getState() == WindowState.HALF_SCREEN) {
            halfToFullHalfToQuarter(windowState);
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
     * @param state current clicked view
     */
    private void halfToFullHalfToQuarter(@NonNull WindowState state) {
        halfToFullStep1(state);
    }

    /**
     * 半屏转全屏。
     * 如果是start和end进行对比，先移动四分屏，然后将被点击的半屏转全屏，将另外一个半屏转四分屏。
     * 如果不是start和end进行对比，先将被点击的半屏转全屏，将另外一个半屏转四分屏，最后移动四分屏。
     *
     * @param view  current clicked view，是半屏
     */
    private void halfToFullStep1(@NonNull final WindowState view) {
        int clickedId = view.getView().getId(); // 被点击的半屏

        WindowState halfScreen = null;
        WindowState quarterScreen = null;

        // find half screen
        // find quarter screen
        for (WindowState win : mWindowStates.values()) {
            int state = win.getState();
            if (state == WindowState.QUARTER_SCREEN) {
                quarterScreen = win;
            } else if (win.getView().getId() != clickedId) {
                halfScreen = win; // 此半屏将转成四分屏
            }
        }

        isOppositeSideMoveCase = isStartVSEnd(view, halfScreen) && isOnOppositeSide(view,
                quarterScreen);
        if (isOppositeSideMoveCase) {
            moveOppositeSideQuarter(view, quarterScreen);
        } else {
            halfToFullStep2(view);
        }
    }

    /**
     * 执行将被点击的半屏转全屏转成全屏，另外一个半屏转成四分屏
     *
     * @param view  current clicked view，是半屏
     */
    private void halfToFullStep2(@NonNull WindowState view) {
        TransitionHelper helper = new TransitionHelper(view);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "halfToFullStep2 onFinish: ");
            halfToFullOnChanged(view);
            halfToQuarter(view);
        });

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        layoutParams.horizontalWeight = 1;
        layoutParams.verticalWeight = 1;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        helper.beginDelayedTransition();
        view.getView().setLayoutParams(layoutParams);
        view.setState(WindowState.FULL_SCREEN);
        mWindowStates.put(String.valueOf(view.getView().getId()), view);
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
     * 执行将半屏转四分屏幕。
     * find the half screen, then convert it to quarter screen and bring it to front.
     * find the quarter screen, then adjust its alignment and bring it to front.
     *
     * @param view 当前点击的view，已经从半屏转成全屏。
     */
    private void halfToQuarter(@NonNull WindowState view) {
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
        // isOppositeSideMoveCase = isStartVSEnd(view, halfScreen) && isOnOppositeSide(view,
        //         quarterScreen);
        // if (isOppositeSideMoveCase) {
        //     moveOppositeSideQuarter(view, quarterScreen);
        // }
        int gravity = halfScreen.getGravity();
        if (gravity == WindowState.START) {//固定位置，左对齐
            halfToQuarterFromStart(view, halfScreen, lpStart, lpEnd, quarterScreen);
        } else if (gravity == WindowState.END) {//固定位置，右对齐
            halfToQuarterFromEnd(view, halfScreen, lpStart, lpEnd, quarterScreen);
        } else {// this half screen is swappable.当前半屏view是gravity等于center的view
            halfToQuarterFromCenter(view, halfScreen, lpEnd, quarterScreen, fullScreen);
        }
    }

    /**
     * 判断是否是start区域的界面和end区域的界面进行对比
     * @param view 被点击的界面，已经转换成了全屏。
     * @param half 对比中的半屏界面，将会被转成四分屏。
     * @return true 是start区域的界面和end区域的界面进行对比，否则false。
     */
    private boolean isStartVSEnd(@NonNull WindowState view, @Nullable WindowState half) {
        if (half == null) {
            return false;
        }

        int hG = half.getGravity();
        int clickedG = view.getGravity();
        return (clickedG == WindowState.START && hG == WindowState.END)
                || (clickedG == WindowState.END && hG == WindowState.START);
    }

    /**
     * start区域的界面和end区域的界面进行对比情况下，判断半屏被点击的半屏和四分屏是不是处于对一侧。
     *
     * start区域的界面和end区域的界面进行对比情况下，被点击的界面和四分屏位于对侧时，半屏转四分屏前先移动四分屏
     * 到半屏的对侧，后处理半屏转四分屏业务效果会更好（尤其是半屏在四分屏之上时，如果先处理半屏转四分屏业务，
     * 后移动之前的四分屏，四分屏会从处理完后的四分屏下面出现，在性能不好的情况下，后移动的四分屏很可能不能出现在
     * 全屏的上面。
     *
     * @param view 被点击的半屏界面，已经转换成了全屏。
     * @param quarter 没有进行对比的四分屏，将会被移动。
     * @return true 被点击的半屏和四分屏处于对侧，否则false。
     */
    private boolean isOnOppositeSide(@NonNull WindowState view, @Nullable WindowState quarter) {
        if (quarter == null) {
            return false;
        }

        int clickedG = view.getGravity();
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) quarter.getView()
                .getLayoutParams();
        if (clickedG == WindowState.START) {
            return params.endToEnd == ConstraintLayout.LayoutParams.PARENT_ID;
        } else if (clickedG == WindowState.END) {
            return params.startToStart == ConstraintLayout.LayoutParams.PARENT_ID;
        }

        return false;
    }

    /**
     * start区域的界面和end区域的界面进行对比情况下，被点击的半屏转全屏后，先移动四分屏（最开始位于中间区域的）
     * @param view 被点击的半屏
     * @param quarterScreen 将要被移动的四分屏（最开始位于中间区域的）
     */
    private void moveOppositeSideQuarter(@NonNull WindowState view,
            @NonNull WindowState quarterScreen) {
        int gravity = view.getGravity();
        int full = getViewIndex(view.getView());
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
        lp.matchConstraintPercentWidth = 0.25f;
        lp.matchConstraintDefaultWidth = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        lp.matchConstraintPercentHeight = 0.25f;
        lp.matchConstraintDefaultHeight = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;

        TransitionHelper helper = new TransitionHelper(quarterScreen, true);
        helper.setDuration(100);
        helper.setOnFinishListener(() -> halfToFullStep2(view));
        helper.beginDelayedTransition();
        if (gravity == WindowState.END) {//固定位置，右对齐
            // it's already been in quarter state, align it to end.
            lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        } else if (gravity == WindowState.START) {// quarter start screen.
            lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            //两个右下角半屏，地图位于右下角，其中一个半屏变全屏，另外一个半屏移动到右下角，地图被覆盖
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        }

        quarterScreen.getView().setLayoutParams(lp);
        if (full > getViewIndex(quarterScreen.getView())) {
            quarterScreen.getView().bringToFront();
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
     * @param view 当前点击的view，已经从半屏转成全屏。
     * @param halfScreen    此时的半屏，将要转成四分屏
     * @param lpStart       start位置的view布局参数
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromStart(@NonNull WindowState view, @NonNull WindowState halfScreen,
            @NonNull ConstraintLayout.LayoutParams lpStart,
            @NonNull final ConstraintLayout.LayoutParams lpEnd,
            @Nullable WindowState quarterScreen) {
        final int full = getViewIndex(view.getView());
        TransitionHelper helper = new TransitionHelper(halfScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: halfToQuarterFromStart");
            halfToQuarterOnChanged(halfScreen);
            if (quarterScreen != null && !isOppositeSideMoveCase) {
                // it's already been in quarter state, align it to end.
                if (quarterScreen.getView().getId() == mSwapId) {//固定位置，右对齐
                    lpEnd.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
                    lpEnd.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    quarterScreen.getView().setLayoutParams(lpEnd);
                }

                if (full > getViewIndex(quarterScreen.getView())) {
                    quarterScreen.getView().bringToFront();
                }
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
        if (full > getViewIndex(halfScreen.getView())) {
            halfScreen.getView().bringToFront();
        }
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);
    }

    /**
     * 从布局参数的end位置开始将该位置的view与其他view进行分屏操作
     *
     * @param view 当前点击的view，已经从半屏转成全屏。
     * @param halfScreen    此时的半屏，将要转成四分屏
     * @param lpStart       start位置的view布局参数
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromEnd(@NonNull WindowState view, @NonNull WindowState halfScreen,
            @NonNull final ConstraintLayout.LayoutParams lpStart,
            @NonNull ConstraintLayout.LayoutParams lpEnd, @Nullable WindowState quarterScreen) {
        final int full = getViewIndex(view.getView());
        TransitionHelper helper = new TransitionHelper(halfScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: halfToQuarterFromEnd");
            halfToQuarterOnChanged(halfScreen);
            if (quarterScreen != null && !isOppositeSideMoveCase) {// quarter start screen.
                if (quarterScreen.getView().getId() == mSwapId) {
                    lpStart.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
                    //两个右下角半屏，地图位于右下角，其中一个半屏变全屏，另外一个半屏移动到右下角，地图被覆盖
                    lpStart.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
                    quarterScreen.getView().setLayoutParams(lpStart);
                }
                if (full > getViewIndex(quarterScreen.getView())) {
                    quarterScreen.getView().bringToFront();
                }
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
        if (full > getViewIndex(halfScreen.getView())) {
            halfScreen.getView().bringToFront();
        }
        halfScreen.setState(WindowState.QUARTER_SCREEN);
        mWindowStates.put(String.valueOf(halfScreen.getView().getId()), halfScreen);
    }

    /**
     * 从布局参数的center位置开始将该位置的view与其他view进行分屏操作
     *
     * @param view 当前点击的view，已经从半屏转成全屏。
     * @param halfScreen    此时的半屏，将要转成四分屏
     * @param lpEnd         end位置的view布局参数
     * @param quarterScreen 此时的全屏view
     */
    private void halfToQuarterFromCenter(@NonNull WindowState view, @NonNull WindowState halfScreen,
            @NonNull ConstraintLayout.LayoutParams lpEnd, @Nullable final WindowState quarterScreen,
            @NonNull WindowState fullScreen) {
        final int full = getViewIndex(view.getView());
        TransitionHelper helper = new TransitionHelper(halfScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: halfToQuarterFromCenter");
            halfToQuarterOnChanged(halfScreen);
            if (quarterScreen != null && full > getViewIndex(quarterScreen.getView())) {
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
        if (full > getViewIndex(halfScreen.getView())) {
            halfScreen.getView().bringToFront();
        }
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
            // if (theOtherQuarter != null) {
            //     theOtherQuarter.getView().bringToFront();
            // }
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
            // if (theOtherQuarter != null) {
            //     theOtherQuarter.getView().bringToFront();
            // }
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
     * @param view       the clicked quarter view，最开始固定于中间区域
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
            onQuarterToHalfFromCenterEnd(view, lpEnd, fullScreen, theOtherQuarter);
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
            onQuarterToHalfFromCenterStart(view, lpStart, fullScreen, theOtherQuarter);
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
     * @param view       被点击的四分屏，已经从四分屏转成半屏。最开始固定于中间区域。
     * @param lpEnd      end位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void onQuarterToHalfFromCenterEnd(@NonNull WindowState view,
            @NonNull ConstraintLayout.LayoutParams lpEnd,
            @NonNull final WindowState fullScreen, @Nullable final WindowState theOtherQuarter) {
        final int center = getViewIndex(view.getView());
        TransitionHelper helper = new TransitionHelper(fullScreen);
        helper.setOnFinishListener(() -> {
            fullToHalfOnChanged(fullScreen);
            if (theOtherQuarter != null && center > getViewIndex(theOtherQuarter.getView())) {
                // center > start
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
     * @param view       被点击的四分屏，已经从四分屏转成半屏。最开始固定于中间区域。
     * @param lpStart    start位置的view布局参数
     * @param fullScreen 此时的全屏view
     */
    private void onQuarterToHalfFromCenterStart(@NonNull WindowState view,
            @NonNull ConstraintLayout.LayoutParams lpStart,
            @NonNull final WindowState fullScreen, @Nullable final WindowState theOtherQuarter) {
        final int center = getViewIndex(view.getView());
        TransitionHelper helper = new TransitionHelper(fullScreen);
        helper.setOnFinishListener(() -> {
            Log.d(TAG, "onFinish: onQuarterToHalfFromCenterStart");
            fullToHalfOnChanged(fullScreen);
            if (theOtherQuarter != null && center > getViewIndex(theOtherQuarter.getView())) {
                // center > end
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
