package com.example.comicapp.config;

public class BasicConfig {
    public static final String INTENT_DATA_NAME_COMIC = "extra_data_comic";
    public static final String INTENT_DATA_NAME_CHAPTER = "extra_data_chapter";
    public static final String HOME_PAGE = "https://www.manhuatai.com";
    // SharedPreferences config
    public static final String REFERENCE_NAME_LAST_VIEW = "last_view";
    public static final String REFERENCE_NAME_LOGIN = "login_cache";
    public static final String REFERENCE_NAME_USER = "user_cache";
    public static final String KEY_NAME_GENERAL = "key_general";
    public static final String KEY_NAME_IS_LOGIN = "key_is_login";
    public static final String KEY_NAME_USER_CACHE = "key_user_cache";
    // Message config
    public static final int MESSAGE_SUCCESS = 1;
    public static final int MESSAGE_FAILURE = -1;
    // TAG config
    public static final String TAG = "测试";
    // Login config
    public static final int REQUEST_CODE_LOGIN = 4;
    public static final int RESULT_CODE_SUCCESS = 11;
    public static final int RESULT_CODE_CANCEL = 12;
    public static final int RESULT_CODE_ERROR = 13;
    public static final String LOGIN_INTENT_KEY = "login_intent_data";

    public enum State {
        EXPANDED,//展开
        COLLAPSED,//折叠
        DOWN_INTERMEDIATE,//中下状态
        UP_INTERMEDIATE // 中上
    }
}