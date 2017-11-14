package com.moriarty.user.runningman.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.moriarty.user.runningman.Fragment.MainPageFragment;
import com.moriarty.user.runningman.Object.MovementData;
import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Object.User;
import com.moriarty.user.runningman.Pedometer.StepService;
import com.moriarty.user.runningman.R;

/**
 * Created by user on 17-10-10.
 */
public class PreferenceUtil {

    public static boolean writeInMyselfPre(SharedPreferences.Editor editor, Context context, PersonInfo myself){
        //将个人信息写入sharedPreference
        if(myself!=null&&Utils.isCorrect(context,myself)){
            try{
                editor.putString(context.getResources().getString(R.string.perf_name), myself.getName());
                editor.putString(context.getResources().getString(R.string.perf_tel), myself.getTel());
                editor.putString(context.getResources().getString(R.string.perf_email), myself.getEmail());
                editor.putString(context.getResources().getString(R.string.perf_headrait), myself.getHeadPortrait());
                editor.putInt(context.getResources().getString(R.string.perf_age), myself.getAge());
                editor.putString(context.getResources().getString(R.string.perf_sexy), myself.getSexy());
                editor.putString(context.getResources().getString(R.string.perf_birthday),myself.getBirthday());
                editor.commit();
                return true;
            }catch (Exception e){
                return false;
            }
        }
        return false;
    }

    public static boolean writeInUserPre(SharedPreferences.Editor editor, Context context,User user){
        editor.putString(context.getResources().getString(R.string.user_uuid), user.getUuid());
        editor.putString(context.getResources().getString(R.string.user_name), user.getUserName());
        editor.putString(context.getResources().getString(R.string.user_password), user.getPassWord());
        editor.putString(context.getResources().getString(R.string.user_email), user.getEmail());
        editor.putString(context.getResources().getString(R.string.user_tel), user.getTel());
        editor.commit();
        return true;
    }

    public static PersonInfo getMyInformation(Context context,SharedPreferences myInfo){
        PersonInfo.Builder builder=new PersonInfo.Builder();
        PersonInfo myself=builder.name(myInfo.getString(context.getResources().getString(R.string.perf_name),null)).
                tel(myInfo.getString(context.getResources().getString(R.string.perf_tel),null)).
                email(myInfo.getString(context.getResources().getString(R.string.perf_email),null)).
                headPortrait(myInfo.getString(context.getResources().getString(R.string.perf_headrait),"")).
                sexy(myInfo.getString(context.getResources().getString(R.string.perf_sexy),"male")).
                birthday(myInfo.getString(context.getResources().getString(R.string.perf_birthday),"")).
                age(myInfo.getInt(context.getResources().getString(R.string.perf_age),0)).
                build();
        return myself;
    }

    public static User getUserInfoExcepPW(Context context,SharedPreferences userInfo){
        User.Builder builder=new User.Builder();
        return builder.setUserName(userInfo.getString(context.getResources().getString(R.string.user_name),null))
                .setTel(userInfo.getString(context.getResources().getString(R.string.user_tel),null))
                .setEmail(userInfo.getString(context.getResources().getString(R.string.user_email),null)).build();
    }

    public static User getUserInfo(Context context,SharedPreferences userInfo){
        User user=null;
        User.Builder builder=new User.Builder();
        user = builder.setUserName(userInfo.getString(context.getResources().getString(R.string.user_name),null))
                .setTel(userInfo.getString(context.getResources().getString(R.string.user_tel),null))
                .setEmail(userInfo.getString(context.getResources().getString(R.string.user_email),null))
                .setPassword(userInfo.getString(context.getResources().getString(R.string.user_password),null))
                .build();
        return user;
    }

    public static String getUuid(Context context){
        SharedPreferences s=context.getSharedPreferences("user",context.MODE_PRIVATE);
        return s.getString(context.getString(R.string.user_uuid),null);
    }

    public static void resetStatePrefValues(boolean updateDisplay, StepService mService, Context context,
                                            boolean mIsRunning, SharedPreferences.Editor stateEditor){
        if (mService != null && mIsRunning) {
            mService.resetValues();
        }
        MainPageFragment.tv1.setText("0");
        MainPageFragment.tv5.setText("0");
        if (updateDisplay) {
            stateEditor.putInt(context.getResources().getString(R.string.pref_walk_step), 0);
            stateEditor.putInt(context.getResources().getString(R.string.pref_walk_pace), 0);
            stateEditor.putFloat(context.getResources().getString(R.string.pref_walk_distance), 0);
            stateEditor.putFloat(context.getResources().getString(R.string.pref_walk_speed), 0);
            stateEditor.putInt(context.getResources().getString(R.string.pref_run_step),0);
            stateEditor.putInt(context.getResources().getString(R.string.pref_run_pace),0);
            stateEditor.putFloat(context.getResources().getString(R.string.pref_run_distance),0);
            stateEditor.putFloat(context.getResources().getString(R.string.pref_run_speed),0);
            stateEditor.putInt(context.getResources().getString(R.string.pref_number_of_pedals),0);
            stateEditor.putFloat(context.getResources().getString(R.string.pref_ride_distance),0);
            stateEditor.putInt(context.getResources().getString(R.string.pref_ride_time),0);
            stateEditor.putFloat(context.getResources().getString(R.string.pref_calories), 0);
            stateEditor.commit();
        }
    }

    public static void writeInStatePre(SharedPreferences.Editor mStateEditor,Context context,MovementData m){
        mStateEditor.putInt(context.getString(R.string.pref_walk_step), m.getWalk_Step());
        mStateEditor.putInt(context.getString(R.string.pref_walk_pace), m.getWalk_Pace());
        mStateEditor.putFloat(context.getString(R.string.pref_walk_distance), (float) m.getWalk_Distance());
        mStateEditor.putFloat(context.getString(R.string.pref_walk_speed), (float) m.getWalk_Speed());
        mStateEditor.putInt(context.getString(R.string.pref_run_step),m.getRun_Step());
        mStateEditor.putFloat(context.getString(R.string.pref_run_distance),(float) m.getRun_Distance());
        mStateEditor.putInt(context.getString(R.string.pref_number_of_pedals),m.getNumber_Of_Pedals());
        mStateEditor.putFloat(context.getString(R.string.pref_ride_distance),(float)m.getRide_Distance());
        mStateEditor.putFloat(context.getString(R.string.pref_calories), (float) m.getCalories());
        mStateEditor.commit();
    }
}
