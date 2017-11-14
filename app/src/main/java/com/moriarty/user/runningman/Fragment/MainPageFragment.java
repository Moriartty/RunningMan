package com.moriarty.user.runningman.Fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Network;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moriarty.user.runningman.Activity.HistoryDataDisplay;
import com.moriarty.user.runningman.Activity.SportsDataDisplay;
import com.moriarty.user.runningman.Utils.NetworkUtil;
import com.moriarty.user.runningman.Utils.Utils;
import com.moriarty.user.runningman.Pedometer.StepDetector;
import com.moriarty.user.runningman.Presenter.MainActivityPresenter;
import com.moriarty.user.runningman.R;
import com.moriarty.user.weather.ApiManager;
import com.moriarty.user.weather.OtherUserDefind.AqiView;
import com.moriarty.user.weather.OtherUserDefind.PullRefreshLayout;
import com.moriarty.user.weather.WeatherActivity;
import com.moriarty.user.weather.WeatherApplication;
import com.moriarty.user.weather.entity.Weather;

/**
 * Created by user on 17-8-9.
 */
public class MainPageFragment extends Fragment {
    View v;
    CardView cardView1,cardView2,cardView3,cardView4,cardView5,cardView6;
    public static TextView tv1,tv2,tv3,tv4,tv5,aqi_text;
    public ImageView img1;
    Context context;
    int flag=0;
    Weather mWeather;
    PullRefreshLayout pullRefreshLayout;
    AqiView aqiView;

    public static final int WALK_STEPS_MSG = 1;
    public static final int PACE_MSG = 2;
    public static final int DISTANCE_MSG = 3;
    public static final int SPEED_MSG = 4;
    public static final int CALORIES_MSG = 5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        super.onCreateView(inflater,container,savedInstanceState);
        context=getActivity();
        v = inflater.inflate(R.layout.fragment_mainpage_layout1, null);
        initView();
        if(NetworkUtil.isNetworkAvailable(context)){
            WeatherApplication.USE_SAMPLE_DATA=false;
        }
        else{
            WeatherApplication.USE_SAMPLE_DATA=true;
            Toast.makeText(context,getString(R.string.connected_filed_tips),Toast.LENGTH_LONG).show();
        }
        updateWeather();
        //Log.d(MainActivity.TAG,"MainPageFrag is Created");
        return v;
    }
    public void update(){
        if(v!=null)
            v.invalidate();
    }
    private void updateWeather(){
        pullRefreshLayout.setRefreshing(true);
        ApiManager.updateWeather(this.getActivity(), WeatherActivity.areaId,WeatherActivity.key, new ApiManager.ApiListener() {
            @Override
            public void onUpdateError() {
                pullRefreshLayout.setRefreshing(false);
                updateWeatherUI();
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

    private void updateWeatherUI(){
        if(mWeather==null){
            tv4.setVisibility(View.VISIBLE);
            tv4.setText(getString(R.string.weather_refresh_tips));
        }
        else {
            aqiView.setVisibility(View.VISIBLE);
            aqiView.setData(mWeather.get().aqi);
        }
       // aqi_text.setVisibility(View.VISIBLE);
       // aqi_text.setText(mWeather.getAqiDescription());
    }

    public static Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case WALK_STEPS_MSG:
                    int mStepValue = (int)msg.arg1;
                    int flag = (int)msg.arg2;
                    switch (flag){
                        case StepDetector.WALK:
                            tv1.setText("" + mStepValue);
                            break;
                        case StepDetector.RUN:
                            tv2.setText("" + mStepValue);
                            break;
                        case StepDetector.RIDE:
                            tv3.setText("" + mStepValue);
                    }
                    break;
                case PACE_MSG:
                   /* mPaceValue = msg.arg1;
                    if (mPaceValue <= 0) {
                        mPaceValueView.setText("0");
                    }
                    else {
                        mPaceValueView.setText("" + (int)mPaceValue);
                    }*/
                    break;
                case DISTANCE_MSG:
                   /* mDistanceValue = ((int)msg.arg1)/1000f;
                    if (mDistanceValue <= 0) {
                        mDistanceValueView.setText("0");
                    }
                    else {
                        mDistanceValueView.setText(
                                ("" + (mDistanceValue + 0.000001f)).substring(0, 5)
                        );
                    }*/
                    break;
                case SPEED_MSG:
                  /*  mSpeedValue = ((int)msg.arg1)/1000f;
                    if (mSpeedValue <= 0) {
                        mSpeedValueView.setText("0");
                    }
                    else {
                        mSpeedValueView.setText(
                                ("" + (mSpeedValue + 0.000001f)).substring(0, 4)
                        );
                    }*/
                    break;
                case CALORIES_MSG:
                    int mCaloriesValue = msg.arg1;
                    if (mCaloriesValue <= 0) {
                        tv5.setText("0");
                    }
                    else {
                        tv5.setText("" + (int)mCaloriesValue);
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }

    };

    public static long currentTimeInMillis() {
        Time time = new Time();
        time.setToNow();
        return time.toMillis(false);
    }

    private void initView(){
        cardView1=(CardView)v.findViewById(R.id.cardview_walk);
        cardView2=(CardView)v.findViewById(R.id.cardview_run);
        cardView3=(CardView)v.findViewById(R.id.cardview_ride);
        cardView4=(CardView)v.findViewById(R.id.cardview_aq);
        cardView5=(CardView)v.findViewById(R.id.cardview_calories);
        cardView6=(CardView)v.findViewById(R.id.cardview_history);
        aqiView=(AqiView)v.findViewById(R.id.fragment_aqi_view);
        pullRefreshLayout=(PullRefreshLayout)v.findViewById(R.id.weather_refreshlayout);
        pullRefreshLayout.setEnabled(false);
        cardView1.setTag(0);
        cardView2.setTag(1);
        cardView3.setTag(2);
        cardView4.setTag(3);
        cardView5.setTag(4);
        cardView6.setTag(5);
        cardView1.setCardBackgroundColor(getResources().getColor(R.color.walk_color));
        cardView2.setCardBackgroundColor(getResources().getColor(R.color.run_color));
        cardView3.setCardBackgroundColor(getResources().getColor(R.color.ride_color));
        cardView4.setCardBackgroundColor(getResources().getColor(R.color.aq_color));
        cardView5.setCardBackgroundColor(getResources().getColor(R.color.calories_color));
        cardView6.setCardBackgroundColor(getResources().getColor(R.color.history_color));
        cardView1.setOnClickListener(cardView_onClickListener);
        cardView2.setOnClickListener(cardView_onClickListener);
        cardView3.setOnClickListener(cardView_onClickListener);
        cardView4.setOnClickListener(cardView_onClickListener);
        cardView5.setOnClickListener(cardView_onClickListener);
        cardView6.setOnClickListener(cardView_onClickListener);
        tv1=(TextView)v.findViewById(R.id.mptv1);
        tv2=(TextView)v.findViewById(R.id.mptv2);
        tv3=(TextView)v.findViewById(R.id.mptv3);
        tv4=(TextView)v.findViewById(R.id.mptv4);
        tv5=(TextView)v.findViewById(R.id.mptv5);
        //aqi_text=(TextView)v.findViewById(R.id.aqi_text);
        img1=(ImageView)v.findViewById(R.id.mpimg6);
        tv1.setText(Integer.toString(MainActivityPresenter.walkStepsValue));
        tv2.setText(Integer.toString(MainActivityPresenter.runStepsValue));
        tv3.setText(Integer.toString(MainActivityPresenter.ridePedalsValue));
        tv5.setText(Integer.toString(MainActivityPresenter.caloriesVale));
    }
    private View.OnClickListener cardView_onClickListener=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Intent intent;
            switch (v.getId()){
                case R.id.cardview_walk:case R.id.cardview_run:case R.id.cardview_ride:case R.id.cardview_calories:
                    intent=new Intent(context,SportsDataDisplay.class);
                    intent.putExtra(getString(R.string.block_flag),(int)v.getTag());
                    startActivity(intent);
                    break;
                case R.id.cardview_aq:
                    intent=new Intent(context, WeatherActivity.class);
                    intent.putExtra("weather",mWeather);
                    startActivity(intent);
                    break;
                case R.id.cardview_history:
                    intent=new Intent(context, HistoryDataDisplay.class);
                    startActivity(intent);
                    break;

            }
        }
    };
}
