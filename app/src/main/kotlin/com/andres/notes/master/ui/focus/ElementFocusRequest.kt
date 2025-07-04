package com.andres.notes.master.ui.focus

class ElementFocusRequest {

    private var isProcessed: Boolean = false

    fun isHandled(): Boolean = isProcessed
    fun confirmProcessing() {
        isProcessed = true
    }
}