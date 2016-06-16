package com.example.qkx.myshare.utils;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by qkx on 16/5/31.
 */
public class ActivityUtil {
    public static void addFragmentToActivity(FragmentManager fragmentManager, Fragment fragment, int containerId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(containerId, fragment);
        transaction.commit();
    }
}
