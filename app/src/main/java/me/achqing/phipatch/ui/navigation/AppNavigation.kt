package me.achqing.phipatch.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.achqing.phipatch.ui.anim.navEnterPopTransition
import me.achqing.phipatch.ui.anim.navEnterTransition
import me.achqing.phipatch.ui.anim.navExitPopTransition
import me.achqing.phipatch.ui.anim.navExitTransition
import me.achqing.phipatch.ui.screens.home.HomeScreen
import me.achqing.phipatch.ui.screens.settings.SettingsScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") },
                modifier = Modifier.padding(horizontal = 0.dp, vertical = 0.dp)
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                    label = { Text("首页") },
                    selected = selectedItem == 0,
                    onClick = {
                        selectedItem = 0
                        navController.navigate("home")
                    }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = "设置") },
                    label = { Text("设置") },

                    selected = selectedItem == 1,
                    onClick = {
                        selectedItem = 1
                        navController.navigate("settings")
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            enterTransition = { navEnterTransition() },
            exitTransition = { navExitTransition() },
            popEnterTransition = { navEnterPopTransition() },
            popExitTransition = { navExitPopTransition() }
        ) {
            composable("home") { HomeScreen() }
            composable("settings") {
                SettingsScreen(contentPadding = innerPadding)
            }
        }
    }
}