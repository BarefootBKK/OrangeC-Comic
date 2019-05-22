package com.example.comicapp.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comicapp.R;
import com.example.comicapp.fragments.subfragments.RecommendComicSubFragment;
import com.example.comicapp.fragments.subfragments.JapaneseComicSubFragment;
import com.example.comicapp.managers.MagicIndicatorManager;

import net.lucode.hackware.magicindicator.MagicIndicator;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends BaseFragment {
    @BindView(R.id.fg_home_magicIndicator)
    public MagicIndicator magicIndicator;
    @BindView(R.id.fg_home_viewPager)
    public ViewPager viewPager;

    private Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                initTabPager();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initTabPager() {
        setDarkStatus(true);
        MagicIndicatorManager indicatorManager = MagicIndicatorManager.getInstance(this);
        indicatorManager.initViewPager(viewPager, getFragmentList(), getTitles());
        indicatorManager.initMagicIndicator(magicIndicator);
    }

    private String[] getTitles() {
        String[] titles = {"推荐", "日漫"};
        return titles;
    }

    private List<Fragment> getFragmentList() {
        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new RecommendComicSubFragment());
        fragmentList.add(new JapaneseComicSubFragment());
        return fragmentList;
    }
}
