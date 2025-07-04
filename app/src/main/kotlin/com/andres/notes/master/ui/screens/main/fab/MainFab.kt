package com.andres.notes.master.ui.screens.main.fab

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.andres.notes.master.R
import com.andres.notes.master.ui.theme.ApplicationTheme

@Composable
fun MainFab(
    clicked: Boolean = false,
    onClick: () -> Unit = {},
) {
    Box(
        modifier = Modifier.wrapContentSize()
    ) {
        FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.background,
            onClick = { onClick() },
        ) {
            val iconRotation by animateFloatAsState(
                targetValue = if (clicked) 45f else 0f,
                animationSpec = tween(durationMillis = 250),
                label = "MainFabRotationAnim"
            )
            Icon(
                modifier = Modifier.graphicsLayer {
                    this.rotationZ = iconRotation
                },
                imageVector = Icons.Sharp.Add,
                contentDescription = stringResource(R.string.main_floating_button_desc)
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        MainFab()
    }
}