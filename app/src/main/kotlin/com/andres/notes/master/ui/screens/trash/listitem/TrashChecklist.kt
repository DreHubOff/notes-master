package com.andres.notes.master.ui.screens.trash.listitem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.andres.notes.master.core.model.NoteColor
import com.andres.notes.master.ui.screens.trash.model.TrashListItem
import com.andres.notes.master.ui.shared.listitem.ChecklistCard
import com.andres.notes.master.ui.shared.listitem.ChecklistCardData
import com.andres.notes.master.ui.shared.rememberChecklistToEditorTransitionKey
import com.andres.notes.master.ui.theme.ApplicationTheme

@Composable
fun TrashChecklist(
    modifier: Modifier,
    item: TrashListItem.Checklist,
    onClick: () -> Unit,
) {
    val cardTransitionKey =
        rememberChecklistToEditorTransitionKey(checklistId = item.id)
    val cardData = remember(item, cardTransitionKey) {
        ChecklistCardData(
            transitionKey = cardTransitionKey,
            title = item.title,
            items = item.items,
            tickedItemsCount = item.tickedItems,
            isSelected = false,
            customBackground = item.customBackground,
        )
    }
    ChecklistCard(
        modifier = modifier,
        item = cardData,
        onClick = onClick,
        itemStatus = {
            Text(
                item.daysLeftMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        TrashChecklist(
            modifier = Modifier,
            item = TrashListItem.Checklist(
                id = 1,
                title = "Title",
                items = listOf(
                    "1. Some content in this text note",
                    "2. Some content in this text note",
                    "3. Some content in this text note",
                    "4. Some content in this text note",
                ),
                daysLeftMessage = "2 day left",
                tickedItems = 2,
                customBackground = NoteColor.Lime,
            ),
            onClick = {},
        )
    }
}