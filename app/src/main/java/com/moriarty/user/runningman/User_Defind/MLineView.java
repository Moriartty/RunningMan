package com.moriarty.user.runningman.User_Defind;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.v4.view.ViewCompat;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.moriarty.user.runningman.Object.MovementData;
import com.moriarty.user.runningman.Utils.TimeUtil;
import com.moriarty.user.runningman.Utils.Utils;

import java.util.ArrayList;

/**
 * Created by user on 17-8-25.
 */
public class MLineView extends View {
    private final float density;
    private int height,width;
    Data[] datas;
    private Path valuePath=new Path();
    private float percent = 0f;
    private final TextPaint paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    public MLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        density = context.getResources().getDisplayMetrics().density;
        if(isInEditMode()){
            return ;
        }
        init(context);
    }
    public class Data{
        public boolean isOverAverage;
        public int value;
        public String date;
        public float valueOffsetPercent;
    }
    private void init(Context context) {
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(1f * density);
        paint.setTextSize(10f * density);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextAlign(Paint.Align.CENTER);
        // paint.setTypeface(MainActivity.getTypeface(context));
    }
    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        if(isInEditMode()){
            return ;
        }
        paint.setStyle(Paint.Style.FILL);
        //一共需要 顶部文字2(+图占8行)+底部文字2+间距1 + 日期1 + 底部边距1f 】 = 15行
        final float textSize = this.height / 15f;
        paint.setTextSize(textSize);
        final float textOffset = getTextPaintOffset(paint);
        final float dH = textSize * 8f;
        final float dCenterY = textSize * 6f ;
        if (datas == null || datas.length <1) {
            canvas.drawLine(0, dCenterY, this.width, dCenterY, paint);//没有数据的情况下只画一条线
            return;
        }
        else if(datas.length==1){
            final Data d = datas[0];
            float x,y;
            x =  this.width / 2f;
            y = dCenterY - d.valueOffsetPercent*dH;
            canvas.drawText(Integer.toString(d.value),x,y-textSize+textOffset,paint);
            canvas.drawText(TimeUtil.prettyDate(d.date),x, textSize * 13.5f + textOffset, paint);
            canvas.drawPoint(x,y,paint);
            canvas.drawLine(0, dCenterY, this.width, dCenterY, paint);
        }
        final float dW = this.width * 1f / datas.length;
        valuePath.reset();
        final int length = datas.length;
        float[] x = new float[length];
        float[] y=new float[length];
        final float textPercent = (percent >= 0.6f) ? ((percent - 0.6f) / 0.4f) : 0f;
        final float pathPercent = (percent >= 0.6f) ? 1f : (percent / 0.6f);
        //画底部的三行文字和标注最高最低温度
        paint.setAlpha((int) (255 * textPercent));
        for (int i = 0; i < length; i++) {
            final Data d = datas[i];
            x[i] = i * dW + dW / 2f;
            y[i] = dCenterY - d.valueOffsetPercent*dH;
            if(d.isOverAverage)
                canvas.drawText(Integer.toString(d.value),x[i],y[i]-textSize+textOffset,paint);
            else
                canvas.drawText(Integer.toString(d.value),x[i],y[i]+textSize+textOffset,paint);
            canvas.drawText(TimeUtil.prettyDate(d.date),x[i], textSize * 13.5f + textOffset, paint);
            canvas.drawPoint(x[i],y[i],paint);
        }
        paint.setAlpha(255);
        for (int i = 0; i < (length - 1); i++) {
            final float midX = (x[i] + x[i + 1]) / 2f;
            final float midY = (y[i] + y[i + 1]) / 2f;
           // final float midYMax = (yMax[i] + yMax[i + 1]) / 2f;
           // final float midYMin = (yMin[i] + yMin[i + 1]) / 2f;
            if(i == 0){
                valuePath.moveTo(0,y[i]);
               // tmpMaxPath.moveTo(0, yMax[i]);
               // tmpMinPath.moveTo(0, yMin[i]);
            }
            //cubicTo用于绘制贝塞尔曲线
            valuePath.cubicTo(x[i]-1,y[i],x[i],y[i],midX,midY);
            if(i == (length - 2)){
                valuePath.cubicTo(x[i+1]-1,y[i+1],x[i+1],y[i+1],this.width,y[i+1]);
            }
        }
        //draw max_tmp and min_tmp path
        paint.setStyle(Paint.Style.STROKE);
        final boolean needClip = pathPercent < 1f;
        if(needClip){
            canvas.save();
            canvas.clipRect( 0 , 0, this.width * pathPercent, this.height);
//canvas.drawColor(0x66ffffff);
        }
        canvas.drawPath(valuePath,paint);
        if(needClip){
            canvas.restore();
        }
        if(percent < 1){
            percent += 0.025f;// 0.025f;
            percent = Math.min(percent, 1f);
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public void setData(ArrayList<MovementData> movementDatas){
        if(movementDatas!=null&&movementDatas.size()>0){
            datas=new Data[movementDatas.size()];
            try {
                int all_max = Integer.MIN_VALUE;
                int all_min = Integer.MAX_VALUE;
                for (int i = 0; i < movementDatas.size(); i++) {
                    int max = movementDatas.get(i).getWalk_Step();
                    int min = movementDatas.get(i).getWalk_Step();
                    if (all_max < max)
                        all_max = max;
                    if (all_min > min)
                        all_min = min;
                    final Data data = new Data();
                    data.value= movementDatas.get(i).getWalk_Step();
                    data.date = movementDatas.get(i).getTime();
                    datas[i] = data;
                }
                float all_distance = Math.abs(all_max - all_min);
                float average_distance = (all_max + all_min) / 2f;
                if(movementDatas.size()>1){
                    for (Data d : datas) {
                        d.valueOffsetPercent = (d.value - average_distance) /all_distance;
                        if(d.value>=average_distance)
                            d.isOverAverage=true;
                        else
                            d.isOverAverage=false;
                    }
                }
                else {
                    datas[0].valueOffsetPercent=0;
                    datas[0].isOverAverage=true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            percent = 0f;
            invalidate();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.width = w;
        this.height = h;
    }
    public static float getTextPaintOffset(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return -(fontMetrics.bottom - fontMetrics.top) / 2f - fontMetrics.top;
    }
}
