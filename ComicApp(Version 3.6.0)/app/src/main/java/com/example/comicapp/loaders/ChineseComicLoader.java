package com.example.comicapp.loaders;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.example.comicapp.entities.Chapter;
import com.example.comicapp.entities.Comic;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

public class ChineseComicLoader extends BaseComicLoader {

    public ChineseComicLoader(Activity mActivity) {
        super(mActivity);
    }

    public ChineseComicLoader(Fragment mFragment) {
        super(mFragment);
    }

    @Override
    protected void setHomepage() {
        super.homepage = "https://www.manhuatai.com";
    }

    @Override
    protected void setStaticSrc() {
        super.staticSrc = "/static/space3x4.gif";
    }

    /**
     * 漫画列表
     * @param html
     */
    @Override
    protected void loadComicData(final String html) {
        try {
            if (!isLoaded) {
                Document document = Jsoup.parse(html);
                Elements elements = document.select("div.mhlist2.mhlist2_fix_top.clearfix>a");
                String src = elements.first().select("div.wrapleft>img").attr("data-url");
                if (src != null && !src.equals("") && !src.equals(staticSrc)) {
                    isLoaded = true;
                    comicList = new ArrayList<>();
                    for (Element element : elements) {
                        Comic comic = new Comic();
                        comic.setLastUpdate(element.select("div.wrapleft>div>span.a").html());
                        comic.setComicName(element.attr("title"));
                        comic.setComicWebUrl(homepage + element.attr("href"));
                        comic.setComicPosterUrl(element.select("div.wrapleft>img").attr("data-url"));
                        comicList.add(comic);
                    }
                    mHandler.sendMessage(getMessage(comicList, LOAD_SUCCESS));
                }
            }
        } catch (Exception e) {
            mHandler.sendMessage(getMessage(e, LOAD_FAILURE));
        }
    }

    /**
     * 章节列表
     * @param comic
     */
    @Override
    protected void loadChapterData(final Comic comic) {
        new Thread() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect(comic.getComicWebUrl()).validateTLSCertificates(false).get();
                    Elements chapterElements = document.select("ul#topic1>li");
                    String lastUpdate = document.select("div.jshtml>ul>li>a.cz").html();
                    chapterList = new ArrayList<>();
                    int i = 0;
                    for (Element element : chapterElements) {
                        Chapter chapter = new Chapter(comic);
                        chapter.setChapterName(element.select("a").attr("title"));
                        chapter.setLastUpdate(lastUpdate);
                        chapter.setChapterUrl(homepage + element.select("a").attr("href"));
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

    /**
     * 漫画图
     * @param html
     */
    @Override
    protected void loadPicsData(final String html) {
        try {
            Document document= Jsoup.parse(html);
            String src = document.select("div.mh_comicpic>img").attr("src");
            if (src != null && !src.equals("") && !isLoaded) {
                isLoaded = true;
                int pNum = document.select("div.mh_headpager>select.mh_select>option").size();
                String urlPrefix = src.substring(0, src.length() - 21);
                picsList = new ArrayList<>();
                for (int i = 0; i < pNum; i++) {
                    String url = urlPrefix + (i + 1) + ".jpg-mht.middle";
                    picsList.add(url);
                }
                mHandler.sendMessage(getMessage(picsList, LOAD_SUCCESS));
            }
        } catch (Exception e) {
            mHandler.sendMessage(getMessage(e, LOAD_FAILURE));
        }
    }
}
