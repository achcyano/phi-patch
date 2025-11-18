# Keep hook classes
-keep public class com.virtue.hook.** { *; }
-keepclassmembers class * {
    native <methods>;
}
