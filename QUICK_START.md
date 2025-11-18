# Quick Start Guide

Get started with Virtue GMS in 5 minutes!

## Prerequisites

✅ Android Studio (Arctic Fox or later)  
✅ Android SDK 26+  
✅ Android NDK r25+  
✅ JDK 17+  

## Step 1: Clone and Open

```bash
# Clone the repository
git clone https://github.com/achcyano/virtue-gms-huawei.git
cd virtue-gms-huawei

# Open in Android Studio
# File → Open → Select the virtue-gms-huawei directory
```

## Step 2: Sync and Build

Android Studio will automatically:
- Download dependencies
- Sync Gradle
- Build native libraries

Or manually:
```bash
./gradlew build
```

## Step 3: Run on Device

1. Connect Android device (Android 8.0+) or start emulator
2. Click **Run** (▶️) in Android Studio
3. Select your device
4. App will install and launch

## First Use

### Main Screen
When you launch Virtue GMS, you'll see three tabs:

**📱 Apps Tab**
- Manage virtual apps
- Launch installed virtual apps
- View app status
- Tap ➕ to add new apps (coming soon)

**📁 Environments Tab**
- Create virtual environments
- Switch between environments
- Configure isolation levels
- Manage environment settings

**⚙️ Settings Tab**
- Configure hook engine
- Manage virtual storage
- Enable device emulation
- View app information

## Project Structure Overview

```
virtue-gms-huawei/
├── 📱 app/          → Main UI (Jetpack Compose)
├── 🎯 core/         → Virtual engine
├── 🔌 hook/         → Hook framework
├── ⚡ native/       → JNI & Native code
└── 📚 docs/         → Documentation
```

## Key Modules

### 1. App Module
**What it does**: Provides the user interface

**Key files**:
- `MainActivity.kt` - Entry point
- `MainScreen.kt` - Main navigation
- `AppsScreen.kt` - App management UI

### 2. Core Module
**What it does**: Manages virtual apps and environments

**Key files**:
- `VirtualCore.java` - Main API
- `VirtualServer.java` - Process management
- `VirtualStorageManager.java` - Storage isolation

### 3. Hook Module
**What it does**: Intercepts system calls

**Key files**:
- `HookManager.java` - Hook management
- `MethodHook.java` - Hook base class
- Service hooks for ActivityManager, PackageManager, etc.

### 4. Native Module
**What it does**: Native code execution and hooks

**Key files**:
- `NativeBridge.java` - JNI interface
- `hook_engine.cpp` - Native hook engine
- `io_redirect.cpp` - File system redirection

## Basic API Usage

### Initialize Virtual Core
```java
// In Application onCreate
VirtualCore.initialize(context);
```

### Install Virtual App (Coming Soon)
```java
VirtualCore.get().installApp(apkPath, flags);
```

### Launch Virtual App (Coming Soon)
```java
VirtualCore.get().launchApp(packageName, userId);
```

### Check App Status
```java
boolean running = VirtualCore.get().isAppRunning(packageName, userId);
```

## Development Workflow

### Making Changes

1. **UI Changes**: Edit Compose files in `app/src/main/kotlin/*/ui/`
2. **Core Logic**: Edit Java files in `core/src/main/java/`
3. **Hooks**: Add hooks in `hook/src/main/java/*/providers/`
4. **Native Code**: Edit C++ files in `native/src/main/cpp/`

### Testing

```bash
# Run unit tests
./gradlew test

# Run on device
./gradlew installDebug
```

### Debugging

**Java/Kotlin**:
- Set breakpoints in Android Studio
- Run in debug mode (Shift + F9)
- Use Logcat for logging

**Native (C++)**:
- Enable LLDB in Android Studio
- Set breakpoints in .cpp files
- Use LOGD for native logging

## Common Tasks

### Add a New Screen

1. Create `NewScreen.kt` in `app/src/main/kotlin/*/ui/screens/`
2. Define composable function
3. Add navigation route in `MainScreen.kt`
4. Add bottom nav item

### Add a System Hook

1. Create hook class in `hook/src/main/java/*/providers/`
2. Extend `MethodHook`
3. Override `beforeHookedMethod` or `afterHookedMethod`
4. Register in `HookManager`

### Add Native Function

1. Add native method in `NativeBridge.java`
2. Implement in `native_bridge.cpp`
3. Rebuild native libraries

## Troubleshooting

### Build Fails

**Issue**: Gradle sync fails  
**Solution**: 
```bash
./gradlew clean --no-daemon
# Then rebuild
```

### NDK Not Found

**Issue**: CMake cannot find NDK  
**Solution**: 
- Open SDK Manager
- Install NDK (Side by side)
- Set path in local.properties

### App Crashes

**Issue**: App crashes on launch  
**Solution**:
- Check Logcat for stack trace
- Verify AndroidManifest.xml permissions
- Ensure all modules are built

## Next Steps

### Learn More
- 📖 [Architecture](ARCHITECTURE.md) - System design
- 🛠️ [Development Guide](DEVELOPMENT.md) - Detailed setup
- 🔗 [SandVXposed Integration](SANDVXPOSED_INTEGRATION.md) - Integration approach
- ✨ [Features](FEATURES.md) - Feature roadmap

### Contribute
- 🐛 Report bugs via GitHub Issues
- 💡 Suggest features via Discussions
- 🔧 Submit pull requests
- 📝 Improve documentation

### Community
- ⭐ Star the repository
- 👁️ Watch for updates
- 🔀 Fork and experiment
- 💬 Join discussions

## Examples

### Example 1: Create Virtual Environment

```kotlin
// In ViewModel
fun createEnvironment(name: String) {
    viewModelScope.launch {
        val env = VirtualEnvironment(
            id = generateId(),
            name = name,
            isActive = false,
            isolationLevel = IsolationLevel.STANDARD
        )
        // Save to core
    }
}
```

### Example 2: Hook System Method

```java
public class MyHook extends MethodHook {
    @Override
    protected void beforeHookedMethod(MethodHookParam param) {
        // Intercept call
        String packageName = (String) param.args[0];
        if (shouldRedirect(packageName)) {
            param.setResult(redirectedResult);
        }
    }
}
```

### Example 3: Native IO Redirection

```cpp
int hooked_open(const char* path, int flags, mode_t mode) {
    std::string newPath = IORedirect::getInstance()
        .redirectPath(path);
    return original_open(newPath.c_str(), flags, mode);
}
```

## Resources

### Documentation
- [README](README.md) - Overview
- [Architecture](ARCHITECTURE.md) - Design
- [Features](FEATURES.md) - Roadmap
- [Summary](PROJECT_SUMMARY.md) - Statistics

### References
- [SandVXposed](https://github.com/asLody/SandVXposed)
- [Android Developers](https://developer.android.com)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Android NDK](https://developer.android.com/ndk)

## FAQ

**Q: Can I use this for commercial purposes?**  
A: Check the MIT license terms. Similar to SandVXposed, commercial use may require authorization.

**Q: What Android versions are supported?**  
A: Android 8.0 (API 26) and above. Tested on Android 8-14.

**Q: Does it support x86 architecture?**  
A: Currently only ARM64 and ARMv7. x86 support is not planned.

**Q: Can I run Xposed modules?**  
A: Not yet. Xposed support is planned for version 2.0.

**Q: How do I contribute?**  
A: Fork the repo, make changes, and submit a pull request. See [DEVELOPMENT.md](DEVELOPMENT.md).

## Quick Reference

### Build Commands
```bash
./gradlew clean              # Clean build
./gradlew build              # Build all modules
./gradlew assembleDebug      # Build debug APK
./gradlew installDebug       # Install on device
./gradlew test               # Run tests
```

### Module Commands
```bash
./gradlew :app:build         # Build app module
./gradlew :core:build        # Build core module
./gradlew :hook:build        # Build hook module
./gradlew :native:build      # Build native module
```

### Useful Paths
```
Source code:    /src/main/kotlin/ or /src/main/java/
Native code:    /native/src/main/cpp/
Resources:      /app/src/main/res/
Manifest:       /app/src/main/AndroidManifest.xml
Build files:    /build.gradle.kts
```

## Getting Help

**Found a bug?** → [Open an issue](https://github.com/achcyano/virtue-gms-huawei/issues)  
**Have a question?** → [Start a discussion](https://github.com/achcyano/virtue-gms-huawei/discussions)  
**Want to contribute?** → See [DEVELOPMENT.md](DEVELOPMENT.md)

---

**Happy Coding! 🚀**

Built with ❤️ using Kotlin, Compose, and JNI
