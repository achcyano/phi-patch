#include "hook_engine.h"
#include <android/log.h>
#include <sys/mman.h>
#include <unistd.h>
#include <cstring>
#include <cerrno>

#define TAG "HookEngine"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

namespace virtue {

HookEngine& HookEngine::getInstance() {
    static HookEngine instance;
    return instance;
}

bool HookEngine::initialize() {
    if (m_initialized) {
        return true;
    }
    
    LOGD("Initializing hook engine");
    m_initialized = true;
    return true;
}

size_t HookEngine::getMinimumHookSize() {
    // ARM64 needs at least 16 bytes (4 instructions) for a far jump
    // We'll use a conservative 20 bytes to be safe
#ifdef __aarch64__
    return 20;
#else
    // ARM32 needs at least 8 bytes
    return 8;
#endif
}

bool HookEngine::makeMemoryWritable(void* addr, size_t size) {
    // Align to page boundary
    uintptr_t pageStart = reinterpret_cast<uintptr_t>(addr) & ~(getpagesize() - 1);
    size_t pageSize = ((reinterpret_cast<uintptr_t>(addr) + size - pageStart) + getpagesize() - 1) & ~(getpagesize() - 1);
    
    if (mprotect(reinterpret_cast<void*>(pageStart), pageSize, PROT_READ | PROT_WRITE | PROT_EXEC) != 0) {
        LOGE("Failed to make memory writable: %s", strerror(errno));
        return false;
    }
    
    return true;
}

bool HookEngine::restoreMemoryProtection(void* addr, size_t size) {
    // Align to page boundary
    uintptr_t pageStart = reinterpret_cast<uintptr_t>(addr) & ~(getpagesize() - 1);
    size_t pageSize = ((reinterpret_cast<uintptr_t>(addr) + size - pageStart) + getpagesize() - 1) & ~(getpagesize() - 1);
    
    if (mprotect(reinterpret_cast<void*>(pageStart), pageSize, PROT_READ | PROT_EXEC) != 0) {
        LOGE("Failed to restore memory protection: %s", strerror(errno));
        return false;
    }
    
    return true;
}

#ifdef __aarch64__
bool HookEngine::isARM64BranchInstruction(uint32_t instruction) {
    // Check for B, BL, CBZ, CBNZ, TBZ, TBNZ instructions
    // B/BL: 0x14000000 / 0x94000000 (mask 0x7C000000)
    if ((instruction & 0x7C000000) == 0x14000000) return true;
    // CBZ/CBNZ: 0x34000000 / 0x35000000 (mask 0x7E000000)
    if ((instruction & 0x7E000000) == 0x34000000) return true;
    // TBZ/TBNZ: 0x36000000 / 0x37000000 (mask 0x7E000000)
    if ((instruction & 0x7E000000) == 0x36000000) return true;
    return false;
}

int32_t HookEngine::getARM64BranchOffset(uint32_t instruction) {
    if ((instruction & 0x7C000000) == 0x14000000) {
        // B/BL instruction - 26 bit offset
        int32_t offset = (instruction & 0x03FFFFFF);
        if (offset & 0x02000000) {
            offset |= 0xFC000000; // Sign extend
        }
        return offset * 4;
    }
    return 0;
}

uint32_t HookEngine::makeARM64BranchInstruction(int32_t offset) {
    // Create a B (branch) instruction
    // B instruction: 0x14000000 | (offset >> 2)
    return 0x14000000 | ((offset >> 2) & 0x03FFFFFF);
}
#endif

void* HookEngine::createTrampoline(void* targetFunc, size_t hookSize) {
    // Allocate memory for trampoline
    size_t trampolineSize = hookSize + 32; // Extra space for jump back
    void* trampoline = mmap(nullptr, trampolineSize, PROT_READ | PROT_WRITE | PROT_EXEC,
                           MAP_PRIVATE | MAP_ANONYMOUS, -1, 0);
    
    if (trampoline == MAP_FAILED) {
        LOGE("Failed to allocate trampoline memory: %s", strerror(errno));
        return nullptr;
    }
    
    // Copy original instructions
    memcpy(trampoline, targetFunc, hookSize);
    
#ifdef __aarch64__
    // Add jump back to original function after hook
    uintptr_t trampolineEnd = reinterpret_cast<uintptr_t>(trampoline) + hookSize;
    uintptr_t returnAddr = reinterpret_cast<uintptr_t>(targetFunc) + hookSize;
    int64_t jumpOffset = returnAddr - trampolineEnd;
    
    // Write branch instruction to jump back
    uint32_t* trampolineInst = reinterpret_cast<uint32_t*>(trampolineEnd);
    
    // Use absolute jump via register for large offsets
    // LDR X16, #8
    trampolineInst[0] = 0x58000050;
    // BR X16  
    trampolineInst[1] = 0xD61F0200;
    // Address (64-bit)
    *reinterpret_cast<uint64_t*>(&trampolineInst[2]) = returnAddr;
#else
    // ARM32 implementation
    uint32_t* trampolineInst = reinterpret_cast<uint32_t*>(
        reinterpret_cast<uintptr_t>(trampoline) + hookSize);
    uintptr_t returnAddr = reinterpret_cast<uintptr_t>(targetFunc) + hookSize;
    
    // LDR PC, [PC, #-4]
    trampolineInst[0] = 0xE51FF004;
    // Address
    trampolineInst[1] = returnAddr;
#endif
    
    __builtin___clear_cache(reinterpret_cast<char*>(trampoline), 
                           reinterpret_cast<char*>(trampoline) + trampolineSize);
    
    return trampoline;
}

bool HookEngine::installHook(void* targetFunc, void* replaceFunc, size_t hookSize) {
    if (!makeMemoryWritable(targetFunc, hookSize)) {
        return false;
    }
    
#ifdef __aarch64__
    uint32_t* targetInst = static_cast<uint32_t*>(targetFunc);
    int64_t offset = reinterpret_cast<int64_t>(replaceFunc) - reinterpret_cast<int64_t>(targetFunc);
    
    // Check if we can use a direct branch (within ±128MB)
    if (offset >= -0x8000000 && offset < 0x8000000) {
        // Use B (branch) instruction
        targetInst[0] = makeARM64BranchInstruction(static_cast<int32_t>(offset));
    } else {
        // Use absolute jump via register
        // LDR X16, #8
        targetInst[0] = 0x58000050;
        // BR X16
        targetInst[1] = 0xD61F0200;
        // Address (64-bit)
        *reinterpret_cast<uint64_t*>(&targetInst[2]) = reinterpret_cast<uint64_t>(replaceFunc);
    }
#else
    // ARM32 implementation
    uint32_t* targetInst = static_cast<uint32_t*>(targetFunc);
    
    // LDR PC, [PC, #-4]
    targetInst[0] = 0xE51FF004;
    // Address
    targetInst[1] = reinterpret_cast<uint32_t>(replaceFunc);
#endif
    
    __builtin___clear_cache(reinterpret_cast<char*>(targetFunc),
                           reinterpret_cast<char*>(targetFunc) + hookSize);
    
    if (!restoreMemoryProtection(targetFunc, hookSize)) {
        return false;
    }
    
    return true;
}

bool HookEngine::hookFunction(void* targetFunc, void* replaceFunc, void** backupFunc) {
    if (!m_initialized) {
        LOGE("Hook engine not initialized");
        return false;
    }
    
    if (!targetFunc || !replaceFunc) {
        LOGE("Invalid parameters");
        return false;
    }
    
    LOGD("Hooking function at %p with replacement at %p", targetFunc, replaceFunc);
    
    // Get minimum hook size
    size_t hookSize = getMinimumHookSize();
    
    // Create trampoline with original instructions
    Trampoline trampoline;
    trampoline.originalFunc = targetFunc;
    trampoline.originalSize = hookSize;
    trampoline.originalBytes.resize(hookSize);
    memcpy(trampoline.originalBytes.data(), targetFunc, hookSize);
    
    // Create trampoline code
    void* trampolineCode = createTrampoline(targetFunc, hookSize);
    if (!trampolineCode) {
        LOGE("Failed to create trampoline");
        return false;
    }
    
    trampoline.backupFunc = trampolineCode;
    
    // Install the hook
    if (!installHook(targetFunc, replaceFunc, hookSize)) {
        LOGE("Failed to install hook");
        munmap(trampolineCode, hookSize + 32);
        return false;
    }
    
    // Store hook info
    m_hookMap[targetFunc] = replaceFunc;
    m_trampolineMap[targetFunc] = trampoline;
    
    if (backupFunc) {
        *backupFunc = trampolineCode;
    }
    
    LOGD("Successfully hooked function at %p, backup at %p", targetFunc, trampolineCode);
    return true;
}

bool HookEngine::unhookFunction(void* targetFunc) {
    if (!m_initialized) {
        LOGE("Hook engine not initialized");
        return false;
    }
    
    LOGD("Unhooking function at %p", targetFunc);
    
    auto it = m_trampolineMap.find(targetFunc);
    if (it == m_trampolineMap.end()) {
        LOGE("Function not hooked");
        return false;
    }
    
    Trampoline& trampoline = it->second;
    
    // Restore original instructions
    if (!makeMemoryWritable(targetFunc, trampoline.originalSize)) {
        return false;
    }
    
    memcpy(targetFunc, trampoline.originalBytes.data(), trampoline.originalSize);
    
    __builtin___clear_cache(reinterpret_cast<char*>(targetFunc),
                           reinterpret_cast<char*>(targetFunc) + trampoline.originalSize);
    
    if (!restoreMemoryProtection(targetFunc, trampoline.originalSize)) {
        return false;
    }
    
    // Free trampoline memory
    munmap(trampoline.backupFunc, trampoline.originalSize + 32);
    
    // Remove from maps
    m_hookMap.erase(targetFunc);
    m_trampolineMap.erase(targetFunc);
    
    LOGD("Successfully unhooked function at %p", targetFunc);
    return true;
}

void* HookEngine::findSymbol(const char* libraryName, const char* symbolName) {
    void* handle = dlopen(libraryName, RTLD_NOW);
    if (!handle) {
        LOGE("Failed to open library: %s, error: %s", libraryName, dlerror());
        return nullptr;
    }
    
    void* symbol = dlsym(handle, symbolName);
    if (!symbol) {
        LOGE("Failed to find symbol: %s in %s, error: %s", 
             symbolName, libraryName, dlerror());
    } else {
        LOGD("Found symbol: %s at %p", symbolName, symbol);
    }
    
    // Don't close the handle, we need the library loaded
    return symbol;
}

} // namespace virtue
