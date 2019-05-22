package com.example.comicapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class Comic implements Parcelable {
    protected String comicName;
    protected String comicPosterUrl;
    protected String comicWebUrl;
    protected String lastUpdate;
    protected int comicType;
    protected List<Chapter> chapterList;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(comicName);
        dest.writeString(comicPosterUrl);
        dest.writeString(comicWebUrl);
        dest.writeString(lastUpdate);
        dest.writeInt(comicType);
        dest.writeList(chapterList);
    }

    public static final Parcelable.Creator<Comic> CREATOR = new Parcelable.Creator<Comic>() {
        @Override
        public Comic createFromParcel(Parcel source) {
            Comic comic = new Comic();
            comic.comicName = source.readString();
            comic.comicPosterUrl = source.readString();
            comic.comicWebUrl = source.readString();
            comic.lastUpdate = source.readString();
            comic.comicType = source.readInt();
            comic.chapterList = new ArrayList<>();
            source.readList(comic.chapterList, getClass().getClassLoader());
            return comic;
        }

        @Override
        public Comic[] newArray(int size) {
            return new Comic[size];
        }
    };

    public String getComicName() {
        return comicName;
    }

    public void setComicName(String comicName) {
        this.comicName = comicName;
    }

    public String getComicPosterUrl() {
        return comicPosterUrl;
    }

    public void setComicPosterUrl(String comicPosterUrl) {
        this.comicPosterUrl = comicPosterUrl;
    }

    public List<Chapter> getChapterList() {
        return chapterList;
    }

    public void setChapterList(List<Chapter> chapterList) {
        this.chapterList = chapterList;
    }

    public String getComicWebUrl() {
        return comicWebUrl;
    }

    public void setComicWebUrl(String comicWebUrl) {
        this.comicWebUrl = comicWebUrl;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getComicType() {
        return comicType;
    }

    public void setComicType(int comicType) {
        this.comicType = comicType;
    }
}
