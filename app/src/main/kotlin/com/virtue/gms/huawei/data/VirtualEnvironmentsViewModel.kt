package com.virtue.gms.huawei.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class VirtualEnvironmentsViewModel : ViewModel() {
    
    private val _environments = MutableStateFlow<List<VirtualEnvironment>>(emptyList())
    val environments: StateFlow<List<VirtualEnvironment>> = _environments.asStateFlow()
    
    init {
        loadEnvironments()
    }
    
    private fun loadEnvironments() {
        viewModelScope.launch {
            // Create default environment
            _environments.value = listOf(
                VirtualEnvironment(
                    id = 0,
                    name = "Default Environment",
                    isActive = true,
                    isolationLevel = IsolationLevel.STANDARD
                )
            )
        }
    }
    
    fun createEnvironment(name: String) {
        viewModelScope.launch {
            val newId = _environments.value.maxOfOrNull { it.id }?.plus(1) ?: 1
            val newEnv = VirtualEnvironment(
                id = newId,
                name = name,
                isActive = false
            )
            _environments.value = _environments.value + newEnv
        }
    }
}
