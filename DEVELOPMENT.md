# Development Guide

## Getting Started

### Prerequisites

Before you begin, ensure you have the following installed:

1. **Android Studio** (Arctic Fox or later)
   - Download from [developer.android.com](https://developer.android.com/studio)

2. **Android SDK**
   - SDK Platform 26 (Android 8.0) minimum
   - SDK Platform 34 (Android 14) for target

3. **Android NDK**
   - NDK r25 or later
   - Install via SDK Manager in Android Studio

4. **Java Development Kit (JDK)**
   - JDK 17 or later

### Project Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/achcyano/virtue-gms-huawei.git
   cd virtue-gms-huawei
   ```

2. **Open in Android Studio**:
   - File → Open → Select the `virtue-gms-huawei` directory
   - Wait for Gradle sync to complete

3. **Configure NDK**:
   - File → Project Structure → SDK Location
   - Set Android NDK location

4. **Build the project**:
   ```bash
   ./gradlew clean build
   ```

## Project Structure

```
virtue-gms-huawei/
├── app/                          # Main application module
│   ├── src/main/
│   │   ├── kotlin/com/virtue/gms/huawei/
│   │   │   ├── MainActivity.kt          # Entry point
│   │   │   ├── VirtueApplication.kt     # Application class
│   │   │   ├── data/                    # Data models & ViewModels
│   │   │   └── ui/                      # Compose UI components
│   │   ├── res/                         # Android resources
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
│
├── core/                         # Core virtualization engine
│   ├── src/main/java/com/virtue/core/
│   │   ├── VirtualCore.java             # Main virtual engine
│   │   ├── client/                      # Client-side components
│   │   │   └── VirtualClient.java
│   │   ├── server/                      # Server-side components
│   │   │   └── VirtualServer.java
│   │   ├── helper/                      # Helper utilities
│   │   │   └── VirtualStorageManager.java
│   │   └── os/                          # OS-level abstractions
│   │       ├── VirtualAppInfo.java
│   │       └── VirtualEnvironmentInfo.java
│   └── build.gradle.kts
│
├── hook/                         # Hook framework
│   ├── src/main/java/com/virtue/hook/
│   │   ├── HookManager.java             # Hook manager
│   │   ├── base/                        # Base hook classes
│   │   │   └── MethodHook.java
│   │   └── providers/                   # System service hooks
│   │       ├── ActivityManagerHook.java
│   │       └── PackageManagerHook.java
│   └── build.gradle.kts
│
├── native/                       # JNI and native code
│   ├── src/main/
│   │   ├── java/com/virtue/native/
│   │   │   └── NativeBridge.java        # JNI interface
│   │   └── cpp/                         # C++ implementation
│   │       ├── native_bridge.cpp        # JNI bridge
│   │       ├── hook_engine.{cpp,h}      # Hook engine
│   │       └── io_redirect.{cpp,h}      # IO redirection
│   ├── CMakeLists.txt                   # CMake build
│   └── build.gradle.kts
│
├── build.gradle.kts              # Root build configuration
├── settings.gradle.kts           # Module settings
├── gradle.properties             # Gradle properties
├── README.md                     # Project overview
├── ARCHITECTURE.md               # Architecture details
└── DEVELOPMENT.md                # This file
```

## Module Descriptions

### App Module

The main application module contains:
- **UI Layer**: Jetpack Compose-based user interface
- **ViewModels**: State management for UI components
- **Data Models**: Kotlin data classes for app state
- **Navigation**: Navigation between different screens

Key files:
- `MainActivity.kt`: Entry point and main activity
- `VirtueApplication.kt`: Application initialization
- `MainScreen.kt`: Main navigation scaffold
- `AppsScreen.kt`: Virtual apps management
- `VirtualEnvironmentsScreen.kt`: Environment configuration
- `SettingsScreen.kt`: App settings

### Core Module

The core virtualization engine:
- **VirtualCore**: Main API for virtual operations
- **VirtualClient**: Client-side virtual environment
- **VirtualServer**: Server-side process management
- **VirtualStorageManager**: Storage isolation

Key concepts:
- Virtual process creation and management
- App isolation and sandboxing
- Storage redirection
- Multi-user support

### Hook Module

System call interception framework:
- **HookManager**: Central hook management
- **MethodHook**: Base class for Java hooks
- **Service Hooks**: Specific hooks for system services

Supported hooks:
- ActivityManager (activity lifecycle)
- PackageManager (package information)
- ContentResolver (content access)
- Location services
- Device information

### Native Module

JNI bridge and native implementation:
- **NativeBridge**: Java-to-C++ interface
- **HookEngine**: Native function hooking
- **IORedirect**: File system redirection

Native features:
- Inline hooking for native functions
- PLT/GOT hooking
- System call interception
- Path redirection

## Development Workflow

### Adding a New Feature

1. **Plan the feature**:
   - Identify which modules are affected
   - Design the API and data flow

2. **Implement in appropriate module**:
   - Core features → core module
   - UI features → app module
   - Hooks → hook module
   - Native code → native module

3. **Test thoroughly**:
   - Unit tests for business logic
   - Integration tests for module interaction
   - Manual testing on device

4. **Document changes**:
   - Update relevant documentation
   - Add code comments
   - Update ARCHITECTURE.md if needed

### Building Native Code

The native module uses CMake:

```bash
# Build native libraries
./gradlew :native:clean :native:build

# Check native library output
ls -la native/build/intermediates/cmake/debug/obj/
```

### Debugging

#### Java/Kotlin Debugging

1. Set breakpoints in Android Studio
2. Run in debug mode (Shift + F9)
3. Use logcat for logging

#### Native Debugging

1. Enable LLDB debugger in Android Studio
2. Set breakpoints in C++ files
3. Attach to process
4. Use `LOGD` macros for logging

Example logging:
```cpp
#include <android/log.h>
#define TAG "MyTag"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

LOGD("Debug message: %s", someVariable);
```

## Testing

### Unit Tests

Located in `src/test/` directories:

```bash
./gradlew test
```

### Instrumentation Tests

Located in `src/androidTest/` directories:

```bash
./gradlew connectedAndroidTest
```

### Manual Testing

1. Install the app on a device or emulator
2. Test virtual app installation
3. Test app execution in virtual environment
4. Verify isolation (storage, processes)

## Code Style

### Kotlin

- Use official Kotlin coding conventions
- Prefer `val` over `var`
- Use data classes for models
- Leverage coroutines for async operations

### Java

- Follow Android style guide
- Use proper naming conventions
- Add Javadoc for public APIs

### C++

- Use C++17 features
- RAII for resource management
- Clear naming and comments
- Avoid raw pointers when possible

## Common Issues

### Gradle Sync Failure

- Check internet connection
- Clear Gradle cache: `./gradlew clean --no-daemon`
- Invalidate caches: File → Invalidate Caches / Restart

### NDK Build Failure

- Verify NDK is installed
- Check CMakeLists.txt for errors
- Ensure SDK/NDK versions match

### App Crashes

- Check logcat for stack traces
- Verify permissions in AndroidManifest.xml
- Debug step-by-step

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Resources

- [SandVXposed Source](https://github.com/asLody/SandVXposed)
- [VirtualApp Documentation](https://github.com/asLody/VirtualApp)
- [Android Developers Guide](https://developer.android.com)
- [Jetpack Compose Guide](https://developer.android.com/jetpack/compose)
- [Android NDK Guide](https://developer.android.com/ndk)

## License

MIT License - See LICENSE file for details
