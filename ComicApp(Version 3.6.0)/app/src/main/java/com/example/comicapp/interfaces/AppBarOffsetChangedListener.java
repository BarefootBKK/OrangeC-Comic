package com.example.comicapp.interfaces;

import android.support.design.widget.AppBarLayout;
import android.util.Log;

public abstract class AppBarOffsetChangedListener implements AppBarLayout.OnOffsetChangedListener {
    public enum State {
        EXPANDED,//展开
        COLLAPSED,//折叠
        DOWN_INTERMEDIATE,//中下状态
        UP_INTERMEDIATE // 中上
    }
    private State mCurrentState = State.DOWN_INTERMEDIATE;

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (verticalOffset == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        } else if (Math.abs(verticalOffset) > 0 && (float) Math.abs(verticalOffset) < ((float) appBarLayout.getTotalScrollRange()) * 0.379){
            if (mCurrentState != State.DOWN_INTERMEDIATE) {
                onStateChanged(appBarLayout, State.DOWN_INTERMEDIATE);
            }
            mCurrentState = State.DOWN_INTERMEDIATE;
        } else {
            if (mCurrentState != State.UP_INTERMEDIATE) {
                onStateChanged(appBarLayout, State.UP_INTERMEDIATE);
            }
            mCurrentState = State.UP_INTERMEDIATE;
        }
    }

    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}
