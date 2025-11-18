package com.virtue.hook;

import android.content.Context;
import com.virtue.hook.base.MethodHook;
import com.virtue.native.NativeBridge;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hook manager for intercepting system calls
 * Based on SandVXposed hook architecture
 */
public class HookManager {
    private static HookManager sInstance;
    private Context mContext;
    private boolean mInitialized = false;
    private Map<String, MethodHook> mHookMap = new ConcurrentHashMap<>();

    private HookManager() {
    }

    public static HookManager get() {
        if (sInstance == null) {
            synchronized (HookManager.class) {
                if (sInstance == null) {
                    sInstance = new HookManager();
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
        
        // Initialize native hooks
        NativeBridge.initializeHooks();
        
        mInitialized = true;
    }

    public boolean isInitialized() {
        return mInitialized;
    }

    /**
     * Hook a Java method
     */
    public boolean hookMethod(Class<?> clazz, String methodName, Object... parameterTypesAndCallback) {
        if (!mInitialized) {
            return false;
        }
        
        try {
            // Extract parameter types and callback
            MethodHook callback = null;
            Class<?>[] paramTypes = new Class<?>[parameterTypesAndCallback.length - 1];
            
            for (int i = 0; i < parameterTypesAndCallback.length; i++) {
                if (parameterTypesAndCallback[i] instanceof MethodHook) {
                    callback = (MethodHook) parameterTypesAndCallback[i];
                } else if (i < paramTypes.length) {
                    paramTypes[i] = (Class<?>) parameterTypesAndCallback[i];
                }
            }
            
            if (callback == null) {
                return false;
            }
            
            // Find the method
            Method method;
            if (paramTypes.length == 0) {
                // Try all methods with this name
                Method[] methods = clazz.getDeclaredMethods();
                method = null;
                for (Method m : methods) {
                    if (m.getName().equals(methodName)) {
                        method = m;
                        break;
                    }
                }
            } else {
                method = clazz.getDeclaredMethod(methodName, paramTypes);
            }
            
            if (method == null) {
                return false;
            }
            
            // Store the hook
            String key = clazz.getName() + "#" + methodName;
            mHookMap.put(key, callback);
            
            // Make method accessible
            method.setAccessible(true);
            
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Hook a native function
     */
    public boolean hookNative(String libraryName, String symbolName, long newFunctionPtr) {
        return NativeBridge.hookNativeFunction(libraryName, symbolName, newFunctionPtr);
    }

    /**
     * Unhook a method
     */
    public boolean unhookMethod(Class<?> clazz, String methodName) {
        if (!mInitialized) {
            return false;
        }
        
        String key = clazz.getName() + "#" + methodName;
        mHookMap.remove(key);
        return true;
    }
    
    /**
     * Get hook for a method
     */
    public MethodHook getHook(String className, String methodName) {
        String key = className + "#" + methodName;
        return mHookMap.get(key);
    }
}
