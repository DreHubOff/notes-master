package com.andres.notes.master.ui.shared.listitem

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.andres.notes.master.demo_data.MainScreenDemoData
import com.andres.notes.master.demo_data.MainScreenDemoData.TextNotes.asCardData
import com.andres.notes.master.ui.theme.ApplicationTheme

private const val MAX_LINES_CONTENT = 10

@Composable
fun TextNoteCard(
    modifier: Modifier,
    item: TextNoteCardData,
    onClick: (() -> Unit)? = null,
    onLongClick: (() -> Unit)? = null,
    itemStatus: @Composable (RowScope.() -> Unit)? = null,
) {
    MainItemContainer(
        modifier = modifier,
        cardTransitionKey = item.transitionKey,
        title = item.title,
        isSelected = item.isSelected,
        onClick = onClick,
        onLongClick = onLongClick,
        itemStatus = itemStatus,
        customBackground = item.customBackground,
        content = { contentModifier ->
            if (item.content.isNotEmpty()) {
                ContentText(modifier = contentModifier, text = item.content)
            }
        }
    )
}

@Composable
private fun ContentText(modifier: Modifier, text: String) {
    Text(
        modifier = modifier,
        text = text,
        color = MaterialTheme.colorScheme.onSurface,
        maxLines = MAX_LINES_CONTENT,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 17.sp,
        letterSpacing = 0.1.sp
    )
}

@Preview
@Composable
private fun Preview(@PreviewParameter(MainTextNoteStateProvider::class) state: TextNoteCardData) {
    ApplicationTheme {
        TextNoteCard(Modifier.padding(8.dp), state)
    }
}

private class MainTextNoteStateProvider : PreviewParameterProvider<TextNoteCardData> {
    override val values: Sequence<TextNoteCardData>
        get() = sequenceOf(
            MainScreenDemoData.TextNotes.welcomeBanner.asCardData(),
            MainScreenDemoData.TextNotes.reminderPinnedNote.asCardData(),
            MainScreenDemoData.TextNotes.emptyTitleNote.asCardData(),
            MainScreenDemoData.TextNotes.reminderPinnedNoteEmptyTitle.asCardData(),
            MainScreenDemoData.TextNotes.reminderPinnedNoteLongTitle.asCardData(),
            MainScreenDemoData.TextNotes.reminderPinnedNoteLongTitle.asCardData(),
            MainScreenDemoData.TextNotes.pinnedOnlyNote.asCardData(),
            MainScreenDemoData.TextNotes.reminderOnlyNote.asCardData(),
            MainScreenDemoData.TextNotes.emptyContentNote.asCardData(),
        )
}