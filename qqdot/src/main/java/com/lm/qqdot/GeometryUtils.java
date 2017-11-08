package com.lm.qqdot;

import android.graphics.PointF;

/**
 * Created by lm on 2017/10/26.
 */

public class GeometryUtils {
    /**
     * @param x1 第一个点横坐标
     * @param y1 第一个点纵坐标
     * @param x2 第二个点横坐标
     * @param y2 第二个点纵坐标
     * @return 倾斜角的弧度表示
     */
    public static double inclinationAngle(double x1, double y1, double x2, double y2) {
        if (x2 - x1 == 0) {
            return Math.PI / 2;
        } else {
            return Math.atan(1d * (y2 - y1) / (x2 - x1));
        }
    }

    /**
     * @param centerX 圆心x坐标
     * @param centerY 圆心y坐标
     * @param radius  圆半径
     * @param degree  直线的倾斜角(弧度表示)
     * @return 垂直倾斜角为degree直线与圆交点
     */
    public static PointF[] getIntersectionPoints(double centerX, double centerY, double radius, double degree) {
        PointF[] pointF = new PointF[2];
        pointF[0] = new PointF();
        pointF[0].x = (float) (centerX - radius * Math.sin(degree));
        pointF[0].y = (float) (centerY + radius * Math.cos(degree));

        pointF[1] = new PointF();
        pointF[1].x = (float) (centerX + radius * Math.sin(degree));
        pointF[1].y = (float) (centerY - radius * Math.cos(degree));
        return pointF;
    }

    /**
     * @param x1 初始点x坐标
     * @param y1 初始点y坐标
     * @param x2 移动后点x坐标
     * @param y2 移动后y坐标
     * @param t  选取控制点点比例
     * @return 控制点
     */
    public static PointF getControlPoint(double x1, double y1, double x2, double y2, double t) {
        PointF pointF = new PointF();
        pointF.x = (float) (x1 + (x2 - x1) * t);
        pointF.y = (float) (y1 + (y2 - y1) * t);
        return pointF;
    }

    //计算两点间的距离
    public static double getDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

}
