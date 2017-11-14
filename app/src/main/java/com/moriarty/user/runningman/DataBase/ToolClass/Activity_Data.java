package com.moriarty.user.runningman.DataBase.ToolClass;

import android.net.Uri;

/**
 * Created by user on 17-9-15.
 */
public final class Activity_Data {

    public final static class activity_Data{
        public final static String _ID="_id";
        public final static String UUID="Uuid";
        public final static String LEADER="Leader";
        public final static String TITLE="Title";
        public final static String TIME="Time";
        public final static String ADDRESS="Address";
        public final static String TEL="Tel";
        public final static String PEOPLE="People";
        public final static String CONTENT="Content";
        public final static String IMGURL="Img_Url";
        public final static Uri ACTIVITY_DATA_URI=Uri.parse("content://"+Person_Inf.AYTHORITY+"/activity_data");
    }
}
