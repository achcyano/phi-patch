package me.achqing.phipatch

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import me.achqing.phipatch.core.UpdateChecker.isUpdateAvailable
import me.achqing.phipatch.ui.navigation.AppNavigation
import me.achqing.phipatch.ui.theme.MainTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainTheme {
                AppNavigation()
            }
        }
    }
}