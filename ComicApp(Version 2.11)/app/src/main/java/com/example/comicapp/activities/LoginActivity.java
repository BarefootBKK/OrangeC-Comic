package com.example.comicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.comicapp.R;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.controllers.LoadingViewController;
import com.example.comicapp.entities.MyUser;
import com.example.comicapp.managers.QQLoginManager;
import com.example.comicapp.utils.UserUtil;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity implements QQLoginManager.QQLoginListener, View.OnClickListener {

    private QQLoginManager qqLoginManager;
    private Button loginButton;
    private TextView cancelTextView;
    private ConstraintLayout containerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        qqLoginManager = new QQLoginManager("1108104187", this);
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        qqLoginManager.onActivityResultData(requestCode, resultCode, data);
    }

    private void initData() {
        loginButton = findViewById(R.id.a_login_qq_bitton);
        cancelTextView = findViewById(R.id.a_login_cancel);
        containerView = findViewById(R.id.a_login_container);
        loginButton.setOnClickListener(this);
        cancelTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.a_login_qq_bitton:
                LoadingViewController.with(this)
                        .setLoadingViewContainer(containerView)
                        .setTouchOutsideToDismiss(false)
                        .setOutsideAlpha(0.7f)
                        .setHintTextSize(15)
                        .setIndicator("BallClipRotatePulseIndicator")
                        .setHintText("正在认证")
                        .build();
                loginButton.setText("正在认证...");
                qqLoginManager.launchQQLogin();
                break;
            case R.id.a_login_cancel:
                finishActivity(BasicConfig.RESULT_CODE_CANCEL, null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onQQLoginSuccess(JSONObject jsonObject) {
        LoadingViewController.dismiss();
        loginButton.setText("认证成功");
        MyUser myUser = parseUserData(jsonObject);
        UserUtil.myUser = myUser;
        UserUtil.saveUserCache(this, myUser);
        finishActivity(BasicConfig.RESULT_CODE_SUCCESS, myUser);
        showToast("登录成功");
    }

    @Override
    public void onQQLoginCancel() {
        LoadingViewController.dismiss();
        loginButton.setText("QQ登录");
        showToast("登录取消");
    }

    @Override
    public void onQQLoginError(UiError uiError) {
        LoadingViewController.dismiss();
        loginButton.setText("QQ登录");
        showToast("登录出错：" + uiError.errorMessage);
    }

    private MyUser parseUserData(JSONObject jsonObject) {
        try {
            MyUser user = new MyUser();
            user.setUserOpenId(jsonObject.getString("open_id"));
            user.setUserNickname(jsonObject.getString("nickname"));
            user.setUserHeadingUrl(jsonObject.getString("figureurl_qq_2"));
            return user;
        } catch (JSONException e) {
            return null;
        }
    }

    private void finishActivity(int resultCode, Parcelable parcelable) {
        if (parcelable != null) {
            Intent intent = new Intent();
            intent.putExtra(BasicConfig.LOGIN_INTENT_KEY, parcelable);
            setResult(resultCode, intent);
        } else {
            setResult(resultCode, null);
        }
        finish();
    }
}
