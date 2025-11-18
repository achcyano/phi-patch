#ifndef VIRTUE_HOOK_ENGINE_H
#define VIRTUE_HOOK_ENGINE_H

#include <dlfcn.h>
#include <string>
#include <map>

namespace virtue {

class HookEngine {
public:
    static HookEngine& getInstance();
    
    bool initialize();
    bool hookFunction(void* targetFunc, void* replaceFunc, void** backupFunc);
    bool unhookFunction(void* targetFunc);
    void* findSymbol(const char* libraryName, const char* symbolName);

private:
    HookEngine() : m_initialized(false) {}
    ~HookEngine() = default;
    
    HookEngine(const HookEngine&) = delete;
    HookEngine& operator=(const HookEngine&) = delete;
    
    bool m_initialized;
    std::map<void*, void*> m_hookMap;
};

} // namespace virtue

#endif // VIRTUE_HOOK_ENGINE_H
