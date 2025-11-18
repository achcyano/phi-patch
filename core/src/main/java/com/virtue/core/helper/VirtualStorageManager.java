package com.virtue.core.helper;

import android.content.Context;
import java.io.File;

/**
 * Manages virtual storage for each virtual app
 */
public class VirtualStorageManager {
    private static VirtualStorageManager sInstance;
    private File mVirtualRoot;

    private VirtualStorageManager() {
    }

    public static VirtualStorageManager get() {
        if (sInstance == null) {
            synchronized (VirtualStorageManager.class) {
                if (sInstance == null) {
                    sInstance = new VirtualStorageManager();
                }
            }
        }
        return sInstance;
    }

    public void initialize(Context context) {
        mVirtualRoot = new File(context.getFilesDir(), "virtual");
        if (!mVirtualRoot.exists()) {
            mVirtualRoot.mkdirs();
        }
    }

    /**
     * Get virtual data directory for an app
     */
    public File getDataDir(String packageName, int userId) {
        File userDir = new File(mVirtualRoot, "user_" + userId);
        File appDir = new File(userDir, packageName);
        File dataDir = new File(appDir, "data");
        
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        return dataDir;
    }

    /**
     * Get virtual cache directory for an app
     */
    public File getCacheDir(String packageName, int userId) {
        File userDir = new File(mVirtualRoot, "user_" + userId);
        File appDir = new File(userDir, packageName);
        File cacheDir = new File(appDir, "cache");
        
        if (!cacheDir.exists()) {
            cacheDir.mkdirs();
        }
        
        return cacheDir;
    }

    /**
     * Get virtual external storage directory for an app
     */
    public File getExternalDir(String packageName, int userId) {
        File sdcard = new File(mVirtualRoot, "sdcard");
        File androidData = new File(sdcard, "Android/data");
        File appDir = new File(androidData, packageName);
        
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        
        return appDir;
    }

    /**
     * Clean up storage for an app
     */
    public void cleanupAppStorage(String packageName, int userId) {
        File userDir = new File(mVirtualRoot, "user_" + userId);
        File appDir = new File(userDir, packageName);
        
        deleteRecursive(appDir);
    }

    private void deleteRecursive(File file) {
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursive(child);
                }
            }
        }
        file.delete();
    }
}
