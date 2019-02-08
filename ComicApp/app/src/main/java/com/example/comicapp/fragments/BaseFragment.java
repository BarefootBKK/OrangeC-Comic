package com.example.comicapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.comicapp.R;
import com.example.comicapp.activities.MainActivity;
import com.example.comicapp.utils.FragmentUtil;

public class BaseFragment extends Fragment {
    protected static final int FRAGMENT_HOME = 0;
    protected static final int FRAGMENT_BOOKSHELF = 1;
    protected static final int FRAGMENT_USER = 2;
    private static final int containerId = R.id.a_main_fl_container;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected void replaceFragment(int fragmentIndex) {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(containerId, FragmentUtil.getFragment(fragmentIndex))
                .commit();
    }

    protected ActionBar setToolbar(int toolbarId) {
//        setHasOptionsMenu(true);
        MainActivity mainActivity = (MainActivity) getActivity();
        Toolbar toolbar = mainActivity.findViewById(toolbarId);
        mainActivity.setSupportActionBar(toolbar);
        ActionBar actionBar = mainActivity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayShowTitleEnabled(true);
            return actionBar;
        }
        return null;
    }

    protected void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected void showToastWithLongShow(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
    }
}
