package com.example.demoarcgis;

import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.mapping.Viewpoint;

public class TrackData {
    private Polyline mPolyline;
    private double mLatitude;
    private double mLongitude;
    private double mHeight;

    private Viewpoint mViewpoint;

    public TrackData() {
        // nothing
    }

    public Polyline getPolyline() {
        return mPolyline;
    }

    public void setPolyline(Polyline polyline) {
        mPolyline = polyline;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }

    public double getHeight() {
        return mHeight;
    }

    public void setHeight(double height) {
        mHeight = height;
    }

    public Viewpoint getViewpoint() {
        return mViewpoint;
    }

    public void setViewpoint(Viewpoint viewpoint) {
        mViewpoint = viewpoint;
    }
}
