package com.example.comicapp.entities;

import android.os.Parcel;
import android.os.Parcelable;

import cn.bmob.v3.BmobObject;

public class MyUser extends BmobObject implements Parcelable {
    private String userOpenId;
    private String userNickname;
    private String userHeadingUrl;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userOpenId);
        dest.writeString(userNickname);
        dest.writeString(userHeadingUrl);
    }

    public final static Parcelable.Creator<MyUser> CREATOR = new Parcelable.Creator<MyUser>() {
        @Override
        public MyUser createFromParcel(Parcel source) {
            MyUser myUser = new MyUser();
            myUser.userOpenId = source.readString();
            myUser.userNickname = source.readString();
            myUser.userHeadingUrl = source.readString();
            return myUser;
        }

        @Override
        public MyUser[] newArray(int size) {
            return new MyUser[size];
        }
    };

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getUserHeadingUrl() {
        return userHeadingUrl;
    }

    public void setUserHeadingUrl(String userHeadingUrl) {
        this.userHeadingUrl = userHeadingUrl;
    }

    public String getUserOpenId() {
        return userOpenId;
    }

    public void setUserOpenId(String userOpenId) {
        this.userOpenId = userOpenId;
    }
}
