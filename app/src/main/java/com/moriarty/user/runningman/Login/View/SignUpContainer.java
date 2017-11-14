package com.moriarty.user.runningman.Login.View;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.moriarty.user.runningman.R;


/**
 * Created by Weiwu on 16/9/2.
 */
public class SignUpContainer extends RelativeLayout {

    private static final int ANIM_DURATION = 2000;
    public static final int LOGIN_STATE = 0;
    public static final int SIGN_UP_STATE = 1;

    private CardView mUserNameCv;
    private CardView mTelCv;
    private CardView mEmailCv;
    private CardView mPassWordCv;
    private CardView mConfirmCv;
    private EditText mUserNameEt;
    private EditText mTelEt;
    private EditText mEmailEt;
    private EditText mPassWordEt;
    private TextView mConfirmTv;
    private LinearLayout mLoginContainer;
    private LinearLayout mNameContainer;

    private int mState = SIGN_UP_STATE;
    private int mLoginHeaderHeight;

    private ValueAnimator mContainerAnim;
    private ValueAnimator mFirCvAnim;

    public interface IConfirmCallBack {
        //you can change it to get the parameters what you wanted.
        void goNext(View v,int mState);
    }

    private IConfirmCallBack mIConfirmCallBack;

    public void setIConfirmCallBack(IConfirmCallBack l) {
        mIConfirmCallBack = l;
    }

    public SignUpContainer(Context context) {
        this(context, null);
    }

    public SignUpContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignUpContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.sign_up_container, this, true);
        initView();
        initAnim();
    }

    private void initView() {
        mUserNameCv = (CardView) findViewById(R.id.user_name_card_v);
        mTelCv = (CardView) findViewById(R.id.tel_card_v);
        mEmailCv = (CardView) findViewById(R.id.email_card_v);
        mPassWordCv = (CardView) findViewById(R.id.password_card_v);
        mConfirmCv = (CardView) findViewById(R.id.confirm_card_v);
        mUserNameEt = (EditText) findViewById(R.id.user_name_edit_input);
        mTelEt = (EditText) findViewById(R.id.tel_edit_input);
        mEmailEt = (EditText) findViewById(R.id.email_edit_input);
        mPassWordEt = (EditText) findViewById(R.id.password_edit_input);
        mConfirmTv = (TextView) findViewById(R.id.confirm_edit);
        mLoginContainer = (LinearLayout) findViewById(R.id.login_container);
        mNameContainer = (LinearLayout) findViewById(R.id.name_container);

        //用于获取view的宽高
        getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mLoginHeaderHeight = mNameContainer.getHeight();
                        initAnim();
                        getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        mLoginContainer.setTranslationY(mLoginHeaderHeight);
                    }
                });
        mConfirmCv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIConfirmCallBack!=null)
                    mIConfirmCallBack.goNext(mConfirmCv,mState);
            }
        });
        mUserNameEt.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus)
                    if(mIConfirmCallBack!=null)
                        mIConfirmCallBack.goNext(mUserNameEt,mState);
            }
        });

    }

    private void initAnim() {
        mContainerAnim = ValueAnimator.ofInt(0, (int) mLoginHeaderHeight)
                .setDuration(ANIM_DURATION);
        mContainerAnim.setInterpolator(new OvershootInterpolator());
        mContainerAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int factor = (int) animation.getAnimatedValue();
                mLoginContainer.setTranslationY(factor);
            }
        });

        mFirCvAnim = ValueAnimator.ofFloat(0, 1)
                .setDuration(ANIM_DURATION);
        mFirCvAnim.setInterpolator(new AccelerateInterpolator());
        mFirCvAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float factor = (float) animation.getAnimatedValue();
                mTelCv.setAlpha(factor);
                mTelCv.setScaleX(factor);
                mTelCv.setScaleY(factor);
                mEmailCv.setAlpha(factor);
                mEmailCv.setScaleX(factor);
                mEmailCv.setScaleY(factor);
            }
        });
    }


    public void setAnimProportion(int timeProportion) {
        float fraction = (float) (timeProportion) / 100f;
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            mContainerAnim.setCurrentFraction(fraction);
            mFirCvAnim.setCurrentFraction(fraction);
        } else {
            mContainerAnim.setCurrentPlayTime((long) (fraction * ANIM_DURATION));
            mFirCvAnim.setCurrentPlayTime((long) (fraction * ANIM_DURATION));
        }
        updataConfirm(timeProportion);
    }

    private void updataConfirm(int process) {
        if (process == 100 && mState != SIGN_UP_STATE) {
            mState = SIGN_UP_STATE;
            mConfirmTv.setText("注册");
        } else if (process == 0 && mState != LOGIN_STATE) {
            mState = LOGIN_STATE;
            mConfirmTv.setText("登录");
        }
    }

    public String getEmailText() {
        return String.valueOf(mEmailEt.getText());
    }

    public String getPassWord() {
        return String.valueOf(mPassWordEt.getText());
    }
    public String getUserName(){
        return String.valueOf(mUserNameEt.getText());
    }
    public String getTel(){
        return String.valueOf(mTelEt.getText());
    }

    //you can add others~
}
