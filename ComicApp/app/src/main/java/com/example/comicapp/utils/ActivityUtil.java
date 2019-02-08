package com.example.comicapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.comicapp.activities.BaseActivity;
import com.example.comicapp.activities.LoginActivity;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.R;
import com.example.comicapp.entities.SPReader;

public class ActivityUtil {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    /**
     * 设置为沉浸式状态栏
     */
    public static void setStatusBar (Activity activity) {
        boolean useThemeStatusBarColor = false;
        boolean useStatusBarColor = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//5.0及以上
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            //根据上面设置是否对状态栏单独设置颜色
            if (useThemeStatusBarColor) {
                activity.getWindow().setStatusBarColor(ContextCompat.getColor(activity, R.color.colorAccent));//设置状态栏背景色
            } else {
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);//透明
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {//4.4到5.0
            WindowManager.LayoutParams localLayoutParams = activity.getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        } else {
            Toast.makeText(activity, "低于4.4的android系统版本不存在沉浸式状态栏", Toast.LENGTH_SHORT).show();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && useStatusBarColor) {//android6.0以后可以对状态栏文字颜色和图标进行修改
            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

    public static void saveDataWithSP(Context context, String referenceName, String keyName, String value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(referenceName, Context.MODE_PRIVATE).edit();
        editor.putString(keyName, value);
        editor.commit();
    }

    public static void saveDataWithSP(Context context, String referenceName, String keyName, String value, int index) {
        SharedPreferences.Editor editor = context.getSharedPreferences(referenceName, Context.MODE_PRIVATE).edit();
        editor.putString(keyName + "_STRING", value);
        editor.putInt(keyName + "_INT", index);
        editor.commit();
    }

    public static String getDataWithSP(Context context, String keyName) {
        SharedPreferences reader = context.getSharedPreferences(BasicConfig.REFERENCE_NAME_LAST_VIEW, Context.MODE_PRIVATE);
        return reader.getString(keyName, null);
    }

    public static SPReader getDataWithSPReader(Context context, String referenceName, String keyName) {
        SharedPreferences reader = context.getSharedPreferences(referenceName, Context.MODE_PRIVATE);
        String value = reader.getString(keyName + "_STRING", null);
        int index = reader.getInt(keyName + "_INT", -1);
        return new SPReader(value, index);
    }

    public static String getSubString(String str, int length, boolean isAddEllipsis) {
        if (str.length() <= length) {
            return str;
        } else {
            if (isAddEllipsis) {
                return str.substring(0, length) + "...";
            } else {
                return str.substring(0, length);
            }
        }
    }

    public static boolean verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                return false;
            } else {
                return true;
            }
        } catch (Exception e) {
            Log.d(BaseActivity.TAG, "verifyStoragePermissions: " + e.toString());
            return false;
        }
    }

    public static void requestStoragePermission(Activity activity) {
        // 申请读写权限
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
    }

    public static void launchLoginView(Activity activity, int requestCode) {
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

    public static void launchLoginView(Fragment fragment, int requestCode) {
        Intent intent = new Intent(fragment.getContext(), LoginActivity.class);
        fragment.startActivityForResult(intent, requestCode);
    }
}
