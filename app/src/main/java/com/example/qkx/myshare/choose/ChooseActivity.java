package com.example.qkx.myshare.choose;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.example.qkx.myshare.Constants;
import com.example.qkx.myshare.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by qkx on 16/6/2.
 */
public class ChooseActivity extends AppCompatActivity{

    private static final String TAG = "ChooseActivity";

    private List<String> mData;

//    private GridViewAdapter mAdapter;
    private RecyclerAdapter mAdapter;

//    @Bind(R.id.gridView)
//    GridView gridView;

    @Bind(R.id.recycle_list)
    RecyclerView recyclerView;

    @Bind(R.id.toolbar_choose)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        ButterKnife.bind(this);

        init();
    }

    private void init() {
        mData = getImagePaths();
//        mAdapter = new GridViewAdapter(this, mData, gridView);
//        gridView.setAdapter(mAdapter);

        mAdapter = new RecyclerAdapter(this, mData, recyclerView);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_18pt_2x);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        toolbar.inflateMenu(R.menu.menu_choose);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_finish:
                        chooseFinished();
                        break;
                }
                return true;
            }
        });

    }

    private void chooseFinished() {
        Intent intent = new Intent();
        List<String> selectedPaths = mAdapter.getSelectedPaths();
        intent.putStringArrayListExtra(Constants.KEY_SELECTED_PATHS, (ArrayList<String>) selectedPaths);
//        setResult(RESULT_OK, intent);
        setResult(RESULT_OK, intent);

        finish();
    }

    private List<String> getImagePaths() {
        List<String> data = new ArrayList<>();
        Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Images.Media.DATE_MODIFIED);

        if (cursor == null) {
            return data;
        }

        if (cursor.moveToFirst()) {
            do {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                data.add(path);

            } while (cursor.moveToNext());
        }

        return data;
    }

}
