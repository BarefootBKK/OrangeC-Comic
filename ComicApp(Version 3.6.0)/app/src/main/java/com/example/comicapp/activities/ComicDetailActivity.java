package com.example.comicapp.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.R;
import com.example.comicapp.adapters.ComicDetailAdapter;
import com.example.comicapp.entities.Chapter;
import com.example.comicapp.interfaces.DataLoadListener;
import com.example.comicapp.loaders.BaseComicLoader;
import com.example.comicapp.loaders.ChineseComicLoader;
import com.example.comicapp.loaders.JapaneseComicLoader;
import com.example.comicapp.utils.ActivityUtil;
import com.example.comicapp.utils.ComicUtil;
import com.example.comicapp.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

public class ComicDetailActivity extends BaseActivity {
    private Chapter chapter;
    private ActionBar actionBar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton floatingActionButton;
    private BaseComicLoader comicLoader;
    private boolean isRenewAdapter = false;
    private ComicDetailAdapter comicDetailAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_detail);
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        comicLoader.destroyLoader();
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
        chapter = getIntent().getParcelableExtra(BasicConfig.INTENT_DATA_NAME_CHAPTER);
        // 设置toolbar
        actionBar = setActivityToolbar(R.id.fg_bs_toolbar, true, true);
        // 初始化swipeRefreshLayout
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.refreshColor));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                isRenewAdapter = false;
                comicLoader.destroyLoader();
                reloadData(chapter);
            }
        });
        // 初始化floatingActionButton
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Chapter nextChapter = getNextChapter();
                    if (nextChapter == null) {
                        Toast.makeText(ComicDetailActivity.this, "已经是最新话了", Toast.LENGTH_SHORT).show();
                    } else {
                        reloadData(nextChapter);
                    }
                } catch (Exception e) {
                    ToastUtil.showShortToast(ComicDetailActivity.this, "加载出错，请重试");
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
        if (chapter.getComicType() == 0) {
            comicLoader = new ChineseComicLoader(ComicDetailActivity.this);
        } else {
            comicLoader = new JapaneseComicLoader(ComicDetailActivity.this);
        }
        actionBar.setTitle(chapter.getChapterName());
        swipeRefreshLayout.setRefreshing(true);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                comicLoader.loadPicsList(chapter.getChapterUrl(), new DataLoadListener() {
                    @Override
                    public void onLoadFinished(Object o, Exception e) {
                        if (e == null) {
                            if (!isRenewAdapter) {
                                isRenewAdapter = true;
                                List<String> picsList;
                                if (comicLoader instanceof JapaneseComicLoader) {
                                    picsList = new ArrayList<>();
                                    picsList.add((String) o);
                                } else {
                                    picsList = (List<String>) o;
                                }
                                chapter.setPicsList(picsList);
                                loadData();
                            } else {
                                /**
                                 * Bug修正记录 2019/4/7 19:16
                                 * 下面的“chapter.getPicsList”注释掉是因为：
                                 * 在ComicDetailAdapter中的addItem方法里有picsList.add这个操作
                                 * 而picsList在ComicDetailAdapter的构造函数中有：this.picsList = picsList这个操作
                                 * 也就是说ComicDetailAdapter中的picsList和这里的chapter.getPicsList()指向的是同一个地址
                                 * 所以下列注释掉的操作会跟ComicDetailAdapter中的addItem方法重复
                                 */
                                // chapter.getPicsList().add((String) o);
                                comicDetailAdapter.addItemBack((String) o);
                            }
                            ActivityUtil.saveDataWithSP(ComicDetailActivity.this, chapter.getComicName(), BasicConfig.KEY_NAME_GENERAL,
                                    chapter.getChapterName(), chapter.getCurrentIndex());
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
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
                comicDetailAdapter = new ComicDetailAdapter(chapter.getPicsList(), R.layout.item_comic_detail,
                        ComicDetailActivity.this, new DataLoadListener() {
                    @Override
                    public void onLoadFinished(Object o, Exception e) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
                if (comicLoader instanceof ChineseComicLoader) {
                    comicDetailAdapter.setShowHint(true);
                } else {
                    comicDetailAdapter.setShowHint(false);
                }
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(comicDetailAdapter);
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        Glide.with(ComicDetailActivity.this).resumeRequests();
//                        if(newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_DRAGGING ){
//                            Glide.with(ComicDetailActivity.this).resumeRequests();
//                        } else {
//                            Glide.with(ComicDetailActivity.this).pauseRequests();
//                        }
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
        isRenewAdapter = false;
        comicLoader.destroyLoader();
        generateUrl();
    }

    /**
     * 是否滑到底了
     * @param recyclerView
     * @return
     */
    private boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        if (recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange())
            return true;
        return false;
    }

    private Chapter getNextChapter() {
        int currentIndex = chapter.getCurrentIndex();
        if (currentIndex > 0) {
            return ComicUtil.currentComic.getChapterList().get(currentIndex - 1);
        } else {
            return null;
        }
    }
}
