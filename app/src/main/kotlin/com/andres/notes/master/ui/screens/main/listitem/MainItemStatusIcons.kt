@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.andres.notes.master.ui.screens.main.listitem

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Alarm
import androidx.compose.material.icons.sharp.AlarmOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.andres.notes.master.R

@Composable
fun MainItemStatusIcons(
    isPinned: Boolean = false,
    reminderCompleted: Boolean = false,
    hasScheduledReminder: Boolean = false,
) {
    if (hasScheduledReminder) {
        Icon(
            imageVector = Icons.Sharp.run { if (reminderCompleted) AlarmOff else Alarm },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
    if (isPinned) {
        Icon(
            modifier = Modifier,
            painter = painterResource(R.drawable.ic_material_keep_filled),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface
        )
    }
}