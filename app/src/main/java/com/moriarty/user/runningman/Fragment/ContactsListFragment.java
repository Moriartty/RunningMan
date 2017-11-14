package com.moriarty.user.runningman.Fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.User_Defind.Fuck.BaseFragment;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by user on 17-9-14.
 */
public class ContactsListFragment extends BaseFragment {
    private View view;
    TextView to_where_group;
    TextView to_where_default;
    FragmentManager fm;
    FragmentTransaction ft;
    Fragment mCurrentFragment;
    String[] fragTag;
    int mCurTag=0;
    Context context;
    BroadcastReceiver receiver2,receiver3;
    MyGroupFragment fragment1;
    Normal_List_Contacts fragment2;

    public static ArrayList<String> names = new ArrayList<>();
    public static ArrayList<String> groupName = new ArrayList<>();
    public static ArrayList<ArrayList<String>> groupContent = new ArrayList<ArrayList<String>>();
    public static ArrayList<String> collectedInfo=new ArrayList<>();
    public static HashMap<String,String> headPortraits=new HashMap<>();
    @Override
    public View initView(LayoutInflater inflater) {
        context=getActivity();
        fragTag=new String[]{"group","default"};
        if (view == null) {
            view = View.inflate(getActivity(), R.layout.fragment_contacts_list, null);
        }
        startQuery();
        to_where_group=(TextView)view.findViewById(R.id.to_where_group);
        to_where_default=(TextView)view.findViewById(R.id.to_where_default);
        to_where_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(fragTag[mCurTag],fragTag[1-mCurTag]);
                styleChange(v);
            }
        });
        to_where_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchFragment(fragTag[mCurTag],fragTag[1-mCurTag]);
                styleChange(v);
            }
        });

        register_BC();
        return view;
    }
    private void register_BC(){
        IntentFilter intentFilter2=new IntentFilter("BC_TWO");    //动态广播，数据已经准备好了，准备更新fragment
        receiver2=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getStringExtra("to").equals(getString(R.string.source_contactslistfragment))
                        ||intent.getStringExtra("to").equals(getString(R.string.source_addcontactspresenter))){
                    initFragments();
                }
            }
        };
        context.registerReceiver(receiver2,intentFilter2);

        IntentFilter intentFilter3=new IntentFilter("BC_THREE");
        //收到联系人数据变动或分组数据变动后发送的广播，
        //突然想到这里还可以用ContentOnserver来处理，就不需要那么多广播了，哎，蠢了蠢了！
        receiver3=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                startQuery();
            }
        };
        context.registerReceiver(receiver3,intentFilter3);
    }

    private void styleChange(View v){
        switch(v.getId()){
            case R.id.to_where_group:
                to_where_group.setTextColor(getResources().getColor(R.color.white));
                to_where_group.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                to_where_default.setTextColor(getResources().getColor(R.color.colorPrimary));
                to_where_default.setBackgroundColor(getResources().getColor(R.color.white));
                break;
            case R.id.to_where_default:
                to_where_default.setTextColor(getResources().getColor(R.color.white));
                to_where_default.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                to_where_group.setTextColor(getResources().getColor(R.color.colorPrimary));
                to_where_group.setBackgroundColor(getResources().getColor(R.color.white));
                break;
        }
    }

    public void startQuery(){
        Intent query=new Intent();
        query.setAction("com.moriarty.service.QUERYINTENTSERVICE");
        query.setPackage(context.getPackageName());
        query.putExtra("from",context.getString(R.string.source_contactslistfragment));
        context.startService(query);
    }

    private void initFragments(){
        fm=getActivity().getSupportFragmentManager();
        ft=fm.beginTransaction();
        if(fragment1!=null&&fragment2!=null) {
            //处理添加或修改联系人之后ContactsListFragment数据更新
            ft.remove(fragment1).remove(fragment2);
        }
        fragment1 = MyGroupFragment.newInstance(getString(R.string.source_contactslistfragment));
        fragment2 = Normal_List_Contacts.newInstance(getString(R.string.source_contactslistfragment));
        Fragment[] fragments=new Fragment[]{fragment1,fragment2};
        ft.add(R.id.contacts_list_fragment,fragment1,fragTag[0])
                .add(R.id.contacts_list_fragment,fragment2,fragTag[1])
                .hide(fragments[1-mCurTag]).commitAllowingStateLoss();
    }

    private void switchFragment(String fromTag,String toTag){
        Fragment from = fm.findFragmentByTag(fromTag);
        Fragment to = fm.findFragmentByTag(toTag);
        mCurTag=1-mCurTag;
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
    @Override
    public void onDestroy(){
        super.onDestroy();
        context.unregisterReceiver(receiver2);
        context.unregisterReceiver(receiver3);
    }
}
