# Keep all public APIs in the core module
-keep public class com.virtue.core.** { *; }
-keepclassmembers class * {
    native <methods>;
}
