package com.moriarty.user.runningman.DataBase.ToolClass;

import android.net.Uri;

/**
 * Created by user on 17-8-25.
 */
public final class Movement_Data {
   // public final static String AYTHORITY="com.moriarty.user.runningman.DataBase.ContentProvider";
    public final static class movement_Data{
        public final static String _ID="_id";
        public final static String TIME="Time";
        public final static String WALK_STEP="Walk_Step";
        public final static String WALK_DISTANCE="Walk_Distance";
        public final static String RUN_STEP="Run_Step";
        public final static String RUN_DISTANCE="Run_Distance";
        public final static String NUMBER_OF_PEDALS="Number_Of_Pedals";
        public final static String RIDE_DISTANCE="Ride_Distance";
        public final static String RIDE_TIME="Ride_Time";
        public final static String CALORIES="Calories";
        public final static Uri MOVEMENT_DATA_URI=Uri.parse("content://"+Person_Inf.AYTHORITY+"/movement_data");
    }

}
