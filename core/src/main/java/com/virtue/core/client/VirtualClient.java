package com.virtue.core.client;

import android.content.Context;
import android.os.Process;

/**
 * Client-side virtual environment manager
 * Runs in the virtual app process
 */
public class VirtualClient {
    private static VirtualClient sInstance;
    private Context mContext;
    private boolean mInVirtualProcess = false;
    private int mVirtualUserId = -1;
    private String mVirtualPackageName;

    private VirtualClient() {
    }

    public static VirtualClient get() {
        if (sInstance == null) {
            synchronized (VirtualClient.class) {
                if (sInstance == null) {
                    sInstance = new VirtualClient();
                }
            }
        }
        return sInstance;
    }

    public void initialize(Context context, int userId, String packageName) {
        mContext = context;
        mInVirtualProcess = true;
        mVirtualUserId = userId;
        mVirtualPackageName = packageName;
    }

    public boolean isInVirtualProcess() {
        return mInVirtualProcess;
    }

    public int getVirtualUserId() {
        return mVirtualUserId;
    }

    public String getVirtualPackageName() {
        return mVirtualPackageName;
    }

    public Context getContext() {
        return mContext;
    }

    public int getVirtualUid() {
        if (!mInVirtualProcess) {
            return Process.myUid();
        }
        // Virtual UID calculation based on user ID and package
        return mVirtualUserId * 100000 + 10000;
    }
}
