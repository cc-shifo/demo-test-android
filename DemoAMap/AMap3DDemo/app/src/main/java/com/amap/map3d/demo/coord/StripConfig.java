package com.amap.map3d.demo.coord;

import android.os.Parcel;
import android.os.Parcelable;

public class StripConfig implements Parcelable {
    /**
     * 航线间隔。从横向重叠率得到。
     */
    private short mInterval;

    /**
     * 旋转角。单位位度，范围0°~360°，指北为0°
     */
    private short mRotation;

    /**
     * 外扩（两边同时扩）航线条数。
     * 当前算法，用户先后输入点的连线确定为中间线，中间线两边扩展n条线的行为称为外扩几条线的行为。
     */
    private short mLineCnt;

    // 起始点首尾调换
    // 转弯半径

    public StripConfig(short interval, short rotation, short lineCnt) {
        mInterval = interval;
        mRotation = rotation;
        mLineCnt = lineCnt;
    }

    protected StripConfig(Parcel in) {
        mInterval = (short) in.readInt();
        mRotation = (short) in.readInt();
        mLineCnt = (short) in.readInt();
    }

    public static final Creator<StripConfig> CREATOR = new Creator<StripConfig>() {
        @Override
        public StripConfig createFromParcel(Parcel in) {
            return new StripConfig(in);
        }

        @Override
        public StripConfig[] newArray(int size) {
            return new StripConfig[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mInterval);
        dest.writeInt(mRotation);
        dest.writeInt(mLineCnt);
    }

    /**
     * 航线间隔。从横向重叠率得到。
     */
    public short getInterval() {
        return mInterval;
    }

    /**
     * 航线间隔。从横向重叠率得到。
     */
    public void setInterval(short interval) {
        mInterval = interval;
    }

    /**
     * 旋转角。单位位度，范围0°~360°，指北为0°
     */
    public short getRotation() {
        return mRotation;
    }

    /**
     * 旋转角。单位位度，范围0°~360°，指北为0°
     */
    public void setRotation(short rotation) {
        mRotation = rotation;
    }

    /**
     * 外扩（两边同时扩）航线条数。
     * 当前算法，用户先后输入点的连线确定为中间线，中间线两边扩展n条线的行为称为外扩几条线的行为。
     */
    public short getLineCnt() {
        return mLineCnt;
    }

    /**
     * 外扩（两边同时扩）航线条数。
     * 当前算法，用户先后输入点的连线确定为中间线，中间线两边扩展n条线的行为称为外扩几条线的行为。
     */
    public void setLineCnt(short lineCnt) {
        mLineCnt = lineCnt;
    }
}
