package com.example.qkx.myshare.friend;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;
import com.example.qkx.myshare.Constants;
import com.example.qkx.myshare.R;
import com.example.qkx.myshare.choose.ChooseActivity;
import com.example.qkx.myshare.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qkx on 16/6/6.
 */
public class FriendActivity extends AppCompatActivity {

    private static final String TAG = "FriendActivity";

    private String userId;

    private Dialog dialog;

    private FriendAdapter mAdapter;

    private ProgressDialog progressDialog;

    @Bind(R.id.toolbar_friend)
    Toolbar toolbar;

    @Bind(R.id.recycle_friend)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        ButterKnife.bind(this);

        userId = getIntent().getStringExtra(Constants.KEY_USER_ID);
        Log.d(TAG, "userId --> " + userId);

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

        toolbar.setTitle("Friends");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.inflateMenu(R.menu.menu_friend);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_add_friend:
                        showAddDialog();
                        break;

                    case R.id.action_identify:
                        queryFriends();
                        break;
                    case R.id.action_upload_head:
                        Intent chooseIntent = new Intent(FriendActivity.this, ChooseActivity.class);
                        ToastUtil.showToastLong(FriendActivity.this, "如选多张,默认第一张作为头像");
                        startActivityForResult(chooseIntent, Constants.REQUEST_FRIEND_TO_CHOOSE);
                        break;
                }
                return true;
            }
        });

        // recyclerView
        mAdapter = new FriendAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // query
        queryFriends();
    }

    private void queryFriends() {
        showRefreshDialog();

        AVQuery<AVObject> query = new AVQuery<>(Constants.TABLE_NAME_FRIEND);
        query.whereStartsWith(Constants.KEY_USER_ID, userId);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
//                    List<String> friendIds = new ArrayList<>();
                    Set<String> friendIds = new HashSet<String>();
                    for (AVObject object : list) {
                        String friendId = object.getString(Constants.KEY_FRIEND_ID);
                        friendIds.add(friendId);
                        Log.d(TAG, "friendId --> " + friendId);
                    }
                    friendIds.add(userId);
                    mAdapter.setData(new ArrayList<String>(friendIds));
//                    mAdapter.setData(friendIds);
                    mAdapter.notifyDataSetChanged();

                    Log.d(TAG, "refresh finish!");
//                    ToastUtil.showToastShort(FriendActivity.this, "refresh finish!");
                } else {
                    ToastUtil.showToastShort(FriendActivity.this, "refresh failed!");
                }

                closeRefreshDialog();
            }
        });
    }

    private void showAddDialog() {
        if (dialog != null) {
            dialog.show();
            return;
        }

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_friend);
        dialog.setTitle("add friend");
        dialog.setCanceledOnTouchOutside(false);

        final EditText editText = (EditText) dialog.findViewById(R.id.edit_add);

        Button btnConfirm = (Button) dialog.findViewById(R.id.btn_add_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editText.getText().toString();
                confirmAdd(username);
            }
        });

        Button btnCancel = (Button) dialog.findViewById(R.id.btn_add_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText(null);
                closeAddDialog();
            }
        });

        dialog.show();
    }

    private void confirmAdd(String username) {
        if (username.length() == 0) {
            ToastUtil.showToastShort(this, "username can not be empty!");
            return;
        }

        AVQuery<AVObject> query1 = new AVQuery<>(Constants.TABLE_NAME_USER);
        query1.whereStartsWith(Constants.KEY_USERNAME, username);

        AVQuery<AVObject> query2 = new AVQuery<>(Constants.TABLE_NAME_USER);
        query2.whereEndsWith(Constants.KEY_USERNAME, username);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(query1, query2));
//        query.whereMatches(Constants.KEY_USERNAME, username);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e != null) {
                    Log.e(TAG, "error!");
                    ToastUtil.showToastShort(FriendActivity.this, "query error!");
                } else if (list.isEmpty()) {
                    Log.e(TAG, "username is not exist!");
                    ToastUtil.showToastShort(FriendActivity.this, "username is not exist!");
                } else {
                    AVObject avObject = list.get(0);
                    String friendId = avObject.getObjectId();

                    checkFriendAdded(friendId);
//                    addFriend(friendId);
                }
            }
        });
    }

    private void checkFriendAdded(final String friendId) {
        AVQuery<AVObject> queryUserId1 = new AVQuery<>(Constants.TABLE_NAME_FRIEND);
        queryUserId1.whereStartsWith(Constants.KEY_USER_ID, userId);
        AVQuery<AVObject> queryUserId2 = new AVQuery<>(Constants.TABLE_NAME_FRIEND);
        queryUserId2.whereEndsWith(Constants.KEY_USER_ID, userId);
        AVQuery<AVObject> queryFriendId1 = new AVQuery<>(Constants.TABLE_NAME_FRIEND);
        queryFriendId1.whereStartsWith(Constants.KEY_FRIEND_ID, friendId);
        AVQuery<AVObject> queryFriendId2 = new AVQuery<>(Constants.TABLE_NAME_FRIEND);
        queryFriendId2.whereEndsWith(Constants.KEY_FRIEND_ID, friendId);

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(queryUserId1, queryUserId2, queryFriendId1, queryFriendId2));
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.isEmpty()) {
                    Log.d(TAG, "username is not added!");
                    addFriend(friendId);
                } else {
                    Log.e(TAG, "username is already added!");
                    ToastUtil.showToastShort(FriendActivity.this, "username is already added!");
                }
            }
        });

    }

    private void addFriend(String friendId) {
//        String friendId = avObject.getObjectId();

        AVObject object = new AVObject(Constants.TABLE_NAME_FRIEND);
        object.put(Constants.KEY_FRIEND_ID, friendId);
        object.put(Constants.KEY_USER_ID, userId);
        object.saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d(TAG, "save success!");
                    ToastUtil.showToastShort(FriendActivity.this, "add friend success!");
                    closeAddDialog();
                    // refresh list
                    queryFriends();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_FRIEND_TO_CHOOSE:
                // 上传头像
                ArrayList<String> selectedPaths = data.getStringArrayListExtra(Constants.KEY_SELECTED_PATHS);
                String path = selectedPaths.get(0);
                upLoadHead(path);
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void upLoadHead(String path) {
        File file = new File(path);
        try {
            AVFile avFile = AVFile.withFile(file.getName(), file);
            AVObject avObject = AVObject.createWithoutData(Constants.TABLE_NAME_USER, userId);
            avObject.put(Constants.KEY_HEAD_PHOTO, avFile);
            avObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    if (e == null) {
                        Log.d(TAG, "upload head photo success!");
                        ToastUtil.showToastLong(FriendActivity.this, "upload success!");
                    } else {
                        Log.d(TAG, "upload head photo failed!");
                        ToastUtil.showToastLong(FriendActivity.this, "upload failed!");
                    }
                }
            });

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void closeAddDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
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

}
