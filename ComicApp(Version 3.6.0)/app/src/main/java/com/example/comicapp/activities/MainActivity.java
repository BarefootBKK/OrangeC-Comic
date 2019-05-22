package com.example.comicapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;

import com.example.comicapp.R;
import com.example.comicapp.controllers.APKUpdateController;
import com.example.comicapp.entities.APKUpdate;
import com.example.comicapp.interfaces.MyTouchListener;
import com.example.comicapp.utils.FragmentUtil;
import com.example.comicapp.utils.GlideCacheUtil;
import com.example.comicapp.utils.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    public static final int FRAGMENT_HOME = 0;
    public static final int FRAGMENT_BOOKSHELF = 1;
    public static final int FRAGMENT_USER = 2;
    private static final int containerId = R.id.a_main_fl_container;
    private FragmentManager mFragmentManager;
    private Fragment mFragment;
    private List<MyTouchListener> myTouchListeners = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarUtil.setRootViewFitsSystemWindows(this, false);
        initFragment();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlideCacheUtil.getInstance().clearImageAllCache(this);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyTouchListener listener : myTouchListeners) {
            listener.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    public void initFragment() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction()
                .add(containerId, FragmentUtil.getFragment(FRAGMENT_HOME))
                .commit();
        mFragment = FragmentUtil.getFragment(FRAGMENT_HOME);
    }

    public void switchFragment(Fragment to) {
        if (mFragment != to) {
            mFragment.onPause();
            FragmentTransaction transaction = mFragmentManager.beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, android.R.anim.fade_out);
            // 先判断是否被add过
            if (!to.isAdded()) {
                transaction.hide(mFragment).add(containerId, to).commit();
            } else {
                to.onResume();
                transaction.hide(mFragment).show(to).commit();
            }
            mFragment = to;
        }
    }

    public void registerMyTouchListener(MyTouchListener listener) {
        myTouchListeners.add(listener);
    }

    public void unRegisterMyTouchListener(MyTouchListener listener) {
        myTouchListeners.remove( listener );
    }
}
