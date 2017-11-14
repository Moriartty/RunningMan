package com.moriarty.user.runningman.Presenter;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.moriarty.user.runningman.Activity.AddContacts;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Activity.Person_InfoCard;
import com.moriarty.user.runningman.Fragment.MainPageFragment;
import com.moriarty.user.runningman.Interface.IMainPresenter;
import com.moriarty.user.runningman.Interface.MainActivityView;
import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Pedometer.PedometerSettings;
import com.moriarty.user.runningman.Pedometer.StepService;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.MDataManageService;
import com.moriarty.user.runningman.Service.QueryContactsService;
import com.moriarty.user.runningman.Thread.CheckMyInfoTask;
import com.moriarty.user.runningman.Utils.PreferenceUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by user on 17-8-9.
 */
public class MainActivityPresenter implements IMainPresenter {
    Context context;
    MainActivityView mainActivityView;
    int REQUEST_PERMISSION1=110;
    int REQUEST_PERMISSION2=111;
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private boolean mIsRunning;//程序是否运行的标志位
    private StepService mService;
    public static int walkStepsValue=0,runStepsValue=0,ridePedalsValue=0,caloriesVale=0;
    private boolean mIsMetric;//公制和米制切换标志
    private boolean mQuitting = false;
    public MainActivityPresenter(Context context,MainActivityView mainActivityView){
        this.context=context;
        this.mainActivityView=mainActivityView;
    }
    @Override
    public void skip2AddContactsView(PersonInfo personInfo) {
        Intent first=new Intent(context,AddContacts.class);
        first.putExtra("flag",1);
        first.putExtra(context.getResources().getString(R.string.person_info_data),personInfo);
        context.startActivity(first);
    }
    @Override
    public void skip2PersonInfo(PersonInfo myself) {
        Intent first=new Intent(context,Person_InfoCard.class);
        first.putExtra("flag",1);
       // Log.d(MainActivity.TAG,currentTag+"source_id is "+myNewInfoList.get(8));
        first.putExtra(context.getResources().getString(R.string.person_info_data),myself);
        context.startActivity(first);
    }


    @Override
    public void inspectPermission(Handler handler){
        String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.BODY_SENSORS};
        if(ContextCompat.checkSelfPermission(context,permissions[0])!= PackageManager.PERMISSION_GRANTED&&
                ContextCompat.checkSelfPermission(context,permissions[1])!= PackageManager.PERMISSION_GRANTED){
            enduePermission(permissions);
        }
        mainActivityView.init();   //当两类权限都有时，直接初始化。
        checkMySelfInfo(handler);
    }
    @Override
    public void checkMySelfInfo(final Handler handler){
        new Thread(){
            @Override
            public void run(){
                Log.d(MainActivity.TAG,"prepare to invalidate");
                CheckMyInfoTask checkMyInfoTask=new CheckMyInfoTask(context,handler);  //只会在onCreate()中进行检查自己信息是否完整,这个问题还需要得到更好的解决
                checkMyInfoTask.execute();
            }
        }.start();
    }
    @Override
    public void enduePermission(String[] permissions) {
        if(ContextCompat.checkSelfPermission(context,permissions[0])!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mainActivityView.getActivity(),new String[]{permissions[0]},REQUEST_PERMISSION1);
        }
        if(ContextCompat.checkSelfPermission(context,permissions[1])!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mainActivityView.getActivity(),new String[]{permissions[1]},REQUEST_PERMISSION2);
        }
    }


    @Override
    public void initPedometerSetting(){
        mSettings = PreferenceManager.getDefaultSharedPreferences(context);
        mPedometerSettings = new PedometerSettings(mSettings);
        mIsRunning=mPedometerSettings.isServiceRunning();
    }
    @Override
    public PedometerSettings getPedometerSettings(){
        if(mPedometerSettings==null)
            initPedometerSetting();
        return mPedometerSettings;
    }
    @Override
    public boolean getIsRunning(){
        return mIsRunning;
    }
    @Override
    public void preStartService(){
        // Start the service if this is considered to be an application start (last onPause was long ago)
        if (!mIsRunning && mPedometerSettings.isNewStart()) {
            Log.d(MainActivity.TAG,"Service isn't running");
            /*如果一个Service又被启动又被绑定，则该Service将会一直在后台运行。
            并且不管如何调用，onCreate始终只会调用一次，对应startService调用多少次，
            Service的onStart便会调用多少次。调用unbindService将不会停止Service，
            而必须调用 stopService 或 Service的 stopSelf 来停止服务*/
            startStepService();
            bindStepService();
        }
        else if (mIsRunning) {
            Log.d(MainActivity.TAG,"Service is running");
            bindStepService();
        }
        mPedometerSettings.clearServiceRunning();

        mIsMetric = mPedometerSettings.isMetric();
       /* ((TextView) findViewById(R.id.distance_units)).setText(getString(
                mIsMetric
                        ? R.string.kilometers
                        : R.string.miles
        ));
        ((TextView) findViewById(R.id.speed_units)).setText(getString(
                mIsMetric
                        ? R.string.kilometers_per_hour
                        : R.string.miles_per_hour
        ));*/
    }
    @Override
    public void startStepService() {
        if (! mIsRunning) {
            Log.i(MainActivity.TAG, "[SERVICE] Start");
            mIsRunning = true;
            context.startService(new Intent(context, StepService.class));
        }
    }
    @Override
    public void bindStepService() {
        Log.i(MainActivity.TAG, "[SERVICE] Bind");
        context.bindService(new Intent(context,
                StepService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }
    @Override
    public void unbindStepService() {
        Log.i(MainActivity.TAG, "[SERVICE] Unbind");
        try{
            context.unbindService(mConnection);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }
    @Override
    public void stopStepService() {
        Log.i(MainActivity.TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(MainActivity.TAG, "[SERVICE] stopService");
            context.stopService(new Intent(context, StepService.class));
        }
        mIsRunning = false;
    }
    @Override
    public void mainActivityPaused(){
        if (mIsRunning) {
            unbindStepService();
        }
        if (mQuitting) {
            mPedometerSettings.saveServiceRunningWithNullTimestamp(mIsRunning);
        }
        else {
            mPedometerSettings.saveServiceRunningWithTimestamp(mIsRunning);
        }
    }
    @Override
    public void setQuitting(boolean mQuitting){
        this.mQuitting=mQuitting;
    }

    @Override
    public void resetValues(boolean updateDisplay) {
        SharedPreferences state = context.getSharedPreferences("state", 0);
        SharedPreferences.Editor stateEditor = state.edit();
        PreferenceUtil.resetStatePrefValues(updateDisplay,mService,context,mIsRunning,stateEditor);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((StepService.StepBinder)service).getService();
            mService.registerCallback(mCallback);
            mService.reloadSettings();
            //由于reloadSetting()操作在MainPageFragment初始化之前，所以操作不起作用，这里直接记录值
            //在MainPageFragment初始化完成之后赋值给相应TextView
            walkStepsValue=mService.getmStepDisplayer().getMWalkCount();
            runStepsValue=mService.getmStepDisplayer().getMRunCount();
            ridePedalsValue=mService.getmStepDisplayer().getmRideCount();
            caloriesVale=(int)mService.getmCaloriesNotifier().getmCalories();

        }
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };
    private StepService.ICallback mCallback = new StepService.ICallback() {
        public void stepsChanged(int value,int flag) {
            MainPageFragment.mHandler.sendMessage
                    (MainPageFragment.mHandler.obtainMessage(MainPageFragment.WALK_STEPS_MSG, value, flag));
        }
        public void paceChanged(int value) {
            MainPageFragment.mHandler.sendMessage
                    (MainPageFragment.mHandler.obtainMessage(MainPageFragment.PACE_MSG, value, 0));
        }
        public void distanceChanged(float value,int flag) {
            MainPageFragment.mHandler.sendMessage
                    (MainPageFragment.mHandler.obtainMessage(MainPageFragment.DISTANCE_MSG, (int)(value), flag));
        }
        public void speedChanged(float value) {
            MainPageFragment.mHandler.sendMessage
                    (MainPageFragment.mHandler.obtainMessage(MainPageFragment.SPEED_MSG, (int)(value*1000), 0));
        }
        public void caloriesChanged(float value) {
            MainPageFragment.mHandler.sendMessage
                    (MainPageFragment.mHandler.obtainMessage(MainPageFragment.CALORIES_MSG, (int)(value), 0));
        }
    };

}
