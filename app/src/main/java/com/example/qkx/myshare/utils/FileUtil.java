package com.example.qkx.myshare.utils;

import java.io.Closeable;
import java.io.File;
import java.io.RandomAccessFile;

/**
 * Created by qkx on 16/6/4.
 */
public class FileUtil {

    // read file to byte[]
    public static byte[] readFile(File file) {
        RandomAccessFile rf = null;
        byte[] data = null;
        try {
            rf = new RandomAccessFile(file, "r");
            data = new byte[(int) rf.length()];
            rf.readFully(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(rf);
        }
        return data;
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}
