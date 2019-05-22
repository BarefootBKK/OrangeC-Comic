package com.example.comicapp.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.example.comicapp.R;
import com.example.comicapp.entities.AppState;
import com.example.comicapp.utils.ActivityUtil;
import com.example.comicapp.utils.FragmentUtil;
import com.example.comicapp.utils.StatusBarUtil;
import com.example.comicapp.utils.UserUtil;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class LaunchActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        StatusBarUtil.setStatusBarDarkTheme(LaunchActivity.this, true);
        Bmob.initialize(this, "e45715eec38e472c193da78c9f39b3df");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BmobQuery<AppState> bmobQuery = new BmobQuery<>();
                bmobQuery.getObject("hJnvEEEa", new QueryListener<AppState>() {
                    @Override
                    public void done(AppState appState, BmobException e) {
                        if (e == null && appState != null) {
                            if (!appState.isActive()) {
                                AlertDialog.Builder dialog = new AlertDialog.Builder(LaunchActivity.this);
                                dialog.setTitle(appState.getTitle());
                                dialog.setMessage(appState.getMessage());
                                dialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.exit(0);
                                    }
                                });
                                dialog.setNegativeButton("关闭", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        System.exit(0);
                                    }
                                });
                                dialog.show();
                            } else {
                                checkPermission();
                            }
                        } else {
                            checkPermission();
                        }
                    }
                });
            }
        }, 1000);
    }

    private void checkPermission() {
        if (!ActivityUtil.verifyStoragePermissions(LaunchActivity.this)) {
            ActivityUtil.requestStoragePermission(LaunchActivity.this);
        } else {
            if (UserUtil.checkIsLogin(LaunchActivity.this)) {
                UserUtil.myUser = UserUtil.readUserCache(LaunchActivity.this);
            }
            FragmentUtil.initFragments();
            startActivity(new Intent(LaunchActivity.this, MainActivity.class));
            LaunchActivity.this.finish();
        }
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
