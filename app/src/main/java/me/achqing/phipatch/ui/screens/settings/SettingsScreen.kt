package me.achqing.phipatch.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.achqing.phipatch.core.Config
import me.achqing.phipatch.ui.MyIcons
import me.achqing.phipatch.utils.SelectionDialog

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues = PaddingValues(0.dp) // 外层 Scaffold 的内边距
) {
    var autoCheckUpdate by remember { mutableStateOf(Config.autoCheckUpdate) }
    var selectedLanguage by remember { mutableStateOf(Config.language) }
    var selectedUpdateChannel by remember { mutableStateOf(Config.updateChannel) }

    var showLanguageDialog by remember { mutableStateOf(false) }
    var showUpdateChannelDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Top,
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        item {
            SettingsSection(title = "通用") {

                SettingsSwitchCard(
                    icon = Icons.Default.Refresh,
                    title = "自动检查更新",
                    checked = autoCheckUpdate,
                    onCheckedChange = {
                        autoCheckUpdate = it
                        Config.autoCheckUpdate = it
                    }
                )

            }
        }

        item {
            SettingsSection(title = "偏好设置") {

                SettingsSelectCard(
                    icon = MyIcons.Translate,
                    title = "语言",
                    value = selectedLanguage,
                    onClick = { showLanguageDialog = true }
                )
                SettingsSelectCard(
                    icon = MyIcons.Cloud,
                    title = "更新频道",
                    value = selectedUpdateChannel,
                    onClick = { showUpdateChannelDialog = true }
                )
            }
        }

        item {
            SettingsSection(title = "关于") {
                SettingsItemCard(
                    icon = Icons.Default.Info,
                    title = "版本信息",
                    description = "1.0.0",
                    onClick = { /* 点击查看版本详情 */ }
                )

                SettingsItemCard(
                    icon = MyIcons.Code,
                    title = "开源许可",
                    onClick = { /* 跳转到开源许可页面 */ }
                )

                SettingsExpandableGroup(
                    title = "高级设置",
                    icon = MyIcons.Cloud
                ) {
                    SettingsItemEntry(
                        title = "SettingsItemEntry",

                        onClick = { /* 打开文件选择器 */ }
                    )
                    SettingsSelectEntry(

                        title = "更新频道",

                        value = "稳定版",
                        onClick = { /* 打开选择弹窗 */ }
                    )
                    SettingsSwitchEntry(
                        title = "启用安全模式",

                        checked = true,
                        onCheckedChange = { /* 更新状态 */ })
                }
            }
        }
    }

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