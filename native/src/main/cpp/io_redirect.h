#ifndef VIRTUE_IO_REDIRECT_H
#define VIRTUE_IO_REDIRECT_H

#include <string>

namespace virtue {

class IORedirect {
public:
    static IORedirect& getInstance();
    
    void setVirtualRoot(const std::string& virtualRoot);
    std::string redirectPath(const std::string& originalPath);
    bool shouldRedirect(const std::string& path);

private:
    IORedirect() = default;
    ~IORedirect() = default;
    
    IORedirect(const IORedirect&) = delete;
    IORedirect& operator=(const IORedirect&) = delete;
    
    std::string m_virtualRoot;
};

} // namespace virtue

#endif // VIRTUE_IO_REDIRECT_H
