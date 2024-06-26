package com.example.demomultiviewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.demomultiviewmodel.base.BaseViewModelFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ┌-------→Level1ViewModel1
 * │
 * {@link MainViewModel}----┤
 * │
 * └-------→Level1ViewModel2
 */
public class MainViewModel extends ViewModel {
    private Level1ViewModel1 mLevel1ViewModel1;
    private Level1ViewModel2 mLevel1ViewModel2;
    private MutableLiveData<String> mText;
    private ScheduledExecutorService mExecutorService = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> mScheduledFuture;

    private Runnable mRepeatRunnable;
    private long mCount;

    public MainViewModel(@NonNull ViewModelStore viewModelStore) {
        mText = new MutableLiveData<>();
        // mLevel1ViewModel1 = new ViewModelProvider(owner.getViewModelStore(),
        //         BaseViewModelFactory.getInstance()).get(Level1ViewModel1.class);
        // mLevel1ViewModel2 = new ViewModelProvider(owner.getViewModelStore(),
        //         BaseViewModelFactory.getInstance()).get(Level1ViewModel2.class);
    }

    /**
     * 拉取数据
     */
    public void getText() {
        initTextGenerator();
        mScheduledFuture = mExecutorService.scheduleAtFixedRate(mRepeatRunnable,
                100, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * 监听数据
     */
    @NonNull
    public LiveData<String> observeText() {
        return mText;
    }

    /**
     * 监听Level1ViewModel1数据
     */
    public LiveData<String> observeLevel1ViewModel1Text() {
        return mLevel1ViewModel1.observeText();
    }

    /**
     * 监听Level1ViewModel2数据
     */
    public LiveData<String> observeLevel1ViewModel2Text() {
        return mLevel1ViewModel2.observeText();
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
