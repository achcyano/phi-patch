#ifndef VIRTUE_HOOK_ENGINE_H
#define VIRTUE_HOOK_ENGINE_H

#include <dlfcn.h>
#include <string>
#include <map>
#include <vector>
#include <cstdint>

namespace virtue {

// ARM64 instruction structure
struct ARM64Instruction {
    uint32_t code;
    bool isRelative;
    int32_t offset;
};

// Trampoline structure to hold original instructions
struct Trampoline {
    void* originalFunc;
    void* backupFunc;
    std::vector<uint8_t> originalBytes;
    size_t originalSize;
};

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
    
    // Hook implementation
    bool makeMemoryWritable(void* addr, size_t size);
    bool restoreMemoryProtection(void* addr, size_t size);
    void* createTrampoline(void* targetFunc, size_t hookSize);
    bool installHook(void* targetFunc, void* replaceFunc, size_t hookSize);
    
    // ARM64 instruction parsing
    bool isARM64BranchInstruction(uint32_t instruction);
    int32_t getARM64BranchOffset(uint32_t instruction);
    uint32_t makeARM64BranchInstruction(int32_t offset);
    size_t getMinimumHookSize();
    
    bool m_initialized;
    std::map<void*, void*> m_hookMap;
    std::map<void*, Trampoline> m_trampolineMap;
};

} // namespace virtue

#endif // VIRTUE_HOOK_ENGINE_H
