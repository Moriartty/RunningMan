package com.moriarty.user.runningman.Service;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.moriarty.user.runningman.Activity.ChooseContacts;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Adapter.MyGroupAdapter;
import com.moriarty.user.runningman.Fragment.ContactsListFragment;
import com.moriarty.user.runningman.Fragment.MyGroupFragment;
import com.moriarty.user.runningman.Others.BroadcastManager;
import com.moriarty.user.runningman.Presenter.MainActivityPresenter;
import com.moriarty.user.runningman.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by user on 17-9-14.
 */
public class QueryIntentService extends IntentService {
    private QueryContactsService queryContactsService;     //查询服务对象
    public ArrayList<String> names = new ArrayList<String>();   //存储联系人姓名
    public ArrayList<String> groupName=new ArrayList<>(); //记录所有小组名
    public ArrayList<ArrayList<String>> groupContent=new ArrayList<>();  //记录每个小组的每个成员
    public HashMap<String,String> headPortraits=new HashMap<>();  //记录每个人的头像
    public ArrayList<String> collectedInfo=new ArrayList<>();
    String flag;
    Context context;

    //需要一个无参构造函数，否则会报java.lang.InstantiationException错误
    public QueryIntentService(){
        super("moriarty");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        context=this;
        flag=intent.getStringExtra("from");
        Intent queryIntent=new Intent();
        queryIntent.setAction("com.moriarty.service.QUERYCONTACTSSERVICE");
        queryIntent.setPackage(getPackageName());
        bindService(queryIntent,connection,BIND_AUTO_CREATE);
    }

    private ServiceConnection connection=new ServiceConnection() {    //查询服务的连接对象
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            queryContactsService=((QueryContactsService.QueryContactsBinder)service).getService();
            names.addAll(queryContactsService.queryContactsName());

            Log.d(MainActivity.TAG,"namesize:"+names.size());
            getGroupName(queryContactsService.queryForGroup());    //在查询服务中生成了包含所有信息的Map
            headPortraits.putAll(queryContactsService.queryForHeadPortrait());  //获取每个人对应头像的URI
            collectedInfo.addAll(queryContactsService.queryForCollectedInfo());
            //然后开始异步加载和缓存头像资源
            unbindService(connection);         // 在下次连接服务之前必须先断开此次服务
            //Log.d(TAG,"QueryContactsService is unBinded");
            QueryContactsService.names.clear();     //每次查询完数据库要清空数据库静态集合变量！！！
            QueryContactsService.details.clear();
            matchSource();
            new BroadcastManager().sendBroadCast_Two_WithValue(context,flag);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
    private void matchSource(){
        if(flag.equals(getString(R.string.source_contactslistfragment))){
            clear_ContactsListFragmentData();
            ContactsListFragment.names.addAll(names);
            ContactsListFragment.headPortraits.putAll(headPortraits);
            ContactsListFragment.collectedInfo.addAll(collectedInfo);
            ContactsListFragment.groupName.addAll(groupName);
            ContactsListFragment.groupContent.addAll(groupContent);
        }
        else if(flag.equals(getString(R.string.source_choosecontacts))){
            clear_ChooseContactsData();
            ChooseContacts.names.addAll(names);
            ChooseContacts.headPortraits.putAll(headPortraits);
            ChooseContacts.collectedInfo.addAll(collectedInfo);
            ChooseContacts.groupName.addAll(groupName);
            ChooseContacts.groupContent.addAll(groupContent);
        }
        clear();
    }
    private void clear_ContactsListFragmentData(){
        ContactsListFragment.names.clear();
        ContactsListFragment.headPortraits.clear();
        ContactsListFragment.collectedInfo.clear();
        ContactsListFragment.groupName.clear();
        ContactsListFragment.groupContent.clear();
    }
    private void clear_ChooseContactsData(){
        ChooseContacts.names.clear();
        ChooseContacts.headPortraits.clear();
        ChooseContacts.collectedInfo.clear();
        ChooseContacts.groupName.clear();
        ChooseContacts.groupContent.clear();
    }

    private void clear(){
        names.clear();
        headPortraits.clear();
        collectedInfo.clear();
        groupName.clear();
        groupContent.clear();
    }
    private Boolean getGroupName(HashMap<String,ArrayList<String>> groupMap){     //person_inf中的group，不含重复,但可能不完全
        Iterator<String> groupIt=groupMap.keySet().iterator();
        while((groupIt.hasNext())){
            String tempGroupName=groupIt.next();
            groupName.add(tempGroupName);  //获取Map中的GroupName
            groupContent.add(groupMap.get(tempGroupName));  //获取每个Group中的people
        }
        return true;
    }
}
