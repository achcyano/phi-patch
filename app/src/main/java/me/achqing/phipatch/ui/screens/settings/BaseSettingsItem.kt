package me.achqing.phipatch.ui.screens.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.achqing.phipatch.ui.MyIcons

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 22.sp,
        modifier = Modifier.padding(vertical = 6.dp),
        textAlign = TextAlign.Left
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        content()
    }
}

@Composable
fun SettingsCard(
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier.padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
        ),
    ) {
        content()
    }
}

@Composable
fun SettingsItemContent(
    icon: ImageVector?,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    onEntry: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (trailing == null && !onEntry)
                    Modifier.padding(vertical = 16.dp, horizontal = 16.dp)
                else
                    Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null)
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.bodyLarge)
            if (description.isNotEmpty()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
            }
        }
        trailing?.invoke() ?: Box(Modifier.size(22.dp), contentAlignment = Alignment.Center) {
            Icon(
                imageVector = MyIcons.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
fun SettingsSwitchContent(
    icon: ImageVector?,
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    SettingsItemContent(
        icon = icon,
        title = title,
        description = description,
        trailing = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

        },
        modifier = Modifier.padding(vertical = 5.dp, horizontal = 16.dp)
    )
}

@Composable
fun SettingsSelectContent(
    icon: ImageVector?,
    title: String,
    description: String,
    value: String,
) {
    SettingsItemContent(
        icon = icon,
        title = title,
        description = description,
        trailing = {
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        modifier = Modifier.padding(vertical = 17.dp, horizontal = 16.dp)
    )
}

@Composable
fun SettingsItemCard(
    icon: ImageVector? = null,
    title: String,
    description: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
    trailing: @Composable (() -> Unit)? = null
) {
    SettingsCard(enabled = enabled, onClick = onClick) {
        SettingsItemContent(icon, title, description, trailing = trailing)
    }
}

@Composable
fun SettingsSwitchCard(
    icon: ImageVector? = null,
    title: String,
    description: String = "",
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingsCard(enabled = enabled) {
        SettingsSwitchContent(
            icon = icon,
            title = title,
            description = description,
            checked = checked,
            enabled = enabled,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
fun SettingsSelectCard(
    icon: ImageVector? = null,
    title: String,
    description: String = "",
    value: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    SettingsCard(enabled = enabled, onClick = onClick) {
        SettingsSelectContent(icon, title, description, value)
    }
}

@Composable
fun SettingsExpandableGroup(
    title: String,
    description: String? = null,
    icon: ImageVector? = null,
    initiallyExpanded: Boolean = false,
    enabled: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    var expanded by remember { mutableStateOf(initiallyExpanded) }

    Card(
        enabled = enabled,
        onClick = {},
        modifier = Modifier.padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.onSurface,
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.5f),
        ),
    ) {
        Column(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 17.dp, top = 16.dp, bottom = 16.dp)
                    .clickable(
                        enabled = enabled,
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = { expanded = !expanded }
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(13.dp))
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = title, style = MaterialTheme.typography.bodyLarge)
                    if (!description.isNullOrEmpty()) {
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }

                Icon(
                    imageVector = if (expanded) MyIcons.KeyboardArrowUp else MyIcons.KeyBoardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, top = 0.dp, bottom = 0.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp),
                    content = { content() }
                )
            }
        }
    }
}

@Composable
fun SettingsItemEntry(
    icon: ImageVector? = null,
    title: String,
    description: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    SettingsCard(
        enabled = enabled,
        onClick = onClick
    ) {
        SettingsItemContent(
            icon = icon,
            title = title,
            description = description,
            modifier = Modifier
                .then(
                    if (icon == null)
                        Modifier.padding(start = 0.dp, end = 12.dp)
                    else
                        Modifier.padding(start = 12.dp, end = 12.dp)
                    ).padding(vertical = 15.dp),
            onEntry = true
        )
    }
}

/**
 * 组内条目：右侧显示当前选择值，点击触发选择对话框/页面。
 * 复用 SettingsItemContent 的 trailing，显示 value。
 */
@Composable
fun SettingsSelectEntry(
    icon: ImageVector? = null,
    title: String,
    description: String = "",
    value: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    SettingsCard(
        enabled = enabled,
        onClick = onClick,
    ) {
        SettingsItemContent(
            icon = icon,
            title = title,
            description = description,
            modifier = Modifier
                .then(
                    if (icon == null)
                        Modifier.padding(start = 0.dp, end = 12.dp)
                    else
                        Modifier.padding(start = 12.dp, end = 12.dp)
                )
                .padding(vertical = 15.dp),
            trailing = {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            onEntry = false
        )
    }
}

/**
 * 组内条目：右侧是开关，行本身不处理点击，开关独立交互。
 * 复用 SettingsSwitchContent。
 */
@Composable
fun SettingsSwitchEntry(
    icon: ImageVector? = null,
    title: String,
    description: String = "",
    checked: Boolean,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit
) {
    SettingsCard {
        SettingsItemContent(
            icon = icon,
            title = title,
            description = description,
            trailing = {
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = enabled,
                )
            },
            onEntry = true,
            modifier = Modifier
                .then(
                    if (icon == null)
                        Modifier.padding(start = 0.dp, end = 12.dp)
                    else
                        Modifier.padding(start = 12.dp, end = 12.dp)
                )
                .padding(start = 0.dp, end = 12.dp)
        )
    }
}