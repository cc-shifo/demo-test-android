package com.example.sliderecyclerview;

import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProcessMenuTouchHelper implements RecyclerView.OnItemTouchListener {
    private static final String TAG = "ProcessMenuTouchHelper";
    private final int MIN_VELOCITY = 500;
    private final int mMenuWidth;
    private VelocityTracker mVelocityTracker;
    private int mInitialTouchX;
    private int mInitialTouchY;
    private int mLastTouchX;
    private int mLastTouchY;
    private int mTouchSlop;
    private ViewGroup mItem;
    // private boolean mDirtIntercept;
    private ObjectAnimator mFlingAnimation;
    private ObjectAnimator mClosingAnimation;
    private int mFirstPointerId;

    public ProcessMenuTouchHelper(@IntRange(from = 1) int menuWidth) {
        mMenuWidth = menuWidth;
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        // 判断是recyclerView本身的上下滑动呢，还是是Item的左右滑，根据判断结果决定拦截与否
        float x = e.getX();
        float y = e.getY();
        // switch (e.getAction() & MotionEvent.ACTION_MASK) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_POINTER_DOWN:
                return true;
            case MotionEvent.ACTION_DOWN:
                mFirstPointerId = e.getPointerId(0);
                Log.d(TAG, "onInterceptTouchEvent: ACTION_DOWN=" + x + ", " + y + "id="
                        + mFirstPointerId);
                mVelocityTracker = VelocityTracker.obtain();
                mVelocityTracker.addMovement(e);
                if ((mFlingAnimation != null && mFlingAnimation.isRunning())
                        || (mClosingAnimation != null && mClosingAnimation.isRunning())) {
                    // animation: scrolling or closing has not finished.
                    return false;
                }

                mInitialTouchX = (int) (x + 0.5f);
                mInitialTouchY = (int) (y + 0.5f);
                mLastTouchX = mInitialTouchX;
                mLastTouchY = mInitialTouchY;

                ViewConfiguration vc = ViewConfiguration.get(rv.getContext());
                mTouchSlop = vc.getScaledTouchSlop();
                // mDirtIntercept = false;

                break;
            case MotionEvent.ACTION_MOVE:
                // 不采集非第一个按下的事件
                if (e.getPointerId(e.getActionIndex()) != mFirstPointerId) {
                    break;
                }

                mLastTouchX = (int) (x + 0.5f);
                mLastTouchY = (int) (y + 0.5f);
                mVelocityTracker.addMovement(e);
                if (rv.getScrollState() != RecyclerView.SCROLL_STATE_IDLE) {
                    break;
                }

                mVelocityTracker.computeCurrentVelocity(1000);
                int vX = (int) (Math.abs(mVelocityTracker.getXVelocity()) + 0.5f);
                int vY = (int) (Math.abs(mVelocityTracker.getYVelocity()) + 0.5f);
                int dX = (int) Math.abs((x - mInitialTouchX));
                int dY = (int) Math.abs((y - mInitialTouchY));
                Log.d(TAG, "onInterceptTouchEvent: ACTION_MOVE" + x + ", " + y
                        + ", dX=" + dX + ", dY=" + dY + ", vX=" + vX + ", vY=" + vY
                        + ", mMenuWidth=" + mMenuWidth);
                boolean horizontal = (dX > dY && dX > mTouchSlop) || (vX > vY && vX > MIN_VELOCITY);
                // 没有打开，仅拦截向左滑
                // 已经打开，仅拦截向右滑
                if (horizontal) {
                    ViewGroup newView = (ViewGroup) rv.findChildViewUnder(mInitialTouchX
                            , mInitialTouchY);
                    if (mItem != null && newView != null && mItem != newView) {
                        closeItem();
                    }
                    mItem = newView;
                    if (mItem == null) {
                        throw new NullPointerException("found no item at " +
                                "position(" + mInitialTouchX + ", " + mInitialTouchY + ")");
                    }
                    /*mMenuWidth = mItem.getChildAt(2).getWidth();
                    if (mMenuWidth <= 0) {
                        throw new InvalidParameterException("Must be exact width");
                    }*/
                }

                if (horizontal) {
                    Log.d(TAG, "onInterceptTouchEvent: intercept= true" +
                            " scrolled=" + mItem.getScrollX());
                } else {
                    Log.d(TAG, "onInterceptTouchEvent: intercept= false");
                }
                return horizontal;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                Log.d(TAG, "onInterceptTouchEvent: ACTION_UP" + x + ", " + y);
                // non intercept case
                closeItem();
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            default:
                break;
        }
        return false;
    }

    /**
     * 处理水平滑动事件
     */
    @Override
    public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
        if (e.getPointerId(e.getActionIndex()) != mFirstPointerId ) {
            return;
        }

        float x = e.getX();
        float y = e.getY();
        // switch (e.getAction() & MotionEvent.ACTION_MASK) {
        switch (e.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                Log.d(TAG, "onTouchEvent: ACTION_DOWN" + x + ", " + y);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(e);
                Log.d(TAG, "onTouchEvent: ACTION_MOVE" + x + ", " + y
                        + ", scrollX=" + mItem.getScrollX());
                if (rv.getScrollState() == RecyclerView.SCROLL_STATE_IDLE && mItem != null) {
                    if (mLastTouchX == mInitialTouchX) {
                        mLastTouchX = (int) (x + 0.5f);
                    }

                    int dx = mLastTouchX - (int) (x + 0.5f); // 大于0,内容左滚，正向滑动。
                    // 关闭状态，取消右滑动作。
                    if (mItem.getScrollX() == 0 && dx < 0) {
                        // mDirtIntercept = true;
                        break;
                    }


                    /*if (Math.abs(dx) > mMenuWidth) {
                        dx = mMenuWidth;
                    }*/
                    if (mItem.getScrollX() + dx <= mMenuWidth
                            && mItem.getScrollX() + dx >= 0 /* reset from left to right*/) {
                        mItem.scrollBy(dx, 0);
                    }


                    mLastTouchX = (int) (x + 0.5f);
                    Log.d(TAG, "onTouchEvent: scrollBy dx=" + dx
                            + ", scrollBy=" + mItem.getScrollX() + ", mMenuWidth=" + mMenuWidth);
                }
                break;

            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                /*if (mDirtIntercept) {
                    Log.d(TAG, "onTouchEvent: ACTION_CANCEL dirty intercept " + x + ", " + y);
                    break;
                }*/

                int scrolled = mItem.getScrollX();
                if (Math.abs(scrolled) >= mMenuWidth) {
                    // scrolling action has finished during moving
                    break;
                }
                mVelocityTracker.addMovement(e);
                mVelocityTracker.computeCurrentVelocity(1000);
                int v = (int) (mVelocityTracker.getXVelocity() + 0.5f);

                Log.d(TAG, "onTouchEvent: ACTION_CANCEL lx="
                        + mLastTouchX + ", iX=" + mInitialTouchX
                        + ", velocity=" + v + ", scrolled=" + scrolled);

                // 速率大于最小速率时优先进行打开和关闭操作，速率小于最小速率时再考虑滚动距离，判断是否复位
                int dx = mInitialTouchX - mLastTouchX;
                if (Math.abs(v) > MIN_VELOCITY) {
                    if (scrolled > 0) {
                        if (dx < 0) { // 关闭动作
                            mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                                    0);
                            mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                            // DynamicAnimation.SCROLL_X,
                            mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                            mFlingAnimation.setDuration(scrolled)
                                    .start();
                        } else if (dx > 0) { // 未完成的打开动作
                            mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                                    mMenuWidth);
                            mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                            // DynamicAnimation.SCROLL_X,
                            mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                            mFlingAnimation.setDuration(mMenuWidth - scrolled)
                                    .start();
                        }
                    }
                    // 不会发生的事情
                    // else if (scrolled < 0) {
                    //     if (dx > 0) { // 关闭动作
                    //         mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                    //                 0);
                    //         mFlingAnimation.setInterpolator(new
                    //         AccelerateDecelerateInterpolator());
                    //         // DynamicAnimation.SCROLL_X,
                    //         mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                    //         mFlingAnimation.setDuration(-scrolled)
                    //                 .start();
                    //     } else if (dx < 0) { // 未完成的打开动作
                    //         mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                    //                 -mMenuWidth);
                    //         mFlingAnimation.setInterpolator(new
                    //         AccelerateDecelerateInterpolator());
                    //         // DynamicAnimation.SCROLL_X,
                    //         mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                    //         mFlingAnimation.setDuration(mMenuWidth+scrolled)
                    //                 .start();
                    //     }
                    //     mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                    //             0, -mMenuWidth);
                    //     mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                    //     // DynamicAnimation.SCROLL_X,
                    //     mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                    //     mFlingAnimation.setDuration(mMenuWidth)
                    //             .start();
                    // }
                } else if (scrolled > 0) {
                    if (dx < 0) { // 关闭动作
                        if ((mMenuWidth - scrolled) < mMenuWidth / 2) {// 不到一半时,复位
                            mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                                    scrolled, mMenuWidth);
                            mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                            // DynamicAnimation.SCROLL_X,
                            mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                            mFlingAnimation.setDuration(mMenuWidth - scrolled)
                                    .start();
                        } else {
                            mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                                    0);
                            mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                            // DynamicAnimation.SCROLL_X,
                            mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                            mFlingAnimation.setDuration(scrolled)
                                    .start();
                        }
                    } else if (dx > 0) { // 未完成的打开动作
                        if (scrolled < mMenuWidth / 2) {// 不到一半时,复位
                            mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                                    scrolled, 0);
                            mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                            // DynamicAnimation.SCROLL_X,
                            mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                            mFlingAnimation.setDuration(scrolled)
                                    .start();
                        } else {
                            mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                                    mMenuWidth);
                            mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                            // DynamicAnimation.SCROLL_X,
                            mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                            mFlingAnimation.setDuration(mMenuWidth - scrolled)
                                    .start();
                        }

                    }
                }
                // 不会发生的事情
                // else if (scrolled < 0) {
                //     if (dx > 0) { // 关闭动作
                //         mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                //                 0);
                //         mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                //         // DynamicAnimation.SCROLL_X,
                //         mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                //         mFlingAnimation.setDuration(-scrolled)
                //                 .start();
                //     } else if (dx < 0) { // 未完成的打开动作
                //         mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                //                 -mMenuWidth);
                //         mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                //         // DynamicAnimation.SCROLL_X,
                //         mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                //         mFlingAnimation.setDuration(mMenuWidth+scrolled)
                //                 .start();
                //     }
                //     mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                //             0, -mMenuWidth);
                //     mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                //     // DynamicAnimation.SCROLL_X,
                //     mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                //     mFlingAnimation.setDuration(mMenuWidth)
                //             .start();
                // }


                // mVelocityTracker.computeCurrentVelocity(1000);
                // int scrollX = mItem.getScrollX();
                // int v = (int) (mVelocityTracker.getXVelocity() + 0.5f);
                // Log.d(TAG, "onTouchEvent: ACTION_CANCEL" + x + ", " + y + ", " +
                //         "\nscroll=" + scrollX + ", velocity=" + v + ", Math.abs(v)=" + Math
                //         .abs(v));
                // if (Math.abs(v) > MIN_VELOCITY) {
                //     // 速率大于最小速率，继续滑动
                //     if (v >= MIN_VELOCITY) {
                //         // mItem.scrollBy(-scrollX, 0);
                //         Log.d(TAG, "onTouchEvent: MIN_VELOCITY, scrolled=" + mItem.getScrollX());
                //         // mItem.scrollBy(-(mMenuWidth + scrollX), 0);
                //         /*mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                //                 scrollX, -mMenuWidth);
                //         mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                //         // DynamicAnimation.SCROLL_X,
                //         mFlingAnimation.setDuration(Math.abs(mMenuWidth + scrollX))
                //                 .start();*/
                //     } else {
                //         // mItem.scrollBy(mMenuWidth - scrollX, 0);
                //         // mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                //         //         /*scrollX, */mMenuWidth);
                //         // mFlingAnimation.setInterpolator(new
                //         AccelerateDecelerateInterpolator());
                //         // // DynamicAnimation.SCROLL_X,
                //         // mFlingAnimation.setDuration(Math.abs(mMenuWidth - scrollX))
                //         //         .start();
                //         // Log.d(TAG, "onTouchEvent: -MIN_VELOCITY, scrolled=" + mItem
                //         .getScrollX());
                //
                //     }
                // }
                // // else {
                // //     // reset
                // //     // mItem.scrollBy(-scrollX, 0);
                // //     /*Log.d(TAG, "onTouchEvent: -absV");
                // //     if (scrollX > 0) {
                // //         // 加速速率小于最小速率，但已滑过距离>=一半的width, continue scrolling
                // //         if (scrollX >= mMenuWidth / 2) {
                // //             // mItem.scrollBy(mMenuWidth - scrollX, 0);
                // //             mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                // //                     scrollX, mMenuWidth);
                // //             mFlingAnimation.setInterpolator(new
                // AccelerateDecelerateInterpolator());
                // //             // DynamicAnimation.SCROLL_X,
                // //             mFlingAnimation.setDuration(Math.abs(mMenuWidth - scrollX))
                // //                     .start();
                // //         } else {
                // //             // reset
                // //             // mItem.scrollBy(-scrollX, 0);
                // //             mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                // //                     -scrollX, 0);
                // //             mFlingAnimation.setInterpolator(new
                // AccelerateDecelerateInterpolator());
                // //             // DynamicAnimation.SCROLL_X,
                // //             mFlingAnimation.setDuration(Math.abs(scrollX))
                // //                     .start();
                // //         }
                // //     } else {
                // //         if (scrollX <= -(mMenuWidth / 2)) {
                // //             // mItem.scrollBy(-(mMenuWidth + scrollX), 0);
                // //             mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                // //                     -(mMenuWidth + scrollX), 0);
                // //             mFlingAnimation.setInterpolator(new
                // AccelerateDecelerateInterpolator());
                // //             // DynamicAnimation.SCROLL_X,
                // //             mFlingAnimation.setDuration(Math.abs(mMenuWidth + scrollX))
                // //                     .start();
                // //         } else {
                // //             // reset
                // //             // mItem.scrollBy(-scrollX, 0);
                // //             mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                // //                     -scrollX, 0);
                // //             mFlingAnimation.setInterpolator(new
                // AccelerateDecelerateInterpolator());
                // //             // DynamicAnimation.SCROLL_X,
                // //             mFlingAnimation.setDuration(Math.abs(scrollX))
                // //                     .start();
                // //         }
                // //     }*/
                // // }
                //


                // int scrolled = mItem.getScrollX();
                // if (scrolled > -mMenuWidth && scrolled < mMenuWidth) {
                //     if (dx < 0) {
                //         // 正在右滑，可能是关闭操作，可能是已处于关闭状态下随意的右滑操作
                //         int end = scrolled + dx;
                //         if (end > mMenuWidth) {
                //             end = mMenuWidth;
                //         }
                //         mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                //                 scrolled, end);
                //     } else if (dx > 0) {
                //         // 正在左滑，可能是打开操作，可能是已处于打开状态下随意的左滑操作
                //     }
                //     int end = scrolled + dx;
                //     if (end > mMenuWidth) {
                //         end = mMenuWidth;
                //     }
                //     mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                //             scrolled, end);
                //     mFlingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                //     // DynamicAnimation.SCROLL_X,
                //     mFlingAnimation.setInterpolator(new AccelerateInterpolator());
                //     mFlingAnimation.setDuration(mMenuWidth)
                //             .start();
                // }
                //
                //
                //
                // // if (mItem.getScrollX() + dx < mMenuWidth
                // //         && mItem.getScrollX() + dx > 0 /* reset from left to right*/ ) {
                // //     mItem.scrollBy(dx, 0);
                // // }
                mVelocityTracker.clear();
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            default:
                break;
        }
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        Log.d(TAG, "onRequestDisallowInterceptTouchEvent: " + disallowIntercept);
    }

    private void closeItem() {
        String strLog = mItem != null ? mItem.getScrollX() + "" : "null";
        Log.d(TAG, "closeItem: ScrollX=" + strLog);
        if (mItem != null) {
            // 未拦截到删除滑动事件时，mFlingAnimation未空
            if (mFlingAnimation != null && mFlingAnimation.isRunning()) {
                mFlingAnimation.cancel();
            }

            int sX = mItem.getScrollX();
            if (sX != 0) {
                ViewGroup view = mItem;
                mClosingAnimation = ObjectAnimator.ofInt(view, "scrollX",
                        0);
                mClosingAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
                // DynamicAnimation.SCROLL_X,
                mClosingAnimation.setInterpolator(new AccelerateInterpolator());
                mClosingAnimation.setDuration(mMenuWidth)
                        .start();
            }

            /*if (sX > 0) {
                mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                        mMenuWidth, 0);
            } else if (mInitialTouchX < mLastTouchX) {
                mFlingAnimation = ObjectAnimator.ofInt(mItem, "scrollX",
                        -mMenuWidth, 0);
            }*/

        }
    }
}
