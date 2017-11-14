package com.moriarty.user.runningman.Service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteException;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;


import com.moriarty.user.runningman.DataBase.ToolClass.Group_Set;
import com.moriarty.user.runningman.DataBase.ToolClass.Person_Inf;
import com.moriarty.user.runningman.Others.BroadcastManager;
import com.moriarty.user.runningman.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 16-8-15.
 */
public class GroupSettingService extends Service {
    String newGroupName;
    ArrayList<String> deleteGroup_Recode=new ArrayList<>();
    ArrayList<String> uniteGroup_Recode=new ArrayList<>();
    HashMap<String,ArrayList<String>> groupMap=new HashMap<String,ArrayList<String>>();
    int groupsetting_flag;
    ArrayList<String> contactPackage=new ArrayList<>();
    ContentResolver contentResolver;
    BroadcastManager broadcastManager=new BroadcastManager();

    public class GroupSettingBinder extends Binder{
        public GroupSettingService getService(){
            return GroupSettingService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        contentResolver=getContentResolver();
        groupsetting_flag=intent.getIntExtra("groupsetting_flag",0);
        switch(groupsetting_flag){
            //各个编号对应着不同的指令
            case 0:newGroupName=intent.getStringExtra("newGroupName");break;
            case 1:deleteGroup_Recode.addAll(intent.getStringArrayListExtra("deleteIntent"));break;
            case 2:
                uniteGroup_Recode.addAll(intent.getStringArrayListExtra("uniteIntent"));
                newGroupName=intent.getStringExtra("uniteIntent_Edit");
                break;
            case 3:contactPackage=intent.getStringArrayListExtra("choose");break;
        }
        return new GroupSettingBinder();
    }

    private void groupTableValidate(){
        ArrayList<String> tempAllGroup=new ArrayList<>();
        Cursor groupTypeCursor=contentResolver.query(Group_Set.group_Set.GROUP_SET_URI,new String[]{"Type"},null,null,null);
        getSQLiteData(groupTypeCursor,tempAllGroup);
        if(!tempAllGroup.contains(getString(R.string.default_group))){
            ContentValues values=new ContentValues();
            values.put(Group_Set.group_Set.TYPE,getString(R.string.default_group));
            contentResolver.insert(Group_Set.group_Set.GROUP_SET_URI,values);
        }
        groupTypeCursor.close();
    }
    private boolean insertData(String newGroupName){
        try{
            if(!QueryContactsService.getAllGroup().contains(newGroupName))   //如果数据库中不包含这个分组，就新建
            {
                ContentValues values=new ContentValues();
                values.put(Group_Set.group_Set.TYPE,newGroupName);
                contentResolver.insert(Group_Set.group_Set.GROUP_SET_URI,values);
            }
            else
                Toast.makeText(GroupSettingService.this,getResources().getString(R.string.alert_addnewgroup),Toast.LENGTH_SHORT).show();
            return true;
        }catch (SQLiteException e){
            e.printStackTrace();
            return false;
        }
    }

    private Map<String,ArrayList<String>> queryForPeople(){
        ArrayList<String> tempName=new ArrayList<String>();
        ArrayList<String> tempGroup=new ArrayList<String>();

        //查询person_inf中所有Name
        Cursor nameCursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"Name"},null,null,null);
        //查询person_inf中所有GroupType
        Cursor groupTypeCursor=contentResolver.query(Person_Inf.person_Inf.PERSON_INF_URI,new String[]{"GroupType"},null,null,null);
        getSQLiteData(nameCursor,tempName);
        getSQLiteData(groupTypeCursor,tempGroup);
        nameCursor.close();
        groupTypeCursor.close();

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

    private void getSQLiteData(Cursor cursor,ArrayList<String> temp){
        while(cursor.moveToNext()){
            temp.add(cursor.getString(0));
        }
    }

    public boolean deleteGroupFromSQLite(){
        boolean returnValue;
        queryForPeople();
        returnValue=deleteGroup(deleteGroup_Recode,getString(R.string.default_group));
        return returnValue;
    }
    public boolean uniteGroupFromSQLite(){   //合并分组，先新建分组，再对原分组的数据进行操作
        boolean returnValue;
        queryForPeople();
        insertData(newGroupName);
        returnValue=UniteGroup(uniteGroup_Recode,newGroupName);
        return returnValue;
    }
    public boolean addGroupIntoSQLite(){
        boolean returnValue;
        groupTableValidate();
        returnValue = insertData(newGroupName);
        sendBroadcast2ContactsListFragment();
        return returnValue;
    }
    public void sendBroadcast2ContactsListFragment(){
        //向ContactsListFragment发送更新视图的广播
        broadcastManager.sendBroadcast(this,3);
    }
    public void sendBroadcast2Category(){
        broadcastManager.sendBroadcast(this,4);
    }

    public boolean deleteGroup(ArrayList<String> recode,String newGroupName){
        for(int i=0;i<recode.size();i++){
            contentResolver.delete(Group_Set.group_Set.GROUP_SET_URI,"Type=?",new String[]{recode.get(i)});
        }
        sendBroadcast2ContactsListFragment();
        return true;
    }

    public boolean UniteGroup(ArrayList<String> recode,String newgroupname){    //删除或合并分组都要进行的基本操作
        ArrayList<String> collect=new ArrayList<String>();
        for(int i=0;i<recode.size();i++){
            if(groupMap.containsKey(recode.get(i))){
                collect.addAll(groupMap.get(recode.get(i)));  //collect集合只获取需要改动数据的联系人
            }
        }
        try{
            for(int i=0;i<collect.size();i++){
                ContentValues values=new ContentValues();
                values.put(Person_Inf.person_Inf.GROUPTYPE,newgroupname);
                contentResolver.update(Person_Inf.person_Inf.PERSON_INF_URI,values,"Name=?",new String[]{collect.get(i)});
            }
            for(int i=0;i<recode.size();i++){
                contentResolver.delete(Group_Set.group_Set.GROUP_SET_URI,"Type=?",new String[]{recode.get(i)});
            }
            sendBroadcast2ContactsListFragment();
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean updateContactAddress(){
        try{
            ContentValues values=new ContentValues();
            values.put(Person_Inf.person_Inf.GROUPTYPE,contactPackage.get(1));
            contentResolver.update(Person_Inf.person_Inf.PERSON_INF_URI,values,"Name=?",new String[]{contactPackage.get(0)});
            sendBroadcast2Category();  //发送广播关闭AlertDialog
            sendBroadcast2ContactsListFragment();  //发送广播更新视图
            return true;
        }catch(SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
