package com.example.demoscreensplithelper;

import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

public class TransitionHelper implements Transition.TransitionListener {
    private static final String TAG = "TransitionHelper";
    private OnFinishListener mOnFinishListener;
    private final Transition mChangeBound = new ChangeBounds();

    /**
     * 当前动画的View
     */
    private WindowState mView;
    /**
     * 下一个动画
     */
    private ConstraintLayout.LayoutParams lpStart;
    /**
     * 下一个动画的View
     */
    private WindowState mNext;
    /**
     * 最后一个动画的View
     */
    @Nullable
    private WindowState mTheOther;

    public TransitionHelper(@NonNull WindowState view, @Nullable WindowState next,
            @Nullable WindowState theOtherQuarter) {
        mView = view;
        mNext = next;
        mTheOther = theOtherQuarter;
        mChangeBound.addListener(this);
    }

    public void setOnFinishListener(@Nullable OnFinishListener onFinishListener) {
        mOnFinishListener = onFinishListener;
    }

    public void beginDelayedTransition() {
        // TransitionManager.beginDelayedTransition((ViewGroup) mView.getView().getParent(),
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

    public interface OnFinishListener {
        void onFinish();
    }
}
