@file:OptIn(ExperimentalMaterial3Api::class)

package com.andres.notes.master.ui.screens.main.actionbar

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.andres.notes.master.ui.screens.main.model.MainScreenItem
import com.andres.notes.master.ui.screens.main.model.MainScreenState
import com.andres.notes.master.ui.screens.main.search.MainSearchBar
import com.andres.notes.master.ui.screens.main.search.MainSearchBarEntryPoint
import com.andres.notes.master.ui.screens.main.selection.SelectionActionBar
import com.andres.notes.master.ui.theme.ApplicationTheme

@Composable
fun MainActionBar(
    modifier: Modifier = Modifier,
    state: MainScreenState,
    onEvent: (MainActionBarIntent) -> Unit = {},
) {
    val windowInsets = WindowInsets.systemBars
    when {
        state.isSelectionMode -> SelectionModeToolbar(
            modifier = modifier,
            windowInsets = windowInsets,
            isPinned = state.selectedItemsArePinned,
            selectedItemCount = state.selectedItemsCount,
            onEvent = onEvent,
        )

        state.searchEnabled -> SearchModeToolbar(
            modifier = modifier,
            searchPrompt = state.searchPrompt.orEmpty(),
            windowInsets = windowInsets,
            onEvent = onEvent,
        )

        else -> RegularModeToolbar(
            modifier = modifier,
            windowInsets = windowInsets,
            onEvent = onEvent,
        )
    }
}

@Composable
private fun RegularModeToolbar(
    modifier: Modifier,
    windowInsets: WindowInsets,
    onEvent: (MainActionBarIntent) -> Unit,
) {
    MainSearchBarEntryPoint(
        modifier = modifier,
        innerPadding = PaddingValues(top = windowInsets.asPaddingValues().calculateTopPadding()),
        onSearchClick = { onEvent(MainActionBarIntent.OpenSearch) },
        onOpenMenuClick = { onEvent(MainActionBarIntent.OpenSideMenu) }
    )
}

@Composable
private fun SearchModeToolbar(
    modifier: Modifier,
    searchPrompt: String,
    windowInsets: WindowInsets,
    onEvent: (MainActionBarIntent) -> Unit,
) {
    MainSearchBar(
        modifier = modifier,
        innerPadding = PaddingValues(top = windowInsets.asPaddingValues().calculateTopPadding()),
        searchPrompt = searchPrompt,
        onHideSearch = { onEvent(MainActionBarIntent.HideSearch) },
        onValueChanged = { onEvent(MainActionBarIntent.Search(it)) },
    )
}

@Composable
private fun SelectionModeToolbar(
    modifier: Modifier,
    windowInsets: WindowInsets,
    isPinned: Boolean,
    selectedItemCount: Int,
    onEvent: (MainActionBarIntent) -> Unit,
) {
    SelectionActionBar(
        modifier = modifier,
        innerPadding = PaddingValues(top = windowInsets.asPaddingValues().calculateTopPadding()),
        selectedItemCount = selectedItemCount,
        isPinned = isPinned,
        onExitSelectionMode = { onEvent(MainActionBarIntent.HideSelection) },
        onMoveToTrashClick = { onEvent(MainActionBarIntent.MoveToTrashSelected) },
        onPinnedStateChanged = { onEvent(MainActionBarIntent.ChangePinnedStateOfSelected(it)) },
        onSelectBackgroundClick = { onEvent(MainActionBarIntent.SelectBackground) },
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun Idle() {
    ApplicationTheme {
        Scaffold(
            topBar = {
                MainActionBar(state = MainScreenState.Companion.EMPTY)
            },
        ) { _ ->
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun Search() {
    ApplicationTheme {
        Scaffold(
            topBar = {
                MainActionBar(
                    state = MainScreenState.Companion.EMPTY.copy(
                        searchEnabled = true,
                        searchPrompt = "Search for..."
                    ),
                )
            },
        ) { _ ->
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Preview
@Composable
private fun Selection() {
    ApplicationTheme {
        Scaffold(
            topBar = {
                MainActionBar(
                    state = MainScreenState.Companion.EMPTY.copy(
                        screenItems = listOf(
                            MainScreenItem.TextNote(
                                id = 0,
                                title = "",
                                content = "",
                                isSelected = true
                            ),
                            MainScreenItem.TextNote(
                                id = 1,
                                title = "",
                                content = "",
                                isSelected = true
                            )
                        )
                    ),
                )
            },
        ) { _ ->
        }
    }
}
