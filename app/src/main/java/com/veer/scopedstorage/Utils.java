package com.veer.scopedstorage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * <li>Package: com.zhaogangandroid10scopedstorage</li>
 * <li>Author: weiwei.fu</li>
 * <li>Date:  2020/8/24</li>
 * <li>Description: </li>
 */
public class Utils {
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
}
