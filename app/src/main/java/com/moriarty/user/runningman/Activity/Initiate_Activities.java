package com.moriarty.user.runningman.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.moriarty.user.runningman.Adapter.Category_DialogListAdapter;
import com.moriarty.user.runningman.Adapter.PeopleReviewAdapter;
import com.moriarty.user.runningman.DataBase.ToolClass.Activity_Data;
import com.moriarty.user.runningman.DataBase.ToolClass.Movement_Data;
import com.moriarty.user.runningman.Fragment.DatePickerFragment;
import com.moriarty.user.runningman.Fragment.TimePickerFragment;
import com.moriarty.user.runningman.Object.ActivityData;
import com.moriarty.user.runningman.Presenter.InitiateActivitiesPresenter;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Utils.JsonUtils;
import com.moriarty.user.runningman.Utils.PreferenceUtil;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by user on 17-9-8.
 */
public class Initiate_Activities extends AppCompatActivity {
    EditText topic;
    EditText address;
    EditText phone;
    EditText details;
    TextView start_date;
    TextView start_time;
    ImageButton add_pic;
    ImageView background;
    CardView select,review;
    RecyclerView reviewDialog;
    Toolbar toolbar;
    String img_url;
    InitiateActivitiesPresenter presenter;
    public static ArrayList<String> selected=new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_activities_main);
        toolsDefind();
        presenter=new InitiateActivitiesPresenter(this,this);

        start_date.setOnClickListener(mylistener);
        start_time.setOnClickListener(mylistener);
        select.setOnClickListener(mylistener);
        review.setOnClickListener(mylistener);
        add_pic.setOnClickListener(mylistener);

        toolbar.setTitle(getString(R.string.activity_pic_show));
        setSupportActionBar(toolbar);
        /*ImageSpan span=new ImageSpan(this,R.drawable.addcontactor_email);
        SpannableString spanStr=new SpannableString("活动主题");
        spanStr.setSpan(span, spanStr.length()-4, spanStr.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        topic.setText(spanStr);*/
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            // 得到图片的全路径
            img_url=data.getData().toString();  //将URI转化为String
           // Log.d(MainActivity.TAG,currentTag+headPortrait);
            // 通过路径加载图片
            //这里省去了图片缩放操作，如果图片过大，可能会导致内存泄漏
            this.background.setImageURI(Uri.parse(img_url));   //在将String转化为URI
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void toolsDefind(){
        topic=(EditText) findViewById(R.id.topic_of_activity);
        address=(EditText)findViewById(R.id.address_of_activity);
        phone=(EditText)findViewById(R.id.phone_of_contact);
        details=(EditText)findViewById(R.id.details_of_activity);
        start_date=(TextView)findViewById(R.id.startdate_of_activity);
        start_time=(TextView)findViewById(R.id.starttime_of_activity);
        select=(CardView)findViewById(R.id.selectPeople);
        review=(CardView)findViewById(R.id.reviewPeople);
        add_pic=(ImageButton)findViewById(R.id.initiate_activities_imagebutton);
        background=(ImageView)findViewById(R.id.initiate_activities_background);
        toolbar=(Toolbar)findViewById(R.id.initiate_activities_toolbar);
    }
    private View.OnClickListener mylistener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.startdate_of_activity:
                    selectStartDate();
                    break;
                case R.id.starttime_of_activity:
                    selectStartTime();
                    break;
                case R.id.selectPeople:
                    Intent intent=new Intent(Initiate_Activities.this,ChooseContacts.class);
                    startActivity(intent);
                    break;
                case R.id.reviewPeople:
                    reviewPeople();
                    break;
                case R.id.initiate_activities_imagebutton:
                    choosePic();
                    break;
            }
        }
    };
    private void choosePic(){
        Intent addImage=new Intent();
        addImage.setAction(Intent.ACTION_PICK);   //通过这个action可以激活图库
        addImage.setType("image/*");   //设置要传递的数据类型
        startActivityForResult(addImage, 0);
    }
    private void reviewPeople(){
        View v1 = getLayoutInflater().inflate(R.layout.groupdelete_recyclerview, null);
        reviewDialog = (RecyclerView) v1.findViewById(R.id.recyclerview_deletegroup);
        reviewDialog.setLayoutManager(new GridLayoutManager(v1.getContext(),2));
        reviewDialog.setAdapter(new PeopleReviewAdapter(this));
        new AlertDialog.Builder(this).setIcon(R.drawable.groupsetting_deletegroup)
                .setTitle(getString(R.string.review_dialog_title)).setView(v1)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*Intent deleteGroupIntent=new Intent();
                        deleteGroupIntent.putExtra("groupsetting_flag",1);
                        deleteGroupIntent.putStringArrayListExtra("deleteIntent",choose);
                        deleteGroupIntent.setAction("com.moriarty.service.GroupSettingService");
                        deleteGroupIntent.setPackage(mcontext.getPackageName());
                        mcontext.bindService(deleteGroupIntent,connection_delete,mcontext.BIND_AUTO_CREATE);*/
                    }
                })
                .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        /*choose.clear();
                        allGroupName.clear();*/
                    }
                })
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        /*choose.clear();
                        allGroupName.clear();*/
                    }
                }).create().show();
    }
    private void selectStartDate(){
        DatePickerFragment datePickerFrg = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                String m=Integer.toString(month),d=Integer.toString(day);
                if(month+1<10)
                    m="0"+(month+1);
                if(day<10)
                    d="0"+day;
                if(month<10)
                start_date.setText(year +"-" + m + "-" + d);
                //Log.d(MainActivity.TAG,"选择的日期是：" + year +"-" + (month + 1) + "-" + day);
            }
        };
        datePickerFrg.show(this.getFragmentManager(), "datePickerFrg");
    }
    private void selectStartTime(){
        TimePickerFragment timePickerFragment = new TimePickerFragment(){
            @Override
            public void onTimeSet(TimePicker view,int hour,int minute){
                String h=Integer.toString(hour),m=Integer.toString(minute);
                if(hour<10)
                    h="0"+hour;
                if(minute<10)
                    m="0"+minute;
                start_time.setText(h+":"+m);
            }
        };
        timePickerFragment.show(this.getFragmentManager(),"timePickerFrg");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int Id = item.getItemId();

        if (Id == R.id.action_settings) {
            presenter.confirm();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public String getTopic(){
        return topic.getText().toString();
    }
    public String getTime(){
        return start_date.getText().toString()+"&"+start_time.getText().toString();
    }
    public String getAddress(){
        return address.getText().toString();
    }
    public String getPhone(){
        return phone.getText().toString();
    }
    public String getContent(){
        return details.getText().toString();
    }

}
