package com.demo.demosafeonclicklistener;

import android.os.SystemClock;
import android.view.View;

/**
 * 给两次点击事件设置时间间隔，默认时间间隔是500ms。大于时间间隔的点击事件才可以传输给View。
 */
public abstract class OnClickFilter implements View.OnClickListener {
    /**
     * 默认时间间隔是500ms
     */
    private static final int INTERVAL_TIME_MS = 500;
    /**
     * 无效时间。
     */
    private static final int INVALID_TIME_STAMP = -1;
    /**
     * 两次点击的最下间隔时间
     */
    private final int mInterval;
    /**
     * 上一次点击事件记录的时间戳，单位毫秒。
     */
    private long mPriorTime;
    private long mCount;

    /**
     * 采用默认时间间隔
     */
    protected OnClickFilter() {
        this(INTERVAL_TIME_MS);
    }

    /**
     * @param filter 两次点击的最小间隔时间。单位毫秒。小于等于0将使用默认时间间隔。
     */
    protected OnClickFilter(int filter) {
        mInterval = filter > 0 ? filter : INTERVAL_TIME_MS;
        mPriorTime = INVALID_TIME_STAMP;
    }

    @Override
    public void onClick(View v) {
        mCount++;
        long current = SystemClock.elapsedRealtime();
        if (mPriorTime == INVALID_TIME_STAMP) {
            mPriorTime =  current;
            onFilter(v);
        } else {
            long t = current - mPriorTime;
            mPriorTime = current;
            if (t > mInterval) {
                onFilter(v);
            }
        }
    }

    /**
     * 大于时间间隔时点击事件时的回调。
     * @param view 被点击的view。
     */
    public abstract void onFilter(View view);
}
