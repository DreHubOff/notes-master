package com.andres.notes.master.core.model

import kotlinx.serialization.Serializable

@Serializable
data class ChecklistItem(
    val id: Long,
    val title: String,
    val isChecked: Boolean,
    val listPosition: Int,
) {

    companion object {
        fun generateEmpty() = ChecklistItem(id = 0, title = "", isChecked = false, listPosition = 0)
    }
}