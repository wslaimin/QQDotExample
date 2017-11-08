package com.lm.qqdot;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by lm on 2017/10/26.
 */

public class QQDot extends FrameLayout {
    //默认圆点半径
    private static final int DEFAULT_RADIUS = 26;
    //默认控制点比例
    private static final float CONTROL_PERCENT = 0.618f;
    //静态显示
    private TextView mTextView;
    //拖动显示
    private QQDotView mQQDotView;
    //圆点半径
    private int mDotRadius;
    private WindowManager mWm;
    private QQDotListener mQQDotListener;

    public QQDot(Context context) {
        this(context, null);
    }

    public QQDot(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mTextView = new TextView(context);
        mTextView.setClickable(true);
        mTextView.setTextColor(ContextCompat.getColor(getContext(),android.R.color.white));
        mQQDotView = new QQDotView(context);
        mWm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.QQDot);
        mQQDotView.setControlPercent(a.getFloat(R.styleable.QQDot_control_percent, CONTROL_PERCENT));
        mDotRadius = a.getInt(R.styleable.QQDot_radius, DEFAULT_RADIUS);
        mQQDotView.setRadius(mDotRadius);
        a.recycle();

        mTextView.setGravity(Gravity.CENTER);
        mTextView.post(new Runnable() {
            @Override
            public void run() {
                //设置背景色
                GradientDrawable textBackground = new GradientDrawable();
                textBackground.setColor(0xFFFF0000);
                textBackground.setCornerRadius(mDotRadius);
                textBackground.setShape(GradientDrawable.OVAL);
                mTextView.setBackground(textBackground);

                //设置初始点,要考虑状态栏高度
                Rect r = new Rect();
                mTextView.getWindowVisibleDisplayFrame(r);
                int statusBarHeight = r.top;
                mTextView.getGlobalVisibleRect(r);
                int x = r.centerX();
                int y = r.centerY() - statusBarHeight;
                mQQDotView.setStickPoint(x, y);
                mQQDotView.setStatusBarHeight(statusBarHeight);

                //触摸代理给QQDotView
                r.top = 0;
                r.left = 0;
                r.right = mTextView.getMeasuredWidth();
                r.bottom = mTextView.getMeasuredHeight();

                mTextView.setTouchDelegate(new TouchDelegate(r, mQQDotView));
            }
        });

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.gravity = Gravity.CENTER;
        addView(mTextView, layoutParams);

        final WindowManager.LayoutParams qqDotViewParams = new WindowManager.LayoutParams();
        qqDotViewParams.format = PixelFormat.TRANSLUCENT;
        mQQDotView.setQQDotListener(new OnQQDotViewListener() {

            @Override
            public void onDown(float x,float y) {
                //不允许父容器拦截事件,因为在ListView中拖动ListView会发生滚动
                requestDisallowInterceptTouchEvent(true);
                if (mQQDotView.getParent() == null) {
                    mWm.addView(mQQDotView, qqDotViewParams);
                }
                if(mQQDotListener!=null){
                    mQQDotListener.onDown(x,y);
                }
                mTextView.setVisibility(INVISIBLE);
            }

            @Override
            public void onMove(float x, float y) {
                if(mQQDotListener!=null){
                    mQQDotListener.onMove(x,y);
                }
            }

            @Override
            public void onDisappear(float x, float y) {
                mWm.removeView(mQQDotView);
                WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, 2, 0, PixelFormat.TRANSLUCENT);
                //很重要，view放置的起点位置，不设置，消失动画位置会发生错误
                params.gravity = Gravity.LEFT | Gravity.TOP;
                AnimationDrawable drawable = (AnimationDrawable) ContextCompat.getDrawable(getContext(), R.drawable.anim_disappear);
                params.x = (int) x - drawable.getIntrinsicWidth() / 2;
                params.y = (int) y - drawable.getIntrinsicHeight() / 2;
                final ImageView imageView = new ImageView(getContext());
                imageView.setImageDrawable(drawable);
                mWm.addView(imageView, params);

                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mWm.removeView(imageView);
                    }
                },501);

                drawable.start();

                if(mQQDotListener!=null){
                    mQQDotListener.onDisappear(x,y);
                }
            }

            @Override
            public void onReset(float x,float y) {
                mTextView.setVisibility(VISIBLE);
                mWm.removeView(mQQDotView);


                //回弹动画
                AnimatorSet set=new AnimatorSet();
                //拖动圆点与固定圆点x方向距离,要同一坐标系
                Rect rect=new Rect();
                mTextView.getGlobalVisibleRect(rect);
                float offsetX=x-rect.centerX();
                ObjectAnimator xAnimator1=ObjectAnimator.ofFloat(mTextView,"translationX",offsetX,0);
                xAnimator1.setDuration(60);
                //相反方向动画距离为圆距离一半
                ObjectAnimator xAnimator2=ObjectAnimator.ofFloat(mTextView,"translationX",0,-offsetX*0.5f);
                xAnimator2.setRepeatMode(ValueAnimator.REVERSE);
                xAnimator2.setRepeatCount(1);
                xAnimator2.setDuration(60);

                //拖动圆点与固定点y方向距离
                float offSetY=y-rect.centerY();
                ObjectAnimator yAnimator1=ObjectAnimator.ofFloat(mTextView,"translationY",offSetY,0);
                yAnimator1.setDuration(60);

                ObjectAnimator yAnimator2=ObjectAnimator.ofFloat(mTextView,"translationY",0,-offSetY*0.5f);
                yAnimator2.setRepeatMode(ValueAnimator.REVERSE);
                yAnimator2.setRepeatCount(1);
                yAnimator2.setDuration(60);

                AnimatorSet set1=new AnimatorSet();
                set1.playTogether(xAnimator1,yAnimator1);
                AnimatorSet set2=new AnimatorSet();
                set2.playTogether(xAnimator2,yAnimator2);
                set.playSequentially(
                    set1,set2
                );
                set.start();

            }
        });

    }

    public void setDotText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setQQDotListener(QQDotListener listener){
        mQQDotListener=listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width = widthSize, height = heightSize;

        if (widthMode == MeasureSpec.AT_MOST || heightMode == MeasureSpec.AT_MOST) {
            width = height = 2 * mDotRadius;
        }

        widthMeasureSpec = MeasureSpec.makeMeasureSpec(2 * mDotRadius, MeasureSpec.EXACTLY);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(2 * mDotRadius, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        setMeasuredDimension(width, height);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //ListView由于View复用，需要在按下动作时，更新QQDotView的stickPoint坐标
        if(ev.getAction()==MotionEvent.ACTION_DOWN) {
            Rect r = new Rect();
            mTextView.getWindowVisibleDisplayFrame(r);
            int statusBarHeight = r.top;
            mTextView.getGlobalVisibleRect(r);
            int x = r.centerX();
            int y = r.centerY() - statusBarHeight;
            mQQDotView.setStickPoint(x, y);
        }
        return super.dispatchTouchEvent(ev);
    }

    private static class QQDotView extends View {
        //初始状态
        public static final int INITIAL=0;
        //界内拖动状态
        public static final int INNER = 1;
        //脱离最远距离状态
        public static final int OUTER = 2;
        //界内拖动最远距离
        public static final int MAX_INNER_DISTANCE = 120;
        //画笔
        private Paint mPaint;
        //贝塞尔曲线路径
        private Path mPath;
        //初始位置
        private PointF mStickPoint;
        //拖动位置
        private PointF mDragPoint;
        //控制点比例
        private float mControlPercent;
        //圆半径
        private int mRadius;
        //圆点状态
        private int mState;
        //状态栏高度
        private int mStatusBarHeight;
        private OnQQDotViewListener mListener;

        public QQDotView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setColor(0xFFFF0000);
            mPaint.setAntiAlias(true);
            mPath = new Path();

            mStickPoint = new PointF();
            mDragPoint = new PointF();
            setClickable(true);
        }


        private void setControlPercent(float percent) {
            mControlPercent = percent;
        }

        private void setRadius(int radius) {
            mRadius = radius;
        }

        private void setQQDotListener(OnQQDotViewListener listener) {
            mListener = listener;
        }

        private void setStickPoint(float x, float y) {
            mStickPoint.x = x;
            mStickPoint.y = y;
        }

        private void setStatusBarHeight(int height) {
            mStatusBarHeight = height;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            switch (mState) {
                case INITIAL:
                    canvas.drawCircle(mStickPoint.x,mStickPoint.y,mRadius,mPaint);
                    break;
                case INNER:

                    double degree = GeometryUtils.inclinationAngle(mStickPoint.x, mStickPoint.y, mDragPoint.x, mDragPoint.y);

                    //radiusFraction=0.3+0.5*(1-distance/MAX_INNER_DISTANCE)
                    double radiusFraction=0.8-0.5*GeometryUtils.getDistance(mStickPoint.x,mStickPoint.y,mDragPoint.x,mDragPoint.y)/MAX_INNER_DISTANCE;
                    //stickRadius范围mradius的30%-80%
                    double stickRadius=mRadius*radiusFraction;
                    PointF[] stickPoints = GeometryUtils.getIntersectionPoints(mStickPoint.x, mStickPoint.y, stickRadius, degree);
                    PointF[] dragPoints = GeometryUtils.getIntersectionPoints(mDragPoint.x, mDragPoint.y, mRadius, degree);

                    PointF controlPoint = GeometryUtils.getControlPoint(mStickPoint.x, mStickPoint.y, mDragPoint.x, mDragPoint.y, mControlPercent);

                    canvas.drawCircle(mStickPoint.x, mStickPoint.y, (float) stickRadius, mPaint);
                    canvas.drawCircle(mDragPoint.x, mDragPoint.y, mRadius, mPaint);

                    mPath.reset();
                    mPath.moveTo(stickPoints[0].x, stickPoints[0].y);
                    mPath.quadTo(controlPoint.x, controlPoint.y, dragPoints[0].x, dragPoints[0].y);

                    mPath.lineTo(dragPoints[1].x, dragPoints[1].y);
                    mPath.quadTo(controlPoint.x, controlPoint.y, stickPoints[1].x, stickPoints[1].y);
                    canvas.drawPath(mPath, mPaint);
                    break;
                case OUTER:
                    canvas.drawCircle(mDragPoint.x, mDragPoint.y, mRadius, mPaint);
                    break;
                default:
                    break;
            }
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //添加到WindowManager
                    mListener.onDown(event.getRawX(),event.getRawY()-mStatusBarHeight);
                    break;

                case MotionEvent.ACTION_MOVE:
                    mDragPoint.x = event.getRawX();
                    mDragPoint.y = event.getRawY() - mStatusBarHeight;
                    //超出范围后只显示拖动圆
                    if((mState&OUTER)==0){
                        mState = GeometryUtils.getDistance(mStickPoint.x, mStickPoint.y, mDragPoint.x, mDragPoint.y) > MAX_INNER_DISTANCE ? OUTER : INNER;
                    }
                    mListener.onMove(mDragPoint.x, mDragPoint.y);
                    invalidate();
                    break;

                case MotionEvent.ACTION_UP:
                    //更新mState到最新状态
                    mState = GeometryUtils.getDistance(mStickPoint.x, mStickPoint.y, mDragPoint.x, mDragPoint.y) > MAX_INNER_DISTANCE ? OUTER : INNER;
                    switch (mState) {
                        case INNER:
                            float nowX=mDragPoint.x;
                            float nowY=mDragPoint.y+mStatusBarHeight;
                            mDragPoint.x = mStickPoint.x;
                            mDragPoint.y = mStickPoint.y;
                            invalidate();
                            mListener.onReset(nowX,nowY);
                            break;

                        case OUTER:
                            //消失动画
                            mListener.onDisappear(event.getRawX(), event.getRawY() - mStatusBarHeight);
                            break;
                    }
                    break;
                default:
                    break;
            }

            return true;
        }
    }

}
