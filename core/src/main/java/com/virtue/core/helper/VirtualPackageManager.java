package com.virtue.core.helper;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.virtue.core.os.VirtualAppInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages virtual packages
 */
public class VirtualPackageManager {
    private static VirtualPackageManager sInstance;
    private Context mContext;
    private Map<String, VirtualAppInfo> mInstalledApps = new ConcurrentHashMap<>();
    private File mAppsDir;

    private VirtualPackageManager() {
    }

    public static VirtualPackageManager get() {
        if (sInstance == null) {
            synchronized (VirtualPackageManager.class) {
                if (sInstance == null) {
                    sInstance = new VirtualPackageManager();
                }
            }
        }
        return sInstance;
    }

    public void initialize(Context context) {
        mContext = context;
        mAppsDir = new File(context.getFilesDir(), "vapps");
        if (!mAppsDir.exists()) {
            mAppsDir.mkdirs();
        }
        
        // Load installed apps
        loadInstalledApps();
    }

    private void loadInstalledApps() {
        File[] appDirs = mAppsDir.listFiles();
        if (appDirs != null) {
            for (File appDir : appDirs) {
                if (appDir.isDirectory()) {
                    String packageName = appDir.getName();
                    File apkFile = new File(appDir, "base.apk");
                    if (apkFile.exists()) {
                        try {
                            PackageInfo packageInfo = mContext.getPackageManager()
                                .getPackageArchiveInfo(apkFile.getAbsolutePath(), 0);
                            if (packageInfo != null) {
                                VirtualAppInfo appInfo = new VirtualAppInfo(
                                    packageName,
                                    packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString()
                                );
                                appInfo.versionName = packageInfo.versionName;
                                appInfo.versionCode = packageInfo.versionCode;
                                mInstalledApps.put(packageName, appInfo);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public boolean installPackage(String apkPath, PackageInfo packageInfo, int flags) {
        try {
            String packageName = packageInfo.packageName;
            
            // Create app directory
            File appDir = new File(mAppsDir, packageName);
            if (!appDir.exists()) {
                appDir.mkdirs();
            }
            
            // Copy APK
            File targetApk = new File(appDir, "base.apk");
            copyFile(new File(apkPath), targetApk);
            
            // Create app info
            VirtualAppInfo appInfo = new VirtualAppInfo(
                packageName,
                packageInfo.applicationInfo.loadLabel(mContext.getPackageManager()).toString()
            );
            appInfo.versionName = packageInfo.versionName;
            appInfo.versionCode = packageInfo.versionCode;
            
            mInstalledApps.put(packageName, appInfo);
            
            // Create storage directories
            VirtualStorageManager.get().getDataDir(packageName, 0);
            VirtualStorageManager.get().getCacheDir(packageName, 0);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean uninstallPackage(String packageName) {
        if (!mInstalledApps.containsKey(packageName)) {
            return false;
        }
        
        // Remove from map
        mInstalledApps.remove(packageName);
        
        // Delete app directory
        File appDir = new File(mAppsDir, packageName);
        deleteRecursive(appDir);
        
        // Clean up storage
        VirtualStorageManager.get().cleanupAppStorage(packageName, 0);
        
        return true;
    }

    public List<ApplicationInfo> getInstalledApplications(int flags) {
        List<ApplicationInfo> result = new ArrayList<>();
        for (VirtualAppInfo appInfo : mInstalledApps.values()) {
            ApplicationInfo applicationInfo = new ApplicationInfo();
            applicationInfo.packageName = appInfo.packageName;
            applicationInfo.name = appInfo.name;
            result.add(applicationInfo);
        }
        return result;
    }

    public Intent getLaunchIntentForPackage(String packageName) {
        VirtualAppInfo appInfo = mInstalledApps.get(packageName);
        if (appInfo == null) {
            return null;
        }
        
        // Create a simple launch intent
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setPackage(packageName);
        
        return intent;
    }

    public VirtualAppInfo getAppInfo(String packageName) {
        return mInstalledApps.get(packageName);
    }

    public boolean isInstalled(String packageName) {
        return mInstalledApps.containsKey(packageName);
    }

    private void copyFile(File source, File dest) throws IOException {
        FileInputStream fis = null;
        FileOutputStream fos = null;
        try {
            fis = new FileInputStream(source);
            fos = new FileOutputStream(dest);
            
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }
        } finally {
            if (fis != null) {
                try { fis.close(); } catch (IOException e) {}
            }
            if (fos != null) {
                try { fos.close(); } catch (IOException e) {}
            }
        }
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
