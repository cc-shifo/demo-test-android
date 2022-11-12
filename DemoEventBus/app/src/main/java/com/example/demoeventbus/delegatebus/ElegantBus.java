/*
 * ************************************************************
 * 文件：ElegantBus.java  模块：ElegantBus.core.main  项目：ElegantBus
 * 当前修改时间：2022年09月12日 17:58:58
 * 上次修改时间：2022年09月12日 17:47:29
 * 作者：Cody.yi   https://github.com/codyer
 *
 * 描述：ElegantBus.core.main
 * Copyright (c) 2022
 * ************************************************************
 */

package com.example.demoeventbus.delegatebus;

import androidx.annotation.NonNull;

/**
 * Created by xu.yi. on 2019/3/31.
 * 使用 LiveData 实现类似event bus功能，支持生命周期管理
 */
@SuppressWarnings("unused")
public class ElegantBus {
    private ElegantBus() {
        // nothing
    }

    /**
     * 日志开关
     *
     * @param debug 是否打印日志
     */
    public static void setDebug(final boolean debug) {
        ElegantLog.setDebug(debug);
    }

    /**
     * 获取默认域的事件包装类
     *
     * @param event 事件名
     * @param type  事件类型
     * @param <T>   事件类型
     * @return 默认域的事件包装类
     * <p>
     * 使用此方法需要自己管理事件，重名等问题，不建议使用，建议使用注解自动生成管理类
     */
    public static <T> ILiveDataWrapper<T> getDefault(@NonNull String event, @NonNull Class<T> type) {
        return getDefault("", event, type);
    }

    /**
     * 获取默认域的事件包装类
     *
     * @param group 分组管理
     * @param event 事件名
     * @param type  事件类型
     * @param <T>   事件类型
     * @return 默认域的事件包装类
     * <p>
     * 使用此方法需要自己管理事件，重名等问题，不建议使用，建议使用注解自动生成管理类
     */
    public static <T> ILiveDataWrapper<T> getDefault(@NonNull String group, @NonNull String event,
                                                     @NonNull Class<T> type) {
        return BusFactory.ready()
                .create(new EventWrapper(group, event, type.getName()));
    }
}
