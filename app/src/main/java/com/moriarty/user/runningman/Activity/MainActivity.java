package com.moriarty.user.runningman.Activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;

import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.moriarty.user.runningman.Adapter.FragmentAdapter;
import com.moriarty.user.runningman.Fragment.ContactsListFragment;
import com.moriarty.user.runningman.Fragment.MainPageFragment;
import com.moriarty.user.runningman.Fragment.SportsPartyFragment;
import com.moriarty.user.runningman.Interface.IMainPresenter;
import com.moriarty.user.runningman.Interface.MainActivityView;
import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Others.BroadcastManager;
import com.moriarty.user.runningman.Others.SignalManager;
import com.moriarty.user.runningman.Utils.FileUtil;
import com.moriarty.user.runningman.Utils.PreferenceUtil;
import com.moriarty.user.runningman.Utils.Utils;
import com.moriarty.user.runningman.Others.ZoomBitmap;
import com.moriarty.user.runningman.Pedometer.Settings;
import com.moriarty.user.runningman.Presenter.MainActivityPresenter;

import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.QueryContactsService;
import com.moriarty.user.runningman.User_Defind.FloatingActionButton.MultiFloatingActionButton;
import com.moriarty.user.runningman.User_Defind.FloatingActionButton.TagFabLayout;

import java.util.ArrayList;
import java.util.List;

import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,MainActivityView{
    public static final String TAG ="Moriarty" ;
    private static final String currentTag="MainActivity";

    private ViewPager mViewPager;       //实现滑动的组件
    private FragmentAdapter mFragmentAdapter;    //viewPager适配器
    public static String[] addresses;      //包含两个fragment的命名
    private int currentTab=0;               //记录当前的页面编号
    public static int isQuit=0;

    //FloatingActionButton start_Running_btn,add_contacts_btn;
    BroadcastReceiver receiver,receiver3,receiver5;
    SharedPreferences myInfo;
    SharedPreferences.Editor editor;
    NavigationView navigationView;
    //MultiFloatingActionButton fab;
    MultiFloatingActionButton floating_button;

    ImageView toolbar_my_headrait,nav_my_headrait,nav_my_qrcode;
    TextView toolbar_my_name,nav_my_name,nav_my_phone;
    PersonInfo myself;
    DrawerLayout drawer;
    TabLayout tabLayout;
    Toolbar toolbar;
    Context context;
    IMainPresenter mainPresenter;
    public Handler handler;
    private int[] tabIcons = {
            R.drawable.ic_tablayout_main,
            R.drawable.ic_tablayout_contacts,
            R.drawable.ic_tablayout_activity
    };

    private static final int MENU_SETTINGS = 8;
    private static final int MENU_QUIT     = 9;
    private static final int MENU_MAP   =10;

    private static final int MENU_PAUSE = 1;
    private static final int MENU_RESUME = 2;
    private static final int MENU_RESET = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Log.i(TAG, "[ACTIVITY] onCreate");
        context=this;
        //声明用到的组件,最好最先调用
        toolsDefined();

        handler=new Handler(){
            @Override
            public void handleMessage(Message message){
                if(message.what == SignalManager.writeMyselfInfo_signal){
                    Log.d(TAG,"prepare to write myself info");
                    mainPresenter.skip2AddContactsView(myself);
                }
            }
        };
        //创建应用路径下的文件夹
        FileUtil.createDir();
        mainPresenter=new MainActivityPresenter(context,MainActivity.this);
        //检查权限
        mainPresenter.inspectPermission(handler);

        //注册广播，，，，暂时没用
        registerBC();

        //start_Running_btn.setOnClickListener(mListener);
        //add_contacts_btn.setOnClickListener(mListener);
        floating_button.setOnFabItemClickListener(mFabListener);
        floating_button.setTextColor(getResources().getColor(R.color.black));

        toolbar_my_headrait.setOnClickListener(mListener);
        toolbar.setTitle("");  //将toolbar上的title先隐藏
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(     //actionbarDrawerToggle暂时不使用！
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        Drawable drawable=toolbar_my_headrait.getDrawable();
        toolbar.setNavigationIcon(null);   //将toolbar上的图标设置为空，以自己头像取而代之
        //无法在.xml文件中直接设置icon,只能在这个地方设置！！！
        navigationView.setNavigationItemSelectedListener(this);
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setCustomView(getTabView(0));
        tabLayout.getTabAt(1).setCustomView(getTabView(1));
        tabLayout.getTabAt(2).setCustomView(getTabView(2));
    }

    private void registerBC(){
        IntentFilter intentFilter=new IntentFilter("BC_ONE");    //动态广播,更新全局界面，先更新数据，然后会在service中发送二号广播进行fragment的更新
        receiver=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                currentTab=mViewPager.getCurrentItem();
                //Log.d(TAG,"MainActivity:"+"currentpage is "+currentTab);
                init();
            }
        };
        registerReceiver(receiver,intentFilter);

        IntentFilter intentFilter5=new IntentFilter("BC_FIVE");    //动态广播,准备更新Toolbar和Navigation
        receiver5=new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                initToolbarAndNav();
            }
        };
        registerReceiver(receiver5,intentFilter5);
    }

    private View getTabView(int position){
        View view = LayoutInflater.from(this).inflate(R.layout.tab_view, null);
        TextView txt_title = (TextView) view.findViewById(R.id.tabtext);
        txt_title.setText(addresses[position]);
        ImageView img_title = (ImageView) view.findViewById(R.id.tabicon);
        img_title.setImageResource(tabIcons[position]);
        //注意：这里有一个坑，直接设置setCurrentItem(0)不会使tab0处于选中状态所以这里手动设置；
        if(position==0)
            view.setSelected(true);
        return view;
    }

    private void initViewPager(){
        final List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MainPageFragment());
        fragmentList.add(new ContactsListFragment());
        fragmentList.add(new SportsPartyFragment());
        mFragmentAdapter=new FragmentAdapter(getSupportFragmentManager(),addresses,fragmentList);
        mViewPager.setAdapter(mFragmentAdapter);//给ViewPager设置适配器
        tabLayout.setupWithViewPager(mViewPager);//将TabLayout和ViewPager关联起来。
        tabLayout.setTabsFromPagerAdapter(mFragmentAdapter);//给Tabs设置适配器
        setupTabIcons();
        setCurrentItem(currentTab);//更新完fragment后，利用之前保存的currentTab回到初始操作页面
    }

    @Override
    public void onResume(){
        super.onResume();
        //初始化PedometerSetting
        mainPresenter.initPedometerSetting();
        //根据运行状态和setitng参数选择service开启方式
        mainPresenter.preStartService();
    }

    @Override
    public void onPause() {
        Log.i(TAG, "[ACTIVITY] onPause");
        //当Activity调用onPause()时操作.
        mainPresenter.mainActivityPaused();
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        unregisterReceiver(receiver);   //当进程结束时，取消对三个广播的注册
        unregisterReceiver(receiver5);
    }

    private void toolsDefined(){
        addresses=new String[]{getString(R.string.main_page),getString(R.string.my_group),getString(R.string.sports_party)};
        //start_Running_btn = (FloatingActionButton) findViewById(R.id.start_running_btn);
        //add_contacts_btn = (FloatingActionButton) findViewById(R.id.add_contacts_btn);
        toolbar_my_name=(TextView)findViewById(R.id.toolbar_my_name);
        toolbar_my_headrait=(ImageView)findViewById(R.id.toolbar_my_headrait);
        nav_my_phone=(TextView)findViewById(R.id.nav_my_phone);
        nav_my_name=(TextView)findViewById(R.id.nav_my_name);
        nav_my_headrait=(ImageView)findViewById(R.id.nav_my_headrait);
        nav_my_qrcode=(ImageView)findViewById(R.id.nav_my_qrcode);
        tabLayout=(TabLayout)findViewById(R.id.tabs);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        /*tabLayout.addTab(tabLayout.newTab().setText(addresses[0]));
        tabLayout.addTab(tabLayout.newTab().setText(addresses[1]));
        tabLayout.addTab(tabLayout.newTab().setText(addresses[2]));*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewPager = (ViewPager) findViewById(R.id.viewPager1);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        floating_button=(MultiFloatingActionButton)findViewById(R.id.floating_button);
    }

    private View.OnClickListener mListener=new View.OnClickListener(){
        @Override
        public void onClick(View v){
            Intent intent;
            switch (v.getId()){
               /* case R.id.start_running_btn:
                    currentTab=mViewPager.getCurrentItem();  //每次只是更新fragment,MainActivity中的currentTab不改变
                    intent=new Intent(MainActivity.this,Initiate_Activities.class);
                    startActivity(intent);
                    break;
                case R.id.add_contacts_btn:
                    currentTab=mViewPager.getCurrentItem();  //每次只是更新fragment,MainActivity中的currentTab不改变
                    intent=new Intent(MainActivity.this,AddContacts.class);
                    intent.putExtra("flag",0);
                    startActivity(intent);
                    break;*/
                case R.id.toolbar_my_headrait:
                    drawer.openDrawer(GravityCompat.START);
                    break;
                default:break;
            }
        }
    };

    private MultiFloatingActionButton.OnFabItemClickListener mFabListener
            =new MultiFloatingActionButton.OnFabItemClickListener(){
        @Override
        public void onFabItemClick(TagFabLayout view, int pos) {
            switch (view.getId()){
                case R.id.add_contacts_btn:
                    currentTab=mViewPager.getCurrentItem();  //每次只是更新fragment,MainActivity中的currentTab不改变
                    Intent intent=new Intent(MainActivity.this,AddContacts.class);
                    intent.putExtra("flag",0);
                    startActivity(intent);
                    break;
                case R.id.start_running_btn:
                    currentTab=mViewPager.getCurrentItem();  //每次只是更新fragment,MainActivity中的currentTab不改变
                    intent=new Intent(MainActivity.this,Initiate_Activities.class);
                    startActivity(intent);
                    break;
            }
        }
    };

    //加载menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        if (mainPresenter.getIsRunning()) {
            menu.add(0, MENU_PAUSE, 0, R.string.pause)
                    .setIcon(android.R.drawable.ic_media_pause)
                    .setShortcut('1', 'p');
        }
        else {
            menu.add(0, MENU_RESUME, 0, R.string.resume)
                    .setIcon(android.R.drawable.ic_media_play)
                    .setShortcut('1', 'p');
        }
        menu.add(0, MENU_RESET, 0, R.string.reset)
                .setIcon(android.R.drawable.ic_menu_close_clear_cancel)
                .setShortcut('2', 'r');
        menu.add(0, MENU_MAP, 0, R.string.map)
                .setIcon(getResources().getDrawable(R.drawable.ic_menu_map))
                .setIntent(new Intent(context, com.moriarty.user.mapdemotest.MainActivity.class))
                .setShowAsAction(2);
        menu.add(0, MENU_SETTINGS, 0, R.string.settings)
                .setIcon(getResources().getDrawable(R.drawable.ic_menu_setting))
                .setShortcut('8', 's')
                .setIntent(new Intent(context, Settings.class)).setShowAsAction(2);
        menu.add(0, MENU_QUIT, 0, R.string.quit)
                .setIcon(android.R.drawable.ic_lock_power_off)
                .setShortcut('9', 'q');
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_PAUSE:
                mainPresenter.unbindStepService();
                mainPresenter.stopStepService();
                return true;
            case MENU_RESUME:
                mainPresenter.startStepService();
                mainPresenter.bindStepService();
                return true;
            case MENU_RESET:
                mainPresenter.resetValues(true);
                return true;
            case MENU_QUIT:
                isQuit=1;
                mainPresenter.resetValues(false);
                mainPresenter.unbindStepService();
                mainPresenter.stopStepService();
                mainPresenter.setQuitting(true);
                finish();
                return true;
        }
        return false;
    }


    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        drawer.setDrawerListener(new DrawerLayout.DrawerListener() {
            int id=item.getItemId();
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (id == R.id.nav_my_info_card) {
                    //这里需要重新获取sharePreference文件中的数据
                    myInfo=getSharedPreferences("myself",MODE_PRIVATE);  //获取toolbar上需要显示的个人信息
                    myself= PreferenceUtil.getMyInformation(context,myInfo);//从sharedPreference中获取本人信息
                    if(!Utils.isNull(myself.getTel())){
                        mainPresenter.skip2PersonInfo(myself);
                    }
                    else{
                        mainPresenter.skip2AddContactsView(myself);
                    }
                } else if (id == R.id.nav_gallery) {
                    Toast.makeText(MainActivity.this,"主题设置",Toast.LENGTH_SHORT).show();

                } else if (id == R.id.nav_manage) {

                } else if (id == R.id.nav_share) {
                   /* TwoDimentionCode twoDimentionCode=new TwoDimentionCode();
                    Bitmap bitmap=twoDimentionCode.generateTDC("",null);
                    twoDimentionCode.shareImage(bitmap,context);*/

                } else if (id == R.id.nav_send) {
                  /*  if(!NetworkManager.isConnectionAvailable(context)){
                        ReserveTask reserveTask=new ReserveTask(context,1);
                        reserveTask.execute();
                    }
                    else
                        Toast.makeText(context,getResources().getString(R.string.network_inaccessible),Toast.LENGTH_SHORT).show();*/
                }
                item.setChecked(false);
                id=0;
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
        return true;
    }


    @Override
    public void initToolbarAndNav(){
        myInfo=getSharedPreferences("myself",MODE_PRIVATE);  //获取toolbar上需要显示的个人信息
        myself=PreferenceUtil.getMyInformation(context,myInfo);//从sharedPreference中获取本人信息
        Log.d(TAG,myself.getHeadPortrait().toString());
        setToolbar();        //设置toolbar上的本人信息
        setNavigation();     //设置navigation上的本人信息
    }
    private void setToolbar(){
        if(Utils.isNull(myself.getHeadPortrait()))
            toolbar_my_headrait.setImageDrawable(getResources().getDrawable(R.drawable.contact_default2));
        else{
            toolbar_my_headrait.setImageBitmap(ZoomBitmap.getZoomBitmap(context,myself.getHeadPortrait(),"small"));
        }
        if(!Utils.isNull(myself.getName()))
            toolbar_my_name.setText(myself.getName());
    }
    private void setNavigation(){
        if(Utils.isNull(myself.getHeadPortrait()))
            nav_my_headrait.setImageDrawable(getResources().getDrawable(R.drawable.contact_default2));
        else
            nav_my_headrait.setImageBitmap(ZoomBitmap.getZoomBitmap(context,myself.getHeadPortrait(),"medium"));
        if(!Utils.isNull(myself.getTel())){
            nav_my_name.setText(myself.getName());
            nav_my_phone.setText(myself.getTel());
        }
    }

    @Override
    public void init(){
        initViewPager();
        initToolbarAndNav();
    }
    @Override
    public Activity getActivity(){
        return MainActivity.this;
    }

    @Override
    public void setCurrentItem(int flag) {
        mViewPager.setCurrentItem(flag);
    }

}
