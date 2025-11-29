package me.achqing.virtuegms.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.achqing.virtuegms.core.Config
import me.achqing.virtuegms.ui.MyIcons

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    var autoCheckUpdate by remember { mutableStateOf(Config.autoCheckUpdate) }
    var selectedLanguage by remember { mutableStateOf(Config.language) }
    var selectedUpdateChannel by remember { mutableStateOf(Config.updateChannel) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showUpdateChannelDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.Top
        ) {
            item {
                SettingsGroup(title = "通用") {
                    SettingsSwitchItem(
                        icon = Icons.Default.Refresh,
                        title = "自动检查更新",
                        description = "启动应用时自动检查更新",
                        checked = autoCheckUpdate,
                        onCheckedChange = {
                            autoCheckUpdate = it
                            Config.autoCheckUpdate = it
                        }
                    )
                }
            }

            // 偏好设置卡片
            item {
                SettingsGroup(title = "偏好设置") {
                    SettingsSelectItem(
                        icon = MyIcons.Translate,
                        title = "语言",
                        description = selectedLanguage,
                        onClick = { showLanguageDialog = true }
                    )

                    SettingsSelectItem(
                        icon = MyIcons.Cloud,
                        title = "更新频道",
                        description = selectedUpdateChannel,
                        onClick = { showUpdateChannelDialog = true }
                    )
                }
            }

            // 关于卡片
            item {
                SettingsGroup(title = "关于") {
                    SettingsItem(
                        icon = Icons.Default.Info,
                        title = "版本信息",
                        description = "1.0.0",
                        onClick = { /* 点击查看版本详情 */ }
                    )

                    SettingsItem(
                        icon = MyIcons.Code,
                        title = "开源许可",
                        description = "查看开源许可证",
                        onClick = { /* 跳转到开源许可页面 */ }
                    )
                }
            }
        }
    }

    // 语言选择对话框
    if (showLanguageDialog) {
        SelectionDialog(
            title = "选择语言",
            options = listOf("简体中文", "English", "日本語", "한국어"),
            selectedOption = selectedLanguage,
            onDismiss = { showLanguageDialog = false },
            onConfirm = { selected ->
                selectedLanguage = selected
                Config.language = selected
                showLanguageDialog = false
            }
        )
    }

    // 更新频道选择对话框
    if (showUpdateChannelDialog) {
        SelectionDialog(
            title = "选择更新频道",
            options = listOf("稳定版", "测试版", "开发版"),
            selectedOption = selectedUpdateChannel,
            onDismiss = { showUpdateChannelDialog = false },
            onConfirm = { selected ->
                selectedUpdateChannel = selected
                Config.updateChannel = selected
                showUpdateChannelDialog = false
            }
        )
    }
}
