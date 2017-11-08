package com.lm.qqdot;

/**
 * Created by lm on 2017/11/5.
 */

public interface QQDotListener {
    /**
     * @param x 当前在屏幕上x坐标
     * @param y 当前在屏幕上y坐标
     */
    void onMove(float x, float y);

    /**
     * 拉动距离太小，重置回原来位置
     */
    void onReset();

    /**
     * @param x 消失位置在屏幕上x坐标
     * @param y 消失位置在屏幕上y坐标
     */
    void onDisappear(float x, float y);

    /**
     * 按下
     *
     * @param x 按下位置在屏幕上x坐标
     * @param y 按下位置在屏幕上y坐标
     */
    void onDown(float x, float y);
}
