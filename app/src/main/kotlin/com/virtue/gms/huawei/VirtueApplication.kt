package com.virtue.gms.huawei

import android.app.Application
import com.virtue.core.VirtualCore
import com.virtue.hook.HookManager
import com.virtue.native.NativeBridge

class VirtueApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize native bridge
        NativeBridge.initialize(this)
        
        // Initialize virtual core
        VirtualCore.initialize(this)
        
        // Initialize hook manager
        HookManager.initialize(this)
    }
}
