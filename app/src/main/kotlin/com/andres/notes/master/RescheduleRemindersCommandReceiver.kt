package com.andres.notes.master

import android.content.Context
import android.content.Intent
import com.andres.notes.master.core.ChecklistEditorFacade
import com.andres.notes.master.core.TextNoteEditorFacade
import com.andres.notes.master.core.interactor.ObserveApplicationMainTypeInteractor
import com.andres.notes.master.core.model.Checklist
import com.andres.notes.master.core.model.TextNote
import com.andres.notes.master.data.PermissionsRepository
import com.andres.notes.master.di.qualifier.ApplicationGlobalScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class RescheduleRemindersCommandReceiver : android.content.BroadcastReceiver() {

    @Inject
    lateinit var checklistEditorFacade: Provider<ChecklistEditorFacade>

    @Inject
    lateinit var textNoteEditorFacade: Provider<TextNoteEditorFacade>

    @Inject
    lateinit var permissionsRepository: Provider<PermissionsRepository>

    @Inject
    lateinit var observeApplicationMainTypeInteractor: Provider<ObserveApplicationMainTypeInteractor>

    @Inject
    @ApplicationGlobalScope
    lateinit var coroutineScope: CoroutineScope

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_MY_PACKAGE_REPLACED &&
            intent.action != Intent.ACTION_BOOT_COMPLETED &&
            intent.action != INTERNAL_ACTION
        ) {
            return
        }
        coroutineScope.launch {
            if (permissionsRepository.get().run { !canPostNotifications() || !canScheduleAlarms() }) {
                return@launch
            }
            val itemsToReschedule = observeApplicationMainTypeInteractor
                .get()
                .invoke(searchPrompt = "")
                .firstOrNull()
                .orEmpty()
                .filter { item -> item.reminderDate != null && !item.reminderHasBeenPosted }
            itemsToReschedule.forEach {
                when (it) {
                    is Checklist -> checklistEditorFacade.get().setReminder(it.id, it.reminderDate!!)
                    is TextNote -> textNoteEditorFacade.get().setReminder(it.id, it.reminderDate!!)
                }
            }
        }
    }

    companion object {

        private const val INTERNAL_ACTION = "com.andres.notes.master.INTERNAL_ACTION"

        fun getIntent(context: Context): Intent =
            Intent(context, RescheduleRemindersCommandReceiver::class.java).setAction(INTERNAL_ACTION)
    }
}