package com.virtue.core.server;

import android.content.Context;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Server-side virtual environment manager
 * Manages all virtual processes and environments
 */
public class VirtualServer {
    private static VirtualServer sInstance;
    private Context mContext;
    private Map<String, ProcessRecord> mProcessMap = new ConcurrentHashMap<>();

    private VirtualServer() {
    }

    public static VirtualServer get() {
        if (sInstance == null) {
            synchronized (VirtualServer.class) {
                if (sInstance == null) {
                    sInstance = new VirtualServer();
                }
            }
        }
        return sInstance;
    }

    public void initialize(Context context) {
        mContext = context;
    }

    public ProcessRecord startProcess(String packageName, int userId) {
        String key = packageName + ":" + userId;
        
        ProcessRecord record = mProcessMap.get(key);
        if (record != null && record.isAlive()) {
            return record;
        }

        // Create new process record
        record = new ProcessRecord(packageName, userId);
        mProcessMap.put(key, record);
        
        // TODO: Start the actual virtual process
        
        return record;
    }

    public void killProcess(String packageName, int userId) {
        String key = packageName + ":" + userId;
        ProcessRecord record = mProcessMap.remove(key);
        
        if (record != null) {
            record.kill();
        }
    }

    public boolean isProcessRunning(String packageName, int userId) {
        String key = packageName + ":" + userId;
        ProcessRecord record = mProcessMap.get(key);
        return record != null && record.isAlive();
    }

    public static class ProcessRecord {
        public final String packageName;
        public final int userId;
        public int pid = -1;
        private boolean alive = false;

        public ProcessRecord(String packageName, int userId) {
            this.packageName = packageName;
            this.userId = userId;
        }

        public boolean isAlive() {
            return alive;
        }

        public void kill() {
            if (pid > 0 && alive) {
                try {
                    android.os.Process.killProcess(pid);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            alive = false;
        }
    }
}
