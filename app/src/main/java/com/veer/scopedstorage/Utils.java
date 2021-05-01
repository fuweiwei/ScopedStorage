package com.veer.scopedstorage;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.RequiresApi;

import com.blankj.utilcode.util.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

/**
 * <li>Package: com.zhaogangandroid10scopedstorage</li>
 * <li>Author: weiwei.fu</li>
 * <li>Date:  2020/8/24</li>
 * <li>Description: </li>
 */
public class Utils {
    public final static String VEER_PHOTOS_OUTSIDE_STORE = Environment.DIRECTORY_PICTURES+ File.separator+"veer/";
    public final static String VEER_VIDEO_OUTSIDE_STORE = Environment.DIRECTORY_MOVIES+ File.separator+"veer/";
    public final static String VEER_FILE_OUTSIDE_STORE = Environment.DIRECTORY_DOCUMENTS+ File.separator+"veer/";
    public static boolean writeFileFromString(final File file,
                                              final String content,
                                              final boolean append) {
        if (file == null || content == null) return false;
        if (!createOrExistsFile(file)) return false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file, append));
            bw.write(content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private static boolean createOrExistsFile(final File file) {
        if (file == null) return false;
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }


    /**
     * 保存图片到相册
     * @param bitmap
     * @param fileName
     * @return
     */
    public static boolean saveBitmapPhotos(Bitmap bitmap, String fileName) {
        boolean result = false;
        String name = fileName;
        //        String mimeType = "image/jpeg";
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        String path = VEER_PHOTOS_OUTSIDE_STORE;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME ,name);
        //        contentValues.put(MediaStore.MediaColumns.MIME_TYPE ,mimeType);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH ,path);
        Uri uri = com.blankj.utilcode.util.Utils.getApp().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if(uri!=null){
            try {
                OutputStream outputStream = com.blankj.utilcode.util.Utils.getApp().getContentResolver().openOutputStream(uri);
                bitmap.compress(compressFormat, 100, outputStream);
                outputStream.close();
                result = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                bitmap.recycle();
            }
        }
        return result;
    }

    /**
     * 保存视频到相册
     * @param videoFile
     * @param fileName
     * @return
     */
    public static boolean saveVideoPhotos(File videoFile, String fileName) {
        boolean result = false;
        if(!FileUtils.isFileExists(videoFile))return result;
        String name = fileName;
        String path = VEER_VIDEO_OUTSIDE_STORE;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME ,name);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH ,path);
        Uri uri = null;
        try {
            uri = com.blankj.utilcode.util.Utils.getApp().getContentResolver().insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(uri!=null){
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(videoFile));
                OutputStream outputStream = com.blankj.utilcode.util.Utils.getApp().getContentResolver().openOutputStream(uri);
                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                byte[] buffer = new byte[1024];
                int bytes = bis.read(buffer);
                while (bytes>=0){
                    bos.write(buffer,0,bytes);
                    bos.flush();
                    bytes = bis.read(buffer);
                }
                bos.close();
                bis.close();
                result = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

            }
        }
        return result;
    }
    /**
     * 保存文件到外部存储  Android 10 以上可用 10以下还是使用之前的存储
     * @param file
     * @param fileName
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static boolean saveFileOutSide(File file, String fileName) {
        boolean result = false;
        if(!FileUtils.isFileExists(file))return result;
        String name = fileName;
        String path = VEER_FILE_OUTSIDE_STORE;
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME ,name);
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH ,path);
        Uri uri = null;
        try {
            uri = com.blankj.utilcode.util.Utils.getApp().getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
        }catch (Exception e){
            e.printStackTrace();
        }
        if(uri!=null){
            try {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
                OutputStream outputStream = com.blankj.utilcode.util.Utils.getApp().getContentResolver().openOutputStream(uri);
                BufferedOutputStream bos = new BufferedOutputStream(outputStream);
                byte[] buffer = new byte[1024];
                int bytes = bis.read(buffer);
                while (bytes>=0){
                    bos.write(buffer,0,bytes);
                    bos.flush();
                    bytes = bis.read(buffer);
                }
                bos.close();
                bis.close();
                result = true;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {

            }
        }
        return result;
    }
}
