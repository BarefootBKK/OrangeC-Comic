package com.example.comicapp.fragments.subfragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import com.example.comicapp.adapters.ComicListAdapter;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.controllers.APKUpdateController;
import com.example.comicapp.controllers.AWCoreController;
import com.example.comicapp.entities.APKUpdate;
import com.example.comicapp.entities.Comic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class ChineseComicSubFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AWCoreController awCoreController;
    private List<Comic> comicList;
    private boolean isLoaded = false;
    private String staticSrc;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.subfragment_chinese, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (awCoreController != null) {
            awCoreController.destroy();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isLoaded = false;
    }

    /**
     * 初始化数据
     */
    private void initData() {
        checkUpdate();
        staticSrc = "/static/space3x4.gif";
        recyclerView = getActivity().findViewById(R.id.sub_fg_chinese_ry);
        swipeRefreshLayout = getActivity().findViewById(R.id.sub_fg_chinese_swipeFreshLayout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.refreshColor));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoaded = false;
                loadData();
            }
        });
        loadData();
    }

    /**
     * 加载数据
     */
    private void loadData() {
        swipeRefreshLayout.setRefreshing(true);
        awCoreController = new AWCoreController(this, new AWCoreController.OnAgentDataLoadingListener() {
            @Override
            public void onDataLoading(String html) {
                parseWebData(html);
            }
        });
        awCoreController.loadUrl("https://www.manhuatai.com/all.html");
    }

    /**
     * 解析网页html数据
     * @param htmlData
     */
    private void parseWebData(final String htmlData) {
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = BasicConfig.MESSAGE_FAILURE;
                try {
                    Document document = Jsoup.parse(htmlData);
                    Elements elements = document.select("div.mhlist2.mhlist2_fix_top.clearfix>a");
                    String src = elements.first().select("div.wrapleft>img").attr("data-url");
                    if (src != null && !src.equals("") && !isLoaded && !src.equals(staticSrc)) {
                        isLoaded = true;
                        comicList = new ArrayList<>();
                        for (Element element : elements) {
                            Comic comic = new Comic();
                            comic.setComicName(element.attr("title"));
                            comic.setComicWebUrl(BasicConfig.HOME_PAGE + element.attr("href"));
                            comic.setComicPosterUrl(element.select("div.wrapleft>img").attr("data-url"));
                            comicList.add(comic);
                        }
                        message.what = BasicConfig.MESSAGE_SUCCESS;
                    }
                } catch (Exception e) {

                }
                mHandler.sendMessage(message);
            }
        }.start();
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case BasicConfig.MESSAGE_SUCCESS:
                    showComicItems();
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 显示recyclerView
     */
    private void showComicItems() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 3);
        ComicListAdapter comicListAdapter = new ComicListAdapter(comicList, R.layout.item_home, getContext());
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(comicListAdapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    /**
     * 检查APP更新
     */
    private void checkUpdate() {
        final APKUpdateController updateController = new APKUpdateController(getActivity());
        updateController.checkNewVersion("IkWE888A", new APKUpdateController.OnAPKUpdateListener() {
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
}
