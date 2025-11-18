package com.virtue.gms.huawei.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.virtue.core.VirtualCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VirtualAppsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _apps = MutableStateFlow<List<VirtualApp>>(emptyList())
    val apps: StateFlow<List<VirtualApp>> = _apps.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        loadApps()
    }
    
    private fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val virtualCore = VirtualCore.get()
                if (virtualCore.isInitialized) {
                    val installedApps = withContext(Dispatchers.IO) {
                        virtualCore.getInstalledApps(0)
                    }
                    
                    _apps.value = installedApps.mapIndexed { index, appInfo ->
                        VirtualApp(
                            id = index,
                            name = appInfo.name ?: appInfo.packageName,
                            packageName = appInfo.packageName,
                            versionName = "1.0",
                            versionCode = 1,
                            userId = 0,
                            isRunning = virtualCore.isAppRunning(appInfo.packageName, 0)
                        )
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun launchApp(app: VirtualApp) {
        viewModelScope.launch {
            try {
                val virtualCore = VirtualCore.get()
                withContext(Dispatchers.IO) {
                    virtualCore.launchApp(app.packageName, app.userId)
                }
                // Reload to update running state
                loadApps()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun installApp(apkPath: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val virtualCore = VirtualCore.get()
                val success = withContext(Dispatchers.IO) {
                    virtualCore.installApp(apkPath, 0)
                }
                if (success) {
                    loadApps()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun uninstallApp(app: VirtualApp) {
        viewModelScope.launch {
            try {
                val virtualCore = VirtualCore.get()
                withContext(Dispatchers.IO) {
                    virtualCore.uninstallApp(app.packageName)
                }
                loadApps()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun refreshApps() {
        loadApps()
    }
}
