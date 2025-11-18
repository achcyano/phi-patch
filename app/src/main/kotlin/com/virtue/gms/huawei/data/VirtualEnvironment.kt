package com.virtue.gms.huawei.data

data class VirtualEnvironment(
    val id: Int,
    val name: String,
    val isActive: Boolean = false,
    val isolationLevel: IsolationLevel = IsolationLevel.STANDARD
)

enum class IsolationLevel {
    STANDARD,   // Standard isolation
    ENHANCED,   // Enhanced with more hooks
    MAXIMUM     // Maximum isolation with full device emulation
}
