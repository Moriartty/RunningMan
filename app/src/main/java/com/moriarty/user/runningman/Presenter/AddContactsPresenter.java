package com.moriarty.user.runningman.Presenter;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.moriarty.user.runningman.Activity.AddContacts;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Fragment.DatePickerFragment;
import com.moriarty.user.runningman.Interface.AddContactsView;
import com.moriarty.user.runningman.Interface.IAddContactsPresenter;
import com.moriarty.user.runningman.Object.PersonInfo;
import com.moriarty.user.runningman.Others.BroadcastManager;
import com.moriarty.user.runningman.Others.PopupMenuManager;
import com.moriarty.user.runningman.Others.ZoomBitmap;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.AddContactsService;
import com.moriarty.user.runningman.Service.GroupSettingService;
import com.moriarty.user.runningman.Utils.PreferenceUtil;

import java.util.ArrayList;

/**
 * Created by user on 17-8-11.
 */
public class AddContactsPresenter implements IAddContactsPresenter {
    Context context;
    GroupSettingService groupSettingService;
    AddContactsView addContactsView;
    PopupMenuManager popupMenuManager;
    AddContactsService addContactsService;
    AddContactsService.AddContactsBinder addContactsBinder;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    BroadcastManager broadcastManager=new BroadcastManager();
    ProgressDialog progressDialog;
    String historyName;
    private String currentTag="AddContactsPresenter:";
    public AddContactsPresenter(Context context,AddContactsView addContactsView){
        this.context=context;
        this.addContactsView=addContactsView;
        popupMenuManager=new PopupMenuManager(context);
    }
    @Override
    public void modelChooser(Intent intent){
        int flag;
        flag=intent.getIntExtra("flag",0);
        PersonInfo personInfo;
        if(flag>0){
            personInfo=(PersonInfo) intent.getSerializableExtra(context.getResources().getString(R.string.person_info_data));
            if(personInfo!=null) {
                addContactsView.iniHistory(personInfo);
                historyName = personInfo.getName();
            }
        }
    }

    @Override
    public void addGroupAction(View v0) {
        final EditText editText=(EditText)v0.findViewById(R.id.addgroup_edittext);
        new AlertDialog.Builder(v0.getContext()).setIcon(R.drawable.groupsetting_addgroup)
                .setTitle(context.getResources().getString(R.string.new_group)).setView(v0)
                .setPositiveButton(context.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(!editText.getText().toString().equals("")){
                            Intent addGroupIntent=new Intent();
                            addGroupIntent.putExtra("groupsetting_flag",0);
                            addGroupIntent.putExtra("newGroupName",editText.getText().toString());
                            addGroupIntent.setAction("com.moriarty.service.GroupSettingService");
                            addGroupIntent.setPackage(context.getPackageName());
                            context.bindService(addGroupIntent, connection_add,context.BIND_AUTO_CREATE);
                        }
                    }
                })
                .setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
    }

    @Override
    public void selectGroupAction(Button selectGroupType, ArrayList<String> allGroupName) {
        allGroupName.clear();  //每次点击都会开启查询，所以用完都要清空
        popupMenuManager.showGroupMenu(selectGroupType,allGroupName);
    }
    @Override
    public void selectSexyAction(Button selectSexyType){
        popupMenuManager.showSexyMenu(selectSexyType);
    }
    @Override
    public void selectDateAction(){
        DatePickerFragment datePickerFrg = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                AddContacts.datePicker.setText(year +"-" + (month + 1) + "-" + day);
                //Log.d(MainActivity.TAG,"选择的日期是：" + year +"-" + (month + 1) + "-" + day);
            }
        };
        datePickerFrg.show(addContactsView.getActivity().getFragmentManager(), "datePickerFrg");
    }

    private ServiceConnection connection_add=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            boolean returnValue;
            groupSettingService=((GroupSettingService.GroupSettingBinder)service).getService();
            returnValue=groupSettingService.addGroupIntoSQLite();
            context.unbindService(connection_add);
            if(returnValue){
                addContactsView.showToast(context.getResources().getString(R.string.addgroup_success));
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public void confirmOthers() {
        AddTask addTask=new AddTask(context);
        addTask.execute();
    }

    @Override
    public void confirmMyself() {
        sharedPreferences=context.getSharedPreferences("myself",context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        //这里需要将该联系人的source_id传进去重新写入
        //Log.d(MainActivity.TAG,currentTag+"source_id is"+source_id);
        boolean isSucceed= PreferenceUtil.writeInMyselfPre(editor,context,addContactsView.getPersonInfo());
        if(isSucceed){
            addContactsView.showToast(context.getResources().getString(R.string.write_in_myself_succeed));
            broadcastManager.sendBroadcast(context,6);
            broadcastManager.sendBroadcast(context,5);
            addContactsView.destroy();
            //AddContact.this.finish();
        }
        else {
            addContactsView.showToast(context.getResources().getString(R.string.write_in_myself_failed));
        }
    }
    class AddTask extends AsyncTask<Void,Void,Boolean> {   //开启异步任务进行联系人数据的写入
        Context mContext;
        public AddTask(Context context){
            mContext=context;
            progressDialog=new ProgressDialog(mContext);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            Intent startServiceIntent=new Intent();    //开启一个service,来执行添加联系人操作
            startServiceIntent.putExtra("person_info",addContactsView.getPersonInfo());
            //Log.d(MainActivity.TAG,addContactsView.getPersonInfo().getName().toString());
            startServiceIntent.setAction("com.moriarty.service.ADDCONTACESSERVICE");
            startServiceIntent.setPackage(context.getPackageName());
            context.bindService(startServiceIntent,conn,context.BIND_AUTO_CREATE);
            Log.d(MainActivity.TAG,"service is connected");
            return true;
        }
        @Override
        protected void onPostExecute(Boolean result){

        }
        @Override
        protected void onPreExecute(){
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage(context.getResources().getString(R.string.wait_to_add));
            progressDialog.show();
        }
    }
    private ServiceConnection conn=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            addContactsBinder=(AddContactsService.AddContactsBinder)service;
            addContactsService=(addContactsBinder).getService();
            Log.d(MainActivity.TAG,currentTag+"I got service");
            if(addContactsView.getFlag()==2){
                if(addContactsService.updateContactInSQLite(historyName)){
                    context.unbindService(conn);
                    Log.d(MainActivity.TAG,currentTag+"service is unbind");
                    broadcastManager.sendBroadCast_Sixth_WithValue(context,addContactsView.getPersonInfo().getName());
                    //broadcastManager.sendBroadcast(context,1);
                    broadcastManager.sendBroadcast(context,3);
                    progressDialog.dismiss();
                    addContactsView.destroy();
                    //AddContacts.this.finish();
                }
                else{     //这里需要进一步判断是哪一步未成功，需要继续进行联系人添加
                    addContactsView.showToast(context.getResources().getString(R.string.unabletoaddcontact));
                    progressDialog.cancel();
                    context.unbindService(conn);
                }
            }
            else {    //flag==0
                Log.d(MainActivity.TAG,"prepare to write");

                if(addContactsService.addIntoSQLite()){
                    context.unbindService(conn);
                    Log.d(MainActivity.TAG,"service is unbind");
                    //broadcastManager.sendBroadcast(context,1);
                    broadcastManager.sendBroadcast(context,3);
                    progressDialog.dismiss();
                    addContactsView.destroy();
                    //AddContacts.this.finish();
                }
                else{     //这里需要进一步判断是哪一步为成功，需要继续进行联系人添加
                    addContactsView.showToast(context.getResources().getString(R.string.unabletoaddcontact));
                    progressDialog.cancel();
                    context.unbindService(conn);
                }
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };
}
