package com.example.demomultiviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Level2ViewModel2Sub1 extends ViewModel {
    private MutableLiveData<String> mText;
    private ScheduledExecutorService mExecutorService = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> mScheduledFuture;

    private Runnable mRepeatRunnable;
    private long mCount;
    public Level2ViewModel2Sub1() {
        mText = new MutableLiveData<>();
    }

    /**
     * 拉取数据
     */
    public void getText() {
        initTextGenerator();
        mScheduledFuture = mExecutorService.scheduleAtFixedRate(mRepeatRunnable,
                100, 1000, TimeUnit.MICROSECONDS);
    }


    /**
     * 监听数据
     */
    @NonNull
    public LiveData<String> observeText() {
        return mText;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopGeneratingText();
        stopExecutorService();
    }

    /**
     * 初始化产生数据逻辑
     */
    private void initTextGenerator() {
        if (mRepeatRunnable == null) {
            mCount = 0;
            mRepeatRunnable = new Runnable() {
                @Override
                public void run() {
                    String s = String.valueOf(mCount++);
                    mText.postValue(s);
                }
            };
        }
    }

    /**
     * 停止产生数据
     */
    private void stopGeneratingText() {
        if (mScheduledFuture != null && !mScheduledFuture.isCancelled() &&
                !mScheduledFuture.isDone()) {
            mScheduledFuture.cancel(true);
            mScheduledFuture = null;
        }
    }

    /**
     * 停止异步服务
     */
    private void stopExecutorService() {
        mExecutorService.shutdownNow();
    }
}
