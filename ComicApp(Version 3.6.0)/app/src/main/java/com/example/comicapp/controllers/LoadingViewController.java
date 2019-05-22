package com.example.comicapp.controllers;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

public class LoadingViewController {
    public final static int MATCH_PARENT = RelativeLayout.LayoutParams.MATCH_PARENT;
    public final static int WRAP_CONTENT = RelativeLayout.LayoutParams.WRAP_CONTENT;

    private static LoadingViewContainer loadingViewContainer;
    private Activity mActivity;

    public static LoadingViewController with(Activity activity) {
        return new LoadingViewController(activity);
    }

    public LoadingViewController (Activity activity) {
        this.mActivity = activity;
    }

    public LoadingViewContainer setLoadingViewContainer(ViewGroup viewGroup) {
        loadingViewContainer = new LoadingViewContainer(viewGroup);
        return loadingViewContainer;
    }

    public static void dismiss() {
        if (loadingViewContainer != null) {
            loadingViewContainer.dismiss();
            loadingViewContainer = null;
        }
    }

    public static boolean isShowing() {
        if (loadingViewContainer == null) {
            return false;
        } else {
            return true;
        }
    }

    public class LoadingViewContainer {
        private ViewGroup parentView;
        private RelativeLayout loadingLayoutContainer;
        private View loadingLayoutOutsideView;
        private AVLoadingIndicatorView avLoadingIndicatorView;
        private TextView loadingTextView;
        private int size;

        public LoadingViewContainer(ViewGroup parentView) {
            this.parentView = parentView;
            buildLayout();
        }

        @SuppressWarnings("ResourceType")
        private void buildLayout() {
            size = mActivity.getResources().getDisplayMetrics().widthPixels / 7;
            // 主容器
            loadingLayoutContainer = new RelativeLayout(mActivity);
            loadingLayoutContainer.setElevation(999f);
            loadingLayoutContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 防止其它控件影响
                }
            });
            loadingLayoutContainer.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            // 外部背景控件
            loadingLayoutOutsideView = new View(mActivity);
            loadingLayoutOutsideView.setBackgroundColor(Color.parseColor("#000000"));
            loadingLayoutOutsideView.setAlpha(0.5f);
            loadingLayoutOutsideView.setLayoutParams(new RelativeLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
            // 动画控件
            avLoadingIndicatorView = new AVLoadingIndicatorView(mActivity);
            avLoadingIndicatorView.setId(1);
            avLoadingIndicatorView.setIndicator("BallSpinFadeLoaderIndicator");
            RelativeLayout.LayoutParams animationLayoutParams = new RelativeLayout.LayoutParams(size, size);
            animationLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            avLoadingIndicatorView.setLayoutParams(animationLayoutParams);
            // 提示控件
            loadingTextView = new TextView(mActivity);
            RelativeLayout.LayoutParams textLayoutParams = new RelativeLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT);
            textLayoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            textLayoutParams.addRule(RelativeLayout.BELOW, avLoadingIndicatorView.getId());
            textLayoutParams.topMargin = 35;
            loadingTextView.setLayoutParams(textLayoutParams);
            loadingTextView.setTextColor(Color.parseColor("#ffffff"));
            loadingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            loadingTextView.setText("loading");
            // 把子控件加入父容器中
            loadingLayoutContainer.addView(loadingLayoutOutsideView);
            loadingLayoutContainer.addView(avLoadingIndicatorView);
            loadingLayoutContainer.addView(loadingTextView);
            loadingLayoutContainer.bringToFront();
        }

        public LoadingViewContainer setHintTextColor(int color) {
            loadingTextView.setTextColor(color);
            return this;
        }

        public LoadingViewContainer setHintTextSize(int size) {
            loadingTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
            return this;
        }

        public LoadingViewContainer setHintText(String msg) {
            loadingTextView.setText(msg);
            return this;
        }

        public LoadingViewContainer setOutsideAlpha(float alpha) {
            loadingLayoutOutsideView.setAlpha(alpha);
            return this;
        }

        public LoadingViewContainer setOnTouchOutsideListener(View.OnClickListener clickListener) {
            loadingLayoutOutsideView.setOnClickListener(clickListener);
            return this;
        }

        public LoadingViewContainer setIndicator(String indicatorName) {
            avLoadingIndicatorView.setIndicator(indicatorName);
            return this;
        }

        public LoadingViewContainer setTouchOutsideToDismiss(boolean isDismiss) {
            if (isDismiss) {
                loadingLayoutOutsideView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dismiss();
                    }
                });
            }
            return this;
        }

        public void dismiss() {
            avLoadingIndicatorView.hide();
            parentView.removeView(loadingLayoutContainer);
        }

        public void build() {
            avLoadingIndicatorView.show();
            parentView.addView(loadingLayoutContainer);
            parentView.bringChildToFront(loadingLayoutContainer);
            parentView.updateViewLayout(loadingLayoutContainer, loadingLayoutContainer.getLayoutParams());
        }
    }
}
