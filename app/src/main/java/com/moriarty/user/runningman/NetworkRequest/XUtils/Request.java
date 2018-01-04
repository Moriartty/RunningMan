package com.moriarty.user.runningman.NetworkRequest.XUtils;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.moriarty.user.runningman.Activity.MainActivity;


import org.json.JSONObject;

import java.util.concurrent.TimeoutException;

/**
 * Created by user on 17-10-11.
 */
public class Request {
    HttpUtils httpUtils;
    public final static int EXCEPTION_FLAG=0x110;
    public Request(){
        httpUtils=new HttpUtils();
        httpUtils.configTimeout(10 * 1000); //10秒的等待时间
    }
    public void doPost(String url, RequestParams p,final Handler handler,final int flag){
        httpUtils.send(HttpRequest.HttpMethod.POST, url,p
                , new RequestCallBack<String>() {
                    public void onFailure(HttpException arg0, String arg1) {
                        if(arg0 instanceof HttpException){
                            Log.d(MainActivity.TAG, "http exception:"+arg1+";"+arg0.toString());
                            Message m=new Message();
                            m.what=EXCEPTION_FLAG;
                            handler.sendMessage(m);
                        }
                    }
                    public void onSuccess(ResponseInfo<String> arg0) {
                        Log.d("onSuccess", arg0.result);
                        Message m=new Message();
                        m.what=flag;
                        m.obj=arg0.result;
                        handler.sendMessage(m);
                    }
                });
        }

    public void doGet(String url, RequestParams p,final Handler handler,final int flag){
        httpUtils.send(HttpRequest.HttpMethod.GET, url,p
                , new RequestCallBack<String>() {
                    public void onFailure(HttpException arg0, String arg1) {
                        if(arg0 instanceof HttpException){
                            Log.d(MainActivity.TAG, "http exception:"+arg1+";"+arg0.toString());
                            Message m=new Message();
                            m.what=EXCEPTION_FLAG;
                            handler.sendMessage(m);
                        }
                    }
                    public void onSuccess(ResponseInfo<String> arg0) {
                        Log.d("onSuccess", arg0.result);
                        Message m=new Message();
                        m.what=flag;
                        m.obj=arg0.result;
                        handler.sendMessage(m);
                    }
                });
    }
}
