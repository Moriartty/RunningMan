/*
 *  Pedometer - Android App
 *  Copyright (C) 2009 Levente Bagi
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.moriarty.user.runningman.Pedometer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Object.MovementData;
import com.moriarty.user.runningman.Utils.PreferenceUtil;
import com.moriarty.user.runningman.Utils.TimeUtil;
import com.moriarty.user.runningman.Utils.Utils;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.MDataManageService;

public class StepService extends Service {
	private static final String TAG = "Moriarty";
    private SharedPreferences mSettings;
    private PedometerSettings mPedometerSettings;
    private SharedPreferences mState;
    private SharedPreferences.Editor mStateEditor;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private StepDetector mStepDetector;
    // private StepBuzzer mStepBuzzer; // used for debugging
    private StepDisplayer mStepDisplayer;
    private PaceNotifier mPaceNotifier;
    private DistanceNotifier mDistanceNotifier;
    private SpeedNotifier mSpeedNotifier;
    private CaloriesNotifier mCaloriesNotifier;
    
    private PowerManager.WakeLock wakeLock;
    private NotificationManager mNM;
    private Notification notification;

    private int walk_step,lastRecodingWalkSteps=0;
    private int run_step,lastRecodingRunSteps=0;
    private int ride_pedal,lastRecodingRidePedals=0;
    private int walk_pace;
    private float walk_distance;
    private float run_distance;
    private float ride_distance;
    private float walk_speed;
    private float mCalories;
    static Context context;
    private String today;


    public class StepBinder extends Binder {
        public StepService getService() {
            return StepService.this;
        }
    }
    
    @Override
    public void onCreate() {

        super.onCreate();
        context=this;
        today= TimeUtil.getToday();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        showNotification();
        
        // Load settings
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        mPedometerSettings = new PedometerSettings(mSettings);
        mState = getSharedPreferences("state", 0);

        acquireWakeLock();
        
        // Start detecting
        mStepDetector = new StepDetector();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        registerDetector();

        // Register our receiver for the ACTION_SCREEN_OFF action. This will make our receiver
        // code be called whenever the phone enters standby mode.
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mReceiver, filter);
        IntentFilter dateChangedFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(mReceiver,dateChangedFilter);

        mStepDisplayer = new StepDisplayer(mPedometerSettings);
        mStepDisplayer.setWalkSteps(walk_step = mState.getInt(getResources().getString(R.string.pref_walk_step), 0));
        mStepDisplayer.setRunSteps(run_step = mState.getInt(getString(R.string.pref_run_step),0));
        mStepDisplayer.setRidePedals(ride_pedal = mState.getInt(getString(R.string.pref_number_of_pedals),0));
        mStepDisplayer.addListener(mStepListener);
        mStepDetector.addStepListener(mStepDisplayer);


        mPaceNotifier     = new PaceNotifier(mPedometerSettings);
        mPaceNotifier.setPace(walk_pace = mState.getInt(getResources().getString(R.string.pref_walk_pace), 0));
        mPaceNotifier.addListener(mPaceListener);
        mStepDetector.addStepListener(mPaceNotifier);

        mDistanceNotifier = new DistanceNotifier(mDistanceListener, mPedometerSettings);
        mDistanceNotifier.setWalkDistance(walk_distance = mState.getFloat(getResources().getString(R.string.pref_walk_distance), 0));
        mDistanceNotifier.setRunDistance(run_distance = mState.getFloat(getString(R.string.pref_run_distance),0));
        mDistanceNotifier.setRideDistance(ride_distance = mState.getFloat(getString(R.string.pref_ride_distance),0));
        mStepDetector.addStepListener(mDistanceNotifier);
        
        mSpeedNotifier    = new SpeedNotifier(mSpeedListener,    mPedometerSettings);
        mSpeedNotifier.setSpeed(walk_speed = mState.getFloat(getResources().getString(R.string.pref_walk_speed), 0));
        mPaceNotifier.addListener(mSpeedNotifier);
        
        mCaloriesNotifier = new CaloriesNotifier(mCaloriesListener, mPedometerSettings);
        mCaloriesNotifier.setCalories(mCalories = mState.getFloat(getResources().getString(R.string.pref_calories), 0));
        mStepDetector.addStepListener(mCaloriesNotifier);
        
        // Used when debugging:
        // mStepBuzzer = new StepBuzzer(this);
        // mStepDetector.addStepListener(mStepBuzzer);

        // Start voice
        reloadSettings();

        // Tell the user we started.
        Log.i(TAG, "[SERVICE] onCreate");
        Toast.makeText(this, getText(R.string.started), Toast.LENGTH_SHORT).show();
    }
    public StepDisplayer getmStepDisplayer(){
        return mStepDisplayer;
    }
    public CaloriesNotifier getmCaloriesNotifier(){
        return mCaloriesNotifier;
    }
    public StepService getInstance(){
        return this;
    }

    
    @Override
    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "[SERVICE] onStart");
        super.onStart(intent, startId);
    }
   /* @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        super.onStartCommand(intent, flags, startId);
        flags = START_STICKY;
        startForeground(startId,notification);
        return super.onStartCommand(intent, flags, startId);
    }*/

    //注意，service的结束并不一定都会调用onDestroy!!!
    @Override
    public void onDestroy() {
        Log.i(TAG, "[SERVICE] onDestroy");

        // Unregister our receiver.
        unregisterReceiver(mReceiver);
        unregisterDetector();
        writeInPre();
        
        mNM.cancel(R.string.app_name);

        wakeLock.release();
        
        super.onDestroy();
        
        // Stop detecting
        mSensorManager.unregisterListener(mStepDetector);
        //如果service被正常kill掉的话，会调用onDestroy()方法，我们可以在其中重新startService();
        if(MainActivity.isQuit==0){
            //如果isQuit==0,说明不是用户自己主动关闭的服务，需要重新启动
            Intent service = new Intent(this, StepService.class);
            this.startService(service);
        }
        // Tell the user we stopped.
        Toast.makeText(this, getText(R.string.stopped), Toast.LENGTH_SHORT).show();
    }

    private void writeInPre(){
        mStateEditor = mState.edit();
        PreferenceUtil.writeInStatePre(mStateEditor,context,data2Object());
    }
    private MovementData data2Object(){
        MovementData m = new MovementData.Builder()
                .walk_Step(walk_step)
                .walk_Distance(walk_distance)
                .walk_Pace(walk_pace)
                .walk_Speed(walk_speed)
                .run_Step(run_step)
                .run_Distance(run_distance)
                .number_Of_Pedals(ride_pedal)
                .ride_Distance(ride_distance)
                .calories(mCalories)
                .build();
        Log.d(MainActivity.TAG,run_step+"");
        return m;
    }

    private void registerDetector() {
        mSensor = mSensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER /*| 
            Sensor.TYPE_MAGNETIC_FIELD | 
            Sensor.TYPE_ORIENTATION*/);
        mSensorManager.registerListener(mStepDetector,
            mSensor,
            SensorManager.SENSOR_DELAY_FASTEST);
    }

    private void unregisterDetector() {
        mSensorManager.unregisterListener(mStepDetector);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "[SERVICE] onBind");
        return mBinder;
    }

    /**
     * Receives messages from activity.
     */
    private final IBinder mBinder = new StepBinder();

    public interface ICallback {
        public void stepsChanged(int value,int flag);
        public void paceChanged(int value);
        public void distanceChanged(float value,int flag);
        public void speedChanged(float value);
        public void caloriesChanged(float value);
    }
    
    private ICallback mCallback;

    public void registerCallback(ICallback cb) {
        mCallback = cb;
        //mStepDisplayer.passValue();
        //mPaceListener.passValue();
    }

    
    public void reloadSettings() {
        mSettings = PreferenceManager.getDefaultSharedPreferences(this);
        
        if (mStepDetector != null) { 
            mStepDetector.setSensitivity(
                    Float.valueOf(mSettings.getString("sensitivity", "10"))
            );
        }
        
        if (mStepDisplayer    != null) mStepDisplayer.reloadSettings();
        if (mPaceNotifier     != null) mPaceNotifier.reloadSettings();
        if (mDistanceNotifier != null) mDistanceNotifier.reloadSettings();
        if (mSpeedNotifier    != null) mSpeedNotifier.reloadSettings();
        if (mCaloriesNotifier != null) mCaloriesNotifier.reloadSettings();
    }
    
    public void resetValues() {
        mStepDisplayer.setWalkSteps(0);
        mStepDisplayer.setRunSteps(0);
        mStepDisplayer.setRidePedals(0);
        mPaceNotifier.setPace(0);
        mDistanceNotifier.setWalkDistance(0);
        mDistanceNotifier.setRunDistance(0);
        mDistanceNotifier.setRideDistance(0);
        mSpeedNotifier.setSpeed(0);
        mCaloriesNotifier.setCalories(0);
    }
    
    /**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
        public void stepsChanged(int value,int flag) {
            switch (flag){
                case StepDetector.WALK:
                    walk_step = value;
                    //为防止异常导致未调用onDestroy()方法就结束service
                    //导致数据为保存，每达100步就记录一次
                    if(walk_step-lastRecodingWalkSteps>=100){
                        lastRecodingWalkSteps=walk_step;
                        writeInPre();
                    }
                    passValue(walk_step,flag);
                    break;
                case StepDetector.RUN:
                    run_step = value;
                    if(run_step-lastRecodingRunSteps>=100){
                        lastRecodingRunSteps=run_step;
                        writeInPre();
                    }
                    passValue(run_step,flag);
                    break;
                case StepDetector.RIDE:
                    ride_pedal = value;
                    if(ride_pedal-lastRecodingRidePedals>=100){
                        lastRecodingRidePedals=ride_pedal;
                        writeInPre();
                    }
                    passValue(ride_pedal,flag);
                    break;
            }
        }
        public void passValue(int value,int flag) {
            if (mCallback != null) {
                mCallback.stepsChanged(value,flag);
            }
        }
    };

    /**
     * Forwards pace values from PaceNotifier to the activity. 
     */
    private PaceNotifier.Listener mPaceListener = new PaceNotifier.Listener() {
        public void paceChanged(int value) {
            walk_pace = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.paceChanged(walk_pace);
            }
        }
    };
    /**
     * Forwards distance values from DistanceNotifier to the activity. 
     */
    private DistanceNotifier.Listener mDistanceListener = new DistanceNotifier.Listener() {
        public void valueChanged(float value,int flag) {
            switch (flag){
                case StepDetector.WALK:
                    walk_distance = value;
                    passValue(walk_distance,flag);
                    break;
                case StepDetector.RUN:
                    run_distance = value;
                    passValue(run_distance,flag);
                    break;
                case StepDetector.RIDE:
                    ride_distance = value;
                    passValue(ride_distance,flag);
                    break;
            }
        }
        public void passValue(float value,int flag) {
            if (mCallback != null) {
                mCallback.distanceChanged(value,flag);
            }
        }
    };
    /**
     * Forwards speed values from SpeedNotifier to the activity. 
     */
    private SpeedNotifier.Listener mSpeedListener = new SpeedNotifier.Listener() {
        public void valueChanged(float value) {
            walk_speed = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.speedChanged(walk_speed);
            }
        }
    };
    /**
     * Forwards calories values from CaloriesNotifier to the activity. 
     */
    private CaloriesNotifier.Listener mCaloriesListener = new CaloriesNotifier.Listener() {
        public void valueChanged(float value) {
            mCalories = value;
            passValue();
        }
        public void passValue() {
            if (mCallback != null) {
                mCallback.caloriesChanged(mCalories);
            }
        }
    };
    
    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        CharSequence text = getText(R.string.app_name);
        Notification.Builder mBuilder = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(text)
                .setContentText(getText(R.string.notification_subtitle))
                .setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap());
        Intent pedometerIntent = new Intent();
        pedometerIntent.setComponent(new ComponentName(this, MainActivity.class));
        pedometerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                pedometerIntent, 0);
        mBuilder.setContentIntent(contentIntent);
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notification = mBuilder.build();
        notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        nm.notify(R.string.app_name, notification);
    }


    // BroadcastReceiver for handling ACTION_SCREEN_OFF.
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check action just to be on the safe side.
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                // Unregisters the listener and registers it again.
                StepService.this.unregisterDetector();
                StepService.this.registerDetector();
                if (mPedometerSettings.wakeAggressively()) {
                    wakeLock.release();
                    acquireWakeLock();
                }
            }
            else if(intent.getAction().equals(Intent.ACTION_TIME_TICK)){
                //日期改变才能触发运动数据读写操作
                if(TimeUtil.isDateChanged(today,TimeUtil.getToday())){
                    Log.d(MainActivity.TAG,"Time has changed!!!!!");
                    Intent queryIntent=new Intent();
                    queryIntent.setAction("com.moriarty.service.MDataManageService");
                    queryIntent.setPackage(context.getPackageName());
                    context.bindService(queryIntent,dataConnection,context.BIND_AUTO_CREATE);
                }
            }
        }
    };
    private ServiceConnection dataConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MDataManageService mDataManageService = ((MDataManageService.MDataManageBinder)service).getService();
            writeInPre();
            mDataManageService.writePredayData2DB(mDataManageService.prefData2Object(today));
            today=TimeUtil.getToday();
            SharedPreferences state = context.getSharedPreferences("state", 0);
            SharedPreferences.Editor stateEditor = state.edit();
            PreferenceUtil.resetStatePrefValues(true,getInstance(),context,true,stateEditor);
            unbindService(dataConnection);
            Toast.makeText(context,"date changed",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    private void acquireWakeLock() {
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        int wakeFlags;
        if (mPedometerSettings.wakeAggressively()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP;
        }
        else if (mPedometerSettings.keepScreenOn()) {
            wakeFlags = PowerManager.SCREEN_DIM_WAKE_LOCK;
        }
        else {
            wakeFlags = PowerManager.PARTIAL_WAKE_LOCK;
        }
        wakeLock = pm.newWakeLock(wakeFlags, TAG);
        wakeLock.acquire();
    }

}

