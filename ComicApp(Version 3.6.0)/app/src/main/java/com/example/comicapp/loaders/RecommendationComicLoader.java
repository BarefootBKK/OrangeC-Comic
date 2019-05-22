package com.example.comicapp.loaders;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.comicapp.entities.Comic;
import com.example.comicapp.interfaces.DataLoadListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class RecommendationComicLoader extends BaseComicLoader {

    public RecommendationComicLoader(Activity mActivity) {
        super(mActivity);
    }

    public RecommendationComicLoader(Fragment mFragment) {
        super(mFragment);
    }

    @Override
    protected void setStaticSrc() {

    }

    @Override
    protected void setHomepage() {
        this.homepage = "https://www.50mh.com";
    }

    @Override
    protected void parseWebData(String htmlData) {
        super.parseWebData(htmlData);
        if (loadType == 4) {
            parseRecommendationData(htmlData);
        }
    }

    public void loadRecommendationComics(final String url, DataLoadListener dataLoadListener) {
        loadType = 4;
        this.dataLoadListener = dataLoadListener;
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                load(url);
            }
        });
    }

    public void parseRecommendationData(String html) {
        try {
            if (!isLoaded) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("div#index-carousel>ul#w0>li").get(0).select("div.sub-item");
                if (elements != null) {
                    isLoaded = true;
                    comicList = new ArrayList<>();
                    int count = 0;
                    for (Element element : elements) {
                        if (count < 4) {
                            Comic comic = new Comic();
                            comic.setComicName(getComicName(element.select("div.carousel-caption").html()));
                            comic.setComicWebUrl(homepage + element.select("a").attr("href"));
                            comic.setComicPosterUrl(element.select("img").attr("src"));
                            comicList.add(comic);
                        } else {
                            break;
                        }
                        count++;
                    }
                    mHandler.sendMessage(getMessage(comicList, LOAD_SUCCESS));
                }
            }
        } catch (Exception e) {
            mHandler.sendMessage(getMessage(e, LOAD_FAILURE));
        }
    }

    @Override
    protected void loadChapterData(Comic comic) {

    }

    @Override
    protected void loadComicData(String html) {
        try {
            if (!isLoaded) {
                Document document = Jsoup.parse(html);
            }
        } catch (Exception e) {

        }
    }

    @Override
    protected void loadPicsData(String html) {

    }

    private String getComicName(String str) {
        String temp = "";
        for (int i = 0; i < str.length(); i++) {
            char ch = str.charAt(i);
            if (ch == ' ' || ch == '-') {
                break;
            } else {
                temp += ch;
            }
        }
        return temp;
    }
}
