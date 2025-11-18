package com.virtue.hook.providers;

import android.app.ActivityManager;
import com.virtue.hook.base.MethodHook;
import com.virtue.core.VirtualCore;

import java.util.ArrayList;
import java.util.List;

/**
 * Hook for ActivityManager service
 * Redirects activity management calls to virtual environment
 */
public class ActivityManagerHook {
    
    public static class StartActivityHook extends MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            // Redirect activity start to virtual environment
            // Check if the package is in virtual environment
            if (param.args.length > 0 && param.args[0] instanceof String) {
                String packageName = (String) param.args[0];
                VirtualCore core = VirtualCore.get();
                if (core.isInitialized() && core.getPackageManager().isInstalled(packageName)) {
                    // This is a virtual app, handle it in virtual environment
                    core.launchApp(packageName, 0);
                    param.setResult(0); // Success
                }
            }
        }
    }

    public static class GetRunningAppProcessesHook extends MethodHook {
        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            // Filter processes to only show virtual processes
            if (param.result instanceof List) {
                @SuppressWarnings("unchecked")
                List<ActivityManager.RunningAppProcessInfo> processes = 
                    (List<ActivityManager.RunningAppProcessInfo>) param.result;
                
                // Filter out system processes, only show virtual ones
                List<ActivityManager.RunningAppProcessInfo> filtered = new ArrayList<>();
                for (ActivityManager.RunningAppProcessInfo info : processes) {
                    // Keep virtual processes
                    if (info.processName.contains("_virtual")) {
                        filtered.add(info);
                    }
                }
                param.result = filtered;
            }
        }
    }

    public static class KillBackgroundProcessesHook extends MethodHook {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            // Redirect to virtual process management
            if (param.args.length > 0 && param.args[0] instanceof String) {
                String packageName = (String) param.args[0];
                VirtualCore core = VirtualCore.get();
                if (core.isInitialized() && core.getPackageManager().isInstalled(packageName)) {
                    // This is a virtual app, kill in virtual environment
                    core.killApp(packageName, 0);
                    param.setResult(null); // Handled
                }
            }
        }
    }
}
