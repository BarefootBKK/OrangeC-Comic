package com.example.comicapp.fragments.subfragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.comicapp.R;
import com.example.comicapp.adapters.JapaneseComicListAdapter;
import com.example.comicapp.entities.Comic;
import com.example.comicapp.interfaces.DataLoadListener;
import com.example.comicapp.loaders.JapaneseComicLoader;

import java.util.List;

public class JapaneseComicSubFragment extends Fragment {
    private String TAG = "测试";
    private boolean isViewCreated = false;
    private boolean isUiVisible = false;
    private boolean isLazyLoaded = false;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<Comic> comicList;
    private JapaneseComicLoader japaneseComicLoader;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subfragment_japanese, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isViewCreated = true;
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        japaneseComicLoader.destroyLoader();
        isUiVisible = false;
        isViewCreated = false;
        isLazyLoaded = false;
    }

    @Override
    public void onStop() {
        super.onStop();
        japaneseComicLoader.pause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUiVisible = true;
            lazyLoad();
        } else {
            isUiVisible = false;
        }
    }

    /**
     * 懒加载
     */
    private void lazyLoad() {
        if (isViewCreated && isUiVisible && !isLazyLoaded) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    loadData();
                    isLazyLoaded = true;
                }
            });
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        japaneseComicLoader = new JapaneseComicLoader(this);
        recyclerView = getActivity().findViewById(R.id.sub_fg_japanese_ry);
        swipeRefreshLayout = getActivity().findViewById(R.id.sub_fg_japanese_swipeFreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.refreshColor));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                japaneseComicLoader.pause();
                loadData();
            }
        });
        swipeRefreshLayout.setRefreshing(true);
        lazyLoad();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        japaneseComicLoader.loadComicList("http://manhua.fzdm.com/", new DataLoadListener() {
            @Override
            public void onLoadFinished(Object o, Exception e) {
                if (e == null) {
                    comicList = (List<Comic>) o;
                    showComicItems();
                } else {
                    Log.d("测试", "加载出错: " + e.toString());
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 显示recyclerView
     */
    private void showComicItems() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        JapaneseComicListAdapter japaneseComicListAdapter = new JapaneseComicListAdapter(comicList, R.layout.item_home_jp, getContext());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(japaneseComicListAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }
}
