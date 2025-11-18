#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include <dlfcn.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <cerrno>
#include <cstring>
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
    
    // Redirect path to virtual storage
    std::string redirectedPath = virtue::IORedirect::getInstance().redirectPath(path);
    LOGD("Hook stat: %s -> %s", path, redirectedPath.c_str());
    
    struct stat st;
    int result = stat(redirectedPath.c_str(), &st);
    
    env->ReleaseStringUTFChars(pathname, path);
    return result;
}

JNIEXPORT jint JNICALL
Java_com_virtue_native_NativeBridge_hookFork(JNIEnv *env, jclass clazz) {
    LOGD("Hook fork - creating virtual process");
    
    // Handle fork in virtual environment
    // In a full implementation, this would:
    // 1. Create a new process with virtual UID/GID
    // 2. Set up isolated namespace
    // 3. Configure binder proxy
    
    pid_t pid = fork();
    
    if (pid == 0) {
        // Child process - set up virtual environment
        LOGD("Virtual process created with PID: %d", getpid());
        
        // Set process name to indicate virtual process
        // In real implementation, set up complete isolation here
    } else if (pid > 0) {
        // Parent process
        LOGD("Forked virtual process with PID: %d", pid);
    } else {
        LOGE("Fork failed: %s", strerror(errno));
    }
    
    return pid;
}

JNIEXPORT jint JNICALL
Java_com_virtue_native_NativeBridge_hookExecve(JNIEnv *env, jclass clazz,
                                                jstring filename, jobjectArray argv,
                                                jobjectArray envp) {
    const char *file = env->GetStringUTFChars(filename, nullptr);
    
    LOGD("Hook execve: %s", file);
    
    // Handle execve in virtual environment
    // Redirect to virtual binary if exists
    std::string redirectedPath = virtue::IORedirect::getInstance().redirectPath(file);
    
    // Convert Java arrays to C arrays
    int argc = argv ? env->GetArrayLength(argv) : 0;
    int envc = envp ? env->GetArrayLength(envp) : 0;
    
    std::vector<char*> argvVec(argc + 1);
    std::vector<char*> envpVec(envc + 1);
    
    for (int i = 0; i < argc; i++) {
        jstring jstr = (jstring)env->GetObjectArrayElement(argv, i);
        const char* str = env->GetStringUTFChars(jstr, nullptr);
        argvVec[i] = strdup(str);
        env->ReleaseStringUTFChars(jstr, str);
    }
    argvVec[argc] = nullptr;
    
    for (int i = 0; i < envc; i++) {
        jstring jstr = (jstring)env->GetObjectArrayElement(envp, i);
        const char* str = env->GetStringUTFChars(jstr, nullptr);
        envpVec[i] = strdup(str);
        env->ReleaseStringUTFChars(jstr, str);
    }
    envpVec[envc] = nullptr;
    
    // Execute with redirected path
    int result = execve(redirectedPath.c_str(), argvVec.data(), envpVec.data());
    
    // Clean up (only reached if execve fails)
    for (char* arg : argvVec) {
        if (arg) free(arg);
    }
    for (char* env_var : envpVec) {
        if (env_var) free(env_var);
    }
    
    LOGE("Execve failed: %s", strerror(errno));
    
    env->ReleaseStringUTFChars(filename, file);
    return result;
}

} // extern "C"
