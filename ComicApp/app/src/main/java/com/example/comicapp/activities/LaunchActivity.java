package com.example.comicapp.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.comicapp.R;
import com.example.comicapp.utils.ActivityUtil;
import com.example.comicapp.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;

public class LaunchActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Bmob.initialize(this, "e45715eec38e472c193da78c9f39b3df");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!ActivityUtil.verifyStoragePermissions(LaunchActivity.this)) {
                    ActivityUtil.requestStoragePermission(LaunchActivity.this);
                } else {
                    if (UserUtil.checkIsLogin(LaunchActivity.this)) {
                        UserUtil.myUser = UserUtil.readUserCache(LaunchActivity.this);
                    }
                    startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                    LaunchActivity.this.finish();
                }
            }
        }, 1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0) {
            List<String> deniedPermissionList = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissionList.add(permissions[i]);
                }
            }
            if (deniedPermissionList.isEmpty()) {
                startActivity(new Intent(LaunchActivity.this, MainActivity.class));
                LaunchActivity.this.finish();
            } else {
                ActivityUtil.requestStoragePermission(this);
            }
        }
    }
}
