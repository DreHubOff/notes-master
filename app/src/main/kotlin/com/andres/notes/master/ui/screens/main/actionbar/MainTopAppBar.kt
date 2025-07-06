package com.andres.notes.master.ui.screens.main.actionbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.sharp.Close
import androidx.compose.material.icons.sharp.Menu
import androidx.compose.material.icons.sharp.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.andres.notes.master.R
import com.andres.notes.master.ui.screens.main.model.MainScreenState
import com.andres.notes.master.ui.shared.PinCheckbox
import com.andres.notes.master.ui.theme.themedTopAppBarColors
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainTopAppBar(
    modifier: Modifier = Modifier,
    state: MainScreenState,
    onIntent: (MainActionBarIntent) -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    TopAppBar(
        modifier = modifier,
        scrollBehavior = scrollBehavior,
        colors = themedTopAppBarColors(),
        navigationIcon = {
            when {
                state.searchEnabled -> {
                    IconButton(onClick = { onIntent(MainActionBarIntent.HideSearch) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }

                state.isSelectionMode -> {
                    IconButton(onClick = { onIntent(MainActionBarIntent.HideSelection) }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }

                else -> {
                    IconButton(onClick = { onIntent(MainActionBarIntent.OpenSideMenu) }) {
                        Icon(
                            imageVector = Icons.Sharp.Menu,
                            contentDescription = stringResource(R.string.menu_desc),
                        )
                    }
                }
            }
        },
        title = {
            when {
                state.searchEnabled -> {
                    Search(modifier = modifier, state = state, onIntent = onIntent)
                }

                state.isSelectionMode -> {
                    Text(
                        text = stringResource(R.string.selected_count_pattern, state.selectedItemsCount),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                else -> {
                    // Empty title
                }
            }
        },
        actions = {
            if (state.isSelectionMode) {
                IconButton(onClick = { onIntent(MainActionBarIntent.SelectBackground) }) {
                    Icon(imageVector = Icons.Outlined.Palette, contentDescription = null)
                }
                PinCheckbox(
                    modifier = Modifier,
                    isChecked = state.selectedItemsArePinned,
                    onCheckedChange = { onIntent(MainActionBarIntent.ChangePinnedStateOfSelected(isPinned = it)) },
                    contentDescription = "",
                )
                IconButton(onClick = { onIntent(MainActionBarIntent.MoveToTrashSelected) }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = stringResource(R.string.action_delete),
                    )
                }
            } else if (!state.searchEnabled) {
                IconButton(onClick = { onIntent(MainActionBarIntent.OpenSearch) }) {
                    Icon(imageVector = Icons.Sharp.Search, contentDescription = null)
                }
            }
        }
    )
}

@Composable
private fun Search(
    modifier: Modifier,
    state: MainScreenState,
    onIntent: (MainActionBarIntent) -> Unit,
) {
    var searchPrompt by remember(state.searchPrompt) { mutableStateOf(state.searchPrompt ?: "") }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        delay(500)
        focusRequester.requestFocus()
    }

    TextField(
        value = searchPrompt,
        onValueChange = {
            onIntent(MainActionBarIntent.Search(prompt = it))
            searchPrompt = it
        },
        placeholder = {
            Text(
                stringResource(R.string.search_notes),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
            )
        },
        trailingIcon = {
            if (searchPrompt.isNotEmpty()) {
                IconButton(
                    onClick = {
                        onIntent(MainActionBarIntent.Search(prompt = ""))
                    }) {
                    Icon(
                        imageVector = Icons.Sharp.Close,
                        contentDescription = stringResource(R.string.clear_search_bar_desc)
                    )
                }
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .background(Color.Transparent),
        singleLine = true,
        textStyle = LocalTextStyle.current.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = {
            keyboardController?.hide()
            focusRequester.freeFocus()
            focusManager.clearFocus()
        }),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            cursorColor = MaterialTheme.colorScheme.onSurface,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun Preview() {
    MainTopAppBar(
        state = MainScreenState.EMPTY,
    )
}