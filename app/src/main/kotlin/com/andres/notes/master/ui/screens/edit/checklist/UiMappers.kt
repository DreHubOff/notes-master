package com.andres.notes.master.ui.screens.edit.checklist

import com.andres.notes.master.core.model.ChecklistItem
import com.andres.notes.master.ui.focus.ElementFocusRequest
import com.andres.notes.master.ui.screens.edit.checklist.model.CheckedListItemUi
import com.andres.notes.master.ui.screens.edit.checklist.model.UncheckedListItemUi

fun ChecklistItem.toUncheckedListItemUi(focusRequest: ElementFocusRequest? = null): UncheckedListItemUi {
    return UncheckedListItemUi(
        id = id,
        text = title,
        focusRequest = focusRequest,
    )
}

fun List<ChecklistItem>.toCheckedListItemsUi(): List<CheckedListItemUi> {
    return filter { it.isChecked }
        .sortedBy { it.listPosition }
        .map {
            CheckedListItemUi(
                id = it.id,
                text = it.title
            )
        }
}

fun List<ChecklistItem>.toUncheckedListItemsUi(
    focusedItemIndex: Int?,
    focusRequest: ElementFocusRequest?,
): List<UncheckedListItemUi> {
    return asSequence()
        .filter { !it.isChecked }
        .sortedBy { it.listPosition }
        .mapIndexed { index, item ->
            item.toUncheckedListItemUi(focusRequest = if (index == focusedItemIndex) focusRequest else null)
        }
        .toList()
}