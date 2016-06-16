package com.example.qkx.myshare.choose;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.example.qkx.myshare.R;
import com.example.qkx.myshare.utils.NativeImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by qkx on 16/6/3.
 */
public class RecyclerAdapter extends RecyclerView.Adapter {
    private static final String TAG = "RecyclerAdapter";

    private Context mContext;
    private List<String> mData;
    private LayoutInflater mInflater;
    private RecyclerView parent;

    private Map<Integer, Boolean> selectedMap;

    public RecyclerAdapter(Context context, List<String> data, RecyclerView recyclerView) {
        this.mContext = context;
        this.mData = data;
        this.mInflater = LayoutInflater.from(context);
        this.parent = recyclerView;
        this.selectedMap = new HashMap<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mInflater.inflate(R.layout.view_item_choose, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(v);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        String path = mData.get(position);
        Log.d("path:", path);

        final ItemViewHolder viewHolder = (ItemViewHolder) holder;

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                selectedMap.put(position, isChecked);
            }
        });

        viewHolder.imageView.setTag(path);
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.checkBox.setChecked(!viewHolder.checkBox.isChecked());
            }
        });

        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
//        int width = wm.getDefaultDisplay().getWidth();
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels / 3;

//        Bitmap mBitmap = NativeImageLoader.getInstance().loadNativeImage(path, new NativeImageLoader.MyPoint(100, 100),
        Bitmap mBitmap = NativeImageLoader.getInstance().loadNativeImage(path, new NativeImageLoader.MyPoint(width, width),
                new NativeImageLoader.NativeImageCallback() {
                    @Override
                    public void onImageLoad(Bitmap bitmap, String path) {
                        ImageView imageView = (ImageView) parent.findViewWithTag(path);
                        if (bitmap != null && imageView != null) {
                            imageView.setImageBitmap(bitmap);
                        }
                    }
                });
        if (mBitmap != null) {
            viewHolder.imageView.setImageBitmap(mBitmap);
        } else {
            viewHolder.imageView.setImageResource(R.drawable.loading);
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public List<String> getSelectedPaths() {
        List<String> list = new ArrayList<>();
        for (Map.Entry<Integer, Boolean> entry : selectedMap.entrySet()) {
            if (entry.getValue()) {
                list.add(mData.get(entry.getKey()));
            }
        }
        return list;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public CheckBox checkBox;
        public ItemViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_photo);
            checkBox = (CheckBox) itemView.findViewById(R.id.chk_choose);
        }
    }

}
