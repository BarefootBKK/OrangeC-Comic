package com.example.comicapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.comicapp.R;
import com.example.comicapp.controllers.APKUpdateController;
import com.example.comicapp.entities.APKUpdate;
import com.example.comicapp.utils.GlideCacheUtil;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlideCacheUtil.getInstance().clearImageAllCache(this);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        APKUpdateController apkUpdateController = new APKUpdateController(this);
        apkUpdateController.checkNewVersion("", new APKUpdateController.OnAPKUpdateListener() {
            @Override
            public void onQueryDone(APKUpdate update, boolean newVersion) {

            }

            @Override
            public void onQueryError(Exception e) {

            }
        });
    }
}
