package com.andres.notes.master.core.model

import androidx.annotation.Keep

@Keep
enum class NoteColor(val day: Long, val night: Long) {
    Sage(0xFFD9F0D9, 0xFF386B38),
    Olive(0xFFE8FCCC, 0xFF5C9900),
    Jade(0xFFC8FCF0, 0xFF128878),
    Teal(0xFFD0FCF5, 0xFF128078),
    Azure(0xFFC8F0FF, 0xFF3388A0),
    Slate(0xFFD9E8FF, 0xFF5A3A70),
    Indigo(0xFFE2E4FF, 0xFF5557C0),
    Violet(0xFFE8E4FF, 0xFF7748D0),
    Pink(0xFFFFE0F0, 0xFF8A4A62),
    Orchid(0xFFFBE0FF, 0xFF8C1AB0),
    Honey(0xFFFFF6D5, 0xFF9C6700),
    Apricot(0xFFFFE5CC, 0xFFB25C2E),
    Coral(0xFFFFE2D5, 0xFFD14A2A),
    Salmon(0xFFFFE0E4, 0xFFB03545),
}