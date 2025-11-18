package com.virtue.core.os;

/**
 * Information about a virtual app
 */
public class VirtualAppInfo {
    public int id;
    public String name;
    public String packageName;
    public String versionName;
    public int versionCode;
    public int userId;
    public boolean isRunning;
    public long installTime;
    public long updateTime;

    public VirtualAppInfo(String packageName, String name) {
        this.packageName = packageName;
        this.name = name;
        this.userId = 0;
        this.isRunning = false;
        this.installTime = System.currentTimeMillis();
        this.updateTime = installTime;
    }
}
