package com.example.demoscreensplithelper;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.AutoTransition;
import androidx.transition.ChangeBounds;
import androidx.transition.ChangeScroll;
import androidx.transition.ChangeTransform;
import androidx.transition.Explode;
import androidx.transition.Fade;
import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

/**
 * 对ChangeBounds动画的封装，用于显示窗口切换过程中UI控件边界变化的动画效果。
 */
public class TransitionHelper implements Transition.TransitionListener {
    private static final String TAG = "TransitionHelper";
    private OnFinishListener mOnFinishListener;
    private Transition mChangeBound;

    /**
     * 当前动画的View
     */
    private final WindowState mView;

    public TransitionHelper(@NonNull WindowState view) {
        this(view, false);
    }

    public TransitionHelper(@NonNull WindowState view, boolean autoTransition) {
        mView = view;
        mChangeBound = !autoTransition ? new ChangeBounds() : new AutoTransition();
        mChangeBound.addListener(this);
    }



    /**
     * 设置ChangeBounds动画的持续时间
     * @param duration 动画的持续时间
     */
    public void setDuration(long duration) {
        mChangeBound.setDuration(duration);
    }

    public long getDuration() {
        return mChangeBound.getDuration();
    }

    /**
     * 设置动画结束时的调用函数。
     * @param onFinishListener 动画结束时调用的函数。
     */
    public void setOnFinishListener(@Nullable OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    /**
     * 启动场景动画
     */
    public void beginDelayedTransition() {
        TransitionManager.beginDelayedTransition((ViewGroup) mView.getView(),
                mChangeBound);
    }

    @Override
    public void onTransitionStart(Transition transition) {
        // nothing
    }

    @Override
    public void onTransitionEnd(Transition transition) {
        Log.d(TAG, "onTransitionEnd: ");
        if (mOnFinishListener != null) {
            mOnFinishListener.onFinish();
        }
        mChangeBound.removeListener(this);
    }

    @Override
    public void onTransitionCancel(Transition transition) {
        Log.d(TAG, "onTransitionCancel: ");
        if (mOnFinishListener != null) {
            mOnFinishListener.onFinish();
        }
        mChangeBound.removeListener(this);
    }

    @Override
    public void onTransitionPause(Transition transition) {
        // nothing
        Log.d(TAG, "onTransitionPause: ");
    }

    @Override
    public void onTransitionResume(Transition transition) {
        // nothing
        Log.d(TAG, "onTransitionResume: ");
    }

    /**
     * 场景动画结束时调用的接口。
     */
    public interface OnFinishListener {
        void onFinish();
    }
}
