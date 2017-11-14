package com.moriarty.user.runningman.Activity;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.moriarty.user.runningman.Adapter.PagerAdapter_SM;
import com.moriarty.user.runningman.Interface.PersonInfoCardView;
import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Others.BroadcastManager;
import com.moriarty.user.runningman.Utils.PreferenceUtil;
import com.moriarty.user.runningman.Utils.Utils;
import com.moriarty.user.runningman.Others.ZoomBitmap;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.QueryContactsService;

import java.util.ArrayList;

/**
 * Created by user on 16-9-24.
 */
public class Person_InfoCard extends AppCompatActivity implements PersonInfoCardView {
    private final static String currentTag="Person_InfCard:";
    String name="";
    ImageView person_info_headrait;
    TextView dialog_card_name,person_info_email,person_info_phone,collect_text;
    String uri;
    String tiebaId,tiebaUrl;
    String weiboId,weiboUrl;
    BroadcastReceiver receiver6;
    Toolbar toolbar;
    CollapsingToolbarLayout mToolbarLayout;
    private ViewPager mViewPager;
    private PagerAdapter_SM mPagerAdapter;
    Button[] mBtnTabs=new Button[3];
    String[] addresses;
    SwipeRefreshLayout swipeRefreshLayout;
    private int flag;
    ArrayList<String> recode;
    SharedPreferences myInfo;
    Handler handler;
    int currentPage=0;
    Context context;
    String phoneText;
    BroadcastManager broadcastManager=new BroadcastManager();
    ContentResolver contentResolver;
    public static Handler handler2;
   // ILoadFragment loadNetDataFragment;
   // IQueryContacts queryContacts;
    PersonInfo personInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.person_info);
        context=this;
        contentResolver=getContentResolver();

        person_info_headrait=(ImageView)findViewById(R.id.person_info_headrait);
        person_info_phone=(TextView)findViewById(R.id.person_info_phone);
        person_info_email=(TextView)findViewById(R.id.person_info_email);

        final Intent intent=getIntent();
        flag=intent.getIntExtra("flag",2);
        if(flag==2){   //显示联系人信息
            name=intent.getStringExtra("info");
            invalidate();
            Log.d(MainActivity.TAG,currentTag+name);
        }
        else if(flag==1){   //显示个人信息
            personInfo=(PersonInfo) intent.
                    getSerializableExtra(context.getResources().getString(R.string.person_info_data));
           // Log.d(MainActivity.TAG,currentTag+"my source_id is recode"+recode.get(8));
            invalidate();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_person_info);
        toolbar.setTitle(name);
        setSupportActionBar(toolbar);

        IntentFilter intentFilter=new IntentFilter("BC_SIX");    //动态广播
        receiver6=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (flag){
                    case 1:
                        myInfo = getSharedPreferences("myself",MODE_PRIVATE);
                        personInfo = PreferenceUtil.getMyInformation(context,myInfo);
                        break;
                    case 2:
                        name=intent.getStringExtra("Name");
                        break;
                }
                invalidate();
            }
        };
        registerReceiver(receiver6,intentFilter);

        addresses=new String[]{getResources().getString(R.string.addresses_walk),getResources().getString(R.string.addresses_run),
                        getResources().getString(R.string.address_ride)};

        mBtnTabs[0]=(Button)findViewById(R.id.person_info_fragment_button_walk);
        mBtnTabs[0].setBackgroundColor(getResources().getColor(R.color.white));
        mBtnTabs[0].setTextColor(getResources().getColor(R.color.colorPrimary));
        mBtnTabs[0].setText(addresses[0]);
        mBtnTabs[1]=(Button)findViewById(R.id.person_info_fragment_button_run);
        mBtnTabs[1].setBackgroundColor(getResources().getColor(R.color.white));
        mBtnTabs[1].setText(addresses[1]);
        mBtnTabs[2]=(Button)findViewById(R.id.person_info_fragment_button_ride);
        mBtnTabs[2].setBackgroundColor(getResources().getColor(R.color.white));
        mBtnTabs[2].setText(addresses[2]);
        mBtnTabs[0].setOnClickListener(mTabClickListener);
        mBtnTabs[1].setOnClickListener(mTabClickListener);
        mBtnTabs[2].setOnClickListener(mTabClickListener);
        mViewPager = (ViewPager) findViewById(R.id.viewPager2);
        mPagerAdapter=new com.moriarty.user.runningman.Adapter.PagerAdapter_SM(getSupportFragmentManager(),phoneText);
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(0);
    }

    @Override
    public void onResume(){
        super.onResume();
        mToolbarLayout=(CollapsingToolbarLayout)findViewById(R.id.toolbar_layout);
        mToolbarLayout.setTitle(name);
    }

    public void invalidate(){
       // Log.d("Moriarty","Person_InfoCard:"+name);
        switch (flag){
            case 1:     //本人信息
                name=personInfo.getName();
                //如果没有联系人的电话或邮箱信息，则文字提示用户
                person_info_phone.setText(Utils.isNull(personInfo.getTel())
                        ?getResources().getString(R.string.no_phone_details):personInfo.getTel());
                person_info_email.setText(Utils.isNull(personInfo.getEmail())
                        ?getResources().getString(R.string.no_email_details):personInfo.getEmail());
                uri=personInfo.getHeadPortrait();
                break;
            case 2:   //联系人信息
                try{
                    personInfo=QueryContactsService.getPersonAllInfo(name);
                }catch (Exception e){
                    e.printStackTrace();
                }
                person_info_phone.setText(Utils.isNull(personInfo.getTel())
                        ?getResources().getString(R.string.no_phone_details):personInfo.getTel());
                person_info_email.setText(Utils.isNull(personInfo.getEmail())
                        ?getResources().getString(R.string.no_email_details):personInfo.getEmail());
                uri=personInfo.getHeadPortrait();
                break;
        }
        phoneText=person_info_phone.getText().toString();
        if(Utils.isNull(uri)){
            this.person_info_headrait.setImageDrawable(getResources().getDrawable(R.drawable.contact_default2));
        }
        else
            this.person_info_headrait.setImageBitmap(ZoomBitmap.getZoomBitmap(context,uri,"medium"));

    }

    @Override
    public void onDestroy(){
        unregisterReceiver(receiver6);
        super.onDestroy();
    }

    private void changeTabColor(int flag){
        for(int i=0;i<3;i++){
            if(i==flag)
                mBtnTabs[i].setTextColor(getResources().getColor(R.color.blue));
            else
                mBtnTabs[i].setTextColor(getResources().getColor(R.color.black));
        }
    }

    private View.OnClickListener mTabClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
            if (v == mBtnTabs[0])
            {
                mViewPager.setCurrentItem(0);
                changeTabColor(0);
            } else if (v == mBtnTabs[1])
            {
                mViewPager.setCurrentItem(1);
                changeTabColor(1);
            }
            else if(v == mBtnTabs[2]){
                mViewPager.setCurrentItem(2);
                changeTabColor(2);
            }
        }
    };

    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int arg0)
        {
            if(arg0==1){
                changeTabColor(1);
            }
            else if(arg0==0){
                changeTabColor(0);
            }
            else{
                changeTabColor(2);
            }
            Log.d(MainActivity.TAG,currentTag+"currentpage is "+arg0);
            currentPage=arg0;
        }
        @Override
        public void onPageScrolled(int arg0, float arg1,int arg2)
        {
        }
        @Override
        public void onPageScrollStateChanged(int arg0)
        {
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_edit) {
            Intent editIntent=new Intent(Person_InfoCard.this,AddContacts.class);
            switch (flag){
                case 1:    //准备修改个人信息
                    editIntent.putExtra("flag",1);
                    editIntent.putExtra(context.getResources().getString(R.string.person_info_data),personInfo);
                   // Log.d(MainActivity.TAG,currentTag+"my source_id is "+dataList.get(8));
                    break;
                case 2:    //准备修改联系人信息
                    editIntent.putExtra("flag",2);
                    editIntent.putExtra(context.getResources().getString(R.string.person_info_data),personInfo);
                    break;
            }
            startActivity(editIntent);
            return true;
        }
        else if(id==R.id.change_personinfo_bg){
            //待补充
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void showToast(String toast) {
        Toast.makeText(this,toast,Toast.LENGTH_SHORT).show();
    }


}
