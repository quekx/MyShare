package com.example.qkx.myshare.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by qkx on 16/6/1.
 */
public class NativeImageLoader {
    private static NativeImageLoader mInstance;

    private LruCache<String, Bitmap> mMemoryCache;

    private ExecutorService mImageThreadPool;

    public NativeImageLoader() {
        mImageThreadPool = Executors.newFixedThreadPool(1);

        int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        int cacheSize = maxMemory / 4;
        mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
    }

    public static NativeImageLoader getInstance() {
        if (mInstance == null) {
            synchronized (NativeImageLoader.class) {
                if (mInstance == null) {
                    mInstance = new NativeImageLoader();
                }
            }
        }
        return mInstance;
    }

    public Bitmap loadNativeImage(String path, NativeImageCallback callback) {
        return loadNativeImage(path, null, callback);
    }

    public Bitmap loadNativeImage(final String path, final MyPoint point, final NativeImageCallback callback) {

        Bitmap bitmap = getBitmapFromMemCache(path);

        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                callback.onImageLoad((Bitmap) msg.obj, path);
                super.handleMessage(msg);
            }
        };

        if (bitmap == null) {
            mImageThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap mBitmap = decodeThumbBitmapFromFile(path, point == null ? 0 : point.width, point == null ? 0 : point.height);
                    Message msg = handler.obtainMessage();
                    msg.obj = mBitmap;
                    handler.sendMessage(msg);

                    addBitmapTpMenCache(path, mBitmap);
                }
            });
        }
        return bitmap;
    }

    private Bitmap decodeThumbBitmapFromFile(String path, int viewWidth, int viewHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 为true时,只获取参数,不读取图片进内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        // 设置缩放
        options.inSampleSize = computeScale(options, viewWidth, viewHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
        int inSampleSize = 1;
        if (viewWidth == 0 || viewHeight == 0) {
            return inSampleSize;
        }

        int bitmapWidth = options.outWidth;
        int bitmapHeight = options.outHeight;

        // 假如Bitmap的宽度或高度大于我们设定图片的View的宽高，则计算缩放比例
        if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
            int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
            int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

            // 为了保证图片不缩放变形，我们取宽高比例最小的那个
            inSampleSize = widthScale < heightScale ? widthScale : heightScale;
        }
        return inSampleSize;
    }

    private Bitmap getBitmapFromMemCache(String path) {
        return mMemoryCache.get(path);
    }

    private void addBitmapTpMenCache(String path, Bitmap bitmap) {
        if (mMemoryCache.get(path) == null && bitmap != null) {
            mMemoryCache.put(path, bitmap);
        }
    }


    public static class MyPoint {
        public int width;
        public int height;

        public MyPoint(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }

    public interface NativeImageCallback {
        void onImageLoad(Bitmap bitmap, String path);
    }

}
