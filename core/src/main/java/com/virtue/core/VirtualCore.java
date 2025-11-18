package com.virtue.core;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.virtue.core.helper.VirtualStorageManager;
import com.virtue.core.helper.VirtualPackageManager;
import com.virtue.core.server.VirtualServer;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Core virtual environment manager
 * Based on SandVXposed architecture
 */
public class VirtualCore {
    private static VirtualCore sInstance;
    private Context mContext;
    private boolean mInitialized = false;
    private VirtualPackageManager mPackageManager;

    private VirtualCore() {
    }

    public static VirtualCore get() {
        if (sInstance == null) {
            synchronized (VirtualCore.class) {
                if (sInstance == null) {
                    sInstance = new VirtualCore();
                }
            }
        }
        return sInstance;
    }

    public static void initialize(Context context) {
        get().startup(context);
    }

    private void startup(Context context) {
        if (mInitialized) {
            return;
        }
        mContext = context.getApplicationContext();
        
        // Initialize storage manager
        VirtualStorageManager.get().initialize(context);
        
        // Initialize server
        VirtualServer.get().initialize(context);
        
        // Initialize package manager
        mPackageManager = VirtualPackageManager.get();
        mPackageManager.initialize(context);
        
        mInitialized = true;
    }

    public Context getContext() {
        return mContext;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    /**
     * Install an APK into virtual environment
     */
    public boolean installApp(String apkPath, int flags) {
        if (!mInitialized || mPackageManager == null) {
            return false;
        }
        
        File apkFile = new File(apkPath);
        if (!apkFile.exists()) {
            return false;
        }
        
        try {
            // Parse APK
            PackageInfo packageInfo = mContext.getPackageManager()
                .getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES | PackageManager.GET_SERVICES);
            
            if (packageInfo == null) {
                return false;
            }
            
            // Install to virtual environment
            return mPackageManager.installPackage(apkPath, packageInfo, flags);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Uninstall an app from virtual environment
     */
    public boolean uninstallApp(String packageName) {
        if (!mInitialized || mPackageManager == null) {
            return false;
        }
        
        // Kill app if running
        killApp(packageName, 0);
        
        // Uninstall from virtual environment
        return mPackageManager.uninstallPackage(packageName);
    }

    /**
     * Launch an app in virtual environment
     */
    public boolean launchApp(String packageName, int userId) {
        if (!mInitialized || mPackageManager == null) {
            return false;
        }
        
        try {
            // Get launch intent
            Intent launchIntent = mPackageManager.getLaunchIntentForPackage(packageName);
            if (launchIntent == null) {
                return false;
            }
            
            // Start virtual process
            VirtualServer.ProcessRecord process = VirtualServer.get().startProcess(packageName, userId);
            if (process == null) {
                return false;
            }
            
            // Launch activity in virtual process
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(launchIntent);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all installed virtual apps
     */
    public List<ApplicationInfo> getInstalledApps(int flags) {
        if (!mInitialized || mPackageManager == null) {
            return new ArrayList<>();
        }
        
        return mPackageManager.getInstalledApplications(flags);
    }

    /**
     * Check if an app is running in virtual environment
     */
    public boolean isAppRunning(String packageName, int userId) {
        if (!mInitialized) {
            return false;
        }
        
        return VirtualServer.get().isProcessRunning(packageName, userId);
    }

    /**
     * Kill an app running in virtual environment
     */
    public void killApp(String packageName, int userId) {
        if (!mInitialized) {
            return;
        }
        
        VirtualServer.get().killProcess(packageName, userId);
    }

    /**
     * Check if running in virtual process
     */
    public static boolean isVirtualProcess() {
        // Check for virtual process marker
        String processName = android.os.Process.myPid() + "";
        return processName != null && processName.contains("_virtual");
    }

    /**
     * Get the virtual user ID for current process
     */
    public static int getVirtualUserId() {
        if (!isVirtualProcess()) {
            return -1;
        }
        // Extract user ID from process info
        return 0; // Default to user 0
    }
    
    /**
     * Get package manager
     */
    public VirtualPackageManager getPackageManager() {
        return mPackageManager;
    }
}
