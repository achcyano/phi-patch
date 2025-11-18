# Virtue GMS

A multi-module virtual framework for Android based on SandVXposed architecture, built with Kotlin, Jetpack Compose, and JNI.

## Features

- **Virtual Environment**: Run multiple instances of apps in isolated environments
- **Native Hooks**: JNI-based native function hooking for system call interception
- **Modern UI**: Jetpack Compose-based user interface
- **Multi-Module Architecture**: Clean separation of concerns across modules

## Architecture

The project follows SandVXposed's approach with a modern tech stack:

### Modules

1. **app**: Main application module with Compose UI
   - User interface built with Jetpack Compose
   - Material Design 3 theming
   - Navigation between apps, environments, and settings

2. **core**: Core virtual environment engine
   - Virtual application management
   - Process isolation
   - Virtual user system

3. **hook**: Hook framework for interception
   - Java method hooking
   - Native function hooking
   - System service hooking

4. **native**: JNI bridge and native implementation
   - Native bridge for Java-C++ communication
   - IO redirection hooks
   - Process management hooks
   - Hook engine implementation

## Building

### Prerequisites

- Android Studio Arctic Fox or later
- Android SDK 26+
- NDK r25 or later
- Gradle 8.2+

### Build Steps

```bash
# Clone the repository
git clone https://github.com/achcyano/virtue-gms-huawei.git
cd virtue-gms-huawei

# Build the project
./gradlew assembleDebug

# Install on device
./gradlew installDebug
```

## Technology Stack

- **Language**: Kotlin + Java
- **UI Framework**: Jetpack Compose
- **Native Code**: C++ (JNI)
- **Build System**: Gradle with Kotlin DSL
- **Architecture**: Multi-module MVVM
- **Minimum SDK**: 26 (Android 8.0)
- **Target SDK**: 34 (Android 14)

## Inspired By

This project is inspired by:
- [SandVXposed](https://github.com/asLody/SandVXposed) - Virtual app framework with Xposed support
- [VirtualApp](https://github.com/asLody/VirtualApp) - Virtual app engine
- [SandHook](https://github.com/ganyao114/SandHook) - Native hook framework

## License

MIT License - see [LICENSE](LICENSE) file for details.

## Disclaimer

This project is for educational and research purposes only. Do not use for commercial purposes without proper authorization.

## Development Status

🚧 **Work in Progress** - This project is under active development.

Current status:
- [x] Project structure and build system
- [x] Multi-module architecture setup
- [x] Basic Compose UI implementation
- [x] Core module framework
- [x] Hook module framework
- [x] Native JNI bridge
- [ ] Virtual app installation
- [ ] Virtual app execution
- [ ] Complete hook implementations
- [ ] Storage redirection
- [ ] Xposed module support
- [ ] Device emulation
- [ ] GMS/HMS integration
