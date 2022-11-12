/*
 * ************************************************************
 * 文件：ObserverWrapper.java  模块：ElegantBus.core.main  项目：ElegantBus
 * 当前修改时间：2022年09月12日 17:58:58
 * 上次修改时间：2022年09月12日 17:47:28
 * 作者：Cody.yi   https://github.com/codyer
 *
 * 描述：ElegantBus.core.main
 * Copyright (c) 2022
 * ************************************************************
 */

package com.example.demoeventbus.delegatebus;


import androidx.annotation.Nullable;
import androidx.arch.core.executor.ArchTaskExecutor;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

/**
 * Created by xu.yi. on 2019/3/31.
 * 不要主动改变属性值
 */
@SuppressWarnings("unused")
public abstract class ObserverWrapper<T> {
    LifecycleOwner owner;// 没有owner就是forever
    Observer<ValueWrapper<T>> observer;
    // 默认在主线程监听
    final boolean uiThread;
    // 默认不是粘性事件，不会收到监听之前发送的事件
    final boolean sticky;
    // 每个观察者都记录自己序号，只有在进入观察状态之后产生的数据才通知到观察者
    int sequence;

    public ObserverWrapper() {
        this(false, true);
    }

    /**
     * 构造函数
     *
     * @param sticky   是否粘性事件
     * @param uiThread 是否在UI线程监听回调
     */
    public ObserverWrapper(final boolean sticky, final boolean uiThread) {
        this.sticky = sticky;
        this.uiThread = uiThread;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    /**
     * 发生了变化
     *
     * @param value 新的值
     */
    public abstract void onChanged(@Nullable T value);
}
