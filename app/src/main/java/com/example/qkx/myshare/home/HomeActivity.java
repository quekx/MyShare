package com.example.qkx.myshare.home;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.example.qkx.myshare.Constants;
import com.example.qkx.myshare.R;
import com.example.qkx.myshare.create.CreateNewActivity;
import com.example.qkx.myshare.data.Share;
import com.example.qkx.myshare.friend.FriendActivity;
import com.example.qkx.myshare.utils.AVUtil;
import com.example.qkx.myshare.utils.ActivityUtil;
import com.example.qkx.myshare.utils.ToastUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qkx on 16/5/30.
 */
public class HomeActivity extends AppCompatActivity{

    private static String TAG = HomeActivity.class.getSimpleName();

    // 记录上传的id, 昵称
    private String userId;
    private String nickname;

    private HomeFragment mFragment;

    private ProgressDialog progressDialog;

    @Bind(R.id.toolbar_home)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        userId = intent.getStringExtra(Constants.KEY_USER_ID);
        Log.d(TAG, Constants.KEY_USER_ID + ":" + userId);
        nickname = intent.getStringExtra(Constants.KEY_NICKNAME);
        Log.d(TAG, Constants.KEY_NICKNAME + ":" + nickname);

        init();
    }

    private void init() {

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_18pt_2x);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setTitle("MyShare");

        toolbar.inflateMenu(R.menu.menu_home);

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_friend:
                        Intent friendIntent = new Intent(HomeActivity.this, FriendActivity.class);
                        friendIntent.putExtra(Constants.KEY_USER_ID, userId);
                        startActivity(friendIntent);
                        break;
                    case R.id.action_create_new:
                        Intent intent = new Intent(HomeActivity.this, CreateNewActivity.class);
                        intent.putExtra(Constants.KEY_USER_ID, userId);
                        intent.putExtra(Constants.KEY_NICKNAME, nickname);
                        startActivityForResult(intent, Constants.REQUEST_HOME_TO_CREATE_NEW);
                        break;
                    case R.id.action_refresh:
                        refresh();
                        break;
                }
                return true;
            }
        });

        mFragment = (HomeFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (mFragment == null) {
            mFragment = HomeFragment.newInstance();
            Bundle args = mFragment.getArguments();
            args.putString(Constants.KEY_USER_ID, userId);
            mFragment.setArguments(args);
            ActivityUtil.addFragmentToActivity(getSupportFragmentManager(), mFragment, R.id.contentFrame);
        }

        // 请求主页信息
        refresh();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_HOME_TO_CREATE_NEW:
                refresh();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // refresh
    private void refresh() {
        showRefreshDialog();

        final Set<String> ownerIds = new HashSet<>();
        ownerIds.add(userId);

        AVQuery<AVObject> query = new AVQuery<>(Constants.TABLE_NAME_FRIEND);
        query.whereStartsWith(Constants.KEY_USER_ID, userId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for (AVObject object : list) {
                    String friendId = object.getString(Constants.KEY_FRIEND_ID);
                    ownerIds.add(friendId);
                }
                AVUtil.queryShareByIds(ownerIds, new AVUtil.QueryShareCallback() {
                    @Override
                    public void onQuerySuccess(List<Share> shares) {
                        mFragment.refresh(shares);
//                        ToastUtil.showToastShort(HomeActivity.this, "refresh finish!");
                        Log.d(TAG, "refresh finish!");
                        closeRefreshDialog();
                    }

                    @Override
                    public void onQueryFailed() {
                        ToastUtil.showToastShort(HomeActivity.this, "refresh failed!");
                        closeRefreshDialog();
                    }

                    @Override
                    public void onQueryEmpty() {
                        ToastUtil.showToastShort(HomeActivity.this, "no data!");
                        closeRefreshDialog();
                    }
                });
            }
        });
    }
    private void refresh2() {
//        testDownload();
        String ownerId = "574c1bc0df0eea005bc67d35";
        AVUtil.queryShareById(ownerId, new AVUtil.QueryShareCallback() {
            @Override
            public void onQuerySuccess(List<Share> shares) {
                mFragment.refresh(shares);
            }

            @Override
            public void onQueryFailed() {

            }

            @Override
            public void onQueryEmpty() {

            }
        });
    }

    private void showRefreshDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeRefreshDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private void testDownload() {
//        AVObject avObject = new AVObject("xXxX");
        AVQuery<AVObject> query = new AVQuery<>("xXxX");
        query.getInBackground("5753abe45bbb5000644026a8", new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                Log.d(TAG, avObject.toString());
            }
        });
    }

//    @Override
//    public void onBackPressed() {
//        moveTaskToBack(true);
//    }
}
