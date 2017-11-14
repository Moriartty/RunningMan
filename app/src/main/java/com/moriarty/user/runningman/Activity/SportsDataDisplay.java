package com.moriarty.user.runningman.Activity;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.moriarty.user.runningman.DataBase.ToolClass.Movement_Data;
import com.moriarty.user.runningman.Fragment.MainPageFragment;
import com.moriarty.user.runningman.Object.MovementData;
import com.moriarty.user.runningman.Pedometer.StepDetector;
import com.moriarty.user.runningman.Pedometer.StepService;
import com.moriarty.user.runningman.Presenter.SDDisplayPresenter;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.MDataManageService;
import com.moriarty.user.runningman.User_Defind.CircleScaleView;
import com.moriarty.user.runningman.User_Defind.ColorArcProgressBar;

import com.moriarty.user.runningman.User_Defind.Fuck.ExtendedLineView;
import com.moriarty.user.runningman.User_Defind.MLineView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by user on 17-8-23.
 */
public class SportsDataDisplay extends AppCompatActivity {

    private ColorArcProgressBar bar1;
    private ColorArcProgressBar bar2;
    private CircleScaleView circleScaleView;
    ExtendedLineView mLineView;
    TextView walk_percent_notes,run_percent_notes,ride_percent_notes;
    TextView pace_tv,distance_tv,fuckyou,calories_chartTitle;
    int blockFlag;
   // int flag;
    SDDisplayPresenter sdDisplayPresenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data_show);

        init();
        sdDisplayPresenter.bindMDataService();
        sdDisplayPresenter.bindStepService();
        sdDisplayPresenter.registerContentObserver();
    }

    public void init(){
        //以下代码是去除actionBar与其下面布局的阴影
        //先在style.xml文件中声明<item name="android:windowContentOverlay">@null</item>
        //然后为了兼容5.0以上版本，需要下面代码
        if(Build.VERSION.SDK_INT>=21){
            getSupportActionBar().setElevation(0);
        }
        //获取当前选择的运动模式
        blockFlag=getIntent().getIntExtra(getString(R.string.block_flag),0);
        sdDisplayPresenter=new SDDisplayPresenter(this,SportsDataDisplay.this);
        bar1=(ColorArcProgressBar)findViewById(R.id.bar1);
        bar2=(ColorArcProgressBar)findViewById(R.id.bar2);
        pace_tv=(TextView)findViewById(R.id.pace_tv);
        distance_tv=(TextView)findViewById(R.id.distance_tv);
        fuckyou=(TextView)findViewById(R.id.fuckyou);
        mLineView = (ExtendedLineView) findViewById(R.id.mLineView);
        switch (blockFlag){
            case 4:
                circleScaleView=(CircleScaleView)findViewById(R.id.circle_scale_view);
                walk_percent_notes=(TextView)findViewById(R.id.walk_percent_notes);
                run_percent_notes=(TextView)findViewById(R.id.run_percent_notes);
                ride_percent_notes=(TextView)findViewById(R.id.ride_percent_notes);
                calories_chartTitle=(TextView)findViewById(R.id.calories_charttitle);
                calories_chartTitle.setVisibility(View.VISIBLE);
                fuckyou.setText(getString(R.string.SDD_fuckyou_unit2));
                circleScaleView.setVisibility(View.VISIBLE);
                walk_percent_notes.setVisibility(View.VISIBLE);
                run_percent_notes.setVisibility(View.VISIBLE);
                ride_percent_notes.setVisibility(View.VISIBLE);
                break;
            default:
                bar1.setVisibility(View.VISIBLE);
                bar2.setVisibility(View.VISIBLE);
                fuckyou.setText(getString(R.string.SDD_fuckyou_unit1));
                sdDisplayPresenter.setMaxSteps(20000);//设定最大步数
                break;
        }
    }

    public void setScalePercent(int[] percent){
        circleScaleView.setScalePercent(percent);
    }

    public void setColorBarMaxValue(int value){
        bar1.setMaxValues(value);
    }

    public void setLineViewData(ArrayList<MovementData> movementDatas,int dateType,int dataType){
        mLineView.setData(movementDatas,dateType,dataType);
    }

    public void setColorBar1Data(int mStepValue,String title){
        bar1.setCurrentValues(mStepValue,title);
    }

    public void setColorBar2Data(float mSpeedValue,String title){
        bar2.setCurrentValues(mSpeedValue,title);
    }

    public void setPace(String s){
        pace_tv.setText(s);
    }

    public void setFuckyou(String s){
        distance_tv.setText(s);
    }

    public void setCaloriesNotes(int[] percent){
        walk_percent_notes.setText(getString(R.string.walk_percent_title)+":"+percent[0]+"%");
        run_percent_notes.setText(getString(R.string.run_percent_title) +":"+percent[1]+"%");
        ride_percent_notes.setText(getString(R.string.ride_percent_title) +":"+percent[2]+"%");
    }

    public int getBlockFlag(){
        return blockFlag;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        sdDisplayPresenter.unbindStepService();
        sdDisplayPresenter.unRegisterContentObserver();
        finish();
    }

}
