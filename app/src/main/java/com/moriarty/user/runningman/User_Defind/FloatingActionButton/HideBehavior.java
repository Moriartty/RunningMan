package com.moriarty.user.runningman.User_Defind.FloatingActionButton;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;

/**
 * Created by user on 17-9-22.
 */
public class HideBehavior extends CoordinatorLayout.Behavior{
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private boolean mIsAnimatingOut = false;
    public HideBehavior() {
    }
    public HideBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout
            , View child
            , View directTargetChild
            , View target
            , int nestedScrollAxes) {
        return nestedScrollAxes== ViewGroup.SCROLL_AXIS_VERTICAL||
                super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
                        nestedScrollAxes);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed,
                dyUnconsumed);
        Log.e("onNestedScroll", "进入");
        if(dyConsumed > 0 /*&& ! mIsAnimatingOut && child.getVisibility() == View.VISIBLE*/){
            Log.e("animateOut", "进入");
            //fab划出去
            animateOut(child);
        }else if(dyConsumed < 0 /*&& child.getVisibility() != View.VISIBLE*/){
            Log.e("animateIn", "进入");
            //fab划进来
            animateIn(child);
        }
    }
    private void animateOut(View child) {
        Log.e("animateOut", "进入");
        //属性动画
        //设置监听判断状态
        ViewCompat.animate(child)
                .scaleX(0.0f)
                .scaleY(0.0f)
                .alpha(0.0f)
                .setInterpolator(INTERPOLATOR)
                .setListener(new ViewPropertyAnimatorListenerAdapter(){
                    @Override
                    public void onAnimationStart(View view) {
                        mIsAnimatingOut = true;
                        super.onAnimationStart(view);
                    }
                    @Override
                    public void onAnimationCancel(View view) {
                        mIsAnimatingOut = false;
                        super.onAnimationCancel(view);
                    }
                    @Override
                    public void onAnimationEnd(View view) {
                        //view.setVisibility(View.GONE);
                        mIsAnimatingOut = false;
                        super.onAnimationEnd(view);
                    }
                }).start();
    }

    //滑进来
    private void animateIn(View child) {
        Log.e("animateIn", "进入");
        //child.setVisibility(View.VISIBLE);
        //属性动画
        ViewCompat.animate(child)
                .scaleX(1.0f)
                .scaleY(1.0f)
                .alpha(1.0f)
                .setInterpolator(INTERPOLATOR)
                .setListener(null)
                .start();
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target) {
        super.onStopNestedScroll(coordinatorLayout, child, target);
    }
}
