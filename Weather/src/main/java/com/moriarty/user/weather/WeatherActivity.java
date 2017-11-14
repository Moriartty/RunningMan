package com.moriarty.user.weather;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TimingLogger;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.moriarty.user.weather.DynamicWeather.BaseDrawer;
import com.moriarty.user.weather.DynamicWeather.DynamicWeatherView;
import com.moriarty.user.weather.OtherUserDefind.AqiView;
import com.moriarty.user.weather.OtherUserDefind.AstroView;
import com.moriarty.user.weather.OtherUserDefind.DailyForecastView;
import com.moriarty.user.weather.OtherUserDefind.HourlyForecastView;
import com.moriarty.user.weather.OtherUserDefind.PullRefreshLayout;
import com.moriarty.user.weather.entity.HeWeatherDataService30;
import com.moriarty.user.weather.entity.Weather;

import java.util.ArrayList;

/**
 * Created by user on 17-9-1.
 */
public class WeatherActivity extends AppCompatActivity {
    public static String TAG = "Weather";
    Weather mWeather;
    private DynamicWeatherView weatherView;
    public static final String areaId="CN101120201";
    public static final String key="52275d4dfad047fbba79bf7972380adf";
    PullRefreshLayout pullRefreshLayout;
    DailyForecastView mDailyForecastView;
    HourlyForecastView mHourlyForecastView;
    AqiView mAqiView;
    AstroView mAstroView;
    Context context;
    private ScrollView mScrollView;
    final int REQUEST_PERMISSION1=110;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_main);
        context=this;
        findTools();
        inspectPermission();
    }
    private void findTools(){
        weatherView=(DynamicWeatherView)findViewById(R.id.dynamicweatherview);
        pullRefreshLayout=(PullRefreshLayout)findViewById(R.id.refreshLayout);
        mDailyForecastView = (DailyForecastView) findViewById(R.id.w_dailyForecastView);
        mHourlyForecastView = (HourlyForecastView) findViewById(R.id.w_hourlyForecastView);
        mAqiView = (AqiView) findViewById(R.id.w_aqi_view);
        mAstroView = (AstroView) findViewById(R.id.w_astroView);
        mScrollView = (ScrollView) findViewById(R.id.w_WeatherScrollView);
    }
    private void intentPrepare(){
        weatherView.setDrawerType(BaseDrawer.Type.CLOUDY_D);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getWeatherData();
            }
        });
        mWeather=(Weather) getIntent().getSerializableExtra("weather");
        if(mWeather==null){
            Log.d(TAG,"mWeather is null");
            getWeatherData();
        }
        else
            updateWeatherUI();
    }

    private void inspectPermission(){
        String[] permissions={Manifest.permission.READ_EXTERNAL_STORAGE};
        if(ContextCompat.checkSelfPermission(context,permissions[0])!= PackageManager.PERMISSION_GRANTED){
            enduePermission(permissions);
        }
        else {
            intentPrepare();
        }
    }

    public void enduePermission(String[] permissions) {
        if(ContextCompat.checkSelfPermission(context,permissions[0])!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{permissions[0]},REQUEST_PERMISSION1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                intentPrepare();
                return;
            }
        }
    }

    private void getWeatherData(){

        pullRefreshLayout.setRefreshing(true);
        if(Utils.isNetworkAvailable(context)){
            WeatherApplication.USE_SAMPLE_DATA = false;
            Log.d(TAG,"USE_SAMPLE_DATA is true");
        }
        else {
            WeatherApplication.USE_SAMPLE_DATA = true;
            Toast.makeText(context,getString(R.string.connected_filed_tips),Toast.LENGTH_LONG).show();
        }

        ApiManager.updateWeather(WeatherActivity.this, areaId,key, new ApiManager.ApiListener() {
            @Override
            public void onUpdateError() {
                pullRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onReceiveWeather(Weather weather, boolean updated) {
                pullRefreshLayout.setRefreshing(false);
                if (updated) {
                    if (ApiManager.acceptWeather(weather)) {
                        mWeather = weather;
                        updateWeatherUI();
                    }
                }
            }
        });
    }
    @Override
    public void onResume(){
        super.onResume();
        weatherView.onResume();
    }
    public void onPause(){
        super.onPause();
        weatherView.onPause();
    }
    public void onDestroy(){
        //onDestroy()方法并不会在Activity退出时立即执行
        super.onDestroy();

    }
    //监听手机回退键事件
    @Override
    public void onBackPressed() {
        weatherView.onPause();
        weatherView.onDestroy();
        super.onBackPressed();
    }

    private void postRefresh() {
        if (pullRefreshLayout != null) {
            if(this != null){
                if(Utils.isNetworkAvailable(this))
                    pullRefreshLayout.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pullRefreshLayout.setRefreshing(true, true);
                        }
                    }, 100);
            }
        }
    }

    private void updateWeatherUI() {
        if (!ApiManager.acceptWeather(mWeather)) {
            return;
        }
        try {
            final Weather weather = mWeather;
           // updateDrawerTypeAndNotify();

            HeWeatherDataService30 w = weather.get();

            mDailyForecastView.setData(weather);
            mHourlyForecastView.setData(weather);
            mAqiView.setData(w.aqi);
            mAstroView.setData(weather);
            // setTextViewString(R.id.w_city, w.basic.city);
            final String tmp = w.now.tmp;
            try {
                final int tmp_int = Integer.valueOf(tmp);
                if(tmp_int < 0){
                    setTextViewString(R.id.w_now_tmp,String.valueOf(-tmp_int));
                    findViewById(R.id.w_now_tmp_minus).setVisibility(View.VISIBLE);
                }else{
                    setTextViewString(R.id.w_now_tmp, tmp);
                    findViewById(R.id.w_now_tmp_minus).setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                setTextViewString(R.id.w_now_tmp, tmp);
                findViewById(R.id.w_now_tmp_minus).setVisibility(View.GONE);
            }

            setTextViewString(R.id.w_now_cond_text, w.now.cond.txt);

            if (ApiManager.isToday(w.basic.update.loc)) {
                setTextViewString(R.id.w_basic_update_loc, w.basic.update.loc.substring(11) + " 发布");
            } else {
                setTextViewString(R.id.w_basic_update_loc, w.basic.update.loc.substring(5) + " 发布");
            }

            setTextViewString(R.id.w_todaydetail_bottomline,  w.now.cond.txt + "  " + mWeather.getTodayTempDescription());
            setTextViewString(R.id.w_todaydetail_temp, w.now.tmp + "°");
//			try {
//				((ImageView)mRootView.findViewById(R.id.w_todaydetail_cond_imageview))
//					.setImageResource(getCondIconDrawableId(mWeather));
//			} catch (Exception e) {
//			}

            setTextViewString(R.id.w_now_fl, w.now.fl + "°");
            setTextViewString(R.id.w_now_hum, w.now.hum + "%");// 湿度
            setTextViewString(R.id.w_now_vis, w.now.vis + "km");// 能见度
            // setTextViewString(R.id.w_now_wind_dir, w.now.wind.dir);
            // setTextViewString(R.id.w_now_wind_sc, w.now.wind.sc);
            // setTextViewString(R.id.w_now_pres, w.now.pres);
            setTextViewString(R.id.w_now_pcpn, w.now.pcpn + "mm"); // 降雨量

            if (weather.hasAqi()) {
                setTextViewString(R.id.w_aqi_text, w.aqi.city.qlty);

                setTextViewString(R.id.w_aqi_detail_text, w.aqi.city.qlty);
                setTextViewString(R.id.w_aqi_pm25, w.aqi.city.pm25 + "μg/m³");
                setTextViewString(R.id.w_aqi_pm10, w.aqi.city.pm10 + "μg/m³");
                setTextViewString(R.id.w_aqi_so2, w.aqi.city.so2 + "μg/m³");
                setTextViewString(R.id.w_aqi_no2, w.aqi.city.no2 + "μg/m³");

            } else {
                setTextViewString(R.id.w_aqi_text, "");
            }
            if (w.suggestion != null) {
                setTextViewString(R.id.w_suggestion_comf, w.suggestion.comf.txt);
                setTextViewString(R.id.w_suggestion_cw, w.suggestion.cw.txt);
                setTextViewString(R.id.w_suggestion_drsg, w.suggestion.drsg.txt);
                setTextViewString(R.id.w_suggestion_flu, w.suggestion.flu.txt);
                setTextViewString(R.id.w_suggestion_sport, w.suggestion.sport.txt);
                setTextViewString(R.id.w_suggestion_tarv, w.suggestion.trav.txt);
                setTextViewString(R.id.w_suggestion_uv, w.suggestion.uv.txt);

                setTextViewString(R.id.w_suggestion_comf_brf, w.suggestion.comf.brf);
                setTextViewString(R.id.w_suggestion_cw_brf, w.suggestion.cw.brf);
                setTextViewString(R.id.w_suggestion_drsg_brf, w.suggestion.drsg.brf);
                setTextViewString(R.id.w_suggestion_flu_brf, w.suggestion.flu.brf);
                setTextViewString(R.id.w_suggestion_sport_brf, w.suggestion.sport.brf);
                setTextViewString(R.id.w_suggestion_tarv_brf, w.suggestion.trav.brf);
                setTextViewString(R.id.w_suggestion_uv_brf, w.suggestion.uv.brf);
            }

        } catch (Exception e) {
            e.printStackTrace();
           // toast(mArea.name_cn + " Error\n" + e.toString());
        }
    }

    private void setTextViewString(int textViewId, String str) {
        TextView tv = (TextView)findViewById(textViewId);
        if (tv != null) {
            tv.setText(str);
        } else {
            //toast("Error NOT found textView id->" + Integer.toHexString(textViewId));
        }
    }
}
