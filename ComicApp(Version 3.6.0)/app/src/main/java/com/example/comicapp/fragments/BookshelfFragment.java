package com.example.comicapp.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.comicapp.R;
import com.example.comicapp.adapters.BookShelfAdapter;
import com.example.comicapp.config.BasicConfig;
import com.example.comicapp.entities.MyUser;
import com.example.comicapp.entities.ComicCollection;
import com.example.comicapp.utils.ActivityUtil;
import com.example.comicapp.utils.StatusBarUtil;
import com.example.comicapp.utils.UserUtil;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class BookshelfFragment extends BaseFragment {
    private Button loginButton;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_bookshelf, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isInitialized) {
            loadUserComicLikeList(UserUtil.myUser);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BasicConfig.REQUEST_CODE_LOGIN) {
            if (resultCode == BasicConfig.RESULT_CODE_SUCCESS) {
                loadUserComicLikeList((MyUser) data.getParcelableExtra(BasicConfig.LOGIN_INTENT_KEY));
            }
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        setDarkStatus(true);
        setToolbar(R.id.fg_bs_toolbar).setTitle("我的书架");
        loginButton = getActivity().findViewById(R.id.fg_bs_login_bn);
        swipeRefreshLayout = getActivity().findViewById(R.id.fg_bs_swipeRefreshLayout);
        recyclerView = getActivity().findViewById(R.id.fg_bs_ry);
        swipeRefreshLayout.setColorSchemeColors(getActivity().getResources().getColor(R.color.refreshColor));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (UserUtil.myUser != null) {
                    loadUserComicLikeList(UserUtil.myUser);
                } else {
                    recyclerView.setVisibility(View.GONE);
                    loginButton.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserUtil.myUser == null) {
                    ActivityUtil.launchLoginView(BookshelfFragment.this, BasicConfig.REQUEST_CODE_LOGIN);
                } else {
                    loadUserComicLikeList(UserUtil.myUser);
                }
            }
        });
        loadUserComicLikeList(UserUtil.myUser);
    }

    /**
     * 获取书架信息
     * @param myUser
     */
    private void loadUserComicLikeList(MyUser myUser) {
        if (myUser != null) {
            recyclerView.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(true);
            BmobQuery<ComicCollection> comicLikeQuery = new BmobQuery<>();
            comicLikeQuery.addWhereEqualTo("userOpenId", UserUtil.myUser.getUserOpenId());
            comicLikeQuery.order("-createdAt")
                    .findObjects(new FindListener<ComicCollection>() {
                @Override
                public void done(List<ComicCollection> list, BmobException e) {
                    swipeRefreshLayout.setRefreshing(false);
                    if (e == null) {
                        showUserComicLike(list);
                    }
                }
            });
        } else {
            recyclerView.setVisibility(View.GONE);
            loginButton.setVisibility(View.VISIBLE);
        }
        isInitialized = true;
    }

    /**
     * 显示用户书架
     * @param userComicLikeList
     */
    private void showUserComicLike(List<ComicCollection> userComicLikeList) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        BookShelfAdapter bookShelfAdapter = new BookShelfAdapter(userComicLikeList, getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(bookShelfAdapter);
    }
}
