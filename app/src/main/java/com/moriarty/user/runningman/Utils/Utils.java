package com.moriarty.user.runningman.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Object.User;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Thread.AsyncImageLoader;
import com.moriarty.user.svprogress.SVProgressHUD;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

/**
 * Created by user on 17-9-1.
 */
public class Utils {

    public static boolean isNull(String string){
        if(string==null||string.equals("")||string.equals("None"))
            return true;
        else
            return false;
    }


    public static boolean isCorrect(Context context,PersonInfo personInfo){
        Pattern pattern = Pattern.compile("[0-9]*");
        if (personInfo.getTel()==null||personInfo.getTel().toString().equals("")){
            //SVProgressHUD.showInfoWithStatus(context,context.getString(R.string.noedit_tips1));
            //Toast.makeText(context, context.getString(R.string.noedit_tips1), Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            String phoneNum=personInfo.getTel().toString().replace(" ","");
            //这里用于判断输入的电话号码的格式是否全为数字。
            //关于正则表达式：+表示1个或多个（如"3"或"225"），*表示0个或多个（[0-9]*）（如""或"1"或"22"），?表示0个或1个([0-9]?)(如""或"7")
            if(pattern.matcher(phoneNum).matches())
                return true;
            else
            {
                //Toast.makeText(context,context.getString(R.string.noedit_tips2),Toast.LENGTH_SHORT).show();
                return false;
            }
        }
    }

    public static boolean isPhoneCorrect(Context context,String phone){
        Pattern pattern = Pattern.compile("[0-9]*");
        if (phone.equals("")){
            Toast.makeText(context, context.getString(R.string.noedit_tips1), Toast.LENGTH_SHORT).show();
            return false;
        }
        else{
            String phoneNum=phone.replace(" ","");
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

    public static PersonInfo user2PersonInfo(User user){
        PersonInfo.Builder builder=new PersonInfo.Builder();
        return builder.name(user.getUserName()).tel(user.getTel()).email(user.getEmail()).build();
    }

}
