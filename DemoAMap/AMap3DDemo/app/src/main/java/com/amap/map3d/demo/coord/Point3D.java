package com.amap.map3d.demo.coord;

/**
 * 用于描述wgs84坐标系下的数据，经纬高。单位为度。
 */
public class Point3D extends Coord {
    /**
     * 纬度，单位度。
     */
    public double mLat;
    /**
     * 经度，单位度。
     */
    public double mLng;
    /**
     * 高，单位度。
     */
    public double mHeight;

    public Point3D() {
        this(Double.NaN, Double.NaN);
    }

    public Point3D(double lat, double lng) {
        this(lat, lng, Double.NaN);
    }

    public Point3D(double lat, double lng, double height) {
        mLat = lat;
        mLng = lng;
        mHeight = height;
    }
}
