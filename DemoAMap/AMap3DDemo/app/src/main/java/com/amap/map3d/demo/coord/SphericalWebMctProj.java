/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.amap.map3d.demo.coord;

import android.support.annotation.NonNull;

/**
 * web mercator 与 wsg84坐标相互转换
 */
public class SphericalWebMctProj {
    /**
     * 6378137 赤道半径。
     */
    private static final double SEMI_PERIMETER = Math.PI * 6378137;

    /**
     * wgs84 to web mercator projection
     *
     * 平面坐标x = 经度*20037508.34/180
     * 平面坐标y = log（tan（（90+纬度）*PI/360））/（PI/360）*20037508.34/180
     * @param lat wgs84坐标，纬度
     * @param lng wgs84坐标，经度
     * @return web墨卡托坐标
     */
    public static XYZ toWebMct(double lat, double lng) {
        return toWebMct(lat, lng, Double.NaN);
    }

    /**
     * wgs84 to web mercator projection
     *
     * 平面坐标x = 经度*20037508.34/180
     * 平面坐标y = log（tan（（90+纬度）*PI/360））/（PI/360）*20037508.34/180
     * @param latLng wgs84坐标
     * @return web墨卡托坐标，z为height。
     */
    public static XYZ toWebMct(@NonNull Point3D latLng) {
        return toWebMct(latLng.mLat, latLng.mLng, latLng.mHeight);
    }

    /**
     * wgs84 to web mercator projection
     *
     * 平面坐标x = 经度*20037508.34/180
     * 平面坐标y = log（tan（（90+纬度）*PI/360））/（PI/360）*20037508.34/180
     * @param lat wgs84坐标，纬度
     * @param lng wgs84坐标，经度
     * @param height wgs84坐标，z值。可以取是海拔高，椭球高
     * @return web墨卡托坐标
     */
    public static XYZ toWebMct(double lat, double lng, double height) {
        double x = lng * SEMI_PERIMETER / 180;
        double y = Math.log(Math.tan((90 + lat) * Math.PI / 360)) / (Math.PI / 180);
        y = y * SEMI_PERIMETER / 180;
        return new XYZ(x, y, height);
    }

    /**
     * web mercator to wgs84
     * 经度= 平面坐标x/20037508.34*180
     * 纬度= 180/(PI*(2*atan(exp(平面坐标y/20037508.34*180*PI/180))-PI/2)
     *
     * @param x web mercator x coordinate
     * @param y web mercator y coordinate
     * @return wgs84
     */
    public static Point3D toWGS84(double x, double y) {
        return toWGS84(x, y, Double.NaN);
    }

    /**
     * web mercator to wgs84
     * 经度= 平面坐标x/20037508.34*180
     * 纬度= 180/(PI*(2*atan(exp(平面坐标y/20037508.34*180*PI/180))-PI/2)
     *
     * @param xyz web mercator
     * @return wgs84
     */
    public static Point3D toWGS84(@NonNull XYZ xyz) {
        return toWGS84(xyz.mX, xyz.mY, xyz.mZ);
    }

    /**
     * web mercator to wgs84
     * 经度= 平面坐标x/20037508.34*180
     * 纬度= 180/(PI*(2*atan(exp(平面坐标y/20037508.34*180*PI/180))-PI/2)
     *
     * @param x web mercator x coordinate
     * @param y web mercator y coordinate
     * @param z 可以取是海拔高，椭球高
     * @return wgs84
     */
    public static Point3D toWGS84(double x, double y, double z) {
        double lng = x / SEMI_PERIMETER * 180;
        double lat = y / SEMI_PERIMETER * 180;
        lat = 180 / Math.PI * (2 * Math.atan(Math.exp(lat * Math.PI / 180)) - Math.PI / 2);

        return new Point3D(lat, lng, z);
    }
}
