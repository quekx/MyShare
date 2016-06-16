package com.example.qkx.myshare.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Jinsen on 15/4/24.
 */
public class ToastUtil {
    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showToastLong(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show();
    }

    public static void showToastShort(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static void showToastShort(Context context, int resId) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show();
    }
}
