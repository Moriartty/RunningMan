package com.moriarty.user.runningman.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.moriarty.user.runningman.Fragment.MyGroupFragment;
import com.moriarty.user.runningman.Fragment.Normal_List_Contacts;
import com.moriarty.user.runningman.R;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 17-9-13.
 */
public class ChooseContacts extends AppCompatActivity {
    FragmentManager fm;
    FragmentTransaction ft;
    FloatingActionButton changeType;
    Fragment mCurrentFragment;
    String[] fragTag;
    int mCurTag=0;
    Toolbar toolbar;
    BroadcastReceiver receiver;
    public static ArrayList<String> names = new ArrayList<String>();   //存储联系人姓名
    public static ArrayList<String> groupName=new ArrayList<>(); //记录所有小组名
    public static ArrayList<ArrayList<String>> groupContent=new ArrayList<>();  //记录每个小组的每个成员
    public static HashMap<String,String> headPortraits=new HashMap<>();  //记录每个人的头像
    public static ArrayList<String> collectedInfo=new ArrayList<>();
    public static ArrayList<String> selectedFromEntity=new ArrayList<>();//记录Entity中选中的人员
    public static ArrayList<String> selectedFromGroup=new ArrayList<>();//记录Group中选中的人员

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choose_contacts_main);

        initTools();
        resetRecodeList();
        startQuery();

        IntentFilter intentFilter=new IntentFilter("BC_TWO");
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("to").equals(getString(R.string.source_choosecontacts)))
                    initFragments();
            }
        };
        registerReceiver(receiver,intentFilter);

        fragTag=new String[]{getString(R.string.normal_list_contacts),getString(R.string.category_list_contacts)};
        toolbar.setTitle(fragTag[mCurTag]);
        changeType.setOnClickListener(myListener);
        setSupportActionBar(toolbar);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initFragments(){
        fm=getSupportFragmentManager();
        ft=fm.beginTransaction();
        //Normal_List_Contacts normal=new Normal_List_Contacts();
        Normal_List_Contacts normal=
                Normal_List_Contacts.newInstance(getString(R.string.source_choosecontacts));
        MyGroupFragment category=MyGroupFragment.newInstance(getString(R.string.source_choosecontacts));
        ft.add(R.id.choose_contacts_fragment,normal,fragTag[0])
                .add(R.id.choose_contacts_fragment,category,fragTag[1])
                .hide(category).commit();
    }

    private void initTools(){
        toolbar=(Toolbar)findViewById(R.id.choose_contacts_toolbar);
        changeType= (FloatingActionButton)findViewById(R.id.change_category_type);
    }
    private void resetRecodeList(){
        Initiate_Activities.selected.clear();
        selectedFromEntity.clear();
        selectedFromGroup.clear();
    }

    private View.OnClickListener myListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.change_category_type:
                    switchFragment(fragTag[mCurTag],fragTag[1-mCurTag]);
                    break;
            }
        }
    };

    private void startQuery(){
        Intent query=new Intent();
        query.setAction("com.moriarty.service.QUERYINTENTSERVICE");
        query.setPackage(getPackageName());
        query.putExtra("from",getString(R.string.source_choosecontacts));
        startService(query);
    }

    private void switchFragment(String fromTag,String toTag){
        Fragment from = fm.findFragmentByTag(fromTag);
        Fragment to = fm.findFragmentByTag(toTag);
        mCurTag=1-mCurTag;
        toolbar.setTitle(fragTag[mCurTag]);
        if (mCurrentFragment != to) {
            mCurrentFragment = to;
            FragmentTransaction transaction = fm.beginTransaction();
            if (!to.isAdded()) {//判断是否被添加到了Activity里面去了
                transaction.hide(from).add(R.id.choose_contacts_fragment, to).commit();
            } else {
                transaction.hide(from).show(to).commit();
            }
        }
    }
    private ArrayList<String> filter(){
        ArrayList<String> selected=new ArrayList<>();
        ArrayList<String> temp=new ArrayList<>(selectedFromEntity);
        getSelectedFromGroup();
        temp.retainAll(selectedFromGroup);
        selected.addAll(selectedFromEntity);
        selectedFromGroup.removeAll(temp);
        selected.addAll(selectedFromGroup);
        return selected;
    }
    private void getSelectedFromGroup(){
        Log.d(MainActivity.TAG,"group size is: "+MyGroupFragment.group_CB.size());
        for(int i=0;i<MyGroupFragment.group_CB.size();i++){
            Log.d(MainActivity.TAG,"child size is: "+MyGroupFragment.child_CB.get(i).size());
            for(int j=0;j<MyGroupFragment.child_CB.get(i).size();j++){
                Log.d(MainActivity.TAG,"j = : "+j);
                if(MyGroupFragment.child_CB.get(i).get(j))
                    selectedFromGroup.add(MyGroupFragment.groupContent.get(i).get(j));
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id == R.id.action_settings){
            Initiate_Activities.selected.addAll(filter());
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
