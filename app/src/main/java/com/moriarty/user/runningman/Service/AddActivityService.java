package com.moriarty.user.runningman.Service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.DataBase.ToolClass.Activity_Data;
import com.moriarty.user.runningman.Object.ActivityData;
import com.moriarty.user.runningman.Utils.TimeUtil;
import com.moriarty.user.runningman.Utils.Utils;
import com.moriarty.user.runningman.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by user on 17-9-15.
 */
public class AddActivityService extends Service {
    ContentResolver contentResolver;
    static Pattern pattern = Pattern.compile("[0-9]*");
    public class AddActivityBinder extends Binder {
        public AddActivityService getService(){
            return AddActivityService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       // data=(ActivityData) intent.getSerializableExtra("activity_info");
        contentResolver=getContentResolver();
        return new AddActivityBinder();
    }

    public boolean clearTable(){
        contentResolver.delete(Activity_Data.activity_Data.ACTIVITY_DATA_URI,null,null);
        return true;
    }

    public boolean addIntoSQLite(ActivityData data){
        if (isCorrect(this,data)){
            contentResolver.insert(Activity_Data.activity_Data.ACTIVITY_DATA_URI,getContentValues(data));
            return true;
        }
        else
            return false;
    }

    public boolean addListIntoSQLite(ArrayList<ActivityData> datas){
        boolean b=true;
        for(int i=0;i<datas.size();i++)
            b=b&&addIntoSQLite(datas.get(i));
        return b;
    }

    public ArrayList<ActivityData> getDataFromSQLite(){
        ArrayList<ActivityData> list=new ArrayList<>();
        String now= TimeUtil.getNow();
        Log.d(MainActivity.TAG,"now is: "+now);
        Cursor cursor=contentResolver.query(Activity_Data.activity_Data.ACTIVITY_DATA_URI
                ,new String[]{"Title","Time","Address","Tel","People","Content","Img_Url"}
                ,"Time>=?",new String[]{now},"Time");
        if(cursor!=null){
            while(cursor.moveToNext()){
                ActivityData.Builder builder = new ActivityData.Builder()
                        .title(cursor.getString(0)).time(cursor.getString(1))
                        .address(cursor.getString(2)).tel(cursor.getString(3))
                        .people(cursor.getString(4)).content(cursor.getString(5))
                        .img_url(cursor.getString(6));
                list.add(builder.build());
               // Log.d(MainActivity.TAG,"title:"+cursor.getString(0));
            }
            cursor.close();
            return list;
        }
        return null;
    }

    private ContentValues getContentValues(ActivityData data){
        ContentValues values=new ContentValues();
        values.put(Activity_Data.activity_Data.UUID,data.getUuid());
        values.put(Activity_Data.activity_Data.LEADER,data.getLeader());
        values.put(Activity_Data.activity_Data.TITLE,data.getTitle());
        values.put(Activity_Data.activity_Data.TIME,data.getTime());
        values.put(Activity_Data.activity_Data.ADDRESS,data.getAddress());
        values.put(Activity_Data.activity_Data.TEL,data.getTel());
        values.put(Activity_Data.activity_Data.PEOPLE,data.getPeople());
        values.put(Activity_Data.activity_Data.CONTENT,data.getContent());
        //暂时不考虑图片
        //values.put(Activity_Data.activity_Data.IMGURL,data.getImg_url());
        return values;
    }

    public static boolean isCorrect(Context context, ActivityData data){
        if (data.getTitle().toString().equals("")||
                data.tel.toString().equals("")||data.address.toString().equals("")){
            Toast.makeText(context, "你有信息没填写完哦", Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            String phoneNum=data.getTel().toString().replace(" ","");
            //这里用于判断输入的电话号码的格式是否全为数字。
            //关于正则表达式：+表示1个或多个（如"3"或"225"），*表示0个或多个（[0-9]*）（如""或"1"或"22"），?表示0个或1个([0-9]?)(如""或"7")
            if(pattern.matcher(phoneNum).matches())
                return true;
            else
            {
                Toast.makeText(context,context.getString(R.string.noedit_tips2),Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

}
