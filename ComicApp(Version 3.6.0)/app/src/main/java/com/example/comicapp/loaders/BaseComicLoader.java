package com.example.comicapp.loaders;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;

import com.example.comicapp.controllers.AWCoreController;
import com.example.comicapp.entities.Chapter;
import com.example.comicapp.entities.Comic;
import com.example.comicapp.interfaces.DataLoadListener;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseComicLoader {
    protected final static String TAG = "测试5";
    protected final static int LOAD_SUCCESS = 123;
    protected final static int LOAD_FAILURE = 321;
    protected List<Comic> comicList;
    protected List<Chapter> chapterList;
    protected List<String> picsList;
    protected String homepage = "";
    protected String staticSrc = "";
    protected boolean isLoaded = false;
    protected int loadType;
    protected String currentUrl = "";
    protected AWCoreController awCoreController;
    private AWCoreController.OnAgentDataLoadingListener loadingListener;
    private Fragment mFragment;
    private Activity mActivity;
    private boolean isActivity;
    protected DataLoadListener dataLoadListener;

    public BaseComicLoader(Activity mActivity) {
        this.mActivity = mActivity;
        this.isActivity = true;
        initData();
    }

    public BaseComicLoader(Fragment mFragment) {
        this.mFragment = mFragment;
        this.mActivity = mFragment.getActivity();
        this.isActivity = false;
        initData();
    }

    private void initData() {
        comicList = new ArrayList<>();
        chapterList = new ArrayList<>();
        picsList = new ArrayList<>();
        setHomepage();
        setStaticSrc();
        loadingListener = new AWCoreController.OnAgentDataLoadingListener() {
            @Override
            public void onWebLoading(String html) {
                if (!isLoaded) {
                    parseWebData(html);
                }
            }
        };
        if (isActivity) {
            awCoreController = new AWCoreController(mActivity, loadingListener);
        } else {
            awCoreController = new AWCoreController(mFragment, loadingListener);
        }
    }

    protected void parseWebData(final String htmlData) {
        if (loadType == 0) {
            loadComicData(htmlData);
        } else if (loadType == 2) {
            loadPicsData(htmlData);
        }
    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOAD_SUCCESS:
                    dataLoadListener.onLoadFinished(msg.obj, null);
                    break;
                case LOAD_FAILURE:
                    dataLoadListener.onLoadFinished(null, (Exception) msg.obj);
                    break;
                default:
                    break;
            }
        }
    };

    public boolean isLoaded() {
        return this.isLoaded;
    }

    public void setLoaded(boolean isLoaded) {
        this.isLoaded = isLoaded;
    }

    public void pause() {
        this.isLoaded = false;
    }

    public void loadComicList(final String url, final DataLoadListener dataLoadListener) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadType = 0;
                BaseComicLoader.this.dataLoadListener = dataLoadListener;
                load(url);
            }
        });
    }

    public void loadChapterList(final Comic comic, final boolean usingWebCore, final DataLoadListener dataLoadListener) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadType = 1;
                BaseComicLoader.this.dataLoadListener = dataLoadListener;
                loadChapterData(comic);
            }
        });
    }

    public void loadPicsList(final String url, final DataLoadListener dataLoadListener) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                loadType = 2;
                currentUrl = url;
                picsList = new ArrayList<>();
                BaseComicLoader.this.dataLoadListener = dataLoadListener;
                load(url);
            }
        });
    }

    public void destroyLoader() {
        if (awCoreController != null) {
            isLoaded = false;
            awCoreController.destroy();
        }
    }

    protected void load(String url) {
        isLoaded = false;
        awCoreController.loadUrl(url);
    }

    protected Message getMessage(Object obj, int what) {
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        return message;
    }

    protected abstract void setStaticSrc();
    protected abstract void setHomepage();
    protected abstract void loadChapterData(final Comic comic);
    protected abstract void loadComicData(final String html);
    protected abstract void loadPicsData(final String html);
}
