package com.amap.map3d.demo.coord;

public class Point2D extends Coord {
    private double mLat;
    private double mLng;
    private double mHeight;

    public Point2D() {
        this(Double.NaN, Double.NaN);
    }

    public Point2D(double lat, double lng) {
        this(lat, lng, Double.NaN);
    }

    public Point2D(double lat, double lng, double height) {
        mLat = lat;
        mLng = lng;
        mHeight = height;
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

    public double getHeight() {
        return mHeight;
    }

    public void setHeight(double height) {
        mHeight = height;
    }
}
