package com.example.qkx.myshare.create;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.qkx.myshare.R;
import com.example.qkx.myshare.utils.NativeImageLoader;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by qkx on 16/6/3.
 */
public class SelectedAdapter extends RecyclerView.Adapter {
    private static final String TAG = "SelectedAdapter";

    private Context mContext;

    private List<String> mData;

    private Set<String> dataSet;

    private LayoutInflater mInflater;

    private RecyclerView parent;

    private int width;

    public SelectedAdapter(Context mContext, List<String> mData, RecyclerView recyclerView) {
        this.mContext = mContext;
        this.mData = mData;
        this.mInflater = LayoutInflater.from(mContext);
        this.parent = recyclerView;
        this.dataSet = new HashSet<>();

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        width = outMetrics.widthPixels / 4;
        Log.e(TAG, "width ---> " + width);
    }

    public void setData(List<String> data) {
        this.mData = data;
    }

    public void addData(List<String> data) {
        for (String s : data) {
            if (!dataSet.contains(s)) {
                this.mData.add(s);
                dataSet.add(s);
            }
        }
    }

    public void addData(String path) {
        this.mData.add(path);
        dataSet.add(path);
    }

    public List<String> getData() {
        return this.mData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_item_selected, parent, false);
        SelectedViewHolder selectedViewHolder = new SelectedViewHolder(view);
        return selectedViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        String path = mData.get(position);
        Log.d(TAG, path);


        SelectedViewHolder viewHolder = (SelectedViewHolder) holder;

        viewHolder.imageView.setTag(path);

        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, new NativeImageLoader.MyPoint(width, width),
                new NativeImageLoader.NativeImageCallback() {
                    @Override
                    public void onImageLoad(Bitmap bitmap, String path) {
                        ImageView imageView = (ImageView) parent.findViewWithTag(path);
                        if (bitmap != null && imageView != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                });

        if (bitmap != null) {
            viewHolder.imageView.setImageBitmap(bitmap);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.loading);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class SelectedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public SelectedViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_photo_selected);
        }
    }
}
