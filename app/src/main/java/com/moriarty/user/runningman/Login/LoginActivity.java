package com.moriarty.user.runningman.Login;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.moriarty.user.runningman.Activity.MainActivity;
import com.moriarty.user.runningman.Login.View.CatchScrollLayout;
import com.moriarty.user.runningman.Login.View.SignUpContainer;
import com.moriarty.user.runningman.R;
import com.moriarty.user.runningman.Utils.NetworkUtil;
import com.moriarty.user.svprogress.SVProgressHUD;


public class LoginActivity extends AppCompatActivity {

    private CatchScrollLayout mCatchScrollLayout;
    private SignUpContainer mSignUpContainer;
    private LoginActivityPresenter presenter;
    Context context;
    String TAG="Moriarty";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_main);
        context=this;
        presenter=new LoginActivityPresenter(context,this);
        initView();
    }

    public void afterConfirm(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void initView() {
        mCatchScrollLayout = (CatchScrollLayout) findViewById(R.id.catch_sroll_layout);
        mSignUpContainer = (SignUpContainer) findViewById(R.id.sign_up_container);

        mCatchScrollLayout.setIScrollCallBack(new CatchScrollLayout.IScrollCallBack() {
            @Override
            public void onScrollProcess(int process, boolean isLeft) {
                if (!isLeft){
                    process = 100 - process;
                }
                mSignUpContainer.setAnimProportion(process);
            }
        });

        mSignUpContainer.setIConfirmCallBack(new SignUpContainer.IConfirmCallBack() {
            @Override
            public void goNext(View v,int mState) {
                //change this method and add the parameters you wanted
                //Add your task here.
                //先进行网络判断
                if(NetworkUtil.isNetworkAvailable(context))
                    presenter.handleEvent(v,mState);
                else
                    SVProgressHUD.showInfoWithStatus(context,getString(R.string.network_inaccessible));

            }
        });
    }

    public String getUserName(){
        return mSignUpContainer.getUserName();
    }
    public String getPassword(){
        return mSignUpContainer.getPassWord();
    }
    public String getTel(){
        return mSignUpContainer.getTel();
    }
    public String getEmail(){
        return mSignUpContainer.getEmailText();
    }
}
