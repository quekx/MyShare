package com.example.qkx.myshare.data.source;

import com.example.qkx.myshare.data.Share;

import java.util.List;

/**
 * Created by qkx on 16/5/31.
 */
public interface ShareDataSource {
    interface LoadCallback {

        void onSharesLoad(List<Share> shares);

        void onDataNotAvailuble();
    }

    interface GetCallback {

        void onShareGet(Share share);

        void onDataNotAvailable();
    }

    void queryHome(LoadCallback callback);

}
