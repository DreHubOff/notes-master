package com.andres.notes.master.demo_data

import com.andres.notes.master.ui.focus.ElementFocusRequest
import com.andres.notes.master.ui.screens.edit.checklist.model.CheckedListItemUi
import com.andres.notes.master.ui.screens.edit.checklist.model.UncheckedListItemUi

object EditChecklistDemoData {

    val uncheckedChecklistItems = listOf(
        UncheckedListItemUi(
            id = 1,
            text = "🛒 Buy groceries for the week",
            focusRequest = null
        ),
        UncheckedListItemUi(
            id = 2,
            text = "📞 Call Mom and check in",
            focusRequest = ElementFocusRequest(),
        ),
        UncheckedListItemUi(
            id = 3,
            text = "📚 Read 20 pages of a book",
            focusRequest = null,
        ),
        UncheckedListItemUi(
            id = 4,
            text = "🏃 Go for a 30-minute jog",
            focusRequest = null,
        ),
        UncheckedListItemUi(
            id = 5,
            text = "💻 Finish coding the checklist feature",
            focusRequest = null,
        )
    )

    val checkedChecklistItems = listOf(
        CheckedListItemUi(
            id = 1,
            text = "🛒 Buy groceries for the week",
        ),
        CheckedListItemUi(
            id = 2,
            text = "📞 Call Mom and check in",
        ),
        CheckedListItemUi(
            id = 3,
            text = "📚 Read 20 pages of a book",
        ),
        CheckedListItemUi(
            id = 4,
            text = "🏃 Go for a 30-minute jog",
        ),
        CheckedListItemUi(
            id = 5,
            text = "💻 Finish coding the checklist feature",
        )
    )
}