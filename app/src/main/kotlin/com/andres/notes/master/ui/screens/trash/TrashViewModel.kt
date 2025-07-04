package com.andres.notes.master.ui.screens.trash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andres.notes.master.core.interactor.ObserveApplicationMainTypeTrashedInteractor
import com.andres.notes.master.core.interactor.PermanentlyDeleteApplicationMainDataTypeInteractor
import com.andres.notes.master.core.interactor.PermanentlyDeleteOldTrashRecordsInteractor
import com.andres.notes.master.core.model.ApplicationMainDataType
import com.andres.notes.master.ui.navigation.NavigationEventsHost
import com.andres.notes.master.ui.screens.Route
import com.andres.notes.master.ui.screens.trash.mapper.ApplicationMainDataTypeToTrashListItemMapper
import com.andres.notes.master.ui.screens.trash.model.TrashScreenState
import com.andres.notes.master.ui.screens.trash.model.UiIntent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject

@HiltViewModel
class TrashViewModel @Inject constructor(
    private val navigationEventsHost: NavigationEventsHost,
    private val observeApplicationMainTypeTrashed: ObserveApplicationMainTypeTrashedInteractor,
    private val permanentlyDeleteApplicationMainDataType: PermanentlyDeleteApplicationMainDataTypeInteractor,
    private val permanentlyDeleteOldTrashRecords: PermanentlyDeleteOldTrashRecordsInteractor,
    private val mainDataTypeToTrashListItemMapper: ApplicationMainDataTypeToTrashListItemMapper,
) : ViewModel() {

    private val _state = MutableStateFlow(TrashScreenState.Companion.EMPTY)
    val state: StateFlow<TrashScreenState> = _state
        .onStart { observeTrashedItems() }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(stopTimeoutMillis = 3000), TrashScreenState.Companion.EMPTY)

    private var dbSubscriptionJob: Job? = null

    fun handleEvent(intent: UiIntent) {
        when (intent) {
            UiIntent.BackClicked -> onBackClicked()
            UiIntent.EmptyTrash -> onEmptyTrashRequested()
            is UiIntent.OpenChecklistScreen -> onOpenChecklistScreen(intent)
            is UiIntent.OpenTextNoteScreen -> onOpenTextNoteScreen(intent)
            UiIntent.DismissEmptyTrashConfirmation -> onDismissEmptyTrashConfirmation()
            UiIntent.EmptyTrashConfirmed -> onEmptyTrashConfirmed()
        }
    }

    private fun onOpenTextNoteScreen(intent: UiIntent.OpenTextNoteScreen) {
        viewModelScope.launch {
            navigationEventsHost.navigate(Route.EditNoteScreen(noteId = intent.item.id))
        }
    }

    private fun onOpenChecklistScreen(intent: UiIntent.OpenChecklistScreen) {
        viewModelScope.launch {
            navigationEventsHost.navigate(Route.EditChecklistScreen(checklistId = intent.item.id))
        }
    }

    private fun onEmptyTrashRequested() {
        _state.update { it.copy(requestEmptyTrashConfirmation = true) }
    }

    private fun onDismissEmptyTrashConfirmation() {
        _state.update { it.copy(requestEmptyTrashConfirmation = false) }
    }

    private fun onEmptyTrashConfirmed() {
        _state.update {
            it.copy(
                requestEmptyTrashConfirmation = false,
                listItems = emptyList()
            )
        }
        viewModelScope.launch(Dispatchers.Default) {
            val itemsToRemove = observeApplicationMainTypeTrashed().first()
            permanentlyDeleteApplicationMainDataType(*itemsToRemove.toTypedArray())
        }
    }

    private fun onBackClicked() {
        viewModelScope.launch(Dispatchers.Default) {
            onDismissEmptyTrashConfirmation()
            navigationEventsHost.navigateBack()
        }
    }

    private fun observeTrashedItems() {
        dbSubscriptionJob?.cancel()
        dbSubscriptionJob = viewModelScope.launch(Dispatchers.Default) {
            supervisorScope {
                launch {
                    permanentlyDeleteOldTrashRecords()
                }
                launch {
                    observeApplicationMainTypeTrashed()
                        .collect { trashedItem: List<ApplicationMainDataType> ->
                            val newScreenItems = trashedItem.map(mainDataTypeToTrashListItemMapper::invoke)
                            _state.update { it.copy(listItems = newScreenItems) }
                        }
                }
            }
        }
    }
}