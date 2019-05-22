package com.example.comicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.example.comicapp.entities.UserComicLike;
import com.example.comicapp.utils.ActivityUtil;
import com.example.comicapp.utils.UserUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class ComicIntroActivity extends BaseActivity implements View.OnClickListener {
    public static String TAG = "测试";
    private ImageView posterImageView;
    private TextView titleTextView;
    private TextView totalTextView;
    private RecyclerView ry;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView likeImageView;
    private TextView likeHintView;
    private Button readButton;
    private Comic comic;
    private Chapter lastChapter;
    private boolean isLikedCurrentComic = false;
    private boolean isLoadLikeData = false;
    private String likeObjectId;
    private List<Chapter> chapterList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comic_intro);
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

    private void initData() {
        // 控件初始化
        posterImageView = findViewById(R.id.a_comic_intro_poster);
        titleTextView = findViewById(R.id.a_comic_intro_title);
        totalTextView = findViewById(R.id.a_comic_intro_totalChapter);
        ry = findViewById(R.id.a_comic_intro_ry);
        readButton = findViewById(R.id.a_comic_intro_bn);
        swipeRefreshLayout = findViewById(R.id.a_comic_intro_swipeRefreshLayout);
        likeImageView = findViewById(R.id.a_comic_intro_like);
        likeHintView = findViewById(R.id.a_comic_intro_like_hint);
        // 控件设置
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.refreshColor));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullData();
            }
        });
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
        pullData();
    }

    private void pullData() {
        new Thread() {
            @Override
            public void run() {
                Message message = new Message();
                try {
                    Document document = Jsoup.connect(comic.getComicWebUrl()).get();
                    Elements chapterElements = document.select("ul#topic1>li");
                    comic.setLastUpdate(document.select("div.jshtml>ul>li>a.cz").html());
                    chapterList = new ArrayList<>();
                    int i = 0;
                    for (Element element : chapterElements) {
                        String chapterName = element.select("a").attr("title");
                        Chapter chapter = new Chapter();
                        chapter.setChapterName(chapterName);
                        chapter.setChapterUrl(BasicConfig.HOME_PAGE + element.select("a").attr("href"));
                        chapter.setComicName(comic.getComicName());
                        chapter.setComicPosterUrl(comic.getComicPosterUrl());
                        chapter.setComicWebUrl(comic.getComicWebUrl());
                        chapter.setListIndex(i);
                        chapterList.add(chapter);
                        i++;
                    }
                    comic.setChapterList(chapterList);
                    lastChapter = chapterList.get(chapterList.size() - 1);
                    message.what = 0;
                } catch (Exception e) {
                    message.what = 1;
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
                case 0:
                    totalTextView.setText("更新至 " + comic.getLastUpdate());
                    initButton();
                    initCatalogue();
                    break;
                default:
                    break;
            }
            swipeRefreshLayout.setRefreshing(false);
        }
    };

    private void initCatalogue() {
        readButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ComicIntroActivity.this, ComicDetailActivity.class);
                intent.putExtra(BasicConfig.INTENT_DATA_NAME_CHAPTER, lastChapter);
                intent.putExtra(BasicConfig.INTENT_DATA_NAME_COMIC, comic);
                startActivity(intent);
            }
        });
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        ComicIntroAdapter comicIntroAdapter = new ComicIntroAdapter(comic, R.layout.item_comic_catalogue, this);
        ry.setLayoutManager(linearLayoutManager);
        ry.setAdapter(comicIntroAdapter);
    }

    private void initButton() {
        SPReader spReader = ActivityUtil.getDataWithSPReader(this, comic.getComicName(), BasicConfig.KEY_NAME_GENERAL);
        if (spReader.value != null) {
            if (isMatchedChapter(spReader)) {
                readButton.setText("继续阅读 " +  ActivityUtil.getSubString(lastChapter.getChapterName(), 6, false));
            } else {
                readButton.setText("开始阅读 " + ActivityUtil.getSubString(lastChapter.getChapterName(), 6, false));
            }
        } else {
            readButton.setText("开始阅读 " + ActivityUtil.getSubString(lastChapter.getChapterName(), 6, false));
        }
    }

    private boolean isMatchedChapter(SPReader spReader) {
        if (spReader.value.equals(chapterList.get(spReader.index).getChapterName())) {
            lastChapter = chapterList.get(spReader.index);
            return true;
        }
        for (int i = 0; i < chapterList.size(); i++) {
            if (spReader.value.equals(chapterList.get(i).getChapterName())) {
                lastChapter = chapterList.get(i);
                return true;
            }
        }
        return false;
    }

    private void like(MyUser myUser) {
        if (isLoadLikeData) {
            if (myUser == null) {
                ActivityUtil.launchLoginView(this, BasicConfig.REQUEST_CODE_LOGIN);
            } else {
                if (!isLikedCurrentComic) {
                    UserComicLike userComicLike = new UserComicLike();
                    userComicLike.setUserOpenId(myUser.getUserOpenId());
                    userComicLike.setComicName(comic.getComicName());
                    userComicLike.setComicPosterUrl(comic.getComicPosterUrl());
                    userComicLike.setComicWebUrl(comic.getComicWebUrl());
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
                    UserComicLike userComicLike = new UserComicLike();
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

    private void queryComic(MyUser myUser) {
        if (myUser != null) {
            BmobQuery<UserComicLike> comicLikeQuery = new BmobQuery<>();
            comicLikeQuery.addWhereEqualTo("userOpenId", UserUtil.myUser.getUserOpenId())
                    .addWhereEqualTo("comicName", comic.getComicName());
            comicLikeQuery.findObjects(new FindListener<UserComicLike>() {
                @Override
                public void done(List<UserComicLike> list, BmobException e) {
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
