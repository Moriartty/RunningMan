package com.moriarty.user.runningman.DataBase.ContentProvider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.moriarty.user.runningman.DataBase.DatabaseHelper.MyDatabaseHelper;
import com.moriarty.user.runningman.DataBase.ToolClass.Activity_Data;
import com.moriarty.user.runningman.DataBase.ToolClass.Group_Set;
import com.moriarty.user.runningman.DataBase.ToolClass.Movement_Data;
import com.moriarty.user.runningman.DataBase.ToolClass.Person_Inf;

/**
 * Created by user on 17-8-29.
 */
public class MyContentProvider extends ContentProvider {
    private static UriMatcher matcher=new UriMatcher(UriMatcher.NO_MATCH);
    private static final int PERSON_INF=1;
    private static final int GROUP_SET=2;
    private static final int MOVEMENT_DATA=3;
    private static final int ACTIVITY_DATA=4;
    private MyDatabaseHelper dbHelper;
    private String groupTableName="group_type";
    private String personTableName="person_inf";
    private String movementTableName="movement_data";
    private String activityTableName="activity_data";
    static {
        matcher.addURI(Person_Inf.AYTHORITY,"person_inf",PERSON_INF);
        matcher.addURI(Person_Inf.AYTHORITY,"group_set",GROUP_SET);
        matcher.addURI(Person_Inf.AYTHORITY,"movement_data",MOVEMENT_DATA);
        matcher.addURI(Person_Inf.AYTHORITY,"activity_data",ACTIVITY_DATA);
    }

    @Override
    public boolean onCreate() {
        dbHelper=new MyDatabaseHelper(this.getContext(),"RunningMan.db3",1);
        return true;
    }
    @Nullable
    @Override
    public String getType(Uri uri) {
        return "vnd.android.cursor.dir/com.moriarty.user.runningman.DataBase";
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        switch (matcher.match(uri)){
            case PERSON_INF:
                return db.query(personTableName,projection,selection,selectionArgs,null,null,sortOrder);
            case GROUP_SET:
                return db.query(groupTableName,projection,selection,selectionArgs,null,null,sortOrder);
            case MOVEMENT_DATA:
                return db.query(movementTableName,projection,selection,selectionArgs,null,null,sortOrder);
            case ACTIVITY_DATA:
                return db.query(activityTableName,projection,selection,selectionArgs,null,null,sortOrder);
            default:
                throw new IllegalArgumentException("未知Uri:"+uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        long rowId;
        switch (matcher.match(uri)){
            case PERSON_INF:
                rowId=db.insert(personTableName, Person_Inf.person_Inf._ID,values);
                break;
            case GROUP_SET:
                rowId=db.insert(groupTableName, Group_Set.group_Set._ID,values);
                break;
            case MOVEMENT_DATA:
                rowId=db.insert(movementTableName, Movement_Data.movement_Data._ID,values);
                break;
            case ACTIVITY_DATA:
                rowId=db.insert(activityTableName, Activity_Data.activity_Data._ID,values);
                break;
            default:
                throw new IllegalArgumentException("未知Uri:"+uri);
        }
        if(rowId>0){
            Uri groupUri= ContentUris.withAppendedId(uri,rowId);
            getContext().getContentResolver().notifyChange(groupUri,null);
            return groupUri;
        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db=dbHelper.getReadableDatabase();
        switch (matcher.match(uri)){
            case PERSON_INF:
                db.delete(personTableName,selection,selectionArgs);
                break;
            case GROUP_SET:
                db.delete(groupTableName,selection,selectionArgs);
                break;
            case MOVEMENT_DATA:
                db.delete(movementTableName,selection,selectionArgs);
                break;
            case ACTIVITY_DATA:
                db.delete(activityTableName,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("未知Uri:"+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db=dbHelper.getWritableDatabase();
        switch (matcher.match(uri)){
            case PERSON_INF:
                db.update(personTableName,values,selection,selectionArgs);
                break;
            case GROUP_SET:
                db.update(groupTableName,values,selection,selectionArgs);
                break;
            case MOVEMENT_DATA:
                db.update(movementTableName,values,selection,selectionArgs);
                break;
            case ACTIVITY_DATA:
                db.update(activityTableName,values,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("未知Uri:"+uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return 0;
    }
}
