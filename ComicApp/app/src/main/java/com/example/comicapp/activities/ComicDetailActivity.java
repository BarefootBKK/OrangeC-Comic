package com.example.comicapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.controllers.AWCoreController;
import com.example.comicapp.R;
import com.example.comicapp.adapters.ComicDetailAdapter;
import com.example.comicapp.entities.Chapter;
import com.example.comicapp.entities.Comic;
import com.example.comicapp.interfaces.LoadDataListener;
import com.example.comicapp.utils.ActivityUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;

public class ComicDetailActivity extends BaseActivity {
    private Comic comic;
    private Chapter chapter;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    private boolean isLoaded = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);
        initData();
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

    private void initData() {
        // 获取控件ID
        recyclerView = findViewById(R.id.a_comic_detail_ry);
        swipeRefreshLayout = findViewById(R.id.a_comic_detail_swipeRefreshLayout);
        floatingActionButton = findViewById(R.id.a_comic_detail_fb);
        comic = getIntent().getParcelableExtra(BasicConfig.INTENT_DATA_NAME_COMIC);
        chapter = getIntent().getParcelableExtra(BasicConfig.INTENT_DATA_NAME_CHAPTER);
        // 设置toolbar
        actionBar = setActivityToolbar(R.id.fg_bs_toolbar, true, true);
        // 初始化swipeRefreshLayout
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.refreshColor));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isLoaded = false;
                reloadData(chapter);
            }
        });
        // 初始化floatingActionButton
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int listIndex = chapter.getListIndex();
                if (listIndex == 0) {
                    Toast.makeText(ComicDetailActivity.this, "已经是最新话了", Toast.LENGTH_SHORT).show();
                } else {
                    isLoaded = false;
                    reloadData(comic.getChapterList().get(listIndex - 1));
                }
            }
        });
        // 获取URL
        generateUrl();
    }

    /**
     * 生成URL
     */
    private void generateUrl() {
        actionBar.setTitle(chapter.getChapterName());
        swipeRefreshLayout.setRefreshing(true);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                awCoreController = new AWCoreController(ComicDetailActivity.this, new AWCoreController.OnAgentDataLoadingListener() {
                    @Override
                    public void onDataLoading(String html) {
                        parseWebData(html);
                    }
                });
                awCoreController.loadUrl(chapter.getChapterUrl());
            }
        });
    }

    /**
     * 加载数据
     */
    private void loadData() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ComicDetailActivity.this);
                ComicDetailAdapter comicDetailAdapter = new ComicDetailAdapter(chapter, R.layout.item_comic_detail,
                        ComicDetailActivity.this, new LoadDataListener() {
                    @Override
                    public void onLoadFinished(Object o) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(comicDetailAdapter);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if(newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING ){
                            Glide.with(ComicDetailActivity.this).resumeRequests();
                        } else {
                            Glide.with(ComicDetailActivity.this).pauseRequests();
                        }
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        if (isSlideToBottom(recyclerView)) {
                            // 滑到底了
                        } else {
                            // 未滑到底
                        }
                    }
                });
            }
        });
    }

    /**
     * 重新拉取数据
     * @param chapter
     */
    private void reloadData(Chapter chapter) {
        this.chapter = chapter;
        generateUrl();
    }

    private void parseWebData(final String htmlData) {
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = BasicConfig.MESSAGE_FAILURE;
                try {
                    Document document = Jsoup.parse(htmlData);
                    String src = document.select("div.mh_comicpic>img").attr("src");
                    if (src != null && !src.equals("") && !isLoaded) {
                        isLoaded = true;
                        int pNum = document.select("div.mh_headpager>select.mh_select>option").size();
                        String urlPrefix = getUrlPrefix(src);
                        chapter.setChapterPNum(pNum);
                        List<String> picsList = new ArrayList<>();
                        for (int i = 0; i < pNum; i++) {
                            String url = urlPrefix + (i + 1) + ".jpg-mht.middle";
                            picsList.add(url);
                        }
                        chapter.setPicsList(picsList);
                        message.what = BasicConfig.MESSAGE_SUCCESS;
                    }
                } catch (Exception e) {
                    Log.d("错误", "run: " + e.toString());
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
                    ActivityUtil.saveDataWithSP(ComicDetailActivity.this, comic.getComicName(), BasicConfig.KEY_NAME_GENERAL,
                            chapter.getChapterName(), chapter.getListIndex());
                    loadData();
                    break;
                default:
                    break;
            }
        }
    };

    private String getUrlPrefix(String src) {
        return src.substring(0, src.length() - 21);
    }

    private boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }
}
