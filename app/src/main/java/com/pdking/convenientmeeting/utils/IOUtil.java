package com.pdking.convenientmeeting.utils;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * @author liupeidong
 * Created on 2019/2/16 16:17
 */
public class IOUtil {

    public static void copyFileNewThread(File source, File dest) throws IOException {
        FileChannel inputChannel = null;
        FileChannel outputChannel = null;
        if (source == null || !source.exists()) {
            return;
        }
        try {
            if (dest.exists()) {
                dest.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        try {
            inputChannel = new FileInputStream(source).getChannel();
            outputChannel = new FileOutputStream(dest).getChannel();
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        } finally {
            inputChannel.close();
            outputChannel.close();
        }
    }

    public static void copyFile(File source, File dest) throws IOException {
        copyFileNewThread(source, dest);
    }
}
