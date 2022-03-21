package com.example.demoarcgis;

import com.esri.arcgisruntime.geometry.Polyline;
import com.esri.arcgisruntime.mapping.Viewpoint;

public class HistoricalTrack {
    private Polyline mPolyline;
    private Viewpoint mViewpoint;

    public HistoricalTrack() {
    }

    public HistoricalTrack(Polyline polyline, Viewpoint viewpoint) {
        mPolyline = polyline;
        mViewpoint = viewpoint;
    }

    public Polyline getPolyline() {
        return mPolyline;
    }

    public void setPolyline(Polyline polyline) {
        mPolyline = polyline;
    }

    public Viewpoint getViewpoint() {
        return mViewpoint;
    }

    public void setViewpoint(Viewpoint viewpoint) {
        mViewpoint = viewpoint;
    }
}
