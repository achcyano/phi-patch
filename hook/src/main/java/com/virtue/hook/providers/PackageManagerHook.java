package com.virtue.hook.providers;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import com.virtue.hook.base.MethodHook;
import com.virtue.core.VirtualCore;
import com.virtue.core.os.VirtualAppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Hook for PackageManager service
 * Provides virtual package information
 */
public class PackageManagerHook {
    
    public static class GetInstalledPackagesHook extends MethodHook {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            // Return virtual installed packages
            if (param.result instanceof List) {
                @SuppressWarnings("unchecked")
                List<PackageInfo> systemPackages = (List<PackageInfo>) param.result;
                
                // Get virtual packages
                VirtualCore core = VirtualCore.get();
                if (core.isInitialized()) {
                    List<ApplicationInfo> virtualApps = core.getInstalledApps(0);
                    
                    // Merge system and virtual packages
                    List<PackageInfo> mergedPackages = new ArrayList<>(systemPackages);
                    for (ApplicationInfo appInfo : virtualApps) {
                        PackageInfo pkgInfo = new PackageInfo();
                        pkgInfo.packageName = appInfo.packageName;
                        pkgInfo.applicationInfo = appInfo;
                        mergedPackages.add(pkgInfo);
                    }
                    param.result = mergedPackages;
                }
            }
        }
    }

    public static class GetPackageInfoHook extends MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            String packageName = (String) param.args[0];
            // Check if this is a virtual package
            VirtualCore core = VirtualCore.get();
            if (core.isInitialized() && core.getPackageManager() != null) {
                VirtualAppInfo virtualApp = core.getPackageManager().getAppInfo(packageName);
                if (virtualApp != null) {
                    // Return virtual package info
                    PackageInfo pkgInfo = new PackageInfo();
                    pkgInfo.packageName = packageName;
                    pkgInfo.versionName = virtualApp.versionName;
                    pkgInfo.versionCode = virtualApp.versionCode;
                    
                    ApplicationInfo appInfo = new ApplicationInfo();
                    appInfo.packageName = packageName;
                    appInfo.name = virtualApp.name;
                    pkgInfo.applicationInfo = appInfo;
                    
                    param.setResult(pkgInfo);
                }
            }
        }
    }

    public static class GetApplicationInfoHook extends MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            String packageName = (String) param.args[0];
            // Return virtual application info
            VirtualCore core = VirtualCore.get();
            if (core.isInitialized() && core.getPackageManager() != null) {
                VirtualAppInfo virtualApp = core.getPackageManager().getAppInfo(packageName);
                if (virtualApp != null) {
                    // Provide virtual app info
                    ApplicationInfo appInfo = new ApplicationInfo();
                    appInfo.packageName = packageName;
                    appInfo.name = virtualApp.name;
                    param.setResult(appInfo);
                }
            }
        }
    }
}
