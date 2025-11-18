#include <jni.h>
#include <string>
#include <android/log.h>
#include <dlfcn.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include "hook_engine.h"
#include "io_redirect.h"

#define TAG "VirtueNative"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

static std::string g_libraryPath;
static bool g_hooksInitialized = false;

extern "C" {

JNIEXPORT void JNICALL
Java_com_virtue_native_NativeBridge_nativeInitialize(JNIEnv *env, jclass clazz,
                                                      jstring libraryPath) {
    const char *path = env->GetStringUTFChars(libraryPath, nullptr);
    g_libraryPath = path;
    env->ReleaseStringUTFChars(libraryPath, path);
    
    LOGD("Native bridge initialized with library path: %s", g_libraryPath.c_str());
    
    // Initialize hook engine
    virtue::HookEngine::getInstance().initialize();
}

JNIEXPORT void JNICALL
Java_com_virtue_native_NativeBridge_nativeInitializeHooks(JNIEnv *env, jclass clazz) {
    if (g_hooksInitialized) {
        return;
    }
    
    LOGD("Initializing native hooks");
    
    // Initialize hook engine
    if (!virtue::HookEngine::getInstance().initialize()) {
        LOGE("Failed to initialize hook engine");
        return;
    }
    
    g_hooksInitialized = true;
}

JNIEXPORT jboolean JNICALL
Java_com_virtue_native_NativeBridge_nativeHookFunction(JNIEnv *env, jclass clazz,
                                                        jstring libraryName,
                                                        jstring symbolName,
                                                        jlong newFunctionPtr) {
    const char *libName = env->GetStringUTFChars(libraryName, nullptr);
    const char *symbol = env->GetStringUTFChars(symbolName, nullptr);
    
    LOGD("Hooking function: %s in library: %s", symbol, libName);
    
    // Find the target function
    void* targetFunc = virtue::HookEngine::getInstance().findSymbol(libName, symbol);
    if (!targetFunc) {
        LOGE("Failed to find symbol: %s", symbol);
        env->ReleaseStringUTFChars(libraryName, libName);
        env->ReleaseStringUTFChars(symbolName, symbol);
        return JNI_FALSE;
    }
    
    // Hook the function
    void* backupFunc = nullptr;
    bool success = virtue::HookEngine::getInstance().hookFunction(
        targetFunc, 
        (void*)newFunctionPtr, 
        &backupFunc
    );
    
    env->ReleaseStringUTFChars(libraryName, libName);
    env->ReleaseStringUTFChars(symbolName, symbol);
    
    return success ? JNI_TRUE : JNI_FALSE;
}

// IO redirection hooks
JNIEXPORT jint JNICALL
Java_com_virtue_native_NativeBridge_hookOpen(JNIEnv *env, jclass clazz,
                                              jstring pathname, jint flags, jint mode) {
    const char *path = env->GetStringUTFChars(pathname, nullptr);
    
    // Redirect path to virtual storage
    std::string redirectedPath = virtue::IORedirect::getInstance().redirectPath(path);
    LOGD("Hook open: %s -> %s", path, redirectedPath.c_str());
    
    int result = open(redirectedPath.c_str(), flags, mode);
    
    env->ReleaseStringUTFChars(pathname, path);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_virtue_native_NativeBridge_hookAccess(JNIEnv *env, jclass clazz,
                                                jstring pathname, jint mode) {
    const char *path = env->GetStringUTFChars(pathname, nullptr);
    
    // Redirect path to virtual storage
    std::string redirectedPath = virtue::IORedirect::getInstance().redirectPath(path);
    LOGD("Hook access: %s -> %s", path, redirectedPath.c_str());
    
    int result = access(redirectedPath.c_str(), mode);
    
    env->ReleaseStringUTFChars(pathname, path);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_virtue_native_NativeBridge_hookStat(JNIEnv *env, jclass clazz,
                                              jstring pathname, jobject statBuf) {
    const char *path = env->GetStringUTFChars(pathname, nullptr);
    
    // TODO: Redirect path to virtual storage
    LOGD("Hook stat: %s", path);
    
    struct stat st;
    int result = stat(path, &st);
    
    env->ReleaseStringUTFChars(pathname, path);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_virtue_native_NativeBridge_hookFork(JNIEnv *env, jclass clazz) {
    LOGD("Hook fork");
    // TODO: Handle fork in virtual environment
    return fork();
}

JNIEXPORT jint JNICALL
Java_com_virtue_native_NativeBridge_hookExecve(JNIEnv *env, jclass clazz,
                                                jstring filename, jobjectArray argv,
                                                jobjectArray envp) {
    const char *file = env->GetStringUTFChars(filename, nullptr);
    
    LOGD("Hook execve: %s", file);
    
    // TODO: Handle execve in virtual environment
    
    env->ReleaseStringUTFChars(filename, file);
    return -1;
}

} // extern "C"
