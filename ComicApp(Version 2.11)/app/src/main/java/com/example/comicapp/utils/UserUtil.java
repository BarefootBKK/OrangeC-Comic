package com.example.comicapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.entities.MyUser;

public class UserUtil {
    public static MyUser myUser = null;
    private static String OPEN_ID = "_OPEN_ID";
    private static String NICKNAME = "_NICKNAME";
    private static String HEADING_URL = "_HEADING_URL";

    public static boolean checkIsLogin(Context context) {
        SharedPreferences reader = context.getSharedPreferences(BasicConfig.REFERENCE_NAME_LOGIN, Context.MODE_PRIVATE);
        return reader.getBoolean(BasicConfig.KEY_NAME_IS_LOGIN, false);
    }

    public static void saveLoginInfo(Context context, boolean isLogin) {
        SharedPreferences.Editor editor = context.getSharedPreferences(BasicConfig.REFERENCE_NAME_LOGIN, Context.MODE_PRIVATE).edit();
        editor.putBoolean(BasicConfig.KEY_NAME_IS_LOGIN, isLogin);
        editor.commit();
    }

    public static void saveUserCache(Context context, MyUser user) {
        if (user != null) {
            SharedPreferences.Editor editor_user = context.getSharedPreferences(BasicConfig.REFERENCE_NAME_USER, Context.MODE_PRIVATE).edit();
            editor_user.putString(BasicConfig.REFERENCE_NAME_USER + OPEN_ID, user.getUserOpenId());
            editor_user.putString(BasicConfig.REFERENCE_NAME_USER + NICKNAME, user.getUserNickname());
            editor_user.putString(BasicConfig.REFERENCE_NAME_USER + HEADING_URL, user.getUserHeadingUrl());
            editor_user.commit();
            saveLoginInfo(context, true);
        }
    }

    public static MyUser readUserCache(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(BasicConfig.REFERENCE_NAME_USER, Context.MODE_PRIVATE);
        String openId = sharedPreferences.getString(BasicConfig.REFERENCE_NAME_USER + OPEN_ID, null);
        String nickname = sharedPreferences.getString(BasicConfig.REFERENCE_NAME_USER + NICKNAME, null);
        String headingPostUrl = sharedPreferences.getString(BasicConfig.REFERENCE_NAME_USER + HEADING_URL, null);
        if (openId != null && nickname != null && headingPostUrl != null) {
            MyUser user = new MyUser();
            user.setUserOpenId(openId);
            user.setUserNickname(nickname);
            user.setUserHeadingUrl(headingPostUrl);
            return user;
        } else {
            return null;
        }
    }
}
