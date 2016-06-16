package com.example.qkx.myshare.utils;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.qkx.myshare.Constants;
import com.example.qkx.myshare.data.Share;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by qkx on 16/6/5.
 */
public class AVUtil {
    private static final String TAG = "AVUtil";

    public static String getThumbnail(String url, int width, ThumbnailCallback callback) {
        AVFile avFile = new AVFile("thumbnail.jpg", url, null);
        return avFile.getThumbnailUrl(true, width, width);
    }

//    public static void parseShareList(final List<AVObject> list, final ParseCallback callback) {
//        final List<Share> shares = new ArrayList<>();
//        for (AVObject object : list) {
//            final String nickname = object.getString(Constants.KEY_OWNER_NICKNAME);
//            final String description = object.getString(Constants.KEY_DESCRIPTION);
//
//            final List<String> urls = new ArrayList<>();
//            final List<String> names = object.getList(Constants.KEY_IMAGES);
//            for (String name : names) {
//                AVQuery<AVObject> avQuery = new AVQuery<>(Constants.TABLE_NAME_FILE);
//                avQuery.whereStartsWith(Constants.KEY_NAME, name);
//                avQuery.findInBackground(new FindCallback<AVObject>() {
//                    @Override
//                    public void done(List<AVObject> mList, AVException e) {
//                        if (e != null) {
//                            e.printStackTrace();
//                        } else if (mList.isEmpty()) {
//                            Log.d(TAG, "list is empty!");
//                        } else {
//                            AVObject fileObject = mList.get(0);
//                            urls.add(fileObject.getString(Constants.KEY_URL));
//                            // 完成查询图片
//                            if (urls.size() == names.size()) {
//                                Share share = new Share(nickname, description, urls);
//                                shares.add(share);
//                                if (shares.size() == list.size()) {
//                                    callback.onParseFinished(shares);
//                                }
//                            }
//                        }
//                    }
//                });
//            }
//        }
//    }

//    public static void parseShareList2(List<AVObject> list, ParseCallback callback) {
//        List<Share> shares = new ArrayList<>();
//        for (AVObject object : list) {
//            String nickname = object.getString(Constants.KEY_OWNER_NICKNAME);
//            String description = object.getString(Constants.KEY_DESCRIPTION);
//            List<String> imageUrls = object.getList(Constants.KEY_IMAGES);
//
//            Share share = new Share(nickname, description, imageUrls);
//            shares.add(share);
//        }
//        callback.onParseFinished(shares);
//    }

    public static void queryShareByIds(Set<String> ownerIds, final QueryShareCallback callback) {
//        AVQuery<AVObject> avQuery = new AVQuery<>(Constants.TABLE_NAME_SHARE);
//        avQuery.whereStartsWith(Constants.KEY_OWNER_ID, ownerId);
        ArrayList<AVQuery<AVObject>> queries = new ArrayList<>();
        for (String ownerId : ownerIds) {
            AVQuery<AVObject> query = new AVQuery<>(Constants.TABLE_NAME_SHARE);
            query.whereStartsWith(Constants.KEY_OWNER_ID, ownerId);
            queries.add(query);
        }

        AVQuery<AVObject> avQuery = AVQuery.or(queries);
        avQuery.orderByDescending("createdAt");

        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e != null) {
                    e.printStackTrace();
                    callback.onQueryFailed();
                } else if (list.isEmpty()) {
                    Log.d(TAG, "list is empty!");
                    callback.onQueryEmpty();
                } else {
                    List<Share> shares = new ArrayList<>();
                    // object每条状态
                    for (AVObject object : list) {
                        String ownerId = object.getString(Constants.KEY_OWNER_ID);
                        String nickname = object.getString(Constants.KEY_OWNER_NICKNAME);
                        String description = object.getString(Constants.KEY_DESCRIPTION);
                        List<String> imageUrls = object.getList(Constants.KEY_IMAGES);

                        Share share = new Share(ownerId, nickname, description, imageUrls);
                        shares.add(share);
                    }
                    callback.onQuerySuccess(shares);
                }
            }
        });
    }

    public static void queryShareById(String ownerId, final QueryShareCallback callback) {
        AVQuery<AVObject> avQuery = new AVQuery<>(Constants.TABLE_NAME_SHARE);
        avQuery.whereStartsWith(Constants.KEY_OWNER_ID, ownerId);
        avQuery.orderByDescending("createdAt");

        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e != null) {
                    e.printStackTrace();
                    callback.onQueryFailed();
                } else if (list.isEmpty()) {
                    Log.d(TAG, "list is empty!");
                } else {
                    List<Share> shares = new ArrayList<>();
                    // object每条状态
                    for (AVObject object : list) {
                        String ownerId = object.getString(Constants.KEY_OWNER_ID);
                        String nickname = object.getString(Constants.KEY_OWNER_NICKNAME);
                        String description = object.getString(Constants.KEY_DESCRIPTION);
                        List<String> imageUrls = object.getList(Constants.KEY_IMAGES);

                        Share share = new Share(ownerId, nickname, description, imageUrls);
                        shares.add(share);
                    }
                    callback.onQuerySuccess(shares);
                }
            }
        });
    }

    public static void upLoadShare(AVObject shareObject, String description, List<String> photoPaths, final UploadCallback callback) {
        shareObject.put(Constants.KEY_DESCRIPTION, description);
        // 存储文件名,用于查找文件
        ArrayList<String> images = new ArrayList<>();
        for (String path : photoPaths) {
            File file = new File(path);
            images.add(file.getName());
            try {
                // 上传文件
                final AVFile photoFile = AVFile.withFile(file.getName(), file);
                photoFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        Log.d(TAG, "image url --> " + photoFile.getUrl());
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        shareObject.put(Constants.KEY_IMAGES, images);
        // 上传分享
        shareObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d(TAG, "upLoad success!");
                    callback.onUploadSuccess();
                } else {
                    e.printStackTrace();
                    callback.onUploadFailed();
                }
            }
        });
    }

    public static void upLoadShare2(final AVObject shareObject, String description, final List<String> photoPaths, final UploadCallback callback) {
        shareObject.put(Constants.KEY_DESCRIPTION, description);
        final ArrayList<String> imageUrls = new ArrayList<>();
        for (String path : photoPaths) {
            File file = new File(path);
            try {
                // 上传文件
                final AVFile photoFile = AVFile.withFile(file.getName(), file);
                photoFile.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                            String url = photoFile.getUrl();
                            Log.d(TAG, "image url --> " + url);
                            imageUrls.add(url);
                            // 所有图片上传完毕,则上传文本和URL
                            if (imageUrls.size() == photoPaths.size()) {
                                shareObject.put(Constants.KEY_IMAGES, imageUrls);
                                // 上传分享
                                shareObject.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(AVException e) {
                                        if (e == null) {
                                            Log.d(TAG, "upLoad success!");
                                            callback.onUploadSuccess();
                                        } else {
                                            e.printStackTrace();
                                            callback.onUploadFailed();
                                        }
                                    }
                                });
                            }
                        }
                    }
                });
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

    }

    public interface ThumbnailCallback {
        void onThumbnailget(String thumbnailUrl);
    }

    public interface ParseCallback {
        void onParseFinished(List<Share> shares);
    }

    public interface QueryShareCallback {
        void onQueryEmpty();

        void onQuerySuccess(List<Share> shares);

        void onQueryFailed();
    }

    public interface UploadCallback {
        void onUploadSuccess();

        void onUploadFailed();
    }


    public interface LoginCallback {
        void onLoginSuccess();

        void onLoginFailed();
    }
}
