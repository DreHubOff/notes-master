package com.andres.notes.master.ui.screens.main.listitem

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.andres.notes.master.demo_data.MainScreenDemoData
import com.andres.notes.master.ui.screens.main.model.MainScreenItem
import com.andres.notes.master.ui.shared.listitem.TextNoteCard
import com.andres.notes.master.ui.shared.listitem.TextNoteCardData
import com.andres.notes.master.ui.shared.rememberTextNoteToEditorTransitionKey
import com.andres.notes.master.ui.theme.ApplicationTheme

@Composable
fun MainTextNote(
    modifier: Modifier,
    item: MainScreenItem.TextNote,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
) {
    val cardTransitionKey =
        rememberTextNoteToEditorTransitionKey(noteId = item.id)
    val cardData = remember(item, cardTransitionKey) {
        TextNoteCardData(
            transitionKey = cardTransitionKey,
            title = item.title,
            content = item.content,
            isSelected = item.isSelected,
            customBackground = item.customBackground,
        )
    }
    TextNoteCard(
        modifier = modifier,
        item = cardData,
        onClick = if (item.interactive) onClick else null,
        onLongClick = if (item.interactive) onLongClick else null,
        itemStatus = {
            MainItemStatusIcons(
                isPinned = item.isPinned,
                hasScheduledReminder = item.hasScheduledReminder,
                reminderCompleted = item.reminderCompleted,
            )
        }
    )
}

@Preview
@Composable
private fun Preview(@PreviewParameter(MainTextNoteStateProvider::class) state: MainScreenItem.TextNote) {
    ApplicationTheme {
        MainTextNote(Modifier.padding(8.dp), state)
    }
}

private class MainTextNoteStateProvider : PreviewParameterProvider<MainScreenItem.TextNote> {
    override val values: Sequence<MainScreenItem.TextNote>
        get() = sequenceOf(
            MainScreenDemoData.TextNotes.welcomeBanner,
            MainScreenDemoData.TextNotes.reminderPinnedNote,
            MainScreenDemoData.TextNotes.emptyTitleNote,
            MainScreenDemoData.TextNotes.reminderPinnedNoteEmptyTitle,
            MainScreenDemoData.TextNotes.reminderPinnedNoteLongTitle,
            MainScreenDemoData.TextNotes.reminderPinnedNoteLongTitle,
            MainScreenDemoData.TextNotes.pinnedOnlyNote,
            MainScreenDemoData.TextNotes.reminderOnlyNote,
            MainScreenDemoData.TextNotes.emptyContentNote,
        )
}