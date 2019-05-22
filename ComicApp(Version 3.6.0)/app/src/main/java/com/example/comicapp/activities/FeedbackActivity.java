package com.example.comicapp.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.comicapp.R;
import com.example.comicapp.entities.Feedback;
import com.example.comicapp.managers.LoadingViewManager;
import com.example.comicapp.utils.ToastUtil;
import com.example.comicapp.utils.UserUtil;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseActivity implements View.OnClickListener {
    private ImageView backView;
    private EditText editView;
    private Button sendButton;
    private TextView countView;
    private boolean isSend = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        Transition slide_bottom = TransitionInflater.from(this).inflateTransition(android.R.transition.slide_bottom);
        getWindow().setEnterTransition(slide_bottom);
        setContentView(R.layout.activity_feedback);
        initView();
    }

    private void initView() {
        backView = findViewById(R.id.a_feedback_back);
        editView = findViewById(R.id.a_feedback_edit);
        sendButton = findViewById(R.id.a_feedback_send);
        countView = findViewById(R.id.a_feedback_textCount);
        backView.setOnClickListener(this);
        sendButton.setOnClickListener(this);
        editView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                check();
            }
        });
    }

    private void check() {
        String content = editView.getText().toString();
        if (content.isEmpty()) {
            countView.setText("0/100");
        } else {
            countView.setText(content.length() + "/100");
            if (content.length() > 100) {
                countView.setTextColor(Color.parseColor("#F07454"));
            } else {
                countView.setTextColor(Color.parseColor("#81AED1"));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.a_feedback_back:
                this.finish();
                break;
            case R.id.a_feedback_send:
                send();
                break;
            default:
                break;
        }
    }

    private void send() {
        String content = editView.getText().toString();
        if (content.isEmpty()) {
            ToastUtil.showShortToast(this, "请不要这么空空如也欸~");
        } else if (content.length() > 100) {
            ToastUtil.showShortToast(this, "请输入100字以内的意见~");
        } else {
            LoadingViewManager.with(this)
                    .setHintText("正在发送...")
                    .setAnimationStyle("LineScalePulseOutRapidIndicator")
                    .setInnerRectangleAlpha(0.95f)
                    .setLoadingContentMargins(90, 50, 90, 50, 18)
                    .setShowInnerRectangle(true)
                    .setTouchOutsideToDismiss(false)
                    .setOnAnimatingListener(new LoadingViewManager.OnAnimatingListener() {
                        @Override
                        public void onAnimating() {

                        }

                        @Override
                        public void onDismiss() {
                            if (isSend) {
                                ToastUtil.showShortToast(FeedbackActivity.this, "感谢您的宝贵意见！");
                                FeedbackActivity.this.finish();
                            } else {
                                ToastUtil.showShortToast(FeedbackActivity.this, "抱歉，出了点小差错...");
                            }
                        }
                    })
                    .build();
            String senderName;
            String senderId;
            if (UserUtil.myUser == null) {
                senderName = "匿名用户";
                senderId = "";
            } else {
                senderName = UserUtil.myUser.getUserNickname();
                senderId = UserUtil.myUser.getUserOpenId();
            }
            Feedback feedBack = new Feedback();
            feedBack.setSenderName(senderName);
            feedBack.setSenderId(senderId);
            feedBack.setContent(content);
            feedBack.save(new SaveListener<String>() {
                @Override
                public void done(String s, BmobException e) {
                    if (e == null) {
                        isSend = true;
                    } else {
                        isSend = false;
                    }
                    LoadingViewManager.dismiss();
                }
            });
        }
    }
}
