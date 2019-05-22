package com.example.comicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.comicapp.R;
import com.example.comicapp.adapters.VersionInfoAdapter;
import com.example.comicapp.entities.APKUpdate;
import com.example.comicapp.utils.APKVersionCodeUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class VersionInfoActivity extends BaseActivity implements View.OnClickListener {
    private AlphaAnimation mShowAnim, mHiddenAnim;
    private boolean isShowAnim = true;
    private ImageView backView;
    private TextView currentVersionView;
    private Button feedbackView;
    private RecyclerView infoRy;
    private VersionInfoAdapter versionInfoAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);
        initView();
    }

    private void initView() {
        backView = findViewById(R.id.a_version_back);
        currentVersionView = findViewById(R.id.a_version_current_version);
        feedbackView = findViewById(R.id.a_version_feedback_bn);
        infoRy = findViewById(R.id.a_version_ry);
        // 注册监听器
        backView.setOnClickListener(this);
        feedbackView.setOnClickListener(this);
        //控件显示的动画
        mShowAnim = new AlphaAnimation(0.0f, 1.0f);
        mShowAnim.setDuration(300);
        mHiddenAnim = new AlphaAnimation(1.0f, 0.0f);
        mHiddenAnim.setDuration(300);
        loadVersionInfo();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.a_version_back:
                this.finish();
                break;
            case R.id.a_version_feedback_bn:
                ActivityOptionsCompat options6 = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                ActivityCompat.startActivity(this, new Intent(this, FeedbackActivity.class), options6.toBundle());
                break;
            default:
                break;
        }
    }

    private void loadVersionInfo() {
        BmobQuery<APKUpdate> query = new BmobQuery<>();
        query.order("-createdAt")
                .findObjects(new FindListener<APKUpdate>() {
                    @Override
                    public void done(List<APKUpdate> list, BmobException e) {
                        if (e == null) {
                            currentVersionView.setText("当前版本：" + APKVersionCodeUtil.getVerName(VersionInfoActivity.this));
                            initInfoList(list);
                        }
                    }
                });
    }

    private void initInfoList(List<APKUpdate> versionList) {
        versionInfoAdapter = new VersionInfoAdapter(versionList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        infoRy.setLayoutManager(linearLayoutManager);
        infoRy.setAdapter(versionInfoAdapter);
        infoRy.addOnScrollListener(new MyRecyclerViewScrollListener());
    }

    private class MyRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (isSlideToBottom(infoRy)) {
                feedbackView.startAnimation(mHiddenAnim);
                feedbackView.setVisibility(View.INVISIBLE);
                isShowAnim = false;
            } else {
                if (!isShowAnim) {
                    isShowAnim = true;
                    feedbackView.startAnimation(mShowAnim);
                }
                feedbackView.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }
}
