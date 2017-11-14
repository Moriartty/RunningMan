package com.moriarty.user.runningman.Fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moriarty.user.runningman.Activity.ChooseContacts;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Adapter.Category_TopListAdapter;
import com.moriarty.user.runningman.Adapter.MyGroupAdapter;
import com.moriarty.user.runningman.Others.HandleContact;
import com.moriarty.user.runningman.Others.PopupMenuManager;
import com.moriarty.user.runningman.Presenter.MainActivityPresenter;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.QueryContactsService;
import com.moriarty.user.runningman.Thread.AsyncImageLoader;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by user on 17-8-9.
 */
public class MyGroupFragment extends Fragment {
    private static final String currentTag="MyGroupFragment:";
    public static ArrayList<String> groupName = new ArrayList<>();
    public static ArrayList<ArrayList<String>> groupContent = new ArrayList<ArrayList<String>>();
    public static ArrayList<String> collectedInfo=new ArrayList<>();
    public static HashMap<String,String> headPortraits=new HashMap<>();
    public static ArrayList<Boolean> group_CB = new ArrayList<>();
    public static ArrayList<ArrayList<Boolean>> child_CB = new ArrayList<>();
    RecyclerView groupSettingView;
    private final static String SOURCEFLAG="source";
    private Category_TopListAdapter mAdapter;
    BroadcastReceiver receiver;
    ExpandableListView list;
    MyGroupAdapter myGroupAdapter;
    BroadcastReceiver receiver2;
    View v;
    private String currentType;


    Context context;

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        context=getActivity();

        IntentFilter intentFilter=new IntentFilter("BC_FOUR");    //动态广播,接受来自GroupSettingService的广播
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(HandleContact.alertDialog!=null)
                    HandleContact.alertDialog.dismiss();//关闭AlertDialog
            }
        };
        context.registerReceiver(receiver,intentFilter);

        currentType=getArguments().getString(SOURCEFLAG);


        v = inflater.inflate(R.layout.fragment_mygroup_layout1, null);
        groupSettingView = (RecyclerView) v.findViewById(R.id.groupsetting_recyclerview);
        groupSettingView.setLayoutManager(new GridLayoutManager(context, 3));
        groupSettingView.setAdapter(mAdapter = new Category_TopListAdapter(context));

        dataPrepared();
        if(currentType.equals(getString(R.string.source_contactslistfragment))){
            groupSettingView.setVisibility(View.VISIBLE);
            initDataFrom1();
        }
        else if(currentType.equals(getString(R.string.source_choosecontacts))){
            initDataFrom2();
        }
        initExpandList();

        return v;
    }

    public static MyGroupFragment newInstance(String text){
        MyGroupFragment fragment = new MyGroupFragment();
        Bundle bundle = new Bundle();
        bundle.putString(SOURCEFLAG, text);
        //fragment保存参数，传入一个Bundle对象
        fragment.setArguments(bundle);
        return fragment;
    }

    private void initExpandList(){
        list = (ExpandableListView) v.findViewById(R.id.list);
        myGroupAdapter=new MyGroupAdapter(context,groupName,groupContent,collectedInfo,
                headPortraits,currentType);
        list.setAdapter(myGroupAdapter);

        if(currentType.equals(getString(R.string.source_contactslistfragment))){
            list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    if(groupPosition==0)
                        return true;  //设置为true可以屏蔽点击事件
                    else
                        return false;
                }
            });
            list.expandGroup(0);
        }
        else if(currentType.equals(getString(R.string.source_choosecontacts))){
           /* for(int i=0;i<groupName.size();i++)
                list.expandGroup(i);*/
        }
        list.expandGroup(0);
    }


    private void dataPrepared(){
        groupName.clear();
        groupContent.clear();
        headPortraits.clear();
        collectedInfo.clear();
        group_CB.clear();
        child_CB.clear();
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        getContext().unregisterReceiver(receiver);
        //getContext().unregisterReceiver(receiver2);
    }

    public void initDataFrom1(){
        ArrayList<String> temp=new ArrayList<>();
        temp.add(getResources().getString(R.string.my_collect));
        temp.addAll(ContactsListFragment.groupName);
        groupName.addAll(temp);
        ArrayList<ArrayList<String>> temp2=new ArrayList<>();
        temp2.add(ContactsListFragment.collectedInfo);
        temp2.addAll(ContactsListFragment.groupContent);
        groupContent.addAll(temp2);
        headPortraits.putAll(ContactsListFragment.headPortraits);
    }
    public void initDataFrom2(){
        ArrayList<String> temp=new ArrayList<>();
        temp.add(getResources().getString(R.string.my_collect));
        temp.addAll(ChooseContacts.groupName);
        groupName.addAll(temp);
        ArrayList<ArrayList<String>> temp2=new ArrayList<>();
        temp2.add(ChooseContacts.collectedInfo);
        temp2.addAll(ChooseContacts.groupContent);
        groupContent.addAll(temp2);
        headPortraits.putAll(ChooseContacts.headPortraits);
        initCheckBox();
    }

    private void initCheckBox(){
        for(int i=0;i<groupName.size();i++){
            group_CB.add(false);
            ArrayList<Boolean> temp_child=new ArrayList<>();
            for(int j=0;j<groupContent.get(i).size();j++){
                temp_child.add(false);
            }
            child_CB.add(temp_child);
        }
    }

}
