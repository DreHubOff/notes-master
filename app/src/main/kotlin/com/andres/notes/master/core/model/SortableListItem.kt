package com.andres.notes.master.core.model

import java.time.OffsetDateTime

interface SortableListItem {
    val isPinned: Boolean
    val creationDate: OffsetDateTime
}