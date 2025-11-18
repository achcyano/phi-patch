#include "io_redirect.h"
#include <android/log.h>
#include <string.h>

#define TAG "IORedirect"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)

namespace virtue {

IORedirect& IORedirect::getInstance() {
    static IORedirect instance;
    return instance;
}

void IORedirect::setVirtualRoot(const std::string& virtualRoot) {
    m_virtualRoot = virtualRoot;
    LOGD("Virtual root set to: %s", virtualRoot.c_str());
}

void IORedirect::setPackageName(const std::string& packageName) {
    m_packageName = packageName;
    LOGD("Package name set to: %s", packageName.c_str());
}

std::string IORedirect::getDataDir() {
    if (m_virtualRoot.empty() || m_packageName.empty()) {
        return "";
    }
    return m_virtualRoot + "/user_0/" + m_packageName + "/data";
}

std::string IORedirect::getCacheDir() {
    if (m_virtualRoot.empty() || m_packageName.empty()) {
        return "";
    }
    return m_virtualRoot + "/user_0/" + m_packageName + "/cache";
}

std::string IORedirect::getFilesDir() {
    if (m_virtualRoot.empty() || m_packageName.empty()) {
        return "";
    }
    return m_virtualRoot + "/user_0/" + m_packageName + "/files";
}

std::string IORedirect::getExternalDir() {
    if (m_virtualRoot.empty() || m_packageName.empty()) {
        return "";
    }
    return m_virtualRoot + "/sdcard/Android/data/" + m_packageName;
}

std::string IORedirect::redirectPath(const std::string& originalPath) {
    if (m_virtualRoot.empty()) {
        return originalPath;
    }
    
    // Check cache first
    auto it = m_pathCache.find(originalPath);
    if (it != m_pathCache.end()) {
        return it->second;
    }
    
    // Check if path needs redirection
    if (!shouldRedirect(originalPath)) {
        return originalPath;
    }
    
    std::string redirected = originalPath;
    
    // Redirect app-specific paths
    if (!m_packageName.empty()) {
        // Redirect /data/data/package to virtual data dir
        std::string dataPrefix = "/data/data/" + m_packageName;
        if (originalPath.find(dataPrefix) == 0) {
            redirected = getDataDir() + originalPath.substr(dataPrefix.length());
        }
        
        // Redirect /data/user/0/package to virtual data dir
        std::string userPrefix = "/data/user/0/" + m_packageName;
        if (originalPath.find(userPrefix) == 0) {
            redirected = getDataDir() + originalPath.substr(userPrefix.length());
        }
        
        // Redirect external storage
        std::string extPrefix = "/sdcard/Android/data/" + m_packageName;
        if (originalPath.find(extPrefix) == 0) {
            redirected = getExternalDir() + originalPath.substr(extPrefix.length());
        }
        
        std::string storagePrefix = "/storage/emulated/0/Android/data/" + m_packageName;
        if (originalPath.find(storagePrefix) == 0) {
            redirected = getExternalDir() + originalPath.substr(storagePrefix.length());
        }
    } else {
        // Generic redirection when package name is not set
        if (originalPath.find("/data/data/") == 0) {
            redirected = m_virtualRoot + originalPath;
        } else if (originalPath.find("/data/user/") == 0) {
            redirected = m_virtualRoot + originalPath;
        } else if (originalPath.find("/sdcard/") == 0 || 
                   originalPath.find("/storage/emulated/") == 0) {
            redirected = m_virtualRoot + "/sdcard" + originalPath.substr(originalPath.find("/Android"));
        }
    }
    
    // Cache the result
    if (redirected != originalPath) {
        m_pathCache[originalPath] = redirected;
        LOGD("Redirecting: %s -> %s", originalPath.c_str(), redirected.c_str());
    }
    
    return redirected;
}

bool IORedirect::shouldRedirect(const std::string& path) {
    // Redirect paths that apps typically access
    // Based on SandVXposed approach
    
    const char* redirectPaths[] = {
        "/data/data/",
        "/data/user/",
        "/data/app/",
        "/sdcard/Android/data/",
        "/sdcard/Android/obb/",
        "/storage/emulated/0/Android/data/",
        "/storage/emulated/0/Android/obb/"
    };
    
    for (const char* prefix : redirectPaths) {
        if (path.find(prefix) == 0) {
            return true;
        }
    }
    
    // Don't redirect system paths
    const char* systemPaths[] = {
        "/system/",
        "/vendor/",
        "/apex/",
        "/dev/",
        "/proc/",
        "/sys/"
    };
    
    for (const char* prefix : systemPaths) {
        if (path.find(prefix) == 0) {
            return false;
        }
    }
    
    return false;
}

} // namespace virtue
