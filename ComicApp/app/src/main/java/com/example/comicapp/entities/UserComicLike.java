package com.example.comicapp.entities;

import cn.bmob.v3.BmobObject;

public class UserComicLike extends BmobObject {
    private String userOpenId;
    private String comicName;
    private String comicPosterUrl;
    private String comicWebUrl;

    public String getUserOpenId() {
        return userOpenId;
    }

    public void setUserOpenId(String userOpenId) {
        this.userOpenId = userOpenId;
    }

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

    public String getComicWebUrl() {
        return comicWebUrl;
    }

    public void setComicWebUrl(String comicWebUrl) {
        this.comicWebUrl = comicWebUrl;
    }
}
