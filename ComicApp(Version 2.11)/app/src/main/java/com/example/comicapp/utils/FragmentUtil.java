package com.example.comicapp.utils;

import android.support.v4.app.Fragment;

import com.example.comicapp.fragments.BookshelfFragment;
import com.example.comicapp.fragments.HomeFragment;
import com.example.comicapp.fragments.UserFragment;

import java.util.ArrayList;
import java.util.List;

public class FragmentUtil {
    private static List<Fragment> fragmentList;

    public static void initFragments() {
        fragmentList = new ArrayList<>();
        fragmentList.add(new HomeFragment());
        fragmentList.add(new BookshelfFragment());
        fragmentList.add(new UserFragment());
    }

    public static Fragment getFragment(int fragmentIndex) {
        if (fragmentList == null) {
            initFragments();
        }
        return fragmentList.get(fragmentIndex);
    }
}
