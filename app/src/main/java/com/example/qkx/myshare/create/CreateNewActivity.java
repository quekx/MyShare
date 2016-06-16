package com.example.qkx.myshare.create;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.avos.avoscloud.AVObject;
import com.example.qkx.myshare.Constants;
import com.example.qkx.myshare.R;
import com.example.qkx.myshare.choose.ChooseActivity;
import com.example.qkx.myshare.utils.AVUtil;
import com.example.qkx.myshare.utils.ToastUtil;
import com.example.qkx.myshare.utils.Tools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qkx on 16/5/31.
 */
public class CreateNewActivity extends AppCompatActivity {
    private static final String TAG = "CreateNewActivity";

    private SelectedAdapter mAdapter;

    private List<String> mData;

    private AVObject shareObject;

    private String uerId;
    private String nickname;

    private String currentPhotoPath;

    @Bind(R.id.toolbar_create)
    Toolbar toolbar;

    @Bind(R.id.recycle_selected)
    RecyclerView recyclerView;

    @Bind(R.id.edit_text)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        // 在Toolbar中初始化Menu
        toolbar.inflateMenu(R.menu.menu_create);
//        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_36pt_3x);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_18pt_2x);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_submit:
                        submit();
                        break;
                    case R.id.action_add:
                        addPhotos();
                        break;
                    case R.id.action_camera:
                        takePhotoFromCamera();
                        break;
                }
                return true;
            }
        });

        // 数据初始化
        mData = new ArrayList<>();
        mAdapter = new SelectedAdapter(this, mData, recyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));

        // 上传对象
        shareObject = new AVObject(Constants.TABLE_NAME_SHARE);

        // user id and nickname
        uerId = getIntent().getStringExtra(Constants.KEY_USER_ID);
        nickname = getIntent().getStringExtra(Constants.KEY_NICKNAME);
    }

    private void takePhotoFromCamera() {
        if (!Tools.hasSdcard()) {
            ToastUtil.showToastShort(this, "未找到存储卡，无法存储照片！");
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
//        currentPhotoPath = Environment.getExternalStorageState() + System.currentTimeMillis() + ".jpg";
        currentPhotoPath = file.getPath();
//        file.getAbsolutePath()
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
        startActivityForResult(intent, Constants.REQUEST_CAMERA);
    }

    private void submit() {
        Log.d(TAG, "submit!");

        String des = editText.getText().toString();
        List<String> photoPaths = mAdapter.getData();

//        testUpload(photoPaths);

        if (des.isEmpty()) {
            editText.setError("The description can not be empty!");
        } else {
            AVObject avObject = new AVObject(Constants.TABLE_NAME_SHARE);
            avObject.put(Constants.KEY_OWNER_ID, uerId);
            avObject.put(Constants.KEY_OWNER_NICKNAME, nickname);
            AVUtil.upLoadShare2(avObject, des, photoPaths, new AVUtil.UploadCallback() {
                @Override
                public void onUploadSuccess() {
                    onSubmitSuccess();
                }

                @Override
                public void onUploadFailed() {
                    onSubmitFailed();
                }
            });
//            AVUtil.upLoadShare(avObject, des, photoPaths, new AVUtil.UploadCallback() {
//                @Override
//                public void onUploadSuccess() {
//                    onSubmitSuccess();
//                }
//
//                @Override
//                public void onUploadFailed() {
//                    onSubmitFailed();
//                }
//            });
        }
    }

    private void testUpload(List<String> picPaths) {
        AVObject avObject = new AVObject(Constants.TABLE_NAME_SHARE);
        avObject.put(Constants.KEY_OWNER_ID, uerId);

//        ArrayList<String> images = new ArrayList<>();
////        ArrayList<AVFile> avFiles = new ArrayList<>();
//        for (int i = 0; i < picPaths.size(); i++) {
//            try {
//                File file = new File(mData.get(i));
//                final AVFile avFile = AVFile.withFile(file.getName(), file);
////                avFiles.add(avFile);
//                images.add(file.getName());
//                avFile.saveInBackground(new SaveCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        if (e == null) {
//                            Log.d(TAG, "url --> " + avFile.getUrl());
//                        } else {
//                            Log.e(TAG, e.getMessage());
//                        }
//                    }
//                });
//            } catch (FileNotFoundException e) {
//                Log.d(TAG, e.getMessage());
//                e.printStackTrace();
//            }
//        }
////        avObject.put(Constants.KEY_IMAGES, avFiles);
//        avObject.put(Constants.KEY_IMAGES, images);
//        Log.d(TAG, "saving!");
//        avObject.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(AVException e) {
//                if (e == null) {
//                    Log.d(TAG, "save success!");
//                } else {
//                    Log.e(TAG, e.getMessage());
//                }
//            }
//        });
    }


    private void onSubmitSuccess() {
        ToastUtil.showToastShort(this, "提交成功!");

        setResult(RESULT_OK);
        finish();
    }

    private void onSubmitFailed() {
        ToastUtil.showToastShort(this, "提交失败!");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case Constants.REQUEST_CREATE_NEW_TO_CHOOSE:
                ArrayList<String> selectedPaths = data.getStringArrayListExtra(Constants.KEY_SELECTED_PATHS);
                mAdapter.addData(selectedPaths);
                mAdapter.notifyDataSetChanged();
                break;
            case Constants.REQUEST_CAMERA:
                mAdapter.addData(currentPhotoPath);
                mAdapter.notifyDataSetChanged();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void addPhotos() {
        Intent intent = new Intent(this, ChooseActivity.class);
        startActivityForResult(intent, Constants.REQUEST_CREATE_NEW_TO_CHOOSE);
    }
}
