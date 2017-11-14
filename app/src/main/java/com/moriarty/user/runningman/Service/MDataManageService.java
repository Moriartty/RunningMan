package com.moriarty.user.runningman.Service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.DataBase.ToolClass.Movement_Data;
import com.moriarty.user.runningman.Fragment.MainPageFragment;
import com.moriarty.user.runningman.Object.MovementData;
import com.moriarty.user.runningman.Pedometer.StepService;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.User_Defind.Fuck.ExtendedLineView;

import java.util.ArrayList;

/**
 * Created by user on 17-8-25.
 */
public class MDataManageService extends Service {
    private  ContentResolver contentResolver;
    public class MDataManageBinder extends Binder{
        public MDataManageService getService(){
            return MDataManageService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        contentResolver=getContentResolver();
        return new MDataManageBinder();
    }

    public MovementData prefData2Object(String today){
        SharedPreferences sp=getSharedPreferences("state",0);
        MovementData.Builder builder=new MovementData.Builder()
                .walk_Step(sp.getInt(getResources().getString(R.string.pref_walk_step),0))
                .walk_Distance(sp.getFloat(getResources().getString(R.string.pref_walk_distance),0))
                .run_Step(sp.getInt(getResources().getString(R.string.pref_run_step),0))
                .run_Distance(sp.getFloat(getResources().getString(R.string.pref_run_distance),0))
                .number_Of_Pedals(sp.getInt(getResources().getString(R.string.pref_number_of_pedals),0))
                .ride_Distance(sp.getFloat(getResources().getString(R.string.pref_ride_distance),0))
                .ride_Time(sp.getInt(getResources().getString(R.string.pref_ride_time),0))
                .calories(sp.getFloat(getResources().getString(R.string.pref_calories),0))
                .time(today);
        return builder.build();
    }

    public void writePredayData2DB(MovementData movementData){
        contentResolver.insert(Movement_Data.movement_Data.MOVEMENT_DATA_URI,getContentValues(movementData));
    }


    public ContentValues getContentValues(MovementData movementData){
        ContentValues values=new ContentValues();
        values.put(Movement_Data.movement_Data.WALK_STEP,movementData.getWalk_Step());
        values.put(Movement_Data.movement_Data.WALK_DISTANCE,movementData.getWalk_Distance());
        values.put(Movement_Data.movement_Data.RUN_STEP,movementData.getRun_Step());
        values.put(Movement_Data.movement_Data.RUN_DISTANCE,movementData.getRun_Distance());
        values.put(Movement_Data.movement_Data.NUMBER_OF_PEDALS,movementData.getNumber_Of_Pedals());
        values.put(Movement_Data.movement_Data.RIDE_DISTANCE,movementData.getRide_Distance());
        values.put(Movement_Data.movement_Data.RIDE_TIME,movementData.getRide_Time());
        values.put(Movement_Data.movement_Data.CALORIES,movementData.getCalories());
        values.put(Movement_Data.movement_Data.TIME,movementData.getTime());
        return values;
    }

    public MovementData getYesterdayDistance(){
        Cursor cursor=null;
        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                new String[]{"Walk_Distance","Run_Distance","Ride_Distance"},null,null,"Time desc limit 1");
        if(cursor!=null){
            while(cursor.moveToNext()){
                MovementData.Builder builder=new MovementData.Builder()
                        .walk_Distance(cursor.getDouble(0))
                        .run_Distance(cursor.getDouble(1))
                        .ride_Distance(cursor.getDouble(2));
                return builder.build();
            }
        }
        return null;
    }

    public ArrayList<MovementData> getMovementDataObject(int dateType,int dataType){
        ArrayList<MovementData> movementDatas=new ArrayList<>();
        Cursor cursor=null;
        switch (dateType){
            case ExtendedLineView.WEEK:
                switch (dataType){
                    case ExtendedLineView.WALK_STEP:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Walk_Step"},null,null,"Time desc limit 7");
                        break;
                    case ExtendedLineView.WALK_DISTANCE:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Walk_Distance"},null,null,"Time desc limit 7");
                        break;
                    case ExtendedLineView.RUN_DISTANCE:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Run_Distance"},null,null,"Time desc limit 7");
                        break;
                    case ExtendedLineView.RIDE_DISTANCE:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Ride_Distance"},null,null,"Time desc limit 7");
                        break;
                    case ExtendedLineView.CALORIES:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Calories"},null,null,"Time desc limit 7");
                        break;
                    default:break;
                }
                break;

            case ExtendedLineView.MONTH:
                switch (dataType){
                    case ExtendedLineView.WALK_STEP:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Walk_Step"},null,null,"Time desc limit 30");
                        break;
                    case ExtendedLineView.WALK_DISTANCE:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Walk_Distance"},null,null,"Time desc limit 30");
                        break;
                    case ExtendedLineView.RUN_DISTANCE:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Run_Distance"},null,null,"Time desc limit 30");
                        break;
                    case ExtendedLineView.RIDE_DISTANCE:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Ride_Distance"},null,null,"Time desc limit 30");
                        break;
                    case ExtendedLineView.CALORIES:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Calories"},null,null,"Time desc limit 30");
                        break;
                    default:break;
                }
                break;

            case ExtendedLineView.THREEMONTH:
                switch (dataType){
                    case ExtendedLineView.WALK_STEP:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Walk_Step"},null,null,"Time desc limit 90");
                        break;
                    case ExtendedLineView.WALK_DISTANCE:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Walk_Distance"},null,null,"Time desc limit 90");
                        break;
                    case ExtendedLineView.RUN_DISTANCE:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Run_Distance"},null,null,"Time desc limit 90");
                        break;
                    case ExtendedLineView.RIDE_DISTANCE:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Ride_Distance"},null,null,"Time desc limit 90");
                        break;
                    case ExtendedLineView.CALORIES:
                        cursor=contentResolver.query(Movement_Data.movement_Data.MOVEMENT_DATA_URI,
                                new String[]{"Time","Calories"},null,null,"Time desc limit 90");
                        break;
                    default:break;
                }
                break;

            default:
                break;
        }
        if(cursor!=null){
            switch (dataType){
                case ExtendedLineView.WALK_STEP:
                    while(cursor.moveToNext()){
                        MovementData.Builder builder=new MovementData.Builder()
                                .time(cursor.getString(0))
                                .walk_Step(cursor.getInt(1));
                        movementDatas.add(builder.build());
                    }
                    break;
                case ExtendedLineView.WALK_DISTANCE:
                    while(cursor.moveToNext()){
                        MovementData.Builder builder=new MovementData.Builder()
                                .time(cursor.getString(0))
                                .walk_Distance(cursor.getInt(1));
                        movementDatas.add(builder.build());
                    }
                    break;
                case ExtendedLineView.RUN_DISTANCE:
                    while(cursor.moveToNext()){
                        MovementData.Builder builder=new MovementData.Builder()
                                .time(cursor.getString(0))
                                .run_Distance(cursor.getFloat(1));
                        movementDatas.add(builder.build());
                    }
                    break;
                case ExtendedLineView.RIDE_DISTANCE:
                    while(cursor.moveToNext()){
                        MovementData.Builder builder=new MovementData.Builder()
                                .time(cursor.getString(0))
                                .ride_Distance(cursor.getInt(1));
                        movementDatas.add(builder.build());
                    }
                    break;
                case ExtendedLineView.CALORIES:
                    while(cursor.moveToNext()){
                        MovementData.Builder builder=new MovementData.Builder()
                                .time(cursor.getString(0))
                                .calories(cursor.getInt(1));
                        movementDatas.add(builder.build());
                    }
                    break;
            }
            cursor.close();
            return movementDatas;
        }
        else
            return null;
    }
}
