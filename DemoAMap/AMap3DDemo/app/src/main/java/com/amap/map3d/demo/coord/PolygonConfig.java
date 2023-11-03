package com.amap.map3d.demo.coord;

public class PolygonConfig {
    /**
     * 航线间隔。从横向重叠率得到。
     */
    private short mInterval;

    /**
     * 旋转角。单位位度，范围0°~360°，指北为0°
     */
    private short mRotation;

    /**
     * 外扩距离，及外边距。当前算法，只能扩折线。
     */
    private short mMargin;
}
