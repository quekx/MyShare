package com.example.qkx.myshare.data;

import java.util.List;

/**
 * Created by qkx on 16/5/31.
 */
public class Share {
    public String ownerId;
    public String nickname;
    public String description;

    public List<String> urls;

    public Share(String ownerId, String nickname, String description, List<String> urls) {
        this.ownerId = ownerId;
        this.nickname = nickname;
        this.description = description;
        this.urls = urls;
    }
}
