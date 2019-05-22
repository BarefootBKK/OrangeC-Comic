package com.example.comicapp.loaders;

import android.app.Activity;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.example.comicapp.entities.Chapter;
import com.example.comicapp.entities.Comic;
import com.example.comicapp.interfaces.DataLoadListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class JapaneseComicLoader extends BaseComicLoader {
    private String lastSrc = "";
    private String lastSuffix = "";

    public JapaneseComicLoader(Activity mActivity) {
        super(mActivity);
    }

    public JapaneseComicLoader(Fragment mFragment) {
        super(mFragment);
    }

    @Override
    protected void setStaticSrc() {

    }

    @Override
    protected void setHomepage() {
        super.homepage = "https://manhua.fzdm.com/";
    }

    @Override
    protected void loadComicData(final String html) {
        try {
            if (!isLoaded) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("div#mhmain>ul>div");
                String src = elements.first().select("img").first().attr("src");
                if (src != null && !src.isEmpty()) {
                    isLoaded = true;
                    comicList = new ArrayList<>();
                    for (Element element : elements) {
                        Comic comic = new Comic();
                        comic.setComicName(element.select("li").get(1).select("a").html());
                        comic.setComicWebUrl(homepage + element.select("li").first().select("a").attr("href"));
                        comic.setComicPosterUrl(getPosterUrl(element.select("img").attr("src")));
                        comicList.add(comic);
                    }
                    mHandler.sendMessage(getMessage(comicList, LOAD_SUCCESS));
                }
            }
        } catch (Exception e) {
            mHandler.sendMessage(getMessage(e, LOAD_FAILURE));
        }
    }

    @Override
    protected void loadChapterData(final Comic comic) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(comic.getComicWebUrl()).validateTLSCertificates(false).get();
                    Elements elements = document.select("div#content>li");
                    chapterList = new ArrayList<>();
                    int i = 0;
                    for (Element element : elements) {
                        Chapter chapter = new Chapter(comic);
                        chapter.setChapterName(element.select("a").html());
                        chapter.setChapterUrl(chapter.getComicWebUrl() + element.select("a").attr("href"));
                        chapter.setCurrentIndex(i);
                        chapterList.add(chapter);
                        i++;
                    }
                    mHandler.sendMessage(getMessage(chapterList, LOAD_SUCCESS));
                } catch (Exception e) {
                    mHandler.sendMessage(getMessage(e, LOAD_FAILURE));
                }
            }
        }.start();
    }

    @Override
    protected void loadPicsData(final String html) {
        try {
            if (!isLoaded) {
                Document document = Jsoup.parse(html);
                String src = document.select("img#mhpic").first().attr("src");
                if (!isRepetitive(src)) {
                    mHandler.sendMessage(getMessage(src, LOAD_SUCCESS));
                    String nextPage = document.select("div#mhimg0>a").attr("href");
                    if (nextPage != null && !nextPage.isEmpty() && nextPage != "") {
                        load(currentUrl + nextPage);
                    } else {
                        currentUrl = "";
                        lastSrc = "";
                        lastSuffix = "";
                    }
                }
            }
        } catch (Exception e) {
            // Log.d(TAG, "loadPicsData:错误 " + e.toString());
        }
    }



    /**
     * 获取处理后的PosterUrl
     * @param url
     * @return
     */
    private String getPosterUrl(String url) {
        String temp = "";
        int index = 0;
        for (int i = url.length() - 1; i >= 0; i--, index++) {
            if (url.charAt(i) == '/') {
                break;
            }
        }
        String finalUrl = "";
        try {
            temp = url.substring(url.length() - index);
            String num = temp.substring(0, temp.length() - 4);
            finalUrl = url.substring(0, url.length() - index) + "img/" + Integer.valueOf(num) + ".jpg";
        } catch (Exception e) {
            finalUrl = url;
        }
        return finalUrl;
    }

    private boolean isRepetitive(String src) {
        String suffix = getSuffix(src);
        if (src != null && src != "" && !src.isEmpty() && !isLoaded && !src.equals(lastSrc)) {
            if (lastSuffix.equals(suffix)) {
                return true;
            } else {
                lastSrc = src;
                lastSuffix = suffix;
                isLoaded = true;
                return false;
            }
        } else {
            return true;
        }
    }

    private String getSuffix(String src) {
        String temp = new String();
        for (int i = src.length() - 1; i >= 0; i--) {
            if (src.charAt(i) == '/') {
                temp = src.substring(i);
                break;
            }
        }
        return temp;
    }
}
