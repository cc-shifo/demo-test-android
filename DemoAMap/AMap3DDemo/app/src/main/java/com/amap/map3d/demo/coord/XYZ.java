package com.amap.map3d.demo.coord;

public class XYZ extends Coord {
    /**
     * 空间直角坐标系，地理平面直角坐标系的x轴坐标
     */
    public double mX;
    /**
     * 空间直角坐标系，地理平面直角坐标系的y轴坐标
     */
    public double mY;
    /**
     * 空间直角坐标系，地理平面直角坐标系的z轴坐标
     */
    public double mZ;

    public XYZ() {
        this(Double.NaN, Double.NaN, Double.NaN);
    }

    public XYZ(double x, double y) {
        this(x, y, Double.NaN);
    }

    public XYZ(double x, double y, double z) {
        mX = x;
        mY = y;
        mZ = z;
    }
}