package com.andres.notes.master.ui.screens.edit.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Alarm
import androidx.compose.material.icons.sharp.AlarmOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andres.notes.master.LocalThemeMode
import com.andres.notes.master.ThemeMode

private val reminderTranslationX = (-4).dp

@Composable
fun ReminderButton(
    modifier: Modifier,
    reminderData: ReminderStateData,
    onClick: () -> Unit,
) {
    val reminderColor = if (LocalThemeMode.current == ThemeMode.DARK) {
        reminderData.reminderColorNight
    } else {
        reminderData.reminderColorDay
    }
    Button(
        modifier = modifier.graphicsLayer {
            translationX = reminderTranslationX.toPx()
        },
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
        colors = ButtonDefaults.buttonColors().copy(
            containerColor = reminderColor,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    ) {
        val contentColor = key(reminderData.outdated) {
            MaterialTheme.colorScheme.onSurfaceVariant.let {
                if (reminderData.outdated) it.copy(alpha = 0.6f) else it
            }
        }
        Icon(
            imageVector = if (reminderData.outdated) Icons.Sharp.AlarmOff else Icons.Sharp.Alarm,
            tint = contentColor,
            contentDescription = null
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = reminderData.dateString,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = contentColor,
        )
    }
}