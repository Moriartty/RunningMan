package com.moriarty.user.runningman.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moriarty.user.runningman.Object.ActivityData;
import com.moriarty.user.runningman.Object.ResponseInfoFromNet;
import com.moriarty.user.runningman.Object.User;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by user on 17-10-10.
 */
public class JsonUtils {
    public static User json2User(String json){
        Gson gson = new Gson();
        User user=gson.fromJson(json,User.class);
        return user;
    }
    public static ResponseInfoFromNet json2RI(String json){
        Gson gson = new Gson();
        ResponseInfoFromNet ri=gson.fromJson(json,ResponseInfoFromNet.class);
        return ri;
    }
    public static ActivityData json2Activity(String json){
        Gson gson = new Gson();
        ActivityData ad=gson.fromJson(json,ActivityData.class);
        return ad;
    }
    public static ArrayList<ActivityData> json2Activities(String json) {
        Gson gson = new Gson();
        ArrayList<ActivityData> list=gson.fromJson(json,new TypeToken<ArrayList<ActivityData>>(){}.getType());
        return list;
    }
    public static String activity2Json(ActivityData ac){
        Gson gson = new Gson();
        Type type = new TypeToken<ActivityData>(){}.getType();
        return  gson.toJson(ac,type);
    }
    public static String user2Json(User user){
        Gson gson = new Gson();
        Type type = new TypeToken<User>(){}.getType();
        return gson.toJson(user,type);
    }
}
