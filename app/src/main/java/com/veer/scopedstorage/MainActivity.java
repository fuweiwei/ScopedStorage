package com.veer.scopedstorage;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.RecoverableSecurityException;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    public static final String TAG = "ScopedStorage";
    public static final int REQUEST_DELETE_PERMISSION  = 101;
    public static final int FILE_REQUEST_CODE  = 102;
    List<Uri> mUris = new ArrayList<>();
    private Button mBtnInside;
    private Button mBtnOutsidePrivate;
    private Button mBtnOutsidePublic;
    private Button mBtnOutsidePublicMedia;
    private Button mBtnOutsidePublicMediaLook;
    private Button mBtnOutsidePublicMediaEdit;
    private Button mBtnOutsidePublicFile;
    private Button mBtnOutsidePublicFileLook;
    private String mContent = "test123456789";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnInside = findViewById(R.id.btn_inside);
        mBtnOutsidePrivate = findViewById(R.id.btn_outside_private);
        mBtnOutsidePublic = findViewById(R.id.btn_outside_public);
        mBtnOutsidePublicMedia = findViewById(R.id.btn_outside_public_media);
        mBtnOutsidePublicMediaLook = findViewById(R.id.btn_outside_public_media_look);
        mBtnOutsidePublicMediaEdit = findViewById(R.id.btn_outside_public_media_edit);
        mBtnOutsidePublicFile = findViewById(R.id.btn_outside_public_file);
        mBtnOutsidePublicFileLook = findViewById(R.id.btn_outside_public_file_look);

        mBtnInside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = getFilesDir().getAbsolutePath();
                String fileString = path+ File.separator +"test.txt";
                File file = new File(fileString);
                boolean success = Utils.writeFileFromString(file,mContent,true);
                Toast.makeText(MainActivity.this,"存储："+success,Toast.LENGTH_SHORT).show();

            }
        });
        mBtnOutsidePrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = getExternalFilesDir("").getAbsolutePath();
                String fileString = path+ File.separator +"test.txt";
                File file = new File(fileString);
                boolean success = Utils.writeFileFromString(file,mContent,true);
                Toast.makeText(MainActivity.this,"存储："+success,Toast.LENGTH_SHORT).show();

            }
        });
        mBtnOutsidePublic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.permission(PermissionConstants.STORAGE).callback(new PermissionUtils.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/test";
                        String fileString = path+ File.separator +"test.txt";
                        File file = new File(fileString);
                        boolean success = Utils.writeFileFromString(file,mContent,true);
                        Toast.makeText(MainActivity.this,"存储："+success,Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onDenied() {
                        Toast.makeText(MainActivity.this,"权限悲剧",Toast.LENGTH_SHORT).show();

                    }
                }).request();

            }
        });

        //存储Media
        mBtnOutsidePublicMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_test);
                String name = "pic_test.jpg";
                String mimeType = "image/jpeg";
                Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                String path = Environment.DIRECTORY_PICTURES+File.separator+"test1/";
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME ,name);
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE ,mimeType);
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH ,path);
                Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                if(uri!=null){
                    try {
                        OutputStream outputStream = getContentResolver().openOutputStream(uri);
                        bitmap.compress(compressFormat, 100, outputStream);
                        outputStream.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        //查看Media
        mBtnOutsidePublicMediaLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //没有存储权限只能访问自己的问题，有权限可以访问其他图片
                Cursor cursor = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        MediaStore.Images.Media.DATE_ADDED + " DESC");
                List<Uri> uris = new ArrayList<>();
                if(cursor!=null){
                    while(cursor.moveToNext()){
                        long id = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns._ID));
                        String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
                        Uri uri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                        Log.d(TAG,"uri:---"+uri.toString());
                        uris.add(uri);
                    }
                }
                mUris = uris;
                Log.d(TAG,"uris--size"+uris.size());
            }
        });

        //修改删除Media
        mBtnOutsidePublicMediaEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMedia();
            }
        });


        mBtnOutsidePublicFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputStream is = new ByteArrayInputStream(mContent.getBytes());
                OutputStream outputStream = null;
                String name = "test1.txt";
                String path = Environment.DIRECTORY_DOWNLOADS+File.separator+"test/";
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME ,name);
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH ,path);
                Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
                if(uri!=null){
                    try {
                        outputStream = getContentResolver().openOutputStream(uri);
                        byte data[] = new byte[1024];
                        for (int len; (len = is.read(data)) != -1; ) {
                            outputStream.write(data, 0, len);
                        }
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            is.close();
                            outputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
        });
        mBtnOutsidePublicFileLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("application/pdf");
                startActivityForResult(intent, FILE_REQUEST_CODE);
            }
        });
    }

    private void deleteMedia(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                Uri uri = mUris.get(0);
                long row = getContentResolver().delete(uri, null, null);
                if (row > 0) {
                    Log.d(TAG,"删除成功");
                }
            }catch (SecurityException securityException ){
                RecoverableSecurityException recoverableSecurityException = (RecoverableSecurityException) securityException;
                try {
                    startIntentSenderForResult(recoverableSecurityException.getUserAction().getActionIntent().getIntentSender(),REQUEST_DELETE_PERMISSION,null,0,0,0,null );
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK &&
                requestCode == REQUEST_DELETE_PERMISSION
        ) {
            deleteMedia();
        }
        if (resultCode == Activity.RESULT_OK &&
                requestCode == FILE_REQUEST_CODE
        ) {
            Uri uri = null;
            if (data != null) {
                uri = data.getData();
                Log.i(TAG, "Uri: " + uri.toString());
            }
        }
    }
}