/*
 * ************************************************************
 * 文件：ActiveLiveDataWrapper.java  模块：ElegantBus.core.main  项目：ElegantBus
 * 当前修改时间：2022年09月12日 17:58:58
 * 上次修改时间：2022年09月12日 17:47:29
 * 作者：Cody.yi   https://github.com/codyer
 *
 * 描述：ElegantBus.core.main
 * Copyright (c) 2022
 * ************************************************************
 */

package com.example.demoeventbus.delegatebus;

import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * 每添加一个observer，LiveDataWrapper 的序列号增加1，并赋值给新加的observer，
 * 每次消息更新使用目前的序列号进行请求，持有更小的序列号才需要获取变更通知。
 * <p>
 * 解决会收到注册前发送的消息更新问题
 */
@SuppressWarnings("unused")
public class ImplLiveDataWrapper<T> implements ILiveDataWrapper<T> {
    private final List<ObserverWrapper<T>> mObserverWrappers;
    private final MutableLiveData<ValueWrapper<T>> mMutableLiveData;
    private int mSequence = 0;
    private int mStickySequence = -1;
    private EventWrapper mEventWrapper;

    ImplLiveDataWrapper() {
        mObserverWrappers = new ArrayList<>();
        mMutableLiveData = new MutableLiveData<>();
    }

    ImplLiveDataWrapper(final EventWrapper eventWrapper) {
        mEventWrapper = eventWrapper;
        mObserverWrappers = new ArrayList<>();
        mMutableLiveData = new MutableLiveData<>();
    }

    /**
     * 获取最后保留的值。
     *
     * @return 获取最后保留的值
     */
    @Nullable
    @Override
    public T getValue() {
        if (mMutableLiveData.getValue() == null) {
            return null;
        }
        return mMutableLiveData.getValue().value;
    }

    /**
     * 如果在多线程中调用，保留每一个值
     * 无需关心调用线程，只要确保在相同进程中就可以
     *
     * @param value 需要更新的值
     */
    @Override
    public void post(@NonNull final T value) {
        checkThread(() -> mMutableLiveData.setValue(new ValueWrapper<>(value, mSequence)));
    }

    /**
     * 监听事件。在此监听设置之前发送的消息，此监听收不到。
     *
     * @param owner           生命周期拥有者
     * @param observerWrapper 观察者包装类
     */
    @Override
    public void observe(@NonNull LifecycleOwner owner,
                        @NonNull ObserverWrapper<T> observerWrapper) {
        checkThread(() -> doObserver(owner, observerWrapper));
    }

    /**
     * 和生命周期无关，全生命周期一直都监听，不用的时候需要用户自己取消监听
     *
     * @param observerWrapper 观察者包装类
     */
    @Override
    public void observeForever(@NonNull final ObserverWrapper<T> observerWrapper) {
        checkThread(() -> doObserver(null, observerWrapper));
    }

    /**
     * 粘性事件，设置监听之前发送的消息也可以接收到
     * 重写 observer 的函数 isSticky ，返回true，可以实现粘性事件
     *
     * @param owner           生命周期拥有者
     * @param observerWrapper 观察者包装类
     * @see #observe(LifecycleOwner, ObserverWrapper)
     */
    @Override
    public void observeSticky(@NonNull LifecycleOwner owner,
                              @NonNull ObserverWrapper<T> observerWrapper) {
        if (!observerWrapper.sticky) {
            throw new IllegalStateException("observerWrapper.sticky must be true");
        }
        observe(owner, observerWrapper);
    }


    /**
     * 重置 Sticky 序列，确保这之后添加的监听，之前的值不回调
     */
    @Override
    public void resetSticky() {
        mStickySequence = mSequence;
        mSequence++;
    }

    /**
     * 主动取消观察
     *
     * @param observerWrapper 观察者包装类
     */
    @Override
    public void removeObserver(@NonNull ObserverWrapper<T> observerWrapper) {
        checkThread(() -> {
            mObserverWrappers.remove(observerWrapper);
            mMutableLiveData.removeObserver(observerWrapper.observer);
        });
    }

    /**
     * 移除某个生命周期拥有者的所有观察者
     *
     * @param owner 生命周期拥有者
     */
    @Override
    public void removeObservers(@NonNull LifecycleOwner owner) {
        checkThread(() -> {
            Iterator<ObserverWrapper<T>> iterator = mObserverWrappers.iterator();
            while (iterator.hasNext()) {
                ObserverWrapper<T> next = iterator.next();
                if (next.owner == owner) {
                    iterator.remove();
                }
            }
            mMutableLiveData.removeObservers(owner);
        });
    }

    /**
     * 是否有观察者
     *
     * @return 是否有观察者
     */
    @Override
    public boolean hasObservers() {
        return mMutableLiveData.hasObservers();
    }

    /**
     * 是否有激活的观察者
     *
     * @return 是否有激活的观察者
     */
    @Override
    public boolean hasActiveObservers() {
        return mMutableLiveData.hasActiveObservers();
    }

    private void doObserver(LifecycleOwner owner, @NonNull ObserverWrapper<T> observerWrapper) {
        observerWrapper.sequence = observerWrapper.sticky ? mStickySequence : mSequence++;
        observerWrapper.owner = owner;
        Observer<ValueWrapper<T>> observer = filterObserver(observerWrapper);
        int i = mObserverWrappers.size();
        for (; i > 0; i--) {
            ObserverWrapper<T> previous = mObserverWrappers.get(i - 1);
            mMutableLiveData.removeObserver(previous.observer);
        }
        mObserverWrappers.add(i, observerWrapper);
        for (; i < mObserverWrappers.size(); i++) {
            ObserverWrapper<T> next = mObserverWrappers.get(i);
            if (next.owner == null) {
                mMutableLiveData.observeForever(next.observer);
            } else {
                mMutableLiveData.observe(next.owner, next.observer);
            }
        }
    }

    /**
     * 从包装类中过滤出原始观察者
     *
     * @param observerWrapper 包装类
     * @return 原始观察者
     */
    @NonNull
    private Observer<ValueWrapper<T>> filterObserver(
            @NonNull final ObserverWrapper<T> observerWrapper) {
        if (observerWrapper.observer != null) {
            return observerWrapper.observer;
        }

        observerWrapper.observer = tValueWrapper -> {
            // 产生的事件序号要大于观察者序号才被通知事件变化
            if (tValueWrapper != null && tValueWrapper.sequence > observerWrapper.sequence) {
                if (observerWrapper.uiThread) {
                    observerWrapper.onChanged(tValueWrapper.value);
                } else {
                    BusFactory
                            .ready()
                            .getExecutorService()
                            .execute(() -> observerWrapper.onChanged(tValueWrapper.value));
                }
            }
        };

        return observerWrapper.observer;
    }

    /**
     * 是否是在主线程
     *
     * @return 是主线程
     */
    private boolean isMainThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * 检查线程并执行不同的操作
     *
     * @param runnable 可运行的一段代码
     */
    private void checkThread(Runnable runnable) {
        if (isMainThread()) {
            runnable.run();
        } else {
            // 从当前线程切换到主线程处理runnable
            BusFactory
                    .ready()
                    .getMainHandler()
                    .post(runnable);
        }
    }
}
