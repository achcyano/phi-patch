# Keep native bridge
-keep public class com.virtue.native.** { *; }
-keepclassmembers class * {
    native <methods>;
}
