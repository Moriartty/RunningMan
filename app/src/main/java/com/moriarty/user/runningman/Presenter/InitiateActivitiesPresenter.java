package com.moriarty.user.runningman.Presenter;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.moriarty.user.runningman.Activity.Initiate_Activities;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.NetworkRequest.APIManager;
import com.moriarty.user.runningman.NetworkRequest.XUtils.Request;
import com.moriarty.user.runningman.Object.ActivityData;
import com.moriarty.user.runningman.Others.BroadcastManager;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.AddActivityService;
import com.moriarty.user.runningman.Utils.JsonUtils;
import com.moriarty.user.runningman.Utils.NetworkUtil;
import com.moriarty.user.runningman.Utils.PreferenceUtil;
import com.moriarty.user.svprogress.SVProgressHUD;

import java.util.ArrayList;

/**
 * Created by user on 17-9-15.
 */
public class InitiateActivitiesPresenter {
    Context context;
    Initiate_Activities initiate_activities;
    BroadcastManager broadcastManager;
    Handler handler;
    private final int ADDA=0x959;
    Request request;
    public InitiateActivitiesPresenter(Context context,Initiate_Activities initiate_activities){
        this.context=context;
        this.initiate_activities=initiate_activities;
        broadcastManager=new BroadcastManager();
        request=new Request();
        initHandle();
    }

    private void initHandle(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message m){
                switch (m.what){
                    case ADDA:
                        SVProgressHUD.dismiss(context);
                        if(JsonUtils.json2RI(m.obj.toString()).getReturnState()){
                            SVProgressHUD.showSuccessWithStatus(context,context.getString(R.string.create_ac_success));
                            broadcastManager.sendBroadcast(context,7);
                            initiate_activities.finish();
                        }
                        else
                            SVProgressHUD.showErrorWithStatus(context,context.getString(R.string.create_ac_failed));
                        break;
                    case Request.EXCEPTION_FLAG:
                        //
                        break;
                }
            }
        };
    }

    private String getSelectedPeople(){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<Initiate_Activities.selected.size();i++){
            sb.append(Initiate_Activities.selected.get(i));
            if(i<Initiate_Activities.selected.size()-1)
                sb.append("$$##");
        }
        return sb.toString();
    }

    public void confirm(){
        /*Trans2ServerTask task=new Trans2ServerTask();
        task.execute();*/
        if(NetworkUtil.isNetworkAvailable(context)){
            SVProgressHUD.showWithStatus(context,context.getString(R.string.create_ac));
            request.doPost(APIManager.BASE_URL+APIManager.CREATE_ACTIVITY_URL
                    ,getRequestParams(object2Json()),handler,ADDA);
        }
        else
            SVProgressHUD.showInfoWithStatus(context,context.getString(R.string.network_inaccessible));
    }

   /* class Trans2ServerTask extends AsyncTask<Void,Void,Boolean>{
        @Override
        protected Boolean doInBackground(Void... params) {
            HttpUtils httpUtils = new HttpUtils();
            RequestParams p = new RequestParams();
            p.addBodyParameter("index",0+"");
            p.addBodyParameter("content", object2Json());
            httpUtils.send(HttpRequest.HttpMethod.POST, URL,p
                    , new RequestCallBack<String>() {
                        public void onFailure(HttpException arg0, String arg1) {
                            Log.d("onFailure", arg1+";"+arg0.toString());
                        }
                        public void onSuccess(ResponseInfo<String> arg0) {
                            Log.d("onSuccess", arg0.result);
                            Message m = new Message();
                            m.what=ADDA;
                            m.obj=arg0.result;
                            handler.sendMessage(m);
                        }
                    });
            return true;
        }
        @Override
        protected void onPreExecute(){
            SVProgressHUD.showWithStatus(context,"发布中...");
        }
    }*/

    public ActivityData data2Object(String leader, String title, String time,
                                    String address, String tel, String content){
        ActivityData.Builder builder=new ActivityData.Builder().leader(leader)
                .title(title).time(time).address(address).tel(tel).people(getSelectedPeople())
                .content(content);
        return builder.build();
    }

    private String object2Json(){
        ActivityData data=data2Object
                (PreferenceUtil.getUuid(context),
                        initiate_activities.getTopic(),
                        initiate_activities.getTime(),
                        initiate_activities.getAddress(),
                        initiate_activities.getPhone(),
                        initiate_activities.getContent());
        //Log.d(MainActivity.TAG,"startTime: "+start_time.getText().toString());
        return JsonUtils.activity2Json(data);
    }

    private RequestParams getRequestParams(String s){
        RequestParams p = new RequestParams();
        p.addBodyParameter("content", object2Json());
        return p;
    }

    /*class AddActivityTask extends AsyncTask<Void,Void,Boolean> {   //开启异步任务进行联系人数据的写入
        Context mContext;
        public AddActivityTask(Context context){
            mContext=context;
            progressDialog=new ProgressDialog(mContext);
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            Intent startServiceIntent=new Intent();    //开启一个service,来执行添加活动操作
           // startServiceIntent.putExtra("activity_info",data);
            startServiceIntent.setAction("com.moriarty.service.AddActivityService");
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
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            addActivityService=((AddActivityService.AddActivityBinder)service).getService();
            boolean isSucceed=addActivityService.addIntoSQLite(data);
            context.unbindService(conn);
            if(isSucceed){
                //broadcastManager.sendBroadcast(context,1);
                Toast.makeText(context,context.getString(R.string.addcontact_success), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                initiate_activities.finish();
            }
            else
                progressDialog.cancel();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };*/
}
