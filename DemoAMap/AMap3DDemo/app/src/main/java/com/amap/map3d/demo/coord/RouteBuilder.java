package com.amap.map3d.demo.coord;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * 数学坐标系（笛卡尔坐标系），x正轴表示0°角，y正轴90°角。从0°逆时针方向，依次角度范围正0~360°。
 *    y ↑
 *      │
 *   2  │   第1象限
 *      │           x
 * ─────┼────────────→
 *   3  │   4
 *      │
 *
 * 地理平面坐标系，x正轴表示0°角，指向北；y正轴90°角，指向东。从0°顺时针方向，依次角度范围正0~360°。
 *    x ↑
 *      │
 *   4  │   第1象限
 *      │           y
 * ─────┼────────────→
 *   3  │   2
 *      │
 */
public class RouteBuilder {

    /**
     * 计算多边形的外接矩形。外接矩形的一对比平行与x轴，另外一对平行与y轴。
     * @param points wgs84坐标系下的点。
     * @return 多边形的外接矩形的四个顶点的坐标。
     */
    private Rect getBound(@NonNull List<Point3D> points) {
        double top = 0.0;
        double left = 0.0;
        double right = 0.0;
        double bottom = 0.0;
        for (Point3D point : points) {
            XYZ xyz = SphericalWebMctProj.toWebMct(point);
            top = Math.max(xyz.mX, top);
            left = Math.min(xyz.mY, left);
            right = Math.max(xyz.mY , right);
            bottom = Math.min(xyz.mX, bottom);
        }

        return new Rect(top, left, right, bottom);
    }


    /**
     * 区域航线规划用的外接矩形。矩形的一对比平行与x轴，另外一对平行与y轴。
     * 坐标数据为web墨卡托数据
     * (top,left)               (top,right)
     * ┌────────────────────┐
     * │                    │
     * │                    │
     * └────────────────────┘
     * (bottom,left)            (bottom,right)
     */
    private static class Rect {
        @SuppressWarnings("PublicField")
        public double mTop;
        @SuppressWarnings("PublicField")
        public double mLeft;
        @SuppressWarnings("PublicField")
        public double mRight;
        @SuppressWarnings("PublicField")
        public double mBottom;

        public Rect(double top, double left, double right, double bottom) {
            mTop = top;
            mLeft = left;
            mRight = right;
            mBottom = bottom;
        }
    }
}
