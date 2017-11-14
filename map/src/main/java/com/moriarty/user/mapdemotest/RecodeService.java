package com.moriarty.user.mapdemotest;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by user on 17-10-13.
 */
public class RecodeService extends Service implements AMapLocationListener {
    Timer timer;
    TimerTask task;
    Handler handler;
    public static boolean isRunning=false;
    private final int RECODE=0x423;
    private  double latitude;
    private  double longitude;
    AMapLocationClient mLocationClient;
    AMapLocationClientOption mLocationOption;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new RecodeBinder();
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                latitude = aMapLocation.getLatitude();//获取纬度
                longitude = aMapLocation.getLongitude();//获取经度

            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("地图错误", "定位失败, 错误码:" + aMapLocation.getErrorCode() + ", 错误信息:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    public class RecodeBinder extends Binder{
        public RecodeService getService(){
            return RecodeService.this;
        }
    }
    @Override
    public void onCreate(){
        super.onCreate();
        isRunning=true;
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            location();
            Log.d(MainActivity.TAG,"location");
        }
        Log.d(MainActivity.TAG,"location2");
        initHandler();
        initTimerAndTask();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        isRunning=false;
        cancelTimerAndTask();
    }

    /*开启定位*/
    private void location() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void initHandler(){
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == RECODE) {
                    Log.d(MainActivity.TAG,"fuck bili");
                    Log.d(MainActivity.TAG,getData());
                    FileUtil.writeInFile(getData());
                }
                super.handleMessage(msg);
            }
        };
    }

    private void initTimerAndTask(){
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                // 需要做的事:发送消息
                Message message = new Message();
                message.what = RECODE;
                handler.sendMessage(message);
            }
        };
        timer.schedule(task,5*1000,5*1000);
        Log.d(MainActivity.TAG,"schedule is begin");
    }
    private void cancelTimerAndTask(){
        if(task!=null)
            task.cancel();
        if(timer!=null)
            timer.cancel();
    }
    private String getData(){
        return this.latitude+","+this.longitude;
    }

}
