package com.example.qkx.myshare.utils;


import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

/**
 * Created by qkx on 16/6/1.
 */
public class ImageUtil {
//    public static List<String>

    public static void loadImage(Context context, String url, ImageView imageView) {
        Picasso.with(context)
                .load(url)
                .into(imageView);
    }

    public static void loadImage(Context context, File file, ImageView imageView) {
        Picasso.with(context)
                .load(file)
                .into(imageView);
    }

}
