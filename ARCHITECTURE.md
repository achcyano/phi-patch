# Architecture Documentation

## Overview

Virtue GMS is a multi-module virtual framework inspired by SandVXposed, designed to run Android applications in isolated virtual environments. The project uses a modern tech stack with Kotlin, Jetpack Compose, and JNI.

## Module Architecture

```
virtue-gms-huawei/
├── app/                    # Main application module
│   ├── ui/                 # Jetpack Compose UI components
│   │   ├── screens/        # Screen composables (Apps, Environments, Settings)
│   │   └── theme/          # Material Design 3 theme
│   └── data/               # ViewModels and data models
│
├── core/                   # Core virtual environment engine
│   ├── client/             # Client-side virtual environment
│   ├── server/             # Server-side process management
│   └── helper/             # Helper classes for virtualization
│
├── hook/                   # Hook framework
│   ├── base/               # Base hook classes
│   ├── providers/          # System service hooks
│   └── delegates/          # Method delegates
│
└── native/                 # JNI bridge and native code
    ├── cpp/                # C++ implementation
    │   ├── hook_engine.*   # Native hook engine
    │   ├── io_redirect.*   # IO redirection system
    │   └── native_bridge.* # JNI bridge
    └── java/               # Java JNI interface

```

## Key Components

### 1. VirtualCore (core module)

The core virtual environment manager that handles:
- Virtual app installation and management
- Virtual process creation and lifecycle
- Virtual user management
- App isolation and sandboxing

**Inspired by**: VirtualApp's core architecture

### 2. HookManager (hook module)

Manages system call interception:
- Java method hooking
- Native function hooking
- Binder service hooking
- System API redirection

**Inspired by**: SandVXposed's hook system

### 3. NativeBridge (native module)

JNI layer for native operations:
- Native hook engine (similar to SandHook)
- IO redirection hooks
- Process management hooks
- System call interception

**Key files**:
- `native_bridge.cpp`: JNI implementation
- `hook_engine.cpp`: Native hook engine
- `io_redirect.cpp`: File system redirection

### 4. UI Layer (app module)

Modern Compose-based UI:
- Material Design 3 components
- Bottom navigation with tabs
- Virtual app management screen
- Virtual environment configuration
- Settings panel

## Virtual Environment Flow

```
User launches virtual app
    ↓
VirtualCore.launchApp()
    ↓
Create isolated process
    ↓
Hook system services
    ↓
Redirect IO operations
    ↓
Start app in virtual environment
```

## Hook System

The hook system operates at multiple levels:

1. **Java Level**: Hook Java methods using reflection or bytecode manipulation
2. **Binder Level**: Intercept IPC calls to system services
3. **Native Level**: Hook native functions in shared libraries
4. **System Call Level**: Intercept system calls for IO operations

## Storage Isolation

Each virtual app has its own isolated storage:

```
/data/data/com.virtue.gms.huawei/
└── virtual/
    ├── user_0/                    # Virtual user 0
    │   └── com.example.app/       # Virtual app package
    │       ├── data/              # App private data
    │       ├── cache/             # App cache
    │       └── files/             # App files
    └── sdcard/                    # Virtual SD card
        └── Android/data/
            └── com.example.app/
```

## Process Model

Similar to SandVXposed:

1. **Main Process**: Runs the Virtue GMS UI
2. **Server Process**: Manages virtual environments
3. **Virtual Processes**: Run virtual apps (one per app)

## Technology Details

### Compose UI

- Material Design 3 theming
- Navigation Compose for screen navigation
- ViewModel for state management
- Kotlin Coroutines for async operations

### Native Layer

- C++17 standard
- Android NDK for JNI
- CMake build system
- Support for ARM64 and ARMv7

### Hook Engine

Inspired by SandHook, the hook engine provides:
- Inline hook for native functions
- Method redirection for Java methods
- Trampoline generation for original function calls

## Security Considerations

- Each virtual app runs in an isolated process
- File system access is redirected to virtual storage
- System service calls are intercepted and validated
- Network access can be controlled per virtual app

## Future Enhancements

1. **Xposed Module Support**: Load and run Xposed modules
2. **GMS/HMS Integration**: Support for Google/Huawei Mobile Services
3. **Device Emulation**: Spoof device information per virtual app
4. **Multiple Profiles**: Support multiple virtual environments
5. **App Cloning**: Clone apps with different configurations
6. **Plugin System**: Extensible plugin architecture

## References

- [SandVXposed](https://github.com/asLody/SandVXposed): Virtual app with SandHook
- [VirtualApp](https://github.com/asLody/VirtualApp): Virtual app engine
- [SandHook](https://github.com/ganyao114/SandHook): Android ART hook framework
