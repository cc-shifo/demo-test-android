package com.amap.map3d.demo.coord;

public class XY extends Coord {
    /**
     * 空间直角坐标系，地理平面直角坐标系的x轴坐标
     */
    public double mX;
    /**
     * 空间直角坐标系，地理平面直角坐标系的y轴坐标
     */
    public double mY;

    public XY() {
        this(Double.NaN, Double.NaN);
    }

    public XY(double x, double y) {
        mX = x;
        mY = y;
    }
}
