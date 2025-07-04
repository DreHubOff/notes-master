package com.andres.notes.master.core.interactor

import com.andres.notes.master.core.model.ApplicationMainDataType
import com.andres.notes.master.core.model.SortableListItem
import com.andres.notes.master.data.ChecklistRepository
import com.andres.notes.master.data.TextNotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import kotlin.sequences.flatten

class ObserveApplicationMainTypeTrashedInteractor @Inject constructor(
    private val textNotesRepository: TextNotesRepository,
    private val checklistRepository: ChecklistRepository,
) {

    operator fun invoke(): Flow<List<ApplicationMainDataType>> {
        return combine(
            flow = textNotesRepository.observeTrashedNotes(),
            flow2 = checklistRepository.observeTrashedChecklists()
        ) { textNotes, checklists ->
            sequenceOf(textNotes, checklists)
                .flatten()
                .sortedByModificationDate()
                .toList()
        }
    }

    private fun <T : SortableListItem> Sequence<T>.sortedByModificationDate(): Sequence<T> =
        this.sortedWith(compareByDescending { it.creationDate })
}