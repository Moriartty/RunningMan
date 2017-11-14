package com.moriarty.user.runningman.DataBase.DatabaseHelper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 16-8-28.
 */
public class MyDatabaseHelper extends SQLiteOpenHelper {
    final String CREATE_TABLE_SQL1="create table person_inf(_id integer primary key autoincrement, Name varchar(50)" +
            ",Tel varchar(50), GroupType varchar(50),Email varchar(50), Head_Portrait varchar(50), IsCollect integer" +
            ", Sexy varchar(50), Age integer,Birthday text)";

    final String CREATE_TABLE_SQL2="create table group_type(_id integer primary key autoincrement, Type varchar(50))";

    final String CREATE_TABLE_SQL3="create table movement_data(_id integer primary key autoincrement, Time text, " +
            "Walk_Step integer, Walk_Distance double, Run_Step integer, Run_Distance double, Number_Of_Pedals integer, " +
            "Ride_Distance double, Ride_Time integer, Calories double)";
    final String CREATE_TABLE_SQL4="create table activity_data(_id integer primary key autoincrement, Uuid text, Leader text, " +
            "Title text, Time text, Address text, Tel text, People text, Content text, Img_Url text)";
    //定义触发器，当对某表进行删除时，会将对应表上的成员转移到默认分组
    final String CREATE_TRIGGER1="create trigger group_delete " +
            "before delete on group_type " +
            "for each row " +
            "begin " +
            "update person_inf set GroupType='默认分组' where GroupType=old.Type; " +
            "end;";

    public MyDatabaseHelper(Context context, String name, int version){
        super(context,name,null,version);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_SQL1);
        db.execSQL(CREATE_TABLE_SQL2);
        db.execSQL(CREATE_TABLE_SQL3);
        db.execSQL(CREATE_TABLE_SQL4);
        db.execSQL(CREATE_TRIGGER1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
