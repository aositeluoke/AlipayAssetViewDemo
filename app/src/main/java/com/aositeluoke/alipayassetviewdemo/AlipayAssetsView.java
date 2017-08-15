package com.aositeluoke.alipayassetviewdemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;



/**
 * 类描述:
 * 作者:xues
 * 时间:2017年08月05日
 */

public class AlipayAssetsView extends View {

    private static final String TAG = "AlipayAssetsView";

    private int mFirstSquareColor;//第一种颜色
    private int mSecondSquareColor;//第二种颜色
    private int mThirdSquareColor;//第三种颜色
    private int mFourthSquareColor;//第四种颜色
    private RotateAnimation mSweepAnim;//扫描动画

    private RectF mGrayRingRectF;//灰色圆环矩形
    private RectF mColorRingRectF;//五颜六色矩形
    private Paint mColorRingPaint;//五颜六色画笔
    private Paint mCirclePaint;//白色圆形画笔
    private Paint mGrayRingPaint;//灰色圆环画笔
    private int mRingWidth;//圆环宽度

    private float mFirstSweep, mSecondSweep, mThirdSweep, mFourSweep;//每一种颜色对应的度数
    private float mTotalPri;//总金额
    private float mCenterX;//中心点x坐标
    private float mSweepAngle = 360;//每次绘制的度数

    public AlipayAssetsView(Context context) {
        this(context, null);
    }

    public AlipayAssetsView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AlipayAssetsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.AlipayAssetsView, defStyleAttr, 0);
        mFirstSquareColor = a.getColor(R.styleable.AlipayAssetsView_first_color, Color.parseColor("#faa62a"));//第一种方块颜色
        mSecondSquareColor = a.getColor(R.styleable.AlipayAssetsView_second_color, Color.parseColor("#f26a55"));//第二种颜色
        mThirdSquareColor = a.getColor(R.styleable.AlipayAssetsView_third_color, Color.parseColor("#5295fd"));//第三种颜色
        mFourthSquareColor = a.getColor(R.styleable.AlipayAssetsView_fourth_color, Color.parseColor("#2268b0"));//第四种颜色
        mRingWidth = a.getDimensionPixelSize(R.styleable.AlipayAssetsView_ring_width, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                16, getResources().getDisplayMetrics()));
        a.recycle();

        //初始化动画并设置动画持续时间为1000毫秒==1秒
        mSweepAnim = new RotateAnimation();
        mSweepAnim.setDuration(1000);
        initPaint();
    }


    /**
     * 初始化画笔
     */
    private void initPaint() {
        //白色圆形画笔
        mCirclePaint = new Paint();
        mCirclePaint.setColor(getResources().getColor(android.R.color.white));
        mCirclePaint.setAntiAlias(true);
        mCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCirclePaint.setStrokeWidth(0f);
        //灰色圆环画笔
        mGrayRingPaint = new Paint();
        mGrayRingPaint.setColor(Color.parseColor("#f4f4f4"));
        mGrayRingPaint.setStrokeJoin(Paint.Join.ROUND);
        mGrayRingPaint.setStyle(Paint.Style.STROKE);
        mGrayRingPaint.setAntiAlias(true);
        mGrayRingPaint.setStrokeWidth(mRingWidth);


        //五颜六色圆环画笔
        mColorRingPaint = new Paint();
        mColorRingPaint.setColor(mFirstSquareColor);
        mColorRingPaint.setStrokeJoin(Paint.Join.ROUND);
        mColorRingPaint.setStyle(Paint.Style.STROKE);
        mColorRingPaint.setAntiAlias(true);
        mColorRingPaint.setStrokeWidth(mRingWidth);
    }


    /**
     * 初始化圆形(centerX在onMeasure方法执行完后，才赋值，因此该方法只能测量完后再调用)
     */
    private void initRectF() {
        //灰色圆环矩形
        mGrayRingRectF = new RectF(mRingWidth / 2, mRingWidth / 2, 2 * mCenterX - mRingWidth / 2, 2 * mCenterX - mRingWidth / 2);
        //五颜六色圆环矩形
        mColorRingRectF = new RectF(mRingWidth / 2, mRingWidth / 2, 2 * mCenterX - mRingWidth / 2, 2 * mCenterX - mRingWidth / 2);
    }


    /**
     * 设置价格
     *
     * @param mfirstPri  余额
     * @param mSecondPri 余额宝
     * @param mThirdPri  定期
     * @param mFourthPri 基金
     */
    public void setPri(float mfirstPri, float mSecondPri, float mThirdPri, float mFourthPri) {
        mTotalPri = mfirstPri + mSecondPri + mThirdPri + mFourthPri;//总价
        mFirstSweep = (mfirstPri / (mTotalPri + 0.0f)) * 360;//第一种总度数
        mSecondSweep = (mSecondPri / (mTotalPri + 0.0f)) * 360;//第二种总度数
        mThirdSweep = (mThirdPri / (mTotalPri + 0.0f)) * 360;//第三种总度数
        mFourSweep = (mFourthPri / (mTotalPri + 0.0f)) * 360;//第四种总度数
        startAnimation(mSweepAnim);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int wSize = 0;


        //match_parent or set value
        if (widthMode == MeasureSpec.EXACTLY) {
            wSize = widthSize;
        } else {
            //wrap_content
            if (widthMode == MeasureSpec.AT_MOST) {
                wSize = getPaddingLeft() + getPaddingRight();
            }
        }


        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int hSize = 0;

        //match_parent or set value
        if (heightMode == MeasureSpec.EXACTLY) {
            hSize = heightSize;
        } else {
            //wrap_content
            if (heightMode == MeasureSpec.AT_MOST) {
            }
        }
        //测量结束后，获取控件宽度的中心
        mCenterX = wSize / 2;
        initRectF();//初始化灰色圆环和多颜色圆环矩形
        setMeasuredDimension(wSize, hSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制灰色圆环
        drawGrayRing(canvas);
        //绘制白色圆形
        drawCircle(canvas);
        drawColorRing(canvas);
    }

    /**
     * 绘制灰色圆环
     *
     * @param canvas
     */
    private void drawGrayRing(Canvas canvas) {
        canvas.drawArc(mGrayRingRectF, -90, 360, false, mGrayRingPaint);
    }

    /**
     * 绘制中心白色圆形
     *
     * @param canvas
     */
    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterX, (getWidth() - 2 * mRingWidth) / 2f, mCirclePaint);
    }


    /**
     * 绘制五颜六色的圆环
     *
     * @param canvas
     */
    private void drawColorRing(Canvas canvas) {
        if (mTotalPri <= 0)
            return;
        //当前时间要绘制的角度小于等于第一种颜色的总度数
        if (mSweepAngle <= mFirstSweep) {
            //第一种颜色
            mColorRingPaint.setColor(mFirstSquareColor);
            canvas.drawArc(mColorRingRectF, -90, mSweepAngle, false, mColorRingPaint);
        } else if (mSweepAngle > mFirstSweep && mSweepAngle <= mFirstSweep + mSecondSweep) {
            //当前时间要绘制的角度大于第一种颜色的总度数 小于等于第二种颜色总度数
            mColorRingPaint.setColor(mFirstSquareColor);
            canvas.drawArc(mColorRingRectF, -90, mFirstSweep, false, mColorRingPaint);
            mColorRingPaint.setColor(mSecondSquareColor);
            canvas.drawArc(mColorRingRectF, -90 + mFirstSweep, mSweepAngle - mFirstSweep, false, mColorRingPaint);
        } else if (mSweepAngle > mSecondSweep + mFirstSweep && mSweepAngle <= mSecondSweep + mFirstSweep + mThirdSweep) {
            //当前时间要绘制的角度大于第二种颜色的总度数 小于等于第三种颜色总度数
            mColorRingPaint.setColor(mFirstSquareColor);
            canvas.drawArc(mColorRingRectF, -90, mFirstSweep, false, mColorRingPaint);
            mColorRingPaint.setColor(mSecondSquareColor);
            canvas.drawArc(mColorRingRectF, -90 + mFirstSweep, mSweepAngle - mFirstSweep, false, mColorRingPaint);
            mColorRingPaint.setColor(mThirdSquareColor);
            canvas.drawArc(mColorRingRectF, -90 + mFirstSweep + mSecondSweep, mSweepAngle - mFirstSweep - mSecondSweep, false, mColorRingPaint);
        } else {
            // 当前时间要绘制的角度大于第三种颜色的总度数 小于等于第四种颜色总度数
            mColorRingPaint.setColor(mFirstSquareColor);
            canvas.drawArc(mColorRingRectF, -90, mFirstSweep, false, mColorRingPaint);
            mColorRingPaint.setColor(mSecondSquareColor);
            canvas.drawArc(mColorRingRectF, -90 + mFirstSweep, mSweepAngle - mFirstSweep, false, mColorRingPaint);
            mColorRingPaint.setColor(mThirdSquareColor);
            canvas.drawArc(mColorRingRectF, -90 + mFirstSweep + mSecondSweep, mSweepAngle - mFirstSweep - mSecondSweep, false, mColorRingPaint);
            mColorRingPaint.setColor(mFourthSquareColor);
            canvas.drawArc(mColorRingRectF, -90 + mFirstSweep + mSecondSweep + mThirdSweep, mSweepAngle - mFirstSweep - mSecondSweep - mThirdSweep, false, mColorRingPaint);
        }
    }


    /**
     * 自定义旋转动画
     */
    public class RotateAnimation extends Animation {
        /**
         * Initializes expand collapse animation, has two types, collapse (1) and expand (0).
         */
        public RotateAnimation() {
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            //interpolatedTime 范围：0到1
            mSweepAngle = interpolatedTime * 360;//当前时间扫描的角度
            postInvalidate();//重绘
        }
    }
}
