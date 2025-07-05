@file:OptIn(ExperimentalMaterial3Api::class)

package com.andres.notes.master.ui.shared

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andres.notes.master.LocalThemeMode
import com.andres.notes.master.R
import com.andres.notes.master.ThemeMode
import com.andres.notes.master.ui.theme.ApplicationTheme
import com.andres.notes.master.ui.theme.themedDropdownMenuItemColors
import com.andres.notes.master.util.lighten

object ThemedDropdownMenu {

    data class Action(
        val text: String,
        val onClick: () -> Unit,
    )
}

@Composable
fun ThemedDropdownMenu(
    modifier: Modifier = Modifier,
    actions: List<ThemedDropdownMenu.Action>,
) {
    var dropDownMenuExpanded by remember { mutableStateOf(false) }

    IconButton(onClick = { dropDownMenuExpanded = true }) {
        Icon(Icons.Sharp.MoreVert, contentDescription = stringResource(R.string.menu_desc))
    }

    val backgroundColor = key(MaterialTheme.colorScheme.background) {
        MaterialTheme.colorScheme.background.lighten(
            if (LocalThemeMode.current == ThemeMode.DARK) 0.15f else 0.3f
        )
    }

    DropdownMenu(
        modifier = modifier
            .padding(horizontal = 10.dp)
            .defaultMinSize(minWidth = 170.dp),
        offset = DpOffset(x = (-12).dp, y = (-8).dp),
        shape = MaterialTheme.shapes.large,
        expanded = dropDownMenuExpanded,
        onDismissRequest = { dropDownMenuExpanded = false },
        containerColor = backgroundColor,
    ) {
        actions.forEach { (actionText, onClick) ->
            DropdownMenuItem(
                colors = themedDropdownMenuItemColors(),
                text = {
                    Text(
                        text = actionText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                onClick = {
                    dropDownMenuExpanded = false
                    onClick()
                },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
private fun ThemedDropdownMenuPreview() {
    ApplicationTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    modifier = Modifier.fillMaxWidth(),
                    title = { },
                    navigationIcon = { },
                    actions = {
                        ThemedDropdownMenu(
                            actions = listOf(
                                ThemedDropdownMenu.Action("Settings") { println("Settings clicked") },
                                ThemedDropdownMenu.Action("Help") { println("Help clicked") },
                                ThemedDropdownMenu.Action("Logout") { println("Logout clicked") }
                            )
                        )
                    }
                )
            }
        ) { paddingValues ->
        }
    }
}