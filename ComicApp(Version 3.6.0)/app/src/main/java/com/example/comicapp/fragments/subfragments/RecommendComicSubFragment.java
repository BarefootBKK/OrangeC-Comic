package com.example.comicapp.fragments.subfragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.comicapp.R;
import com.example.comicapp.activities.MainActivity;
import com.example.comicapp.adapters.ChineseComicListAdapter;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.controllers.APKUpdateController;
import com.example.comicapp.entities.APKUpdate;
import com.example.comicapp.entities.Comic;
import com.example.comicapp.interfaces.DataLoadListener;
import com.example.comicapp.interfaces.MyTouchListener;
import com.example.comicapp.loaders.BaseComicLoader;
import com.example.comicapp.loaders.ChineseComicLoader;
import com.example.comicapp.loaders.RecommendationComicLoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;

import com.lcodecore.tkrefreshlayout.header.SinaRefreshView;

import java.util.List;


public class RecommendComicSubFragment extends Fragment {
    private RecyclerView recyclerView;
    private TwinklingRefreshLayout mRefreshLayout;
    private ChineseComicLoader chineseComicLoader;
    private RecommendationComicLoader comicLoader;
    private MyTouchListener myTouchListener;
    private ChineseComicListAdapter comicListAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subfragment_recommend, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chineseComicLoader.destroyLoader();
        ((MainActivity)this.getActivity()).unRegisterMyTouchListener(myTouchListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        chineseComicLoader.pause();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        checkUpdate();
        comicLoader = new RecommendationComicLoader(this);
        chineseComicLoader = new ChineseComicLoader(this);
        recyclerView = getActivity().findViewById(R.id.sub_fg_recommend_ry);
        mRefreshLayout = getActivity().findViewById(R.id.sub_fg_recommend_refresh);
        initRefreshLayout();
        loadData();
        initTouchListener();
        mRefreshLayout.startRefresh();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        chineseComicLoader.loadComicList("https://www.manhuatai.com/all.html", new DataLoadListener() {
            @Override
            public void onLoadFinished(Object o, Exception e) {
                if (e == null) {
                    showComicItems((List<Comic>) o);
                    mRefreshLayout.finishRefreshing();
                }
            }
        });
        comicLoader.loadRecommendationComics("https://www.50mh.com/", new DataLoadListener() {
            @Override
            public void onLoadFinished(Object o, Exception e) {
                if (comicListAdapter != null) {
                    comicListAdapter.updateBannerImgList((List<Comic>) o);
                }
            }
        });
    }

    /**
     * 显示recyclerView
     */
    private void showComicItems(List<Comic> comicList) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        comicListAdapter = new ChineseComicListAdapter(comicList, R.layout.item_home, getContext());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(comicListAdapter);
    }

    /**
     * 检查APP更新
     */
    private void checkUpdate() {
        final APKUpdateController updateController = new APKUpdateController(getActivity());
        updateController.checkNewVersion(new APKUpdateController.OnAPKUpdateListener() {
            @Override
            public void onQueryDone(APKUpdate update, boolean newVersion) {
                if (newVersion && update != null) {
                    updateController.showNewVersionDialog(update);
                }
            }
            @Override
            public void onQueryError(Exception e) {
                Log.d(BasicConfig.TAG, "onQueryError: " + e.toString());
            }
        });
    }

    private void initRefreshLayout() {
        mRefreshLayout.setEnableLoadmore(false);
        SinaRefreshView headerView = new SinaRefreshView(getContext());
        headerView.setArrowResource(R.drawable.ic_my_arrow);
        headerView.setTextColor(Color.parseColor("#7C7676"));
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadData();
                    }
                }, 800);
            }
        });
    }

    private void initTouchListener() {
        myTouchListener = new MyTouchListener() {
            float xDown = -1, yDown = -1;
            @Override
            public void onTouchEvent(MotionEvent ev) {
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        xDown = ev.getX();
                        yDown = ev.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        if(Math.abs(ev.getX() - xDown) < Math.abs(ev.getY() - yDown) && (ev.getY() - yDown) > 0) {
                            // ptrFrame.setEnabled(true);
                        } else {
                            // ptrFrame.setEnabled(false);
                        }
                        break;
                    case MotionEvent.ACTION_CANCEL:
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
            }
        };
        ((MainActivity) getActivity()).registerMyTouchListener(myTouchListener);
    }
}
