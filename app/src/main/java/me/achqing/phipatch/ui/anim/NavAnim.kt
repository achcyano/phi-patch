package me.achqing.phipatch.ui.anim

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin

fun navEnterTransition(duration: Int = 200): EnterTransition {
    return fadeIn(
        animationSpec = tween(durationMillis = duration),
        initialAlpha = 0f
    ) + scaleIn(
        animationSpec = tween(durationMillis = duration),
        initialScale = 0.9f,
        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 0.5f)
    )
}

fun navEnterPopTransition(duration: Int = 200): EnterTransition {
    return fadeIn(
        animationSpec = tween(durationMillis = duration),
        initialAlpha = 0f
    ) + scaleIn(
        animationSpec = tween(durationMillis = duration),
        initialScale = 1.1f,
        transformOrigin = TransformOrigin(0.5f, 0.5f)
    )
}

fun navExitTransition(duration: Int = 200): ExitTransition {
    return fadeOut(
        animationSpec = tween(durationMillis = duration),
        targetAlpha = 0f
    ) + scaleOut(
        animationSpec = tween(durationMillis = duration),
        targetScale = 1.1f,
        transformOrigin = TransformOrigin(0.5f, 0.5f)
    )
}

fun navExitPopTransition(duration: Int = 200): ExitTransition {
    return fadeOut(
        animationSpec = tween(durationMillis = duration),
        targetAlpha = 0f
    ) + scaleOut(
        animationSpec = tween(durationMillis = duration),
        targetScale = 0.9f,
        transformOrigin = TransformOrigin(0.5f, 0.5f)
    )
}

