# SandVXposed Integration Guide

This document explains how Virtue GMS implements concepts from SandVXposed and provides a roadmap for full integration.

## Overview

[SandVXposed](https://github.com/asLody/SandVXposed) is a virtual app framework that combines:
- **VirtualApp**: Virtual application engine for app sandboxing
- **SandHook**: Native hooking framework for method interception
- **Xposed Compatibility**: Ability to load and run Xposed modules

Virtue GMS follows the same architectural principles but with a modern tech stack.

## Core Components Mapping

### 1. Virtual Environment (VirtualApp → VirtueCore)

**SandVXposed Approach**:
```java
// VirtualApp initialization
VirtualCore.get().startup(context);
VirtualCore.get().installPackageAsUser(apkPath, userId);
VirtualCore.get().launchApp(packageName);
```

**Virtue GMS Equivalent**:
```java
// VirtualCore initialization
VirtualCore.initialize(context);
VirtualCore.get().installApp(apkPath, flags);
VirtualCore.get().launchApp(packageName, userId);
```

**Implementation Status**: ✅ Basic structure complete, needs APK parsing

### 2. Process Management

**SandVXposed**: Uses a stub process mechanism with different UIDs per virtual app

**Virtue GMS**: 
- `VirtualServer`: Manages all virtual processes
- `VirtualClient`: Runs in each virtual app process
- `ProcessRecord`: Tracks process lifecycle

**Key Files**:
- `core/src/main/java/com/virtue/core/server/VirtualServer.java`
- `core/src/main/java/com/virtue/core/client/VirtualClient.java`

**TODO**:
- [ ] Implement stub activity/service/provider
- [ ] Add binder proxy for IPC redirection
- [ ] Implement virtual process creation

### 3. Storage Isolation

**SandVXposed**: Redirects file system operations to virtual directories

**Virtue GMS**:
- `VirtualStorageManager`: Manages isolated storage per app
- `IORedirect` (native): Intercepts file operations at native layer

**Storage Structure**:
```
/data/data/com.virtue.gms.huawei/
└── virtual/
    ├── user_0/                    # Virtual user 0
    │   └── com.example.app/       # Virtual app
    │       ├── data/              # Private data
    │       ├── cache/             # Cache
    │       └── files/             # Files
    └── sdcard/                    # Virtual SD card
```

**Implementation Status**: ✅ Java layer complete, native hooks need implementation

### 4. Hook System

#### Java Hooks

**SandVXposed**: Uses reflection and dynamic proxy for Java method hooking

**Virtue GMS**:
```java
HookManager.get().hookMethod(
    ActivityManager.class,
    "startActivity",
    new MethodHook() {
        @Override
        protected void beforeHookedMethod(MethodHookParam param) {
            // Redirect activity start
        }
    }
);
```

**Key Classes**:
- `hook/src/main/java/com/virtue/hook/HookManager.java`
- `hook/src/main/java/com/virtue/hook/base/MethodHook.java`
- `hook/src/main/java/com/virtue/hook/providers/*Hook.java`

#### Native Hooks

**SandVXposed**: Uses SandHook for inline hooking at native level

**Virtue GMS**: Custom hook engine inspired by SandHook
```cpp
// Hook native function
HookEngine::getInstance().hookFunction(
    targetFunc,
    replaceFunc,
    &backupFunc
);
```

**Key Files**:
- `native/src/main/cpp/hook_engine.{cpp,h}`
- `native/src/main/cpp/native_bridge.cpp`

**TODO**:
- [ ] Implement inline hook algorithm
- [ ] Add trampoline generation
- [ ] Support ARM32/ARM64 architectures
- [ ] Add PLT/GOT hooking

### 5. System Service Hooks

**Critical Services to Hook**:

1. **ActivityManagerService**
   - `startActivity` - Redirect to virtual activities
   - `getRunningAppProcesses` - Filter virtual processes
   - `killBackgroundProcesses` - Handle virtual process lifecycle

2. **PackageManagerService**
   - `getInstalledPackages` - Include virtual packages
   - `getPackageInfo` - Provide virtual package info
   - `getApplicationInfo` - Return virtual app info

3. **ContentResolver**
   - `query` - Redirect to virtual content providers
   - `insert/update/delete` - Handle virtual data

4. **LocationManager**
   - `getLastKnownLocation` - Support location spoofing
   - `requestLocationUpdates` - Control location access

**Implementation Status**: ⚠️ Hook classes created, integration pending

### 6. Xposed Module Support

**SandVXposed Approach**:
1. Load Xposed modules as plugins
2. Provide XposedBridge API compatibility
3. Hook methods requested by modules
4. Forward callbacks to module code

**Virtue GMS Roadmap**:

#### Phase 1: XposedBridge Compatibility Layer
```java
public class XposedBridge {
    public static void hookMethod(Member method, XC_MethodHook callback) {
        // Convert to MethodHook and register
    }
    
    public static void hookAllMethods(Class<?> clazz, String methodName, 
                                     XC_MethodHook callback) {
        // Hook all overloads
    }
}
```

#### Phase 2: Module Loading
```java
public class XposedModuleLoader {
    public void loadModule(String modulePath) {
        // Load module APK
        // Find IXposedHookLoadPackage
        // Call handleLoadPackage
    }
}
```

#### Phase 3: API Implementation
- Implement all XposedBridge APIs
- Support XSharedPreferences
- Handle resource hooks

**TODO**:
- [ ] Create XposedBridge compatibility layer
- [ ] Implement module discovery
- [ ] Add module loading system
- [ ] Support resource redirection
- [ ] Implement XSharedPreferences

## Native Layer Architecture

### Hook Engine Implementation

Based on SandHook's approach:

```cpp
class HookEngine {
    // 1. Parse target function
    bool parseFunction(void* targetFunc);
    
    // 2. Create backup (trampoline)
    void* createTrampoline(void* targetFunc);
    
    // 3. Replace with jump
    bool installHook(void* targetFunc, void* replaceFunc);
    
    // 4. Restore original
    bool uninstallHook(void* targetFunc);
};
```

**Key Techniques**:
1. **Instruction Parsing**: Decode ARM/ARM64 instructions
2. **Trampoline Creation**: Copy original instructions + jump back
3. **Inline Patching**: Replace function start with jump
4. **Cache Flush**: Clear CPU instruction cache

### IO Redirection Implementation

```cpp
// Hook open() system call
int hooked_open(const char* pathname, int flags, mode_t mode) {
    // 1. Check if path needs redirection
    if (IORedirect::getInstance().shouldRedirect(pathname)) {
        // 2. Redirect to virtual path
        std::string newPath = IORedirect::getInstance().redirectPath(pathname);
        pathname = newPath.c_str();
    }
    
    // 3. Call original open
    return original_open(pathname, flags, mode);
}
```

**Functions to Hook**:
- `open`, `openat`
- `stat`, `lstat`, `fstat`
- `access`, `faccessat`
- `readlink`, `readlinkat`
- `mkdir`, `mkdirat`
- `unlink`, `unlinkat`
- `rename`, `renameat`

## Integration Checklist

### Core Features
- [x] Basic project structure
- [x] Module architecture
- [x] Virtual storage management
- [x] Process record system
- [ ] APK parsing and installation
- [ ] Virtual process creation
- [ ] Binder proxy system
- [ ] Activity lifecycle management

### Hook System
- [x] Hook manager architecture
- [x] Method hook base classes
- [ ] Java method hooking implementation
- [ ] Native function hooking
- [ ] System service hooks
- [ ] Binder transaction hooks

### Storage & IO
- [x] Virtual storage structure
- [x] IO redirection design
- [ ] Native IO hooks implementation
- [ ] Content provider redirection
- [ ] Shared preferences isolation

### Xposed Compatibility
- [ ] XposedBridge API
- [ ] Module loading system
- [ ] Resource hooks
- [ ] XSharedPreferences
- [ ] Module management UI

### UI & UX
- [x] Compose-based UI
- [x] App management screen
- [x] Environment management
- [x] Settings screen
- [ ] App installation flow
- [ ] Module management
- [ ] Advanced settings

## Testing Strategy

### Unit Tests
- Virtual storage operations
- Path redirection logic
- Hook registration
- Module loading

### Integration Tests
- Install virtual app
- Launch virtual app
- File operations in virtual environment
- System service calls
- Xposed module loading

### Manual Tests
1. Install popular app (WeChat, QQ, etc.)
2. Run in virtual environment
3. Verify isolation
4. Test Xposed modules
5. Performance testing

## Performance Considerations

### Memory
- Keep hook tables in memory
- Cache path redirections
- Optimize binder proxy

### CPU
- Minimize hook overhead
- Use fast path for unhook calls
- Optimize instruction parsing

### Storage
- Lazy create virtual directories
- Clean up unused virtual apps
- Compress app data when possible

## Security Considerations

1. **Process Isolation**: Each virtual app in separate process
2. **Storage Isolation**: Strict path validation
3. **Permission Control**: Virtual permission system
4. **Network Isolation**: Optional network restrictions
5. **IPC Security**: Validate all binder calls

## Known Limitations

1. **Android Version**: Currently targets Android 8.0+ (API 26+)
2. **Architecture**: ARM32/ARM64 (x86 not supported)
3. **Performance**: ~5-10% overhead for virtual apps
4. **Compatibility**: Some apps with strong anti-virtual detection may not work
5. **System Apps**: Cannot virtualize system apps

## References

- [SandVXposed GitHub](https://github.com/asLody/SandVXposed)
- [VirtualApp Analysis (Chinese)](https://blog.csdn.net/ganyao939543405/article/details/76146760)
- [SandHook Documentation](https://github.com/ganyao114/SandHook)
- [Android Virtual App Principles](https://www.jianshu.com/p/47b4b8d6c8b1)

## Contributing

To contribute to SandVXposed integration:

1. Study the original SandVXposed source code
2. Identify the component you want to implement
3. Follow the architecture patterns established in this project
4. Write tests for your implementation
5. Submit a pull request with clear documentation

## Next Steps

1. **Immediate**: Implement APK installation
2. **Short-term**: Complete Java hook system
3. **Mid-term**: Add Xposed module support
4. **Long-term**: Performance optimization and compatibility improvements
