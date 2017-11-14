package com.moriarty.user.runningman.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.moriarty.user.runningman.Interface.AddContactsView;
import com.moriarty.user.runningman.Interface.IAddContactsPresenter;
import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Others.BroadcastManager;
import com.moriarty.user.runningman.Others.PopupMenuManager;
import com.moriarty.user.runningman.Utils.Utils;
import com.moriarty.user.runningman.Others.ZoomBitmap;
import com.moriarty.user.runningman.Presenter.AddContactsPresenter;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.GroupSettingService;

import java.util.ArrayList;

/**
 * Created by user on 17-8-10.
 */
public class AddContacts extends AppCompatActivity implements AddContactsView {
    public static EditText name;
    public static EditText phone;
    public static EditText email;
    public static EditText datePicker;
    public static TextView groupType,sexy;
    public Button selectGroupType,addGroupBtn,selectSexyBtn,selectDateBtn;
    private ArrayList<String> allGroupName=new ArrayList<>();
    private static final String currentTag="AddContacts";
    public static ArrayList<String> history=new ArrayList<>();
    static String headPortrait="";
    String source_id;
    ImageButton add_imagebutton;
    GroupSettingService groupSettingService;
    ImageView addContacts_Top;
    int flag=0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ProgressDialog progressDialog;
    Context context;
    IAddContactsPresenter addContactsPresenter;
    PersonInfo personInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact);

        context=this;
        addContactsPresenter=new AddContactsPresenter(context,this);
        personInfo=new PersonInfo();
        toolsPrepared();

        addGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View v0 = getLayoutInflater().inflate(R.layout.dialog_addgroup, null);
                addContactsPresenter.addGroupAction(v0);
            }
        });

        selectGroupType.setOnClickListener(ac_listener);
        selectSexyBtn.setOnClickListener(ac_listener);
        selectDateBtn.setOnClickListener(ac_listener);

        add_imagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addImage=new Intent();
                addImage.setAction(Intent.ACTION_PICK);   //通过这个action可以激活图库
                addImage.setType("image/*");   //设置要传递的数据类型
                startActivityForResult(addImage, 0);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //模式选择
        Intent intent=getIntent();
        flag=intent.getIntExtra("flag",0);
        addContactsPresenter.modelChooser(getIntent());

    }
    private View.OnClickListener ac_listener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.ac_group_selectBtn:
                    addContactsPresenter.selectGroupAction(selectGroupType,allGroupName);
                    break;
                case R.id.ac_sexy_selectBtn:
                    addContactsPresenter.selectSexyAction(selectSexyBtn);
                    break;
                case R.id.ac_date_selectBtn:
                    addContactsPresenter.selectDateAction();
                    break;
                default:break;
            }
        }
    };
    private void toolsPrepared(){
        name=(EditText)findViewById(R.id.ac_name_edit);
        phone=(EditText)findViewById(R.id.ac_tel_edit);
        email=(EditText)findViewById(R.id.ac_email_edit);
        datePicker=(EditText)findViewById(R.id.ac_date_edit);
        groupType=(TextView)findViewById(R.id.ac_group_text);
        sexy=(TextView)findViewById(R.id.ac_sexy_text);
        headPortrait="";
        addGroupBtn=(Button)findViewById(R.id.ac_group_addBtn);
        selectGroupType=(Button)findViewById(R.id.ac_group_selectBtn);
        selectSexyBtn=(Button)findViewById(R.id.ac_sexy_selectBtn);
        selectDateBtn=(Button)findViewById(R.id.ac_date_selectBtn);
        add_imagebutton=(ImageButton)findViewById(R.id.Add_ImageButton);
        addContacts_Top=(ImageView)findViewById(R.id.AddContacts_Top);
    }

    @Override
    public void iniHistory(PersonInfo personInfo){
        name.setText(personInfo.getName());
        phone.setText(personInfo.getTel());
        email.setText(personInfo.getEmail());
        headPortrait=personInfo.getHeadPortrait();
        if(headPortrait.equals("")||headPortrait.equals("None"))  //如果为设置头像，即uri为空，addContacts_Top设置为默认头像
            addContacts_Top.setImageDrawable(getResources().getDrawable(R.drawable.contact_default2));
        else
            addContacts_Top.setImageBitmap(ZoomBitmap.getZoomBitmap(context,headPortrait,"large"));
        datePicker.setText(personInfo.getBirthday());
        groupType.setText(personInfo.getGroup());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            // 得到图片的全路径
            headPortrait=data.getData().toString();  //将URI转化为String
            Log.d(MainActivity.TAG,currentTag+headPortrait);
            // 通过路径加载图片
            //这里省去了图片缩放操作，如果图片过大，可能会导致内存泄漏
            this.addContacts_Top.setImageURI(Uri.parse(headPortrait));   //在将String转化为URI
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void showToast(String s){
        Toast.makeText(this,s,Toast.LENGTH_SHORT).show();
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
        //无论在何种方式进行写入之前，都要将已有信息打包
        writeInPersonInfo();
        if (Id == R.id.action_settings) {
            switch (flag){
                case 0:
                    addContactsPresenter.confirmOthers();
                    break;
                case 1:
                    addContactsPresenter.confirmMyself();
                    break;
                case 2:
                    addContactsPresenter.confirmOthers();
                    break;
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void destroy(){
        //bug,无论是在此Acticity添加联系人还是添加分组，返回到主界面后都会数据为空。
        AddContacts.this.finish();
        //   onDestroy();
    }
    @Override
    public int getFlag() {
        return flag;
    }

    @Override
    public ArrayList<String> getHistory() {
        return history;
    }
    @Override
    public PersonInfo getPersonInfo(){
        return personInfo;
    }
    @Override
    public Activity getActivity(){
        return this;
    }
    private void writeInPersonInfo(){
        PersonInfo.Builder builder=new PersonInfo.Builder();
        personInfo=builder.name(Utils.isNull(name.getText().toString())?phone.getText().toString():name.getText().toString()).
                tel(phone.getText().toString()).
                email(email.getText().toString()).
                group(groupType.getText().toString()).
                headPortrait(headPortrait).
                sexy(sexy.getText().toString()).
                birthday(datePicker.getText().toString()).
                build();
    }
}
