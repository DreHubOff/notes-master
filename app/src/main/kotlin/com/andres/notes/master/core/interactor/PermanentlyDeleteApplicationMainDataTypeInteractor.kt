package com.andres.notes.master.core.interactor

import com.andres.notes.master.core.model.ApplicationMainDataType
import com.andres.notes.master.core.model.Checklist
import com.andres.notes.master.core.model.TextNote
import com.andres.notes.master.data.ChecklistRepository
import com.andres.notes.master.data.TextNotesRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

class PermanentlyDeleteApplicationMainDataTypeInteractor @Inject constructor(
    private val textNotesRepository: TextNotesRepository,
    private val checklistRepository: ChecklistRepository,
) {

    suspend operator fun invoke(vararg item: ApplicationMainDataType) {
        val textNotes = mutableListOf<TextNote>()
        val checklists = mutableListOf<Checklist>()
        item.forEach {
            when (it) {
                is TextNote -> textNotes.add(it)
                is Checklist -> checklists.add(it)
            }
        }
        supervisorScope {
            launch { textNotesRepository.permanentlyDelete(textNotes) }
            launch { checklistRepository.delete(checklists) }
        }
    }
}