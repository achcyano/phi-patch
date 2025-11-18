package com.virtue.gms.huawei.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.virtue.gms.huawei.data.VirtualEnvironment
import com.virtue.gms.huawei.data.VirtualEnvironmentsViewModel

@Composable
fun VirtualEnvironmentsScreen(viewModel: VirtualEnvironmentsViewModel = viewModel()) {
    val environments by viewModel.environments.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        if (environments.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Folder,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "No virtual environments",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "Tap + to create an environment",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(environments) { env ->
                    EnvironmentCard(environment = env)
                }
            }
        }
        
        FloatingActionButton(
            onClick = { viewModel.createEnvironment("New Environment") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Create Environment")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnvironmentCard(environment: VirtualEnvironment) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.Folder,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    environment.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "ID: ${environment.id}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(
                checked = environment.isActive,
                onCheckedChange = null
            )
        }
    }
}
