package com.andres.notes.master.core.interactor

import com.andres.notes.master.core.model.MainTypeTextRepresentation
import com.andres.notes.master.data.ChecklistRepository
import javax.inject.Inject

class BuildTextFromChecklistInteractor @Inject constructor(
    private val checklistRepository: ChecklistRepository,
) {

    suspend operator fun invoke(checklistId: Long): MainTypeTextRepresentation? {
        val checklist = checklistRepository.getChecklistById(checklistId) ?: return null
        val content = buildString {
            val checkedItems = checklist
                .items
                .sortedBy { it.listPosition }
                .mapNotNull { item ->
                    if (item.isChecked) {
                        item
                    } else {
                        append("[ ] ")
                        appendLine(item.title)
                        null
                    }
                }
            checkedItems.forEach { item ->
                append("[X] ")
                appendLine(item.title)
            }
        }
        return MainTypeTextRepresentation(title = checklist.title, content = content)
    }
}