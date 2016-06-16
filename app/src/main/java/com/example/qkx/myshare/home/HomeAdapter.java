package com.example.qkx.myshare.home;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.example.qkx.myshare.Constants;
import com.example.qkx.myshare.R;
import com.example.qkx.myshare.data.Share;
import com.example.qkx.myshare.utils.AVUtil;
import com.example.qkx.myshare.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qkx on 16/6/5.
 */
public class HomeAdapter extends RecyclerView.Adapter {

    private List<Share> mData;

    private Context mContext;

    private LayoutInflater mInflater;

    public HomeAdapter(Context ctx) {
        this.mContext = ctx;
        this.mData = new ArrayList<>();
        this.mInflater = LayoutInflater.from(ctx);
    }

    public HomeAdapter(Context ctx, List<Share> data) {
        this.mContext = ctx;
        this.mData = data;
        this.mInflater = LayoutInflater.from(ctx);
    }

    public void setData(List<Share> shares) {
        this.mData = shares;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycerview_home_item, parent, false);
        return new HomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Share share = mData.get(position);

        final HomeViewHolder homeViewHolder = (HomeViewHolder) holder;

        // description
        homeViewHolder.tvDescription.setText(share.description);

        // nickname
        String nickname = share.nickname;
        homeViewHolder.tvNickname.setText(nickname);

        // photo
        homeViewHolder.frameContainer.removeAllViews();
        for (int i = 0; i < share.urls.size(); i++) {
            String url = share.urls.get(i);
            String thumbnail = AVUtil.getThumbnail(url, 180, null);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(180, 180);
            params.leftMargin = i % 3 * 190;
            params.topMargin = i / 3 * 190;

            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            homeViewHolder.frameContainer.addView(imageView, params);

            ImageUtil.loadImage(mContext, thumbnail, imageView);

        }

        // head photo
        AVQuery<AVObject> headQuery = new AVQuery<>(Constants.TABLE_NAME_USER);
        Log.d("HOME", "OWNER ID -->" + share.ownerId);
        headQuery.getInBackground(share.ownerId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {
                if (e == null) {
                    AVFile head = object.getAVFile(Constants.KEY_HEAD_PHOTO);
                    String headUrl = null;
                    if (head != null) {
                        headUrl = head.getUrl();
                    }
                    if (headUrl != null) {
                        String thumbnail = AVUtil.getThumbnail(headUrl, 100, null);
                        ImageUtil.loadImage(mContext, thumbnail, homeViewHolder.ivHeadPhoto);
//                    ImageUtil.loadImage(mContext, headUrl, friendViewHolder.ivFriendHead);
                    } else {
                        homeViewHolder.ivHeadPhoto.setImageResource(R.drawable.photo_head);
                    }
                }
            }
        });

//        for (String url : share.urls) {
//            ImageView imageView = new ImageView(mContext);
//            homeViewHolder.frameContainer.addView(imageView);
////            FrameLayout.LayoutParams p
////            ImageUtil.loadImage(mContext, url, imageView);
//            String thumbnail = AVUtil.getThumbnail(url, 100, null);
////            ImageUtil.loadImage(mContext, url, imageView);
//            ImageUtil.loadImage(mContext, thumbnail, imageView);
//        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class HomeViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivHeadPhoto;
        public TextView tvNickname;
        public TextView tvDescription;
        public FrameLayout frameContainer;

        public HomeViewHolder(View itemView) {
            super(itemView);
            ivHeadPhoto = (ImageView) itemView.findViewById(R.id.iv_head_photo);
            tvNickname = (TextView) itemView.findViewById(R.id.tv_nickname);
            tvDescription = (TextView) itemView.findViewById(R.id.tv_description);
            frameContainer = (FrameLayout) itemView.findViewById(R.id.frame_image_container);
        }
    }
}
