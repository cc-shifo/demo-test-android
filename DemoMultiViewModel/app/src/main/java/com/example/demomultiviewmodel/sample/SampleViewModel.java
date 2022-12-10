package com.example.demomultiviewmodel.sample;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SampleViewModel extends ViewModel {
    private static final ViewModelProvider.Factory FACTORY = new ViewModelProvider.Factory() {
        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            SampleViewModel viewModel = new SampleViewModel(true);
            return (T) viewModel;
        }
    };

    public static class SampleFactory implements ViewModelProvider.Factory {
        private int mInitValue;
        private boolean mB;

        public void initParameters(int initValue, boolean b) {
            mInitValue = initValue;
            mB = b;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            SampleViewModel viewModel = new SampleViewModel(mInitValue, mB);
            return (T) viewModel;
        }
    }

    @NonNull
    public static SampleViewModel getInstance(ViewModelStore viewModelStore, int value, boolean b) {
        /*ViewModelProvider viewModelProvider = new ViewModelProvider(viewModelStore,
                FACTORY);*/
        SampleFactory factory = new SampleFactory();
        factory.initParameters(value, b);
        ViewModelProvider viewModelProvider = new ViewModelProvider(viewModelStore,
                factory);
        return viewModelProvider.get(SampleViewModel.class);
    }

    private MutableLiveData<String> mText;
    private ScheduledExecutorService mExecutorService = new ScheduledThreadPoolExecutor(1);
    private ScheduledFuture<?> mScheduledFuture;

    private Runnable mRepeatRunnable;
    private long mCount;

    private int mSampleInitValue;
    private boolean mSampleInitB;

    public SampleViewModel(boolean b) {
        mText = new MutableLiveData<>();
        mSampleInitB = b;
    }

    public SampleViewModel(int initValue, boolean b) {
        mText = new MutableLiveData<>();
        mSampleInitValue = initValue;
        mSampleInitB = b;
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
        final String iS = "InitValue: " + mSampleInitValue + ", "
                + String.valueOf(mSampleInitB) + "\n";
        if (mRepeatRunnable == null) {
            mCount = 0;
            mRepeatRunnable = new Runnable() {
                @Override
                public void run() {
                    String s = iS + "SampleViewModel: " + String.valueOf(mCount++);
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
