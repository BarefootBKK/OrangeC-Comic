package com.example.comicapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Chapter extends Comic implements Parcelable {
    private String chapterName;
    private String chapterUrl;
    private int chapterPNum;
    private int listIndex;
    private List<String> picsList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(chapterName);
        dest.writeString(chapterUrl);
        dest.writeInt(chapterPNum);
        dest.writeInt(listIndex);
        dest.writeString(comicName);
        dest.writeString(comicPosterUrl);
        dest.writeString(comicWebUrl);
        dest.writeList(chapterList);
        dest.writeList(picsList);
    }

    public static final Parcelable.Creator<Chapter> CREATOR = new Parcelable.Creator<Chapter>() {
        @Override
        public Chapter createFromParcel(Parcel source) {
            Chapter chapter = new Chapter();
            chapter.chapterName = source.readString();
            chapter.chapterUrl = source.readString();
            chapter.chapterPNum = source.readInt();
            chapter.listIndex = source.readInt();
            chapter.comicName = source.readString();
            chapter.comicPosterUrl = source.readString();
            chapter.comicWebUrl = source.readString();
            chapter.chapterList = new ArrayList<>();
            source.readList(chapter.chapterList, getClass().getClassLoader());
            chapter.picsList = new ArrayList<>();
            source.readList(chapter.picsList, getClass().getClassLoader());
            return chapter;
        }

        @Override
        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public String getChapterUrl() {
        return chapterUrl;
    }

    public void setChapterUrl(String chapterUrl) {
        this.chapterUrl = chapterUrl;
    }

    public int getChapterPNum() {
        return chapterPNum;
    }

    public void setChapterPNum(int chapterPNum) {
        this.chapterPNum = chapterPNum;
    }

    public int getListIndex() {
        return listIndex;
    }

    public void setListIndex(int listIndex) {
        this.listIndex = listIndex;
    }

    public List<String> getPicsList() {
        return picsList;
    }

    public void setPicsList(List<String> picsList) {
        this.picsList = picsList;
    }
}
