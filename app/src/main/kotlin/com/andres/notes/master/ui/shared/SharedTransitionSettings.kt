@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.andres.notes.master.ui.shared

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope

class SharedTransitionSettings(
    val transitionScope: SharedTransitionScope,
    val animationScope: AnimatedContentScope,
)