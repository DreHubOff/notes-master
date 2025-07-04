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
import com.andres.notes.master.ui.shared.listitem.ChecklistCard
import com.andres.notes.master.ui.shared.listitem.ChecklistCardData
import com.andres.notes.master.ui.shared.rememberChecklistToEditorTransitionKey
import com.andres.notes.master.ui.theme.ApplicationTheme

@Composable
fun MainChecklist(
    modifier: Modifier,
    item: MainScreenItem.Checklist,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null,
) {
    val cardTransitionKey = rememberChecklistToEditorTransitionKey(checklistId = item.id)
    val rememberChecklistCardData = remember(item, cardTransitionKey) {
        ChecklistCardData(
            transitionKey = cardTransitionKey,
            title = item.title,
            items = item.items.map { it.text },
            tickedItemsCount = item.tickedItems,
            isSelected = item.isSelected,
            customBackground = item.customBackground,
        )
    }
    ChecklistCard(
        modifier = modifier,
        item = rememberChecklistCardData,
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
private fun Preview(@PreviewParameter(MainCheckListStateProvider::class) state: MainScreenItem.Checklist) {
    ApplicationTheme {
        MainChecklist(modifier = Modifier.padding(8.dp), item = state)
    }
}

private class MainCheckListStateProvider : PreviewParameterProvider<MainScreenItem.Checklist> {
    override val values: Sequence<MainScreenItem.Checklist>
        get() = sequenceOf(
            MainScreenDemoData.CheckLists.reminderPinnedChecklist,
            MainScreenDemoData.CheckLists.reminderOnlyChecklist,
            MainScreenDemoData.CheckLists.pinnedOnlyChecklist,
            MainScreenDemoData.CheckLists.emptyTitleChecklist,
            MainScreenDemoData.CheckLists.emptyContentChecklist,
            MainScreenDemoData.CheckLists.longTitleChecklist,
        )
}