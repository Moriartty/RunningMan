package com.moriarty.user.runningman.Others;

import android.content.Context;
import android.content.Intent;

/**
 * Created by user on 17-8-10.
 */
public class BroadcastManager {
    public void sendBroadcast(Context context, int flag){
        Intent changestate=new Intent();
        switch (flag){
            case 1:
                changestate.setAction("BC_ONE");break;   //向MainActivity发送更新全局界面的广播
            case 2:
                changestate.setAction("BC_TWO");break;    //MainActivity发送更新fragment的广播
            case 3:
                changestate.setAction("BC_THREE");break;
            case 4:
                changestate.setAction("BC_FOUR");break;  //更新自定义分组的分组列表
            case 5:
                changestate.setAction("BC_FIVE");break; //向MainActivity的Toolbar和navigation发送广播
            case 6:
                changestate.setAction("BC_SIX");break; //向Person_InfoCard发送更新联系人信息广播
            case 7:
                changestate.setAction("BC_SEVEN");break;//向SportsPartyFragment发送跟心数据的广播
            case 8:
                changestate.setAction("BC_EIGHT");break;
        }
        context.sendBroadcast(changestate);
    }
    public void sendBroadCast_Two_WithValue(Context context,String value){
        Intent intent=new Intent();
        intent.putExtra("to",value);
        intent.setAction("BC_TWO");
        context.sendBroadcast(intent);
    }
    public void sendBroadCast_Sixth_WithValue(Context context,String name){
        Intent changePerson_Info_state=new Intent();
        changePerson_Info_state.putExtra("Name",name);
        changePerson_Info_state.setAction("BC_SIX");
        context.sendBroadcast(changePerson_Info_state);
    }
}
