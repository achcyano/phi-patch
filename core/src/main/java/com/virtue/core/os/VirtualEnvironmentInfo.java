package com.virtue.core.os;

/**
 * Information about a virtual environment
 */
public class VirtualEnvironmentInfo {
    public int id;
    public String name;
    public boolean isActive;
    public int isolationLevel;

    public static final int ISOLATION_STANDARD = 0;
    public static final int ISOLATION_ENHANCED = 1;
    public static final int ISOLATION_MAXIMUM = 2;

    public VirtualEnvironmentInfo(int id, String name, boolean isActive, int isolationLevel) {
        this.id = id;
        this.name = name;
        this.isActive = isActive;
        this.isolationLevel = isolationLevel;
    }
}
