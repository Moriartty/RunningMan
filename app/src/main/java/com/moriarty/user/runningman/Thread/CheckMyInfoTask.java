package com.moriarty.user.runningman.Thread;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Object.User;
import com.moriarty.user.runningman.Others.SignalManager;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.QueryContactsService;
import com.moriarty.user.runningman.Utils.PreferenceUtil;
import com.moriarty.user.runningman.Utils.Utils;

import java.util.HashMap;


/**
 * Created by user on 16-11-6.
 */
public class CheckMyInfoTask extends AsyncTask<Void,Void,Boolean> {
    SharedPreferences myInfo,userInfo;
    Context mcontext;
    Handler handler;
    PersonInfo myself;
    public CheckMyInfoTask(Context context, Handler handler){
        this.mcontext=context;
        myInfo=mcontext.getSharedPreferences("myself",mcontext.MODE_PRIVATE);  //获取toolbar上需要显示的个人信息
        userInfo=mcontext.getSharedPreferences("user",mcontext.MODE_PRIVATE);
        this.handler=handler;
        myself=new PersonInfo();
    }
    @Override
    protected Boolean doInBackground(Void... params) {
        myself = PreferenceUtil.getMyInformation(mcontext,myInfo);
        if(myself.getTel()==null||myself.getTel().equals("")){
            return writeInMyself();
        }
        return true;
    }
    @Override
    protected void onPostExecute(Boolean has) {
        /*if (!has) {
            AlertDialog.Builder builder=new AlertDialog.Builder(mcontext)
                    .setIcon(mcontext.getResources().getDrawable(R.drawable.ic_dialog_alert_holo_light))
                    .setMessage(mcontext.getResources().getString(R.string.lack_of_myself_info))
                    .setTitle("注意");
            builder.setPositiveButton(mcontext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Log.d(MainActivity.TAG,"write myself_info signal");
                    Message message=new Message();
                    message.what= SignalManager.writeMyselfInfo_signal;
                    handler.sendMessage(message);
                }
            });
            builder.setNegativeButton(mcontext.getResources().getString(R.string.cancel),null).create().show();
            builder.setOnCancelListener(null);
        }*/
        if(!has)
            Toast.makeText(mcontext,mcontext.getResources().getString(R.string.write_in_myself_failed)
                    ,Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPreExecute(){

    }

    private Boolean writeInMyself(){
        User user=PreferenceUtil.getUserInfoExcepPW(mcontext,userInfo);
        return PreferenceUtil.writeInMyselfPre(myInfo.edit(),mcontext, Utils.user2PersonInfo(user));
    }

}
