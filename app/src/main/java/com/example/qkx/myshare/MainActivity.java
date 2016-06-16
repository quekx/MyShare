package com.example.qkx.myshare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.example.qkx.myshare.choose.ChooseActivity;
import com.example.qkx.myshare.create.CreateNewActivity;
import com.example.qkx.myshare.home.HomeActivity;
import com.example.qkx.myshare.login.LoginActivity;
import com.example.qkx.myshare.setting.Setting;
import com.example.qkx.myshare.utils.PreferenceUtil;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        check();

        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        Intent intent = new Intent(this, HomeActivity.class);
//        Intent intent = new Intent(this, CreateNewActivity.class);
        startActivity(intent);

    }

    private void check() {
        // check login info
        String username = PreferenceUtil.getString(this, Constants.KEY_USERNAME, null);
        String password = PreferenceUtil.getString(this, Constants.KEY_PASSWORD, null);
        final String userId = PreferenceUtil.getString(this, Constants.KEY_USER_ID, null);
        final String nickname = PreferenceUtil.getString(this, Constants.KEY_NICKNAME, null);

        AVQuery<AVObject> usernameQuery = new AVQuery<>(Constants.TABLE_NAME_USER);
        usernameQuery.whereEqualTo(Constants.KEY_USERNAME, username);
        AVQuery<AVObject> passwordQuery = new AVQuery<>(Constants.TABLE_NAME_USER);
        passwordQuery.whereEqualTo(Constants.KEY_PASSWORD, password);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(usernameQuery, passwordQuery));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null && !list.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    intent.putExtra(Constants.KEY_USER_ID, userId);
                    intent.putExtra(Constants.KEY_NICKNAME, nickname);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });


    }
}
