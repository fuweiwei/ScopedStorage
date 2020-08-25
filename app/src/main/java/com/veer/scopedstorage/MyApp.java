package com.veer.scopedstorage;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * <li>Package: com.zhaogangandroid10scopedstorage</li>
 * <li>Author: weiwei.fu</li>
 * <li>Date:  2020/8/24</li>
 * <li>Description: </li>
 */
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
