#ifndef VIRTUE_IO_REDIRECT_H
#define VIRTUE_IO_REDIRECT_H

#include <string>
#include <map>

namespace virtue {

class IORedirect {
public:
    static IORedirect& getInstance();
    
    void setVirtualRoot(const std::string& virtualRoot);
    void setPackageName(const std::string& packageName);
    std::string redirectPath(const std::string& originalPath);
    bool shouldRedirect(const std::string& path);
    
    // Get redirected paths for specific directories
    std::string getDataDir();
    std::string getCacheDir();
    std::string getFilesDir();
    std::string getExternalDir();

private:
    IORedirect() = default;
    ~IORedirect() = default;
    
    IORedirect(const IORedirect&) = delete;
    IORedirect& operator=(const IORedirect&) = delete;
    
    std::string m_virtualRoot;
    std::string m_packageName;
    std::map<std::string, std::string> m_pathCache;
};

} // namespace virtue

#endif // VIRTUE_IO_REDIRECT_H
