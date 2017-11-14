package com.moriarty.user.runningman.Activity;


import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import com.moriarty.user.runningman.DataBase.ToolClass.Movement_Data;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.User_Defind.Fuck.ExtendedLineView;
import com.moriarty.user.runningman.User_Defind.Fuck.Fragment1;
import com.moriarty.user.runningman.User_Defind.Fuck.Fragment2;
import com.moriarty.user.runningman.User_Defind.Fuck.Fragment3;
import com.moriarty.user.runningman.User_Defind.Fuck.NoScrollViewPager;
import com.moriarty.user.runningman.User_Defind.Fuck.Selector_Adapter;

import java.util.ArrayList;

/**
 * Created by user on 17-8-28.
 */
public class HistoryDataDisplay extends AppCompatActivity {

    String[] period;
    ContentObserver contentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_data_show);
        period = new String[]{getString(R.string.period1),
                getString(R.string.period2),getString(R.string.period3)};
        registerContentObserver();
        init();
    }

    public void registerContentObserver(){
        contentObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange){
                super.onChange(selfChange);
                init();
            }
        };
        getContentResolver().registerContentObserver
                (Movement_Data.movement_Data.MOVEMENT_DATA_URI,true,contentObserver);
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        //如果不在onDestroy中对contentObserver进行取消注册，
        //则会引发二次d调用onChange方法的情况
        getContentResolver().unregisterContentObserver(contentObserver);
        finish();
    }

    private void init(){
        initWalkStepView();
        initWalkDistanceView();
        initRunDistanceView();
        initRideDistanceView();
        initCaloriesView();
    }

    private void initWalkStepView(){
        ArrayList<Fragment> fragments=new ArrayList<>();
        Selector_Adapter selector_adapter;
        NoScrollViewPager walkStepViewPager = (NoScrollViewPager)findViewById(R.id.walkstep_history_show);
        TabLayout walkStepTabLayout = (TabLayout)findViewById(R.id.walkstep_history_selector);
        Bundle[] bundles = new Bundle[3];
        for(int i=0;i<3;i++){
            Bundle bundle = new Bundle();
            bundle.putInt(getString(R.string.datetype),i);
            bundle.putInt(getString(R.string.datatype),ExtendedLineView.WALK_STEP);
            bundles[i]=bundle;
        }
        fragments.add(Fragment1.newInstance(bundles[0]));
        fragments.add(Fragment2.newInstance(bundles[1]));
        fragments.add(Fragment3.newInstance(bundles[2]));
        selector_adapter=new Selector_Adapter(getSupportFragmentManager(),period,fragments);
        walkStepViewPager.setAdapter(selector_adapter);
        walkStepTabLayout.setupWithViewPager(walkStepViewPager);//将TabLayout和ViewPager关联起来。
        walkStepTabLayout.setTabsFromPagerAdapter(selector_adapter);//给Tabs设置适配器
    }
    private void initWalkDistanceView(){
        ArrayList<Fragment> fragments=new ArrayList<>();
        Selector_Adapter selector_adapter;
        NoScrollViewPager walkDistanceViewPager = (NoScrollViewPager)findViewById(R.id.walkdistance_history_show);
        TabLayout walkDistanceTabLayout = (TabLayout)findViewById(R.id.walkdistance_history_selector);
        Bundle[] bundles = new Bundle[3];
        for(int i=0;i<3;i++){
            Bundle bundle = new Bundle();
            bundle.putInt(getString(R.string.datetype),i);
            bundle.putInt(getString(R.string.datatype),ExtendedLineView.WALK_DISTANCE);
            bundles[i]=bundle;
        }
        fragments.add(Fragment1.newInstance(bundles[0]));
        fragments.add(Fragment2.newInstance(bundles[1]));
        fragments.add(Fragment3.newInstance(bundles[2]));
        selector_adapter=new Selector_Adapter(getSupportFragmentManager(),period,fragments);
        walkDistanceViewPager.setAdapter(selector_adapter);
        walkDistanceTabLayout.setupWithViewPager(walkDistanceViewPager);//将TabLayout和ViewPager关联起来。
        walkDistanceTabLayout.setTabsFromPagerAdapter(selector_adapter);//给Tabs设置适配器
    }
    private void initRunDistanceView(){
        ArrayList<Fragment> fragments=new ArrayList<>();
        Selector_Adapter selector_adapter;
        NoScrollViewPager runDistanceViewPager = (NoScrollViewPager)findViewById(R.id.rundistance_history_show);
        TabLayout runDistanceTabLayout = (TabLayout)findViewById(R.id.rundistance_history_selector);
        Bundle[] bundles = new Bundle[3];
        for(int i=0;i<3;i++){
            Bundle bundle = new Bundle();
            bundle.putInt(getString(R.string.datetype),i);
            bundle.putInt(getString(R.string.datatype),ExtendedLineView.RUN_DISTANCE);
            bundles[i]=bundle;
        }
        fragments.add(Fragment1.newInstance(bundles[0]));
        fragments.add(Fragment2.newInstance(bundles[1]));
        fragments.add(Fragment3.newInstance(bundles[2]));
        selector_adapter=new Selector_Adapter(getSupportFragmentManager(),period,fragments);
        runDistanceViewPager.setAdapter(selector_adapter);
        runDistanceTabLayout.setupWithViewPager(runDistanceViewPager);//将TabLayout和ViewPager关联起来。
        runDistanceTabLayout.setTabsFromPagerAdapter(selector_adapter);//给Tabs设置适配器
    }
    private void initRideDistanceView(){
        ArrayList<Fragment> fragments=new ArrayList<>();
        Selector_Adapter selector_adapter;
        NoScrollViewPager rideDistanceViewPager = (NoScrollViewPager)findViewById(R.id.ridedistance_history_show);
        TabLayout rideDistanceTabLayout = (TabLayout)findViewById(R.id.ridedistance_history_selector);
        Bundle[] bundles = new Bundle[3];
        for(int i=0;i<3;i++){
            Bundle bundle = new Bundle();
            bundle.putInt(getString(R.string.datetype),i);
            bundle.putInt(getString(R.string.datatype),ExtendedLineView.RIDE_DISTANCE);
            bundles[i]=bundle;
        }
        fragments.add(Fragment1.newInstance(bundles[0]));
        fragments.add(Fragment2.newInstance(bundles[1]));
        fragments.add(Fragment3.newInstance(bundles[2]));
        selector_adapter=new Selector_Adapter(getSupportFragmentManager(),period,fragments);
        rideDistanceViewPager.setAdapter(selector_adapter);
        rideDistanceTabLayout.setupWithViewPager(rideDistanceViewPager);//将TabLayout和ViewPager关联起来。
        rideDistanceTabLayout.setTabsFromPagerAdapter(selector_adapter);//给Tabs设置适配器
    }
    private void initCaloriesView(){
        ArrayList<Fragment> fragments=new ArrayList<>();
        Selector_Adapter selector_adapter;
        NoScrollViewPager caloriesViewPager = (NoScrollViewPager)findViewById(R.id.calories_history_show);
        TabLayout caloriesTabLayout = (TabLayout)findViewById(R.id.calories_history_selector);
        Bundle[] bundles = new Bundle[3];
        for(int i=0;i<3;i++){
            Bundle bundle = new Bundle();
            bundle.putInt(getString(R.string.datetype),i);
            bundle.putInt(getString(R.string.datatype),ExtendedLineView.CALORIES);
            bundles[i]=bundle;
        }
        fragments.add(Fragment1.newInstance(bundles[0]));
        fragments.add(Fragment2.newInstance(bundles[1]));
        fragments.add(Fragment3.newInstance(bundles[2]));
        selector_adapter=new Selector_Adapter(getSupportFragmentManager(),period,fragments);
        caloriesViewPager.setAdapter(selector_adapter);
        caloriesTabLayout.setupWithViewPager(caloriesViewPager);//将TabLayout和ViewPager关联起来。
        caloriesTabLayout.setTabsFromPagerAdapter(selector_adapter);//给Tabs设置适配器
    }
}
