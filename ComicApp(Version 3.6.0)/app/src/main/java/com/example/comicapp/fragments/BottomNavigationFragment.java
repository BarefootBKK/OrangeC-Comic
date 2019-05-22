package com.example.comicapp.fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.comicapp.R;
import com.example.comicapp.activities.MainActivity;
import com.example.comicapp.utils.FragmentUtil;

public class BottomNavigationFragment extends BaseFragment {
    private BottomNavigationView navigationView;
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bottomnavigation, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        navigationView = getActivity().findViewById(R.id.fragment_bm_navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        // 设置导航栏图标和字体样式
        navigationView.setItemIconTintList(null);
        ColorStateList colorStateList = getResources().getColorStateList(R.color.bm_navigation_text);
        navigationView.setItemTextColor(colorStateList);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.bm_navigation_home:
                    mainActivity.switchFragment(FragmentUtil.getFragment(MainActivity.FRAGMENT_HOME));
                    break;
                case R.id.bm_navigation_bookshelf:
                    mainActivity.switchFragment(FragmentUtil.getFragment(MainActivity.FRAGMENT_BOOKSHELF));
                    break;
                case R.id.bm_navigation_user:
                    mainActivity.switchFragment(FragmentUtil.getFragment(MainActivity.FRAGMENT_USER));
                    break;
                default:
                    return false;
            }
            return true;
        }
    };
}
