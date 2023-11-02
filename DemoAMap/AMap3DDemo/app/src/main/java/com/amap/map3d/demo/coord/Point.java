package com.amap.map3d.demo.coord;

public class Point extends Coord {
    /**
     * 纬度，单位度。
     */
    public double mLat;
    /**
     * 经度，单位度。
     */
    public double mLng;

    public Point() {
        this(Double.NaN, Double.NaN);
    }

    public Point(double lat, double lng) {
        mLat = lat;
        mLng = lng;
    }

    public double getLat() {
        return mLat;
    }

    public void setLat(double lat) {
        mLat = lat;
    }

    public double getLng() {
        return mLng;
    }

    public void setLng(double lng) {
        mLng = lng;
    }
}
