package me.achqing.virtuegms.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import me.achqing.virtuegms.ui.anim.fragmentEnterPopTransition
import me.achqing.virtuegms.ui.anim.fragmentEnterTransition
import me.achqing.virtuegms.ui.anim.fragmentExitPopTransition
import me.achqing.virtuegms.ui.anim.fragmentExitTransition
import me.achqing.virtuegms.ui.screens.settings.SettingsScreen

//import me.achqing.virtuegms.ui.screens.settings.SettingsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
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
            startDestination = "settings",
            modifier = Modifier.padding(innerPadding),
            enterTransition = { fragmentEnterTransition() },
            exitTransition = { fragmentExitTransition() },
            popEnterTransition = { fragmentEnterPopTransition() },
            popExitTransition = { fragmentExitPopTransition() }
        ) {
            composable("home") { /* TODO 首页内容 */ }
            composable("settings") { SettingsScreen() }
        }
    }
}