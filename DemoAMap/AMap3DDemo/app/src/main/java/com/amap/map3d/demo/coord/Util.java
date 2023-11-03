package com.amap.map3d.demo.coord;

import android.support.annotation.NonNull;

import java.security.InvalidParameterException;
import java.util.List;

public class Util {

    /**
     * 计算p1，p2两个点的中点坐标。
     *
     * @param p1 坐标单位度。
     * @param p2 坐标单位度。
     */
    @NonNull
    public Point calCenter(@NonNull Point p1, @NonNull Point p2) {
        return new Point((p1.mLat + p2.mLat) / 2, (p1.mLng + p2.mLng) / 2);
    }

    /**
     * 计算p1，p2两个点的中点坐标。
     *
     * @param p1 坐标单位米。
     * @param p2 坐标单位米。
     */
    @NonNull
    public XY calCenter(@NonNull XY p1, @NonNull XY p2) {
        return new XY((p1.mX + p2.mX) / 2, (p1.mY + p2.mY) / 2);
    }

    /**
     * 计算p1，p2两个点的中点坐标。
     *
     * @param p1 坐标单位度。
     * @param p2 坐标单位度。
     */
    @NonNull
    public Point3D calCenter3D(@NonNull Point3D p1, @NonNull Point3D p2) {
        return new Point3D((p1.mLat + p2.mLat) / 2, (p1.mLng + p2.mLng) / 2,
                (p1.mHeight + p2.mHeight) / 2);
    }

    /**
     * 计算p1，p2两个点的中点坐标。
     *
     * @param p1 坐标单位米。
     * @param p2 坐标单位米。
     */
    @NonNull
    public XYZ calCenter3D(@NonNull XYZ p1, @NonNull XYZ p2) {
        return new XYZ((p1.mX + p2.mX) / 2, (p1.mY + p2.mY) / 2, (p1.mZ + p2.mZ) / 2);
    }


    /**
     * 根据视场角，航高，得到航带宽度，然后根据旁向重叠率转换出航线间隔。
     * 重叠率计算规则：重叠部分长度 / 航带带宽
     *
     * @param angle   眼睛（激光器）能看到的视场角，单位度。
     * @param height  眼睛（激光器）观看的高度，单位米。
     * @param percent 计算两条航带，旁向重叠率。
     * @return 两条航带之间的间距，单位米。
     */
    public int getStripInterval(float angle, float height, int percent) {
        double a = height * Math.tan(angle / 2.0) * 2.0;
        return (int) (a * (1.0 - percent / 100.0));
    }

    /**
     * 旁向重叠率，计算两条航带（平行）之间重叠的那部分（垂直与航线）线段长度占航线间距的百分比。
     * 重叠率计算规则：重叠部分长度 / 航带带宽
     *
     * @param angle    眼睛（激光器）能看到的视场角，单位度。
     * @param height   眼睛（激光器）观看的高度，单位米。
     * @param interval 两条航带之间的间距，单位米。。
     * @return 旁向重叠率。用百分比表示。
     */
    public int getLateralOverlapRate(float angle, float height, int interval) {
        double a = height * Math.tan(angle / 2.0) * 2.0;
        return Math.round((float) ((a - interval) / a * 100));
    }

    /**
     * 计算二维线段的长度。
     *
     * @param x1 线段的顶点1。x坐标，单位米。
     * @param y1 线段的顶点1。y坐标，单位米。
     * @param x2 线段的顶点2。x坐标，单位米。
     * @param y2 线段的顶点2。y坐标，单位米。
     * @return 线段的长度，单位米。
     */
    public double calDist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
    }

    /**
     * 计算二维三角形的面积。
     * <p>
     * 向量a = (x1, x2, x3, ..., xn)，和向量b =  (y1, y2, y3, ..., yn)的点积公式为：
     * 向量点积：a*b = x1*y1 + x2*y2 + ... + xn*yn
     *
     * @param p1 三角形的顶点。顶点为二维坐标，坐标单位米。
     * @param p2 三角形的顶点。顶点为二维坐标，坐标单位米。
     * @param p3 三角形的顶点。顶点为二维坐标，坐标单位米。
     * @return 多边形的面积。单位平方米。
     */
    private double calArea(@NonNull XY p1, @NonNull XY p2, @NonNull XY p3) {
        double x2 = p2.mX - p1.mX;
        double y2 = p2.mY - p1.mY;
        double x1 = p3.mX - p1.mX;
        double y1 = p3.mY - p1.mY;
        return Math.abs(x1 * y2 - x2 * y1) / 2;
    }

    /**
     * 计算二维多边形的面积。
     *
     * @param list 多边形的顶点。顶点为二维坐标，坐标单位米。
     * @return 多边形的面积。单位平方米。
     */
    public double calArea(@NonNull List<XY> list) {
        if (list.size() < 3) {
            return 0;
        }

        double a = 0;
        double x0 = list.get(0).mX;
        double y0 = list.get(0).mY;
        for (int i = 2; i < list.size(); i++) {
            XY p2 = list.get(i);
            XY p1 = list.get(i - 1);
            double x2 = p2.mX - x0;
            double y2 = p2.mY - y0;
            double x1 = p1.mX - x0;
            double y1 = p1.mY - y0;
            a += Math.abs(x1 * y2 - x2 * y1);
        }

        return a / 2;
    }

    /**
     * 计算三维线段的长度。
     *
     * @param x1 线段的顶点1。x坐标，单位米。
     * @param y1 线段的顶点1。y坐标，单位米。
     * @param z1 线段的顶点1。z坐标，单位米。
     * @param x2 线段的顶点2。x坐标，单位米。
     * @param y2 线段的顶点2。y坐标，单位米。
     * @param z2 线段的顶点2。z坐标，单位米。
     * @return 线段的长度，单位米。
     */
    public double calDist3D(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2)
                + Math.pow(Math.abs(z1 - z2), 2));
    }

    /**
     * 计算三维多边形的面积。
     *
     * @param list 多边形的顶点。顶点为三维坐标，坐标单位米。
     * @return 多边形的面积。
     * 向量a(x1,y1,z1)，向量b(x2,y2,z2)
     * <p>
     * i        j       k
     * x1       y1      z1
     * x2       y2      z2
     * a x b = (y1*z2  - y2*z1) * i - (x1*z1 - x2*z1) * j + (x1*y2 - x2*y1) * k
     * 等于c向量，c(y1*z2  - y2*z1, - (x1*z1 - x2*z1), (x1*y2 - x2*y1))
     * c向量的模等于sqrt(x²+y²+z²)
     * 面积等于c向量模长的1/2。
     */
    public double calArea3D(@NonNull List<XYZ> list) {
        if (list.size() < 3) {
            return 0;
        }
        double a = 0;
        double x0 = list.get(0).mX;
        double y0 = list.get(0).mY;
        double z0 = list.get(0).mZ;
        for (int i = 2; i < list.size(); i++) {
            XYZ p2 = list.get(i);
            XYZ p1 = list.get(i - 1);
            double x2 = p2.mX - x0;
            double y2 = p2.mY - y0;
            double z2 = p2.mZ - z0;
            double x1 = p1.mX - x0;
            double y1 = p1.mY - y0;
            double z1 = p1.mZ - z0;
            a += Math.sqrt(Math.pow(Math.abs((y1 * z2 - y2 * z1)), 2) + Math.pow(Math.abs(x1 * z1
                    - x2 * z1), 2) + Math.pow(Math.abs(x1 * y2 - x2 * y1), 2));
        }
        return a / 2;
    }

    /**
     * (x1,y1)表示向量v1，(x2,y2)表示向量v2，求向量v1和v2的夹角，角度范围0~180°
     *
     * @param x1 表示v1点x轴坐标
     * @param y1 表示v1点y轴坐标
     * @param x2 表示v2点x轴坐标
     * @param y2 表示v2点y轴坐标
     * @return 向量的夹角。单位度。
     */
    public double getVecAngle(double x1, double y1, double x2, double y2) {
        double a = x1 * x2 + y1 * y2;
        double m = Math.sqrt(x1 * x1 + y1 * y1);
        double n = Math.sqrt(x2 * x2 + y2 * y2);

        if (m == 0 || n == 0 || Double.isNaN(x1) || Double.isNaN(y1)
                || Double.isNaN(x2) || Double.isNaN(y2)) {
            throw new InvalidParameterException();
        }
        return Math.toDegrees(Math.acos(a / (m * m)));
    }


    /**
     * (x1,y1,z1)表示向量v1，(x2,y2,z2)表示向量v2，求向量v1和v2的夹角，角度范围0~180°
     *
     * @param x1 表示v1点x轴坐标
     * @param y1 表示v1点y轴坐标
     * @param z1 表示v1点z轴坐标
     * @param x2 表示v2点x轴坐标
     * @param y2 表示v2点y轴坐标
     * @param z2 表示v2点z轴坐标
     * @return 向量的夹角。单位度。
     */
    public double getVecAngle(double x1, double y1, double z1, double x2, double y2, double z2) {
        double a = x1 * x2 + y1 * y2 + z1 * z2;
        double m = Math.sqrt(x1 * x1 + y1 * y1 + z1 * z1);
        double n = Math.sqrt(x2 * x2 + y2 * y2 + z1 * z2);

        if (m == 0 || n == 0 || Double.isNaN(x1) || Double.isNaN(y1) || Double.isNaN(z1)
                || Double.isNaN(x2) || Double.isNaN(y2) || Double.isNaN(z2)) {
            throw new InvalidParameterException();
        }
        return Math.toDegrees(Math.acos(a / (m * m)));
    }
}
