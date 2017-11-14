package com.moriarty.user.runningman.Login;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Login.LoginActivity;
import com.moriarty.user.runningman.Login.View.SignUpContainer;

import com.moriarty.user.runningman.NetworkRequest.APIManager;
import com.moriarty.user.runningman.NetworkRequest.XUtils.Request;
import com.moriarty.user.runningman.Object.ResponseInfoFromNet;
import com.moriarty.user.runningman.Object.User;
import com.moriarty.user.runningman.Utils.JsonUtils;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Service.AddContactsService;
import com.moriarty.user.runningman.Utils.NetworkUtil;
import com.moriarty.user.runningman.Utils.PreferenceUtil;
import com.moriarty.user.runningman.Utils.Utils;
import com.moriarty.user.svprogress.SVProgressHUD;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 17-10-10.
 */
public class LoginActivityPresenter {
    Context context;
    LoginActivity loginActivity;
    Handler handler;
    String TAG="Moriarty";
    int index=0;
    boolean isRightName=false;
    private final static int L=0x213;
    private final static int S=0x324;
    private final static int C=0x534;
    Request request;
    String preName="";
    String curName="";

    public LoginActivityPresenter(Context context,LoginActivity loginActivity){
        this.context=context;
        this.loginActivity=loginActivity;
        request=new Request();
        initHandler();
        //先尝试直接登陆
        direct_Login();
    }

    private void direct_Login(){
        SharedPreferences sharedPreferences=context.getSharedPreferences("user",context.MODE_PRIVATE);
        User user = PreferenceUtil.getUserInfo(context,sharedPreferences);
        //如果user文件中有之前的用户关键信息，则直接登陆
        if(!isNull(user.getUserName())&&!isNull(user.getPassWord())){
            if(NetworkUtil.isNetworkAvailable(context)){
                SVProgressHUD.showWithStatus(context,context.getString(R.string.logining));
                request.doPost(APIManager.BASE_URL+APIManager.LOGIN_URL,getRequestParams(JsonUtils.user2Json(user)),handler,L);
            }
            else
                SVProgressHUD.showInfoWithStatus(context,context.getString(R.string.network_inaccessible));
        }
    }

    private void initHandler(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message m){
                switch (m.what){
                    case Request.EXCEPTION_FLAG:
                        //handle exception
                        SVProgressHUD.dismiss(context);
                        SVProgressHUD.showInfoWithStatus(context,"网络连接超时");
                        break;
                    default:
                        if(!isNull(m.obj.toString())){
                            ResponseInfoFromNet ri=JsonUtils.json2RI(m.obj.toString());
                            switch (m.what){
                                case L:
                                    SVProgressHUD.dismiss(context);
                                    if(ri.getReturnState()){
                                        Log.d(MainActivity.TAG,"LoginSuccess");
                                        loginActivity.afterConfirm();
                                    }
                                    break;
                                case S:
                                    SVProgressHUD.dismiss(context);
                                    if(ri.getReturnState()){
                                        Log.d(TAG,"注册成功");
                                        SVProgressHUD.showSuccessWithStatus(context,context.getString(R.string.sign_up_success));
                                        //注册完之后将user文件中信息写入user和myInfo文件
                                        if(writeUserDatasInUser(ri.getContent())&&writeUserDatasInMySelf(ri.getContent()))
                                            //都写入正确才能进行下一步操作
                                            loginActivity.afterConfirm();
                                        else
                                            SVProgressHUD.showErrorWithStatus(context,"信息保存失败");
                                    }
                                    else {
                                        Log.d(TAG,"注册失败");
                                        SVProgressHUD.showErrorWithStatus(context,context.getString(R.string.username_error));
                                    }
                                    break;
                                case C:
                                    Log.d(TAG,ri.getReturnState()+"");
                                    if(ri.getReturnState()){
                                        isRightName=false;//用户名状态为非法
                                        SVProgressHUD.showInfoWithStatus(context,context.getString(R.string.username_error));
                                    }
                                    else if(!ri.getReturnState())
                                        isRightName=true;
                                    break;
                            }
                        }
                        break;
                }
            }
        };
    }

    private Boolean isNull(String text){
        if(text==null||text.equals("")){
            return true;
        }
        return false;
    }

    //将服务端返回user数据写入user文件中
    private Boolean writeUserDatasInUser(String s){
        User user= JsonUtils.json2User(s);
        SharedPreferences sharedPreferences=context.getSharedPreferences("user",context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        return PreferenceUtil.writeInUserPre(editor,context,user);
    }
    //将user信息写入myself文件中
    private Boolean writeUserDatasInMySelf(String s){
        User user = JsonUtils.json2User(s);
        SharedPreferences sharedPreferences=context.getSharedPreferences("myself",context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        return PreferenceUtil.writeInMyselfPre(editor,context, Utils.user2PersonInfo(user));
    }

    //检查数据的完整性
    private Boolean checkDatas(int index){
        boolean isOk=false;
        if(!isNull(loginActivity.getUserName())&&!isNull(loginActivity.getPassword())){
            if(index==1){
                if(!isNull(loginActivity.getTel())&&!isNull(loginActivity.getEmail())
                        &&Utils.isPhoneCorrect(context,loginActivity.getTel()))
                    isOk=true;
                else
                    isOk=false;
            }
            else if(index==0)
                isOk=true;
        }
        else
            isOk=false;
        return isOk;
    }

    private JSONObject toJsonObject(){
        JSONObject object = new JSONObject();
        try{
            object.put("userName",loginActivity.getUserName());
            object.put("passWord",loginActivity.getPassword());
            object.put("phone",loginActivity.getTel());
            object.put("email",loginActivity.getEmail());
            return object;
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    private RequestParams getRequestParams(String s){
        RequestParams p = new RequestParams();
        p.addBodyParameter("content",s);
        return p;
    }

    public void handleEvent(View v,int mState){

        if(v.getId()== R.id.confirm_card_v) {
            Log.d(TAG,"OnClick");
            index=mState;
            //检查用户名是否合法和数据完整性
            switch (mState){
                case 0:
                    if(checkDatas(index)){
                        //留一个后门
                        if(loginActivity.getUserName().equals("fuck")&&loginActivity.getPassword().equals("2333"))
                            loginActivity.afterConfirm();
                        ////////////////////////////////////////
                        request.doPost(APIManager.BASE_URL+APIManager.LOGIN_URL
                                ,getRequestParams(toJsonObject().toString()),handler,L);
                        SVProgressHUD.showWithStatus(context,context.getString(R.string.logining));
                    }
                    else
                        SVProgressHUD.showInfoWithStatus(context,context.getString(R.string.info_incomplete));
                    break;
                case 1:
                    preName=curName;
                    curName=loginActivity.getUserName();
                    //如果用户名正确或者用户名不正确但已修改，则进入注册阶段
                    if(isRightName||(!isRightName&&!preName.equals(curName))){
                        if(checkDatas(index)){
                            request.doPost(APIManager.BASE_URL+APIManager.SIGNUP_URL
                                    ,getRequestParams(toJsonObject().toString()),handler,S);
                            SVProgressHUD.showWithStatus(context,context.getString(R.string.signing_up));
                        }
                        else
                            SVProgressHUD.showInfoWithStatus(context,context.getString(R.string.info_incomplete));
                    }
                    else
                        SVProgressHUD.showInfoWithStatus(context,context.getString(R.string.username_error));
                    break;
            }
        }
        else if(v.getId()==R.id.user_name_edit_input){
            Log.d(TAG,"OnFocusChanged");
            if(mState == SignUpContainer.SIGN_UP_STATE){
                if(!isNull(loginActivity.getUserName())){
                    //如果处于注册状态并且username不为空，则发往服务端checkname
                    index=2;
                    request.doPost(APIManager.BASE_URL+APIManager.CHECK_USERNAME_URL
                            ,getRequestParams(toJsonObject().toString()),handler,C);
                }
            }
        }
    }
}
