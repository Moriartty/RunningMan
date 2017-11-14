package com.moriarty.user.runningman.Service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Binder;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.util.Log;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.DataBase.ToolClass.Group_Set;
import com.moriarty.user.runningman.DataBase.ToolClass.Person_Inf;
import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Others.BroadcastManager;
import com.moriarty.user.runningman.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 17-8-11.
 */
public class QueryContactsService extends Service {public static ArrayList<String> names = new ArrayList<String>();
    public static ArrayList<ArrayList<String>> details=new ArrayList<ArrayList<String>>();
    HashMap<String,ArrayList<String>> groupMap=new HashMap<String,ArrayList<String>>();
    HashMap<String,String> headPortrait_Map=new HashMap<>();
    static ContentResolver contentResolver;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    static Resources res;

    public class QueryContactsBinder extends Binder {
        public QueryContactsService getService(){
            return QueryContactsService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        contentResolver=getContentResolver();
        res=getResources();
        return new QueryContactsBinder();
    }
    @Override
    public void onCreate(){
        super.onCreate();
    }

    private static void getSQLiteData(Cursor cursor,ArrayList<String> temp){  //读取数据的关键函数
        while(cursor.moveToNext()){
            temp.add(cursor.getString(0));
        }
    }

    public ArrayList<String> queryContactsName(){
        ArrayList<String> names=new ArrayList<>();
        Cursor cursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"Name"},null,null,null);
        getSQLiteData(cursor,names);
        cursor.close();
       // new BroadcastManager().sendBroadcast(this,2);
        return names;
    }



    private void insertData(PersonInfo personInfo){
        ContentValues values=new ContentValues();
        values.put(Person_Inf.person_Inf.NAME,personInfo.getName());
        values.put(Person_Inf.person_Inf.TEL,personInfo.getTel());
        values.put(Person_Inf.person_Inf.GROUPTYPE,personInfo.getGroup());
        values.put(Person_Inf.person_Inf.EMAIL,personInfo.getEmail());
        values.put(Person_Inf.person_Inf.HEAD_PORTRAIT,personInfo.getHeadPortrait());
        values.put(Person_Inf.person_Inf.ISCOLLECT,personInfo.getIsCollected());
        contentResolver.insert(Person_Inf.person_Inf.PERSON_INF_URI,values);
    }

    public HashMap<String,ArrayList<String>> queryForGroup(){
        ArrayList<String> tempName=new ArrayList<String>();
        ArrayList<String> tempGroup=new ArrayList<String>();     //记录person_inf表中GroupType数据
        ArrayList<String> tempAllgroup=new ArrayList<String>();  //记录group_type表中group

        //查询person_inf中所有Name
        Cursor nameCursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"Name"},null,null,null);
        //查询person_inf中所有GroupType
        Cursor groupTypeCursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"GroupType"},null,null,null);
        getSQLiteData(nameCursor,tempName);
        getSQLiteData(groupTypeCursor,tempGroup);

        tempAllgroup.addAll(getAllGroup());
        //这里的groupMap主要用于Category中expandableListView的数据，因此必须获取到全部分组
        for(int i=0;i<tempAllgroup.size();i++){
            ArrayList<String> temp=new ArrayList<String>();
            groupMap.put(tempAllgroup.get(i),temp);
        }           //先将group_type表中的数据作为key,然后用person_inf中的name进行value插入
        for(int i=0;i<tempName.size();i++){
            if(groupMap.containsKey(tempGroup.get(i))){
                ArrayList<String> temp=new ArrayList<String>();
                temp=groupMap.get(tempGroup.get(i));
                temp.add(tempName.get(i));
                groupMap.put(tempGroup.get(i),temp);
            }
            else{
                ArrayList<String> newGroup=new ArrayList<String>();
                newGroup.add(tempName.get(i));
                groupMap.put(tempGroup.get(i), newGroup);
            }
        }
        return groupMap;
    }
    //这个方法很搞笑，要改过来！！！
    public static ArrayList<String> getAllGroup(){
        ArrayList<String> tempAllGroup=new ArrayList<>();
        Cursor groupTypeCursor=contentResolver.query(Group_Set.group_Set.GROUP_SET_URI,new String[]{"Type"},null,null,null);
        getSQLiteData(groupTypeCursor,tempAllGroup);
        //如果数据库是新建的，不包含defaultfroup，则将defaultgroup插入数据库中group_type表
        if(!tempAllGroup.contains(res.getString(R.string.default_group))){
            ContentValues values=new ContentValues();
            values.put(Group_Set.group_Set.TYPE,res.getString(R.string.default_group));
            contentResolver.insert(Group_Set.group_Set.GROUP_SET_URI,values);
            tempAllGroup.add(res.getString(R.string.default_group));
            /*tempAllGroup.clear();
            groupTypeCursor=contentResolver.query(Group_Set.group_Set.GROUP_SET_URI,new String[]{"Type"},null,null,null);
            getSQLiteData(groupTypeCursor,tempAllGroup);*/
        }
        groupTypeCursor.close();
        return tempAllGroup;
    }

    /*public static ArrayList<String> getAllGroup(){        //为选择分组，删除分组，合并分组提供group数据
        ArrayList<String> tempAllGroup=new ArrayList<>();
        Cursor groupTypeCursor=contentResolver.query(Group_Set.group_Set.GROUP_SET_URI,new String[]{"Type"},null,null,null);
        getSQLiteData(groupTypeCursor,tempAllGroup);
        groupTypeCursor.close();
        return tempAllGroup;
    }*/


    public HashMap<String,String> queryForHeadPortrait(){
        ArrayList<String> tempName=new ArrayList<>();
        ArrayList<String> tempPath=new ArrayList<>();
        Cursor nameCursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"Name"},null,null,null);
        Cursor headCursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"Head_Portrait"},null,null,null);
        getSQLiteData(nameCursor,tempName);
        getSQLiteData(headCursor,tempPath);
        for(int i=0;i<tempName.size();i++){
            headPortrait_Map.put(tempName.get(i),tempPath.get(i));
        }
        return headPortrait_Map;
    }


    public ArrayList<String> queryForCollectedInfo(){
        ArrayList<String> tempName=new ArrayList<>();
        Cursor cursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"Name"}
                ,"IsCollect=1",null,null);
        while(cursor.moveToNext()){
            tempName.add(cursor.getString(0));
        }
        return tempName;
    }
    //视图相关
   /*public HashMap<String,String> queryForHeadPortrait(){
       ArrayList<String> tempName=new ArrayList<>();
       ArrayList<String> tempPath=new ArrayList<>();
       SQLiteDatabase db = openOrCreateDatabase("RunningMan.db3", Context.MODE_PRIVATE, null);
       Cursor nameCursor=db.query("View1",new String[]{"Name"},null,null,null,null,null);
       Cursor headCursor=db.query("View2",new String[]{"Head_Portrait"},null,null,null,null,null);
       getSQLiteData(nameCursor,tempName);
       getSQLiteData(headCursor,tempPath);
       for(int i=0;i<tempName.size();i++){
           headPortrait_Map.put(tempName.get(i),tempPath.get(i));
       }
       return headPortrait_Map;
   }

    public ArrayList<String> queryForCollectedInfo(){
        ArrayList<String> names=new ArrayList<>();
        SQLiteDatabase db = openOrCreateDatabase("RunningMan.db3", Context.MODE_PRIVATE, null);
        Cursor c = db.query("View",new String[]{"Name"},null,null,null,null,null);
        while (c.moveToNext()) {
            names.add(c.getString(0));
        }
        c.close();
        db.close();
        return names;
    }*/

    public static boolean isCollected(String name){
        Cursor cursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"IsCollect"}
                ,"Name=?",new String[]{name},null);
        Integer temp=0;
        while (cursor.moveToNext()){
            temp=cursor.getInt(0);
        }
        cursor.close();
        if(temp==1)
            return true;
        else
            return false;
    }

    public static PersonInfo getPersonAllInfo(String personName){
        Cursor cursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"Name","Tel","GroupType","Email"
                ,"Head_Portrait","IsCollect","Sexy","Age","Birthday"}
                ,"Name=?",new String[]{personName},null);
        PersonInfo.Builder builder=new PersonInfo.Builder();
        PersonInfo personInfo=null;
        while(cursor.moveToNext()){
            personInfo=builder.name(cursor.getString(0)).
                    tel(cursor.getString(1)).
                    group(cursor.getString(2)).
                    email(cursor.getString(3)).
                    headPortrait(cursor.getString(4)).
                    isCollected(cursor.getInt(5)).
                    sexy(cursor.getString(6)).
                    age(cursor.getInt(7)).
                    birthday(cursor.getString(8)).
                    build();
        }
        cursor.close();
        return personInfo;
    }
}
