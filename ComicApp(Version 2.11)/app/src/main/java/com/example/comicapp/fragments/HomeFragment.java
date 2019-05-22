package com.example.comicapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comicapp.R;
import com.example.comicapp.fragments.subfragments.ChineseComicSubFragment;
import com.example.comicapp.fragments.subfragments.JapaneseComicSubFragment;
import com.example.comicapp.managers.TabPagerManager;

public class HomeFragment extends BaseFragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initTabPager();
    }

    private void initTabPager() {
        tabLayout = getActivity().findViewById(R.id.fg_home_tabLayout);
        viewPager = getActivity().findViewById(R.id.fg_home_viewPager);

        TabPagerManager.with(getChildFragmentManager())
                .setTabPager(tabLayout, viewPager)
                .add("国漫", new ChineseComicSubFragment())
                .add("日漫", new JapaneseComicSubFragment())
                .setIndicatorLineMode(TabPagerManager.EQUAL_TEXT)
                .commit();
    }
}
