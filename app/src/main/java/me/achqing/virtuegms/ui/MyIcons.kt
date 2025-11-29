package me.achqing.virtuegms.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import me.achqing.virtuegms.R

object MyIcons{
    val Translate: ImageVector
        @Composable
        get() = ImageVector.Companion.vectorResource(id = R.drawable.translate)

    val Cloud: ImageVector
        @Composable
        get() = ImageVector.Companion.vectorResource(id = R.drawable.cloud)

    val Code: ImageVector
        @Composable
        get() = ImageVector.Companion.vectorResource(id = R.drawable.code)

    val ChevronRight: ImageVector
        @Composable
        get() = ImageVector.Companion.vectorResource(id = R.drawable.chevron_right)
}