package com.moriarty.user.mapdemotest;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 17-10-13.
 */
public class MainActivityPresenter {
    MainActivity mainActivity;
    Context context;
    private boolean state=false;
    private int REQUEST_PERMISSION1=110;
    private int REQUEST_PERMISSION2=111;
    private RecodeService mService;

    public MainActivityPresenter(MainActivity mainActivity,Context context){
        this.mainActivity=mainActivity;
        this.context=context;
    }

    public void inspectStoragePermission(){
        String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(context,permissions[0])!= PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(context,permissions[0])!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(mainActivity,new String[]{permissions[0]},REQUEST_PERMISSION1);
            }
        }
    }

    public void inspectLocationPermission() {
        /*String[] permissions={Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};
        if(ContextCompat.checkSelfPermission(context,permissions[0])!= PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(context,permissions[0])!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(mainActivity,new String[]{permissions[0]},REQUEST_PERMISSION2);
            }
        }
        if(ContextCompat.checkSelfPermission(context,permissions[1])!= PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(context,permissions[1])!=PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(mainActivity,new String[]{permissions[1]},REQUEST_PERMISSION1);
            }
        }*/
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "没有权限,请手动开启定位权限", Toast.LENGTH_SHORT).show();
            // 申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_PERMISSION1);
        }
    }

    public void changeListenState(){
        state=!state;
        if(state){
            inspectLocationPermission();
            startListenerService();
            bindListenerService();
        }
        else{
            unbindListenerService();
            stopListenerService();
        }
    }

    public void startListenerService() {
        if (!RecodeService.isRunning) {
            Log.i(MainActivity.TAG, "[SERVICE] Start");
            RecodeService.isRunning = true;
            context.startService(new Intent(context, RecodeService.class));
        }
    }

    public void bindListenerService() {
        Log.i(MainActivity.TAG, "[SERVICE] Bind");
        context.bindService(new Intent(context,
                RecodeService.class), mConnection, Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
    }

    public void unbindListenerService() {
        Log.i(MainActivity.TAG, "[SERVICE] Unbind");
        try{
            context.unbindService(mConnection);
        }catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    public void stopListenerService() {
        Log.i(MainActivity.TAG, "[SERVICE] Stop");
        if (mService != null) {
            Log.i(MainActivity.TAG, "[SERVICE] stopService");
            context.stopService(new Intent(context, RecodeService.class));
        }
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            mService = ((RecodeService.RecodeBinder)service).getService();
            /*mService.registerCallback(mCallback);
            mService.reloadSettings();
            //由于reloadSetting()操作在MainPageFragment初始化之前，所以操作不起作用，这里直接记录值
            //在MainPageFragment初始化完成之后赋值给相应TextView
            walkStepsValue=mService.getmStepDisplayer().getMWalkCount();
            runStepsValue=mService.getmStepDisplayer().getMRunCount();
            ridePedalsValue=mService.getmStepDisplayer().getmRideCount();
            caloriesVale=(int)mService.getmCaloriesNotifier().getmCalories();*/

        }
        public void onServiceDisconnected(ComponentName className) {
            mService = null;
        }
    };

}
