package com.virtue.gms.huawei.data

data class VirtualApp(
    val id: Int,
    val name: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Int,
    val userId: Int = 0,
    val isRunning: Boolean = false
)
