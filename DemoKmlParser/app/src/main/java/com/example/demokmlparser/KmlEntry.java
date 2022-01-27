/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20220118 	         LiuJian                  Create
 */

package com.example.demokmlparser;

import android.graphics.Point;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

public class KmlEntry {
    private String name;
    private int mType;
    /**
     * wgs84 coordination
     */
    private List<Point> mPointList;

    public KmlEntry() {
        // nothing
    }

    public KmlEntry(@Nullable String name, int type, @Nullable List<Point> pointList) {
        this.name = name;
        mType = type;
        mPointList = pointList != null ? pointList : new ArrayList<>(0);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getType() {
        return mType;
    }

    public void setType(@GeometryType int type) {
        mType = type;
    }

    public List<Point> getPointList() {
        return mPointList;
    }

    public void setPointList(List<Point> pointList) {
        mPointList = pointList;
    }

    @IntDef(value = {GeometryType.NONE, GeometryType.POINT,
            GeometryType.LINE, GeometryType.POLYGON})
    public @interface GeometryType {
        int NONE = 0;
        int POINT = 1;
        int LINE = 2;
        int POLYGON = 3;
    }
}
