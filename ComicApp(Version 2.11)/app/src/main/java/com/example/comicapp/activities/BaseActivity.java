package com.example.comicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.comicapp.controllers.AWCoreController;
import com.example.comicapp.utils.ActivityUtil;
import com.example.comicapp.utils.GlideCacheUtil;

public class BaseActivity extends AppCompatActivity {
    protected AWCoreController awCoreController;
    public static final String TAG = "更新";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityUtil.setStatusBar(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (awCoreController != null) {
            awCoreController.destroy();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                break;
            default:
                break;
        }
        return true;
    }

    protected ActionBar setActivityToolbar(int toolbarId, boolean showHomeAsUp, boolean isDisplayToolbarTitle) {
        setSupportActionBar((Toolbar) findViewById(toolbarId));
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(showHomeAsUp);
            actionBar.setDisplayShowTitleEnabled(isDisplayToolbarTitle);
            return actionBar;
        }
        return null;
    }

    protected void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
