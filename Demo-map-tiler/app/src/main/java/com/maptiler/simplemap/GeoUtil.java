package com.maptiler.simplemap;

import androidx.annotation.NonNull;

import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.ArrayList;
import java.util.List;

public class GeoUtil {

    private GeoUtil() {
        // nothing
    }

    public static LineString lineFrom(@NonNull List<LatLng> list) {
        return LineString.fromLngLats(getLineLngLats(list));
    }

    public static List<Point> getLineLngLats(@NonNull List<LatLng> point3DList) {
        List<Point> latLngList = new ArrayList<>(point3DList.size());
        for (LatLng latLng : point3DList) {
            latLngList.add(Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude(),
                    latLng.getAltitude()));
        }
        return latLngList;
    }
}
