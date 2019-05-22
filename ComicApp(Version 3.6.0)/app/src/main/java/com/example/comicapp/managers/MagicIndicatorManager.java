package com.example.comicapp.managers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.example.comicapp.R;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.UIUtil;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MagicIndicatorManager {
    private Activity mActivity;
    private Context mContext;
    private FragmentManager fragmentManager;
    private MagicIndicator magicIndicator;
    private CommonNavigator commonNavigator;
    private ViewPager viewPager;
    private List<String> titleList;
    private List<Fragment> fragmentList;
    private int selectedSize = 16;
    private int selectedColor;
    private int unselectedColor;
    private int indicatorColor;

    public static MagicIndicatorManager getInstance(AppCompatActivity activity) {
        return new MagicIndicatorManager(activity);
    }

    public static MagicIndicatorManager getInstance(Fragment fragment) {
        return new MagicIndicatorManager(fragment);
    }

    public MagicIndicatorManager(AppCompatActivity mActivity) {
        this.mActivity = mActivity;
        this.mContext = mActivity;
        this.fragmentManager = mActivity.getSupportFragmentManager();
        initColor();
    }

    public MagicIndicatorManager(Fragment mFragment) {
        this.mActivity = mFragment.getActivity();
        this.mContext = mFragment.getContext();
        this.fragmentManager = mFragment.getChildFragmentManager();
        initColor();
    }
    
    public MagicIndicatorManager initViewPager(ViewPager viewPager, List<Fragment> fragmentList, String[] titles) {
        this.viewPager = viewPager;
        this.fragmentList = fragmentList;
        this.titleList = new ArrayList<>(Arrays.asList(titles));
        return this;
    }

    /**
     * 初始化颜色
     */
    private void initColor() {
        this.selectedColor = mContext.getResources().getColor(R.color.floatingActionButton);
        this.indicatorColor = mContext.getResources().getColor(R.color.floatingActionButton);
        this.unselectedColor = Color.parseColor("#3C3939");
    }

    public void initMagicIndicator(final MagicIndicator magicIndicator) {
        this.magicIndicator = magicIndicator;
        commonNavigator = new CommonNavigator(mContext);
        commonNavigator.setAdapter(new CommonNavigatorAdapter() {

            @Override
            public int getCount() {
                return titleList.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context mContext, final int index) {
                MyPagerTitleView myPagerTitleView = new MyPagerTitleView(mContext);
                myPagerTitleView.setText(titleList.get(index));
                myPagerTitleView.setTextSize(selectedSize);
                myPagerTitleView.setTextColor(selectedColor);
                myPagerTitleView.getPaint().setFakeBoldText(true);
                myPagerTitleView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewPager.setCurrentItem(index);
                    }
                });
                return myPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context mContext) {
                LinePagerIndicator indicator = new LinePagerIndicator(mContext);
                indicator.setYOffset(22); // indicator到底部的距离
                indicator.setVisibility(View.VISIBLE);
                indicator.setMode(LinePagerIndicator.MODE_EXACTLY);
                indicator.setLineHeight(UIUtil.dip2px(mContext, 3));
                indicator.setLineWidth(UIUtil.dip2px(mContext, 18));
                indicator.setRoundRadius(UIUtil.dip2px(mContext, 1.5));
                indicator.setColors(indicatorColor);
                return indicator;
            }
        });
        magicIndicator.setNavigator(commonNavigator);
        LinearLayout titleContainer = commonNavigator.getTitleContainer(); // must after setNavigator
        titleContainer.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        titleContainer.setDividerPadding(UIUtil.dip2px(mContext, 15));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                magicIndicator.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                magicIndicator.onPageSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                magicIndicator.onPageScrollStateChanged(state);
            }
        });
        viewPager.setAdapter(getAdapter());
        viewPager.setCurrentItem(0);
        ViewPagerHelper.bind(magicIndicator, viewPager);
        magicIndicator.bringToFront();
    }

    public MagicIndicator getMagicIndicator() {
        return magicIndicator;
    }

    public MagicIndicatorManager setMagicIndicator(MagicIndicator magicIndicator) {
        this.magicIndicator = magicIndicator;
        return this;
    }

    private MyAdapter getAdapter() {
        MyAdapter myAdapter = new MyAdapter(fragmentManager);
        myAdapter.setFragmentList(fragmentList);
        myAdapter.setTitles(titleList);
        return myAdapter;
    }

    private class MyAdapter extends FragmentPagerAdapter {
        private List<String> titles = new ArrayList<>();
        private List<Fragment> fragmentList = new ArrayList<>();

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        public void setTitles(List<String> titles) {
            this.titles = titles;
        }

        public void setFragmentList(List<Fragment> fragmentList) {
            this.fragmentList = fragmentList;
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return titles.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    public class MyPagerTitleView extends ColorTransitionPagerTitleView {

        public MyPagerTitleView(Context mContext) {
            super(mContext);
        }

        @Override
        public void onLeave(int index, int totalCount, float leavePercent, boolean leftToRight) {

        }

        @Override
        public void onEnter(int index, int totalCount, float enterPercent, boolean leftToRight) {

        }

        @Override
        public void onSelected(int index, int totalCount) {
            setTextColor(selectedColor);
        }

        @Override
        public void onDeselected(int index, int totalCount) {
            setTextColor(unselectedColor);
        }
    }
}
