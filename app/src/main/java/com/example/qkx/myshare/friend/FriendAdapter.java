package com.example.qkx.myshare.friend;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.example.qkx.myshare.Constants;
import com.example.qkx.myshare.R;
import com.example.qkx.myshare.utils.AVUtil;
import com.example.qkx.myshare.utils.ImageUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by qkx on 16/6/6.
 */
public class FriendAdapter extends RecyclerView.Adapter {

    private static final String TAG = "FriendAdapter";

    private List<String> mData;

    private Context mContext;

    private LayoutInflater mInflater;

    public FriendAdapter(Context ctx) {
        this.mContext = ctx;
        this.mData = new ArrayList<>();
        this.mInflater = LayoutInflater.from(ctx);
    }

    public FriendAdapter(Context ctx, List<String> data) {
        this.mContext = ctx;
        this.mData = data;
        this.mInflater = LayoutInflater.from(ctx);
    }

    public void setData(List<String> friendIds) {
        this.mData = friendIds;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recyclerview_friend_item, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final FriendViewHolder friendViewHolder = (FriendViewHolder) holder;

        String friendId = mData.get(position);

        Log.d(TAG, "friendId --> " + friendId);
        AVQuery<AVObject> query = new AVQuery<>(Constants.TABLE_NAME_USER);
        query.getInBackground(friendId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject object, AVException e) {

                String nickname = object.getString(Constants.KEY_NICKNAME);
                friendViewHolder.tvFriendName.setText(nickname);

//                AVObject head = object.getAVObject(Constants.KEY_HEAD_PHOTO);
                AVFile head = object.getAVFile(Constants.KEY_HEAD_PHOTO);
                String headUrl = null;
                if (head != null) {
                    headUrl = head.getUrl();
                }
                if (headUrl != null) {
                    String thumbnail = AVUtil.getThumbnail(headUrl, 60, null);
                    ImageUtil.loadImage(mContext, thumbnail, friendViewHolder.ivFriendHead);
//                    ImageUtil.loadImage(mContext, headUrl, friendViewHolder.ivFriendHead);
                } else {
                    friendViewHolder.ivFriendHead.setImageResource(R.drawable.photo_head);
                }

                Log.d(TAG, "nickname --> " + nickname);
            }
        });
//        query.whereStartsWith(Constants.KEY_OBJECT_ID, friendId);
//        query.findInBackground(new FindCallback<AVObject>() {
//            @Override
//            public void done(List<AVObject> list, AVException e) {
//                if (e == null && !list.isEmpty()) {
//                    AVObject object = list.get(0);
//
//                    AVObject head = object.getAVObject(Constants.KEY_HEAD_PHOTO);
//                    String headUrl = null;
//                    if (head != null) {
//                        headUrl = head.getString(Constants.KEY_URL);
//                    }
//
//                    String nickname = object.getString(Constants.KEY_NICKNAME);
//
//                    if (headUrl != null) {
//                        ImageUtil.loadImage(mContext, headUrl, friendViewHolder.ivFriendHead);
//                    } else {
//                        friendViewHolder.ivFriendHead.setImageResource(R.drawable.photo_head);
//                    }
//                    friendViewHolder.tvFriendName.setText(nickname);
//                    Log.d(TAG, "nickname --> " + nickname);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivFriendHead;
        public TextView tvFriendName;

        public FriendViewHolder(View itemView) {
            super(itemView);
            ivFriendHead = (ImageView) itemView.findViewById(R.id.iv_friend_head);
            tvFriendName = (TextView) itemView.findViewById(R.id.tv_friend_name);
        }
    }
}
