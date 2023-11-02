package com.amap.map3d.demo.coord;

import java.security.InvalidParameterException;

public class Util {

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
