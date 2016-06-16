package com.example.qkx.myshare;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;

/**
 * Created by qkx on 16/5/30.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AVOSCloud.initialize(this, "ymNzK1N4az2AJQ7OmT09t4Bc-gzGzoHsz", "mmGzT1T5oOFSPlT2xkpbPhs0");
    }
}
