package com.moriarty.user.runningman.DataBase.ToolClass;

import android.net.Uri;

/**
 * Created by user on 16-8-27.
 */
public final class Person_Inf {
    public final static String AYTHORITY="com.moriarty.user.runningman.Database.ContentProvider";
    public final static class person_Inf{
        public final static String _ID="_id";
        public final static String NAME="Name";
        public final static String TEL="Tel";
        public final static String GROUPTYPE="GroupType";
        public final static String EMAIL="Email";
        public final static String ISCOLLECT="IsCollect";
        public final static String HEAD_PORTRAIT="Head_Portrait";
        public final static String SEXY="Sexy";
        public final static String AGE="Age";
        public final static String BIRTHDAY="Birthday";
        public final static Uri PERSON_INF_URI=Uri.parse("content://"+AYTHORITY+"/person_inf");
    }
}
