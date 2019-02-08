package com.example.comicapp.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.comicapp.R;
import com.example.comicapp.activities.BaseActivity;
import com.example.comicapp.entities.APKUpdate;
import com.example.comicapp.utils.APKVersionCodeUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class APKUpdateController {
    private Activity mActivity;
    private boolean mIsCancel;
    private ProgressBar mProgressBar;
    private AlertDialog mDownloadDialog;
    private TextView progressTextView;
    private String mSavePath;
    private String mAPKFileName;
    private int mProgress;
    private int count;
    private int length;

    public APKUpdateController(Activity mActivity) {
        this.mActivity = mActivity;
    }

    public void checkNewVersion(String objectId, final OnAPKUpdateListener onAPKUpdateListener) {
        BmobQuery<APKUpdate> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(objectId, new QueryListener<APKUpdate>() {
            @Override
            public void done(APKUpdate apkUpdate, BmobException e) {
                if (e == null) {
                    if (apkUpdate.getVersion().compareTo(APKVersionCodeUtil.getVerName(mActivity)) > 0) {
                        onAPKUpdateListener.onQueryDone(apkUpdate, true);
                    } else {
                        onAPKUpdateListener.onQueryDone(apkUpdate, false);
                    }
                } else {
                    onAPKUpdateListener.onQueryError(e);
                }
            }
        });
    }

    public void showNewVersionDialog(final APKUpdate apkUpdate) {
        AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setIcon(R.drawable.icon)
                .setTitle("检查到新版本")
                .setMessage(apkUpdate.getUpdateMessage())
                //设置对话框的按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("更新", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mIsCancel = false;
                        //展示对话框
                        showDownloadDialog(apkUpdate);
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private void showDownloadDialog(APKUpdate apkUpdate) {
        length = 0;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setTitle("下载中");
        View view = LayoutInflater.from(mActivity).inflate(R.layout.view_progress_bar, null);
        mProgressBar = view.findViewById(R.id.view_progressbar);
        progressTextView = view.findViewById(R.id.view_dialog_text);
        builder.setView(view);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 隐藏当前对话框
                dialog.dismiss();
                // 设置下载状态为取消
                mIsCancel = true;
            }
        });
        mDownloadDialog = builder.create();
        mDownloadDialog.show();
        mAPKFileName = "comic-" + apkUpdate.getVersion() + ".apk";
        // 下载APK
        downloadAPK(apkUpdate.getAPKUrl());
    }

    private void downloadAPK(final String fileUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        String sdPath = Environment.getExternalStorageDirectory() + "/";
//                      文件保存路径
                        mSavePath = sdPath + "comic/Download/Update";
                        File dir = new File(mSavePath);
                        if (!dir.exists()){
                            dir.mkdirs();
                        }
                        // 下载文件
                        HttpURLConnection conn = (HttpURLConnection) new URL(fileUrl).openConnection();
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        length = conn.getContentLength();

                        File apkFile = new File(mSavePath, mAPKFileName);
                        FileOutputStream fos = new FileOutputStream(apkFile);

                        count = 0;
                        byte[] buffer = new byte[1024];
                        while (!mIsCancel){
                            int numRead = is.read(buffer);
                            count += numRead;
                            // 计算进度条的当前位置
                            mProgress  = (int) (((float) count / length) * 100);
                            // 更新进度条
                            mUpdateProgressHandler.sendEmptyMessage(1);
                            // 下载完成
                            if (numRead < 0){
                                mUpdateProgressHandler.sendEmptyMessage(2);
                                break;
                            }
                            fos.write(buffer, 0, numRead);
                        }
                        fos.close();
                        is.close();
                    }
                }catch(Exception e){
                    Log.d(BaseActivity.TAG, "run: " + e.toString());
                }
            }
        }).start();
    }

    /**
     * 接收消息
     */
    private Handler mUpdateProgressHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    // 设置进度条
                    mProgressBar.setProgress(mProgress);
                    progressTextView.setText(count + " / " + length);
                    break;
                case 2:
                    // 隐藏当前下载对话框
                    mDownloadDialog.dismiss();
                    // 安装 APK 文件
                    installAPK();
            }
        };
    };

    /*
     * 下载到本地后执行安装
     */
    protected void installAPK() {
        File apkFile = new File(mSavePath, mAPKFileName);
        if (!apkFile.exists()){
            return;
        }
        Log.d(BaseActivity.TAG, "installAPK: " + apkFile.getAbsolutePath());
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(mActivity, ".fileprovider", apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        mActivity.startActivity(intent);
    }

    public interface OnAPKUpdateListener {
        void onQueryDone(APKUpdate update, boolean newVersion);
        void onQueryError(Exception e);
    }
}


