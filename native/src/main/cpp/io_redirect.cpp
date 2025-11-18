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

std::string IORedirect::redirectPath(const std::string& originalPath) {
    if (m_virtualRoot.empty()) {
        return originalPath;
    }
    
    // Check if path needs redirection
    if (shouldRedirect(originalPath)) {
        std::string redirected = m_virtualRoot + originalPath;
        LOGD("Redirecting: %s -> %s", originalPath.c_str(), redirected.c_str());
        return redirected;
    }
    
    return originalPath;
}

bool IORedirect::shouldRedirect(const std::string& path) {
    // Redirect paths that apps typically access
    // Based on SandVXposed approach
    
    const char* redirectPaths[] = {
        "/data/data/",
        "/data/user/",
        "/sdcard/Android/data/",
        "/storage/emulated/0/Android/data/"
    };
    
    for (const char* prefix : redirectPaths) {
        if (path.find(prefix) == 0) {
            return true;
        }
    }
    
    return false;
}

} // namespace virtue
