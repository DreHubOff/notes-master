package com.andres.notes.master.ui.animation

import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.graphics.TransformOrigin
import com.andres.notes.master.ui.shared.defaultTransitionAnimationDuration

fun defaultAnimationSpec(): TweenSpec<Float> = tween(durationMillis = defaultTransitionAnimationDuration)

fun scaleInFromBottomRight() =
    scaleIn(animationSpec = defaultAnimationSpec(), transformOrigin = TransformOrigin(1f, 1f))

fun scaleOutToBottomRight() =
    scaleOut(animationSpec = defaultAnimationSpec(), transformOrigin = TransformOrigin(1f, 1f))