package com.moriarty.user.runningman.Fragment;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Adapter.ActivitiesGalleryAdapter;
import com.moriarty.user.runningman.DataBase.ToolClass.Activity_Data;
import com.moriarty.user.runningman.Object.ActivityData;
import com.moriarty.user.runningman.Object.ResponseInfoFromNet;
import com.moriarty.user.runningman.Presenter.SPFragmentPresenter;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.AddActivityService;
import com.moriarty.user.runningman.Service.QueryContactsService;
import com.moriarty.user.runningman.User_Defind.DividerItemDecoration;
import com.moriarty.user.runningman.Utils.JsonUtils;
import com.moriarty.user.runningman.Utils.NetworkUtil;
import com.moriarty.user.runningman.Utils.PreferenceUtil;
import com.moriarty.user.svprogress.SVProgressHUD;
import com.moriarty.user.weather.OtherUserDefind.PullRefreshLayout;


import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by user on 17-8-9.
 */
public class SportsPartyFragment extends Fragment {
    View v;
    PullRefreshLayout refreshLayout;
    RecyclerView activitiesList;
    LinearLayoutManager linearLayoutManager;
    ActivitiesGalleryAdapter adapter;
    Context context;
    ContentObserver contentObserver;
    BroadcastReceiver receiver;
    SPFragmentPresenter presenter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        context=getContext();
        v = inflater.inflate(R.layout.fragment_sportsparty_layout1, null);
        refreshLayout=(PullRefreshLayout) v.findViewById(R.id.activities_refresh);
        activitiesList=(RecyclerView)v.findViewById(R.id.activities_list);
        refreshLayout.setColor(getResources().getColor(R.color.black));
        refreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                presenter.doRefreshFromNet();
            }
        });
        registerContentObserver();
        register_BC();
        presenter=new SPFragmentPresenter(context,this);
        //不能每次刷新fragment吼都去请求网络数据，太频繁了，暂时没有好的解决方法
        //只能先读本地数据
        presenter.doRefreshFromLocal(SPFragmentPresenter.READ);
        return v;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        context.getContentResolver().unregisterContentObserver(contentObserver);
        context.unregisterReceiver(receiver);
    }

    private void register_BC(){
        IntentFilter intentFilter=new IntentFilter("BC_SEVEN");
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                presenter.doRefreshFromNet();
            }
        };
        context.registerReceiver(receiver,intentFilter);
    }

    public void registerContentObserver(){
        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange){
                super.onChange(selfChange);
                presenter.doRefreshFromLocal(SPFragmentPresenter.READ);
            }
        };
        context.getContentResolver().registerContentObserver
                (Activity_Data.activity_Data.ACTIVITY_DATA_URI,true,contentObserver);
    }

    public void initList(ArrayList<ActivityData> list){
        linearLayoutManager=new LinearLayoutManager(context);
        adapter=new ActivitiesGalleryAdapter(context,activitiesList,list);
        activitiesList.setLayoutManager(linearLayoutManager);
        activitiesList.setAdapter(adapter);
    }

    public void updateList(ArrayList<ActivityData> list){
        adapter.updateListView(list);
    }

    public void setRefreshing(boolean b){
        refreshLayout.setRefreshing(b);
    }

}
