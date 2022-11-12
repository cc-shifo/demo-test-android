/*
 * ************************************************************
 * 文件：EventWrapper.java  模块：ElegantBus.core.main  项目：ElegantBus
 * 当前修改时间：2022年09月12日 17:58:58
 * 上次修改时间：2022年09月12日 17:47:29
 * 作者：Cody.yi   https://github.com/codyer
 *
 * 描述：ElegantBus.core.main
 * Copyright (c) 2022
 * ************************************************************
 */

package com.example.demoeventbus.delegatebus;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 进程间缓存事件封装类，事件定义
 */
public class EventWrapper implements Parcelable {
    public static final Creator<EventWrapper> CREATOR = new Creator<EventWrapper>() {
        @Override
        public EventWrapper createFromParcel(Parcel in) {
            return new EventWrapper(in);
        }

        @Override
        public EventWrapper[] newArray(int size) {
            return new EventWrapper[size];
        }
    };
    // 发送事件到某个分组
    String group;
    // 发送的事件名
    String event;
    // 发送的事件类型
    String type;

    public EventWrapper(final String group, final String event, final String type) {
        this.group = group;
        this.event = event;
        this.type = type;
    }


    protected EventWrapper(Parcel in) {
        group = in.readString();
        event = in.readString();
        type = in.readString();
    }

    /**
     * 获取唯一值确定一个事件
     *
     * @return key
     */
    String getKey() {
        return group + event + type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeString(group);
        dest.writeString(event);
        dest.writeString(type);
    }
}