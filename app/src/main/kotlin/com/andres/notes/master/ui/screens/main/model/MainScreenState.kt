package com.andres.notes.master.ui.screens.main.model

import com.andres.notes.master.ui.focus.ElementFocusRequest
import com.andres.notes.master.ui.shared.SnackbarEvent

data class MainScreenState(
    val screenItems: List<MainScreenItem>,
    val searchPrompt: String?,
    val addItemsMode: Boolean,
    val snackbarEvent: SnackbarEvent?,
    val searchEnabled: Boolean = searchPrompt != null,
    val showNavigationOverlay: ElementFocusRequest?,
    val openSideMenuEvent: ElementFocusRequest?,
    val selectedItemsArePinned: Boolean,
    val backgroundSelectionData: BackgroundSelectionData?,
    val themeSelectorData: ThemeSelectorData?,
) {

    val selectedItemsCount: Int by lazy { screenItems.count { it.isSelected } }
    val isSelectionMode: Boolean get() = selectedItemsCount > 0

    companion object {
        val EMPTY: MainScreenState = MainScreenState(
            screenItems = emptyList(),
            searchPrompt = null,
            addItemsMode = false,
            snackbarEvent = null,
            showNavigationOverlay = null,
            openSideMenuEvent = null,
            selectedItemsArePinned = false,
            backgroundSelectionData = null,
            themeSelectorData = null,
        )
    }
}