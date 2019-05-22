package com.example.comicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.R;
import com.example.comicapp.adapters.ComicIntroAdapter;
import com.example.comicapp.config.PictureConfig;
import com.example.comicapp.entities.Chapter;
import com.example.comicapp.entities.Comic;
import com.example.comicapp.entities.MyUser;
import com.example.comicapp.entities.SPReader;
import com.example.comicapp.entities.ComicCollection;
import com.example.comicapp.interfaces.DataLoadListener;
import com.example.comicapp.loaders.BaseComicLoader;
import com.example.comicapp.loaders.ChineseComicLoader;
import com.example.comicapp.loaders.JapaneseComicLoader;
import com.example.comicapp.utils.ActivityUtil;
import com.example.comicapp.utils.ComicUtil;
import com.example.comicapp.utils.UserUtil;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ComicIntroActivity extends BaseActivity implements View.OnClickListener {
    public static String TAG = "测试";
    @BindView(R.id.a_comic_intro_poster) public ImageView posterImageView;
    @BindView(R.id.a_comic_intro_title) public TextView titleTextView;
    @BindView(R.id.a_comic_intro_totalChapter) public TextView totalTextView;
    @BindView(R.id.a_comic_intro_ry) public RecyclerView ry;
    @BindView(R.id.a_comic_intro_swipeRefreshLayout) public SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.a_comic_intro_like) public ImageView likeImageView;
    @BindView(R.id.a_comic_intro_like_hint) public TextView likeHintView;
    @BindView(R.id.a_comic_intro_bn) public Button readButton;
    private Comic comic;
    private Chapter lastRead;
    private boolean isLikedCurrentComic = false;
    private boolean isLoadLikeData = false;
    private String likeObjectId;
    private List<Chapter> chapterList;
    private BaseComicLoader comicLoader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_intro);
        ButterKnife.bind(this);
        setActivityToolbar(R.id.fg_bs_toolbar, true, false);
        initData();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initButton();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BasicConfig.REQUEST_CODE_LOGIN) {
            if (resultCode == BasicConfig.RESULT_CODE_SUCCESS) {
                queryComic((MyUser) data.getParcelableExtra(BasicConfig.LOGIN_INTENT_KEY));
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.a_comic_intro_like:
                like(UserUtil.myUser);
                break;
            case R.id.a_comic_intro_like_hint:
                like(UserUtil.myUser);
                break;
            default:
                break;
        }
    }

    /**
     * 初始化数据
     */

    private void initData() {
        // 控件初始化
        /*
        posterImageView = findViewById(R.id.a_comic_intro_poster);
        titleTextView = findViewById(R.id.a_comic_intro_title);
        totalTextView = findViewById(R.id.a_comic_intro_totalChapter);
        ry = findViewById(R.id.a_comic_intro_ry);
        readButton = findViewById(R.id.a_comic_intro_bn);
        swipeRefreshLayout = findViewById(R.id.a_comic_intro_swipeRefreshLayout);
        likeImageView = findViewById(R.id.a_comic_intro_like);
        likeHintView = findViewById(R.id.a_comic_intro_like_hint);
        */
        // 控件设置
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.refreshColor));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullData();
            }
        });
        // 获取当前漫画
        comic = getIntent().getParcelableExtra(BasicConfig.INTENT_DATA_NAME_COMIC);
        RequestOptions options = new RequestOptions().override(290, 420);
        Glide.with(this)
                .load(comic.getComicPosterUrl())
                .apply(options)
                .thumbnail(PictureConfig.PIC_MIDDLE)
                .into(posterImageView);
        titleTextView.setText(comic.getComicName());
        likeImageView.setOnClickListener(this);
        likeHintView.setOnClickListener(this);
        queryComic(UserUtil.myUser);
        // 判断漫画类型
        if(comic.getComicType() == 0) {
            comicLoader = new ChineseComicLoader(this);
        } else {
            comicLoader = new JapaneseComicLoader(this);
        }
        pullData();
    }

    /**
     * 拉取数据
     */
    private void pullData() {
        comicLoader.loadChapterList(comic, false, new DataLoadListener() {
            @Override
            public void onLoadFinished(Object o, Exception e) {
                if (e == null) {
                    chapterList = (List<Chapter>) o;
                    comic.setChapterList(chapterList);
                    lastRead = chapterList.get(chapterList.size() - 1);
                    if (comic.getLastUpdate() == null || comic.getLastUpdate() == "" || comic.getLastUpdate().isEmpty()) {
                        totalTextView.setText("更新至 " + chapterList.get(0).getChapterName());
                    } else {
                        totalTextView.setText("更新至 " + comic.getLastUpdate());
                    }
                    initButton();
                    initCatalogue();
                    ComicUtil.currentComic = comic;
                } else {
                    Log.d(TAG, "onLoadFinished: " + e.toString());
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    /**
     * 初始化目录
     */
    private void initCatalogue() {
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComicIntroActivity.this, ComicDetailActivity.class);
                intent.putExtra(BasicConfig.INTENT_DATA_NAME_CHAPTER, lastRead);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ComicIntroAdapter comicIntroAdapter = new ComicIntroAdapter(chapterList, R.layout.item_comic_catalogue, this);
        ry.setLayoutManager(linearLayoutManager);
        ry.setAdapter(comicIntroAdapter);
    }

    /**
     * 初始化阅读按钮
     */
    private void initButton() {
        SPReader spReader = ActivityUtil.getDataWithSPReader(this, comic.getComicName(), BasicConfig.KEY_NAME_GENERAL);
        if (spReader.value != null) {
            if (isMatchedChapter(spReader)) {
                readButton.setText("继续阅读 " +  ActivityUtil.getSubString(lastRead.getChapterName(), 6, false));
            } else {
                readButton.setText("开始阅读 " + ActivityUtil.getSubString(lastRead.getChapterName(), 6, false));
            }
        } else {
            readButton.setText("开始阅读 " + ActivityUtil.getSubString(lastRead.getChapterName(), 6, false));
        }
    }

    /**
     * 是否从缓存中匹配到章节
     * @param spReader
     * @return
     */
    private boolean isMatchedChapter(SPReader spReader) {
        if (spReader.value.equals(chapterList.get(spReader.index).getChapterName())) {
            lastRead = chapterList.get(spReader.index);
            return true;
        }
        for (int i = 0; i < chapterList.size(); i++) {
            if (spReader.value.equals(chapterList.get(i).getChapterName())) {
                lastRead = chapterList.get(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 收藏
     * @param myUser
     */
    private void like(MyUser myUser) {
        if (isLoadLikeData) {
            if (myUser == null) {
                ActivityUtil.launchLoginView(this, BasicConfig.REQUEST_CODE_LOGIN);
            } else {
                if (!isLikedCurrentComic) {
                    ComicCollection userComicLike = new ComicCollection();
                    userComicLike.setUserOpenId(myUser.getUserOpenId());
                    userComicLike.setComicName(comic.getComicName());
                    userComicLike.setComicPosterUrl(comic.getComicPosterUrl());
                    userComicLike.setComicWebUrl(comic.getComicWebUrl());
                    userComicLike.setComicType(comic.getComicType());
                    userComicLike.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                likeObjectId = s;
                                isLikedCurrentComic = true;
                                updateLikeView();
                                showToast("收藏成功");
                            } else {
                                showToast("收藏失败：" + e.toString());
                            }
                        }
                    });
                } else {
                    ComicCollection userComicLike = new ComicCollection();
                    userComicLike.setObjectId(likeObjectId);
                    userComicLike.delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null) {
                                isLikedCurrentComic = false;
                                updateLikeView();
                                showToast("已取消收藏");
                            } else {
                                showToast("取消收藏失败：" + e.toString());
                            }
                        }
                    });
                }
            }
        }
    }

    /**
     * 查询
     * @param myUser
     */
    private void queryComic(MyUser myUser) {
        if (myUser != null) {
            BmobQuery<ComicCollection> comicLikeQuery = new BmobQuery<>();
            comicLikeQuery.addWhereEqualTo("userOpenId", UserUtil.myUser.getUserOpenId())
                    .addWhereEqualTo("comicName", comic.getComicName());
            comicLikeQuery.findObjects(new FindListener<ComicCollection>() {
                @Override
                public void done(List<ComicCollection> list, BmobException e) {
                    if (e == null) {
                        likeObjectId = list.get(0).getObjectId();
                        ComicIntroActivity.this.isLikedCurrentComic = true;
                    } else {
                        ComicIntroActivity.this.isLikedCurrentComic = false;
                    }
                    isLoadLikeData = true;
                    updateLikeView();
                }
            });
        } else {
            isLoadLikeData = true;
        }
    }

    /**
     * 更新收藏icon
     */
    private void updateLikeView() {
        if (isLikedCurrentComic) {
            likeImageView.setImageResource(R.drawable.ic_like_fill);
            likeHintView.setText("已收藏");
        } else {
            likeImageView.setImageResource(R.drawable.ic_like_o);
            likeHintView.setText("收藏");
        }
    }
}
