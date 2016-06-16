package com.example.qkx.myshare;

/**
 * Created by qkx on 16/5/30.
 */
public class Constants {

    public static final String KEY_OBJECT_ID = "objectId";

    //user
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_NICKNAME = "nickname";
    public static final String KEY_HEAD_PHOTO = "headPhoto";

    public static final String KEY_SELECTED_PATHS = "selectedPaths";

    // share
    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_IMAGES = "images";
    public static final String KEY_OWNER_ID = "ownerId";
    public static final String KEY_OWNER_NICKNAME = "ownerNickname";

    // friend
    public static final String KEY_FRIEND_ID = "friendId";

    // _file
    public static final String KEY_NAME = "name";
    public static final String KEY_URL = "url";

    // table
    public static final String TABLE_NAME_SHARE = "MyShare";
    public static final String TABLE_NAME_USER = "MyUser";
    public static final String TABLE_NAME_FRIEND = "MyFriend";

    public static final String TABLE_NAME_FILE= "_File";

    public static final int REQUEST_HOME_TO_CREATE_NEW = 10;
    public static final int REQUEST_CREATE_NEW_TO_CHOOSE = 20;
    public static final int REQUEST_FRIEND_TO_CHOOSE = 30; // upload head pic
    public static final int REQUEST_CAMERA = 40;


}
