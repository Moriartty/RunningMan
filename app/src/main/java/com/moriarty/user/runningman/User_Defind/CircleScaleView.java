package com.moriarty.user.runningman.User_Defind;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Pedometer.CaloriesNotifier;
import com.moriarty.user.runningman.R;

/**
 * Created by user on 17-9-4.
 */
public class CircleScaleView extends View{
    private static final String TAG = "CircleScaleView";
    private int mRadius;
    private int Color1;
    private int Color2;
    private int Color3;

    private Paint mPaint;
    private float mCircleWidth;
    private int mWidth;
    private int mHeight;
    private RectF mRectF;
   /* private float lifeSweepAngle;
    private float communicateSweep;
    private float trafficSweep;
    private float entertainmentSweep;*/

    private float walkSweepAngle;
    private float runSweepAngle;
    private float rideSweepAngle;

    private float walkPercent;
    private float runPercent;
    private float ridePercent;
    public CircleScaleView(Context context) {
        this(context,null);
    }
    public CircleScaleView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }
    public CircleScaleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleScaleView,defStyleAttr,0 );
        int n = array.getIndexCount();
        for (int i = 0;i<n;i++){
            int attr = array.getIndex(i);
            switch (attr){
                case R.styleable.CircleScaleView_Color1:
                    Color1 = array.getColor(attr, Color.parseColor("#fac62d"));
                    break;
                case R.styleable.CircleScaleView_Color2:
                    Color2 = array.getColor(attr, Color.parseColor("#65cff6"));
                    break;
                case R.styleable.CircleScaleView_Color3:
                    Color3 = array.getColor(attr, Color.parseColor("#fe9a9c"));
                    break;
                case R.styleable.CircleScaleView_circleWidth:
                    mCircleWidth = array.getDimensionPixelSize(attr,dip2px(context,20));
                    break;
                case R.styleable.CircleScaleView_radius:
                    mRadius = array.getDimensionPixelSize(attr,dip2px(context,20));
                    break;
            }
        }
        array.recycle();
        initPaint();
    }
    private void initPaint(){
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }
    /*public void setCostPercent(float liftCost,float trafficCost,float communicateCost,float entertainmentCost){
        float total = liftCost + trafficCost + communicateCost + entertainmentCost;
        lifePercent = liftCost/total;
        trafficPercent = trafficCost/total;
        communicatePercent = communicateCost/total;
        entertainmentPercent = entertainmentCost/total;
        invalidate();//重绘
    }*/
    public void setScalePercent(int[] percent){
        walkPercent = (float)percent[0]/100;
        runPercent =(float)percent[1]/100;
        ridePercent =(float)percent[2]/100;
        invalidate();
    }

   /* public void setScalePercent(float walkDistance,float runDistance,float rideDistance){
        double walkCost = walkDistance * CaloriesNotifier.METRIC_WALKING_FACTOR;
        double runCost = runDistance * CaloriesNotifier.METRIC_RUNNING_FACTOR;
        double rideCost = rideDistance * CaloriesNotifier.METRIC_RIDING_FACTOR;
        double total = walkCost + runCost + rideCost;
        walkPercent = (float) (walkCost / total);
        Log.d(MainActivity.TAG,"walkPercent:"+walkPercent);
        runPercent = (float) (runCost / total);
        Log.d(MainActivity.TAG,"runPercent:"+runPercent);
        ridePercent = (float) (rideCost / total);
        Log.d(MainActivity.TAG,"ridePercent"+ridePercent);
        invalidate();
    }*/
    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setStrokeWidth(mCircleWidth);
        mRectF = new RectF(mCircleWidth/2,mCircleWidth/2,mRadius*2-mCircleWidth/2,mRadius*2-mCircleWidth/2);
        float startAngle = -90;
        walkSweepAngle = 360 * walkPercent;
        mPaint.setColor(Color1);
        canvas.drawArc(mRectF,startAngle,walkSweepAngle,false,mPaint);

        startAngle = startAngle + walkSweepAngle;
        runSweepAngle = 360 * runPercent;
        mPaint.setColor(Color2);
        canvas.drawArc(mRectF,startAngle,runSweepAngle,false,mPaint);

        startAngle = startAngle + runSweepAngle;
        rideSweepAngle = 360*ridePercent;
        mPaint.setColor(Color3);
        canvas.drawArc(mRectF,startAngle,rideSweepAngle,false,mPaint);
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * 设置宽度
         */
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            Log.e("xxx", "EXACTLY");
            mWidth = (int) (specSize + mCircleWidth);
        } else
        {
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mWidth = (int) (mRadius*2);
                Log.e("xxx", "AT_MOST");
            }
        }
        /***
         * 设置高度
         */
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY)// match_parent , accurate
        {
            mHeight = (int) (specSize+mCircleWidth);
        } else
        {
            if (specMode == MeasureSpec.AT_MOST)// wrap_content
            {
                mHeight = (int) (mRadius*2);
            }
        }
        setMeasuredDimension(mWidth+10, mHeight+10);
    }
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
