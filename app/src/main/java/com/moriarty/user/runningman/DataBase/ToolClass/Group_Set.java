package com.moriarty.user.runningman.DataBase.ToolClass;

import android.net.Uri;

/**
 * Created by user on 16-8-27.
 */
public final class Group_Set {
   // public final static String AYTHORITY="com.moriarty.user.runningman.Database.ContentProvider";
    public final static class group_Set{
        public final static String _ID="_id";
        public final static String TYPE="Type";
        public final static Uri GROUP_SET_URI=Uri.parse("content://"+Person_Inf.AYTHORITY+"/group_set");
    }
}
