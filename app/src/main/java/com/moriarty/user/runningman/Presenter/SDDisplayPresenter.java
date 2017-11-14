package com.moriarty.user.runningman.Presenter;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Activity.SportsDataDisplay;
import com.moriarty.user.runningman.DataBase.ToolClass.Movement_Data;
import com.moriarty.user.runningman.Object.MovementData;
import com.moriarty.user.runningman.Pedometer.CaloriesNotifier;
import com.moriarty.user.runningman.Pedometer.StepDetector;
import com.moriarty.user.runningman.Pedometer.StepService;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.MDataManageService;
import com.moriarty.user.runningman.User_Defind.Fuck.ExtendedLineView;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by user on 17-8-31.
 */
public class SDDisplayPresenter {
    Context context;
    SportsDataDisplay sportsDataDisplay;
    ContentObserver contentObserver;
    MDataManageService MDService;
    ArrayList<MovementData> movementDatas;
    StepService mService;
    String[] colorBarTitle;
    int flag;
    public static final int STEPS_MSG = 1;
    public static final int PACE_MSG = 2;
    public static final int DISTANCE_MSG = 3;
    public static final int SPEED_MSG = 4;
    public static final int CALORIES_MSG = 5;

    public SDDisplayPresenter(Context context, SportsDataDisplay sportsDataDisplay){
        this.context=context;
        this.sportsDataDisplay=sportsDataDisplay;
        colorBarTitle=new String[]{context.getString(R.string.colorbar_walktitle)
                ,context.getString(R.string.colorbar_runtitle),context.getString(R.string.colorbar_ridetitle)};
        flag=this.sportsDataDisplay.getBlockFlag();
    }
    public void registerContentObserver(){
        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange){
                super.onChange(selfChange);
                reUpdate();
            }
        };
        context.getContentResolver().registerContentObserver
                (Movement_Data.movement_Data.MOVEMENT_DATA_URI,true,contentObserver);
    }
    private void reUpdate(){
        unbindStepService();
        bindMDataService();
        bindStepService();
    }
    public void bindMDataService(){
        context.bindService(new Intent(sportsDataDisplay, MDataManageService.class),
                queryConnection,Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }
    public void unbindMDataService(){
        context.unbindService(queryConnection);
    }
    public void unbindStepService() {
        Log.i(MainActivity.TAG, "[SERVICE] Unbind");
        context.unbindService(mConnection);
    }
    public void bindStepService() {
        Log.i(MainActivity.TAG, "[SERVICE] Bind");
        context.bindService(new Intent(sportsDataDisplay,
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }
    public void unRegisterContentObserver(){
        context.getContentResolver().unregisterContentObserver(contentObserver);
    }
    private int[] calculateScalePercent(MovementData movementData){
        int[] percent=new int[3];
        double walkDistance=movementData.getWalk_Distance();
        double runDistance=movementData.getRun_Distance();
        double rideDistance=movementData.getRide_Distance();

        double walkCost = walkDistance * CaloriesNotifier.METRIC_WALKING_FACTOR;
        double runCost = runDistance * CaloriesNotifier.METRIC_RUNNING_FACTOR;
        double rideCost = rideDistance * CaloriesNotifier.METRIC_RIDING_FACTOR;
        double total = walkCost + runCost + rideCost;
        percent[0] = (int)Math.round((walkCost / total)*100);
        //Log.d(MainActivity.TAG,"walkPercent:"+walkPercent);
        percent[1] = (int) Math.round((runCost / total)*100);
       // Log.d(MainActivity.TAG,"runPercent:"+runPercent);
        percent[2] = 100-percent[0]-percent[1];
        return percent;
    }

    public void setMaxSteps(int steps){
        sportsDataDisplay.setColorBarMaxValue(steps);
    }
    private ServiceConnection queryConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(MainActivity.TAG,"bind MDataManagerService");
            MDService = ((MDataManageService.MDataManageBinder)service).getService();
            switch (flag){
                case StepDetector.WALK:
                    movementDatas=MDService.getMovementDataObject(ExtendedLineView.WEEK,ExtendedLineView.WALK_STEP);
                    if(movementDatas!=null){
                        Collections.reverse(movementDatas);
                        sportsDataDisplay.setLineViewData(movementDatas,ExtendedLineView.WEEK,ExtendedLineView.WALK_STEP);
                    }
                    break;
                case StepDetector.RUN:
                    movementDatas=MDService.getMovementDataObject(ExtendedLineView.WEEK,ExtendedLineView.RUN_DISTANCE);
                    if(movementDatas!=null){
                        Collections.reverse(movementDatas);
                        sportsDataDisplay.setLineViewData(movementDatas,ExtendedLineView.WEEK,ExtendedLineView.RUN_DISTANCE);
                    }
                    break;
                case StepDetector.RIDE:
                    movementDatas=MDService.getMovementDataObject(ExtendedLineView.WEEK,ExtendedLineView.RIDE_DISTANCE);
                    if(movementDatas!=null){
                        Collections.reverse(movementDatas);
                        sportsDataDisplay.setLineViewData(movementDatas,ExtendedLineView.WEEK,ExtendedLineView.RIDE_DISTANCE);
                    }
                    break;
                case StepDetector.CALORIES:
                    //计算卡路里消耗占比
                    MovementData movementData=MDService.getYesterdayDistance();
                    if(movementData!=null){
                        int[] percent = calculateScalePercent(movementData);
                        sportsDataDisplay.setScalePercent(percent);
                        sportsDataDisplay.setCaloriesNotes(percent);
                    }

                    movementDatas=MDService.getMovementDataObject(ExtendedLineView.WEEK,ExtendedLineView.CALORIES);
                    if(movementDatas!=null){
                        Collections.reverse(movementDatas);
                        sportsDataDisplay.setLineViewData(movementDatas,ExtendedLineView.WEEK,ExtendedLineView.CALORIES);
                    }
                    break;
            }
            unbindMDataService();

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();
            mService.registerCallback(mCallback);
            mService.reloadSettings();

        }
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value,int flag) {
            mHandler.sendMessage
                    (mHandler.obtainMessage(STEPS_MSG, value, flag));
        }
        public void paceChanged(int value) {
            mHandler.sendMessage
                    (mHandler.obtainMessage(PACE_MSG, value, 0));
        }
        public void distanceChanged(float value,int flag) {
            mHandler.sendMessage
                    (mHandler.obtainMessage(DISTANCE_MSG, (int)(value*1000), flag));
        }
        public void speedChanged(float value) {
            mHandler.sendMessage
                    (mHandler.obtainMessage(SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            mHandler.sendMessage
                    (mHandler.obtainMessage(CALORIES_MSG, (int)(value), 0));
        }
    };
    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                //根据flag判断部分控件的独立显示
                case STEPS_MSG:
                    if(flag==msg.arg2){
                        int mStepValue = (int)msg.arg1;
                        sportsDataDisplay.setColorBar1Data
                                (mStepValue,colorBarTitle[flag]);
                    }
                    break;
                case PACE_MSG:
                    int mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) {
                        sportsDataDisplay.setPace("0");
                    }
                    else {
                        sportsDataDisplay.setPace(""+(int)mPaceValue);
                    }
                    break;
                case DISTANCE_MSG:
                    if(flag==msg.arg2){
                        float mDistanceValue =((int)msg.arg1)/1000f;
                        if (mDistanceValue <= 0) {
                            sportsDataDisplay.setFuckyou("0");
                        }
                        else {
                            sportsDataDisplay.setFuckyou((""+(mDistanceValue)));
                        }
                    }
                    break;
                case SPEED_MSG:
                    float mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) {
                        sportsDataDisplay.setColorBar2Data(0,context.getString(R.string.colorbar_speed));
                    }
                    else {
                        sportsDataDisplay.setColorBar2Data(mSpeedValue,context.getString(R.string.colorbar_speed));
                    }
                    break;
                case CALORIES_MSG:
                    if(flag==StepDetector.CALORIES){
                        int mCaloriesValue = msg.arg1;
                        if (mCaloriesValue <= 0)
                            sportsDataDisplay.setFuckyou("0");
                        else
                            sportsDataDisplay.setFuckyou("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

}
