package com.andres.notes.master.ui.screens.trash.listitem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.andres.notes.master.ui.screens.trash.model.TrashListItem
import com.andres.notes.master.ui.shared.listitem.TextNoteCard
import com.andres.notes.master.ui.shared.listitem.TextNoteCardData
import com.andres.notes.master.ui.shared.rememberTextNoteToEditorTransitionKey
import com.andres.notes.master.ui.theme.ApplicationTheme
import com.andres.notes.master.ui.theme.WinkySansFontFamily

@Composable
fun TrashTextNote(
    modifier: Modifier,
    item: TrashListItem.TextNote,
    onClick: () -> Unit,
) {
    val cardTransitionKey =
        rememberTextNoteToEditorTransitionKey(noteId = item.id)
    val cardData = remember(item, cardTransitionKey) {
        TextNoteCardData(
            transitionKey = cardTransitionKey,
            title = item.title,
            content = item.content,
            isSelected = false,
            customBackground = item.customBackground,
        )
    }
    TextNoteCard(
        modifier = modifier,
        item = cardData,
        onClick = onClick,
        itemStatus = {
            Text(
                item.daysLeftMessage,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = WinkySansFontFamily,
            )
        }
    )
}

@Preview
@Composable
private fun Preview() {
    ApplicationTheme {
        TrashTextNote(
            modifier = Modifier,
            item = TrashListItem.TextNote(
                id = 1,
                title = "Title",
                content = "Some content in this text note",
                daysLeftMessage = "2 day left",
                customBackground = null,
            ),
            onClick = {},
        )
    }
}