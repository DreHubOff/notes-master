package com.andres.notes.master.ui.screens.edit.note

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.andres.notes.master.R
import com.andres.notes.master.core.TextNoteEditorFacade
import com.andres.notes.master.core.interactor.BuildModificationDateTextInteractor
import com.andres.notes.master.core.interactor.BuildPdfFromTextNoteInteractor
import com.andres.notes.master.core.model.MainTypeTextRepresentation
import com.andres.notes.master.core.model.TextNote
import com.andres.notes.master.data.PermissionsRepository
import com.andres.notes.master.data.TextNotesRepository
import com.andres.notes.master.di.qualifier.ApplicationGlobalScope
import com.andres.notes.master.ui.focus.ElementFocusRequest
import com.andres.notes.master.ui.intent.ShareFileIntentBuilder
import com.andres.notes.master.ui.intent.ShareTextIntentBuilder
import com.andres.notes.master.ui.navigation.NavigationEventsHost
import com.andres.notes.master.ui.screens.Route
import com.andres.notes.master.ui.screens.edit.core.EditScreenViewModel
import com.andres.notes.master.ui.screens.edit.note.model.EditNoteScreenState
import com.andres.notes.master.ui.shared.defaultTransitionAnimationDuration
import dagger.Lazy
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Provider

@HiltViewModel
class EditNoteViewModel @Inject constructor(
    shareFileIntentBuilder: Provider<ShareFileIntentBuilder>,
    shareTextIntentBuilder: Provider<ShareTextIntentBuilder>,
    buildModificationDateText: Lazy<BuildModificationDateTextInteractor>,
    editorFacade: TextNoteEditorFacade,
    permissionsRepository: Provider<PermissionsRepository>,
    private val savedStateHandle: SavedStateHandle,
    @param:ApplicationContext
    private val context: Context,
    @param:ApplicationGlobalScope
    private val applicationCoroutineScope: CoroutineScope,
    private val buildPdfFromTextNote: Provider<BuildPdfFromTextNoteInteractor>,
    private val navigationEventsHost: NavigationEventsHost,
    private val textNotesRepository: TextNotesRepository,
) : EditScreenViewModel<EditNoteScreenState, TextNote>(
    navigationEventsHost = navigationEventsHost,
    editorFacade = editorFacade,
    applicationCoroutineScope = applicationCoroutineScope,
    context = context,
    buildModificationDateText = buildModificationDateText,
    shareTextIntentBuilder = shareTextIntentBuilder,
    shareFileIntentBuilder = shareFileIntentBuilder,
    permissionsRepository = permissionsRepository,
) {

    override val itemRestoredMessageRes: Int = R.string.note_restored

    override fun getCurrentIdFromNavigationArgs(): Long =
        savedStateHandle.toRoute<Route.EditNoteScreen>().noteId ?: 0

    override fun itemUpdatesFlow(itemId: Long): Flow<TextNote> =
        textNotesRepository.observeNoteById(itemId)

    override fun getEmptyState(): EditNoteScreenState = EditNoteScreenState.Companion.EMPTY

    override fun fillWithScreenSpecificData(
        oldState: EditNoteScreenState,
        newState: EditNoteScreenState,
        updatedItem: TextNote,
    ): EditNoteScreenState {
        return newState.copy(
            content = updatedItem.content,
            contentFocusRequest = oldState.contentFocusRequest,
        )
    }

    private var contentModificationJob: Job? = null

    fun onContentChanged(content: String) {
        contentModificationJob?.cancel()
        contentModificationJob = applicationCoroutineScope.launch {
            delay(600)
            val currentState = _state.value
            textNotesRepository.storeNewContent(content = content, itemId = currentState.itemId)
        }
    }

    fun onBackClicked() {
        navigateBack(itemId = _state.value.itemId, isTrashed = false)
    }

    override fun navigateBack(itemId: Long, isTrashed: Boolean) {
        viewModelScope.launch(Dispatchers.Default) {
            val result = if (isTrashed) {
                Route.EditNoteScreen.Result.Trashed(noteId = itemId)
            } else {
                Route.EditNoteScreen.Result.Edited(noteId = itemId)
            }
            navigationEventsHost.navigateBack(Route.EditNoteScreen.Result.KEY to result)
        }
    }

    override suspend fun getPdfRepresentation(state: EditNoteScreenState): File? =
        buildPdfFromTextNote.get().invoke(state.itemId)

    override suspend fun getTextRepresentation(state: EditNoteScreenState): MainTypeTextRepresentation =
        MainTypeTextRepresentation(title = state.title, content = state.content)

    fun onTitleNextClick() {
        _state.update { state -> state.copy(contentFocusRequest = ElementFocusRequest()) }
    }

    override suspend fun loadFirstItem(itemIdFromNavArgs: Long): TextNote {
        var currentNote = textNotesRepository.getNoteById(itemIdFromNavArgs)
        var requestContentFocus = false
        if (currentNote == null) {
            delay(defaultTransitionAnimationDuration.toLong())
            currentNote = textNotesRepository.saveTextNote(TextNote.Companion.generateEmpty())
            requestContentFocus = true
        }
        _state.update { state ->
            state.copy(contentFocusRequest = if (requestContentFocus) ElementFocusRequest() else null)
        }
        return currentNote
    }
}