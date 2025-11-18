# Native Layer Implementation

## Overview

The native layer provides low-level hooking capabilities and file system redirection for the virtual environment. It's implemented in C++17 and uses JNI to interface with Java/Kotlin code.

## Components

### 1. Hook Engine (`hook_engine.cpp/h`)

Complete inline hooking implementation for ARM32 and ARM64 architectures.

#### Features

**Memory Management**
- `makeMemoryWritable()`: Changes memory protection to allow writing
- `restoreMemoryProtection()`: Restores original memory protection
- Handles page alignment automatically

**Trampoline Generation**
- Creates executable code that preserves original instructions
- Adds jump back to original function after hooked instructions
- Uses `mmap()` to allocate executable memory
- Supports both ARM32 and ARM64 instruction sets

**ARM64 Support**
- Parses branch instructions (B, BL, CBZ, CBNZ, TBZ, TBNZ)
- Calculates branch offsets correctly
- Generates proper branch instructions
- Handles both direct and indirect jumps

**Hook Installation**
- Replaces function start with jump to replacement
- For nearby functions: Uses B (branch) instruction (±128MB range)
- For far functions: Uses LDR + BR register jump (full 64-bit range)
- Clears instruction cache after modifications

**Hook Management**
- Stores original instructions for unhooking
- Maintains map of hooked functions
- Tracks trampolines for cleanup

#### ARM64 Implementation Details

```cpp
// Direct branch (within ±128MB)
targetInst[0] = makeARM64BranchInstruction(offset);

// Absolute jump (any distance)
targetInst[0] = 0x58000050;  // LDR X16, #8
targetInst[1] = 0xD61F0200;  // BR X16
*(uint64_t*)&targetInst[2] = replaceFunc; // 64-bit address
```

#### ARM32 Implementation Details

```cpp
// Indirect jump via PC-relative load
targetInst[0] = 0xE51FF004;  // LDR PC, [PC, #-4]
targetInst[1] = replaceFunc;  // 32-bit address
```

### 2. IO Redirect (`io_redirect.cpp/h`)

Advanced file system path redirection for virtual storage isolation.

#### Features

**Path Mapping**
- App-specific path redirection based on package name
- Generic fallback for unknown packages
- Path caching for performance
- System path exclusion

**Virtual Directories**
- `getDataDir()`: App's private data directory
- `getCacheDir()`: App's cache directory
- `getFilesDir()`: App's files directory
- `getExternalDir()`: App's external storage directory

**Redirection Rules**
```
/data/data/com.example.app/*      -> /virtual/user_0/com.example.app/data/*
/data/user/0/com.example.app/*    -> /virtual/user_0/com.example.app/data/*
/sdcard/Android/data/com.example.app/* -> /virtual/sdcard/Android/data/com.example.app/*
```

**System Path Protection**
- `/system/`, `/vendor/`, `/apex/` - Never redirected
- `/dev/`, `/proc/`, `/sys/` - System interfaces excluded
- Ensures system functionality remains intact

### 3. Native Bridge (`native_bridge.cpp`)

JNI interface between Java and native code.

#### Implemented Hooks

**File Operations**
- `hookOpen()`: Redirects file opening to virtual paths
- `hookAccess()`: Redirects file access checks
- `hookStat()`: Redirects file stat operations

**Process Management**
- `hookFork()`: Creates virtual processes with logging
- `hookExecve()`: Redirects binary execution to virtual paths
  - Converts Java String arrays to C arrays
  - Handles argument and environment variable passing
  - Cleans up allocated memory

**Hook Management**
- `nativeInitialize()`: Initializes hook engine
- `nativeInitializeHooks()`: Sets up hook infrastructure
- `nativeHookFunction()`: Installs hooks on native functions

## Technical Details

### Memory Safety

1. **Page Alignment**: All memory operations respect page boundaries
2. **Protection Restoration**: Memory protection is restored after modifications
3. **Cache Coherency**: Instruction cache is cleared after code modifications
4. **Cleanup**: Trampolines are freed when hooks are removed

### Thread Safety

- Singleton pattern for global instances
- No locking currently (assumes single-threaded hook installation)
- Hook maps use STL containers (not thread-safe by default)

### Performance

- Path caching reduces repeated redirections
- Trampolines are pre-allocated once
- Hook installation is O(1) after initial setup

### Error Handling

- All operations check for null pointers
- System call errors are logged
- Failed operations return appropriate error codes
- Resource cleanup in error paths

## Architecture-Specific Code

### ARM64 (AArch64)

```cpp
#ifdef __aarch64__
// ARM64-specific code
uint32_t* targetInst = static_cast<uint32_t*>(targetFunc);
// Branch instruction: B offset
targetInst[0] = 0x14000000 | ((offset >> 2) & 0x03FFFFFF);
#endif
```

### ARM32

```cpp
#ifndef __aarch64__
// ARM32-specific code
uint32_t* targetInst = static_cast<uint32_t*>(targetFunc);
// LDR PC, [PC, #-4]
targetInst[0] = 0xE51FF004;
targetInst[1] = replaceFunc;
#endif
```

## Usage Examples

### Hooking a Function

```cpp
// From Java
NativeBridge.nativeHookFunction("libc.so", "open", newFunctionPtr);

// Native implementation
void* targetFunc = HookEngine::getInstance().findSymbol("libc.so", "open");
void* backupFunc = nullptr;
HookEngine::getInstance().hookFunction(targetFunc, replaceFunc, &backupFunc);
```

### Path Redirection

```cpp
// Set up redirection
IORedirect::getInstance().setVirtualRoot("/data/data/com.virtue.gms.huawei/virtual");
IORedirect::getInstance().setPackageName("com.example.app");

// Redirect path
std::string redirected = IORedirect::getInstance().redirectPath("/data/data/com.example.app/files/test.txt");
// Result: /data/data/com.virtue.gms.huawei/virtual/user_0/com.example.app/data/files/test.txt
```

## Limitations and Future Work

### Current Limitations

1. **No PLT/GOT Hooking**: Only direct function hooking supported
2. **Single-threaded**: Hook installation not thread-safe
3. **No Thumb Mode**: ARM32 assumes ARM mode instructions
4. **Basic Instruction Parsing**: Only common branch instructions handled

### Future Enhancements

1. **PLT/GOT Hooking**: Hook library imports
2. **Better Instruction Analysis**: Handle more ARM instruction types
3. **Thread Safety**: Add mutex protection for hook operations
4. **Thumb Support**: Handle Thumb mode for ARM32
5. **SELinux Integration**: Handle security contexts properly
6. **Namespace Isolation**: Use Linux namespaces for better isolation

## Testing

To test the native layer:

```bash
# Build with debug symbols
./gradlew :native:clean :native:build

# Check generated libraries
ls -la native/build/intermediates/cmake/debug/obj/

# Test on device
adb push <library> /data/local/tmp/
adb shell chmod 755 /data/local/tmp/<library>
```

## References

- [ARM Architecture Reference Manual](https://developer.arm.com/documentation/)
- [SandHook Implementation](https://github.com/ganyao114/SandHook)
- [Android Native Development](https://developer.android.com/ndk)
- [Linux mprotect()](https://man7.org/linux/man-pages/man2/mprotect.2.html)

## License

MIT License - See main project LICENSE file
