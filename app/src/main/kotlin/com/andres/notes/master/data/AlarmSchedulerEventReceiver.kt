package com.andres.notes.master.data

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.IntentCompat
import com.andres.notes.master.MainActivity
import com.andres.notes.master.R
import com.andres.notes.master.core.model.ApplicationMainDataType
import com.andres.notes.master.core.model.Checklist
import com.andres.notes.master.core.model.TextNote
import com.andres.notes.master.di.qualifier.ApplicationGlobalScope
import com.andres.notes.master.di.qualifier.BulletPointSymbol
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Provider
import kotlin.time.Clock
import kotlin.time.Instant

private val TAG = AlarmSchedulerEventReceiver::class.java.simpleName

@AndroidEntryPoint
class AlarmSchedulerEventReceiver : android.content.BroadcastReceiver() {

    @Inject
    lateinit var checklistRepository: Provider<ChecklistRepository>

    @Inject
    lateinit var textNotesRepository: Provider<TextNotesRepository>

    @Inject
    lateinit var permissionsRepository: Provider<PermissionsRepository>

    @Inject
    @ApplicationGlobalScope
    lateinit var coroutineScope: CoroutineScope

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    @BulletPointSymbol
    lateinit var bulletPointSymbol: String

    @Inject
    lateinit var json: Json

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null || intent == null) return
        createNotificationChannel(context)

        if (intent.itemId != -1L && intent.itemType != null) {
            when (intent.itemType) {
                TextNote::class.java -> processTextNoteReminder(intent.itemId)
                Checklist::class.java -> processChecklistReminder(intent.itemId)
            }
        }
        if (intent.notificationToRemove != -1) {
            notificationManager.cancel(intent.notificationToRemove)
        }
    }

    private fun createNotificationChannel(context: Context) {
        val channel = NotificationChannel(
            context.getString(R.string.notes_notification_channel_id),
            context.getString(R.string.notes_notification_channel_name),
            NotificationManager.IMPORTANCE_HIGH
        )
        channel.description = context.getString(R.string.notes_notification_channel_desc)
        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        notificationManager.createNotificationChannel(channel)
    }

    private fun processChecklistReminder(checklistId: Long) {
        coroutineScope.launch {
            val checklist = checklistRepository.get().getChecklistById(checklistId) ?: return@launch
            if (checklist.reminderDate == null) {
                return@launch
            }
            val notificationId = buildNotificationIdForItem(checklist)
            val successfullyPosted = showReminderNotification(
                notificationId = notificationId,
                notification = buildChecklistNotification(checklist, notificationId) ?: return@launch
            )
            if (successfullyPosted) {
                checklistRepository.get().updateChecklistReminderShownState(checklistId, isShown = true)
            }
        }
    }

    private fun processTextNoteReminder(noteId: Long) {
        coroutineScope.launch {
            val textNote = textNotesRepository.get().getNoteById(noteId) ?: return@launch
            if (textNote.reminderDate == null) {
                return@launch
            }
            val notificationId = buildNotificationIdForItem(textNote)
            val successfullyPosted = showReminderNotification(
                notificationId = notificationId,
                notification = buildTextNoteNotification(textNote, notificationId) ?: return@launch
            )
            if (successfullyPosted) {
                textNotesRepository.get().updateChecklistReminderShownState(noteId, isShown = true)
            }
        }
    }

    private fun buildTextNoteNotification(
        textNote: TextNote,
        notificationId: Int,
    ): Notification? {
        return buildNotification(
            title = textNote.title,
            content = textNote.content,
            showDate = textNote.reminderDate ?: Clock.System.now(),
            openItemEditorIntent = getOpenItemEditorPendingIntent(context = context, item = textNote, json = json),
            hideNotificationIntent = getHideNotificationPendingIntent(context = context, notificationId = notificationId)
        )
    }

    private fun buildChecklistNotification(
        checklist: Checklist,
        notificationId: Int,
    ): Notification? {
        val content = buildString {
            checklist.items.forEach { item ->
                if (!item.isChecked) {
                    append(bulletPointSymbol)
                    append(" ")
                    append(item.title)
                    appendLine(" ")
                }
            }
        }
        return buildNotification(
            title = checklist.title,
            content = content,
            openItemEditorIntent = getOpenItemEditorPendingIntent(context = context, item = checklist, json = json),
            hideNotificationIntent = getHideNotificationPendingIntent(context = context, notificationId = notificationId),
            showDate = checklist.reminderDate ?: Clock.System.now(),
        )
    }

    private fun buildNotification(
        title: String,
        content: String,
        openItemEditorIntent: PendingIntent,
        hideNotificationIntent: PendingIntent,
        showDate: Instant,
    ): Notification? {
        Log.d(TAG, "Building notification. Title: $title, content: $content")
        return NotificationCompat.Builder(context, context.getString(R.string.notes_notification_channel_id))
            .setSmallIcon(R.drawable.ic_circle_notifications)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(openItemEditorIntent)
            .setAutoCancel(true)
            .setShowWhen(true)
            .setWhen(showDate.toEpochMilliseconds())
            .addAction(R.drawable.ic_check, context.getString(R.string.done), hideNotificationIntent)
            .build()
    }

    private fun showReminderNotification(
        notificationId: Int,
        notification: Notification,
    ): Boolean {
        return if (permissionsRepository.get().canPostNotifications()) {
            notificationManager.notify(notificationId, notification)
            true
        } else {
            false
        }
    }

    companion object {

        private const val KEY_ITEM_ID = "item_id"
        private const val KEY_ITEM_TYPE = "item_type"

        private const val KEY_NOTIFICATION_TO_HIDE = "notification_to_hide"

        val Intent.itemId: Long get() = getLongExtra(KEY_ITEM_ID, -1)
        val Intent.itemType: Class<*>? get() = IntentCompat.getSerializableExtra(this, KEY_ITEM_TYPE, Class::class.java)

        val Intent.notificationToRemove: Int get() = getIntExtra(KEY_NOTIFICATION_TO_HIDE, -1)

        fun getIntent(context: Context, target: ApplicationMainDataType): Intent {
            return Intent(context, AlarmSchedulerEventReceiver::class.java)
                .putExtra(KEY_ITEM_ID, target.id)
                .putExtra(KEY_ITEM_TYPE, target::class.java)
        }

        fun removeNotificationRequest(
            context: Context,
            target: ApplicationMainDataType,
        ): PendingIntent =
            getHideNotificationPendingIntent(context, buildNotificationIdForItem(target))

        private fun getHideNotificationPendingIntent(context: Context, notificationId: Int): PendingIntent {
            val intent = Intent(context, AlarmSchedulerEventReceiver::class.java)
                .putExtra(KEY_NOTIFICATION_TO_HIDE, notificationId)
            return PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun getOpenItemEditorPendingIntent(
            context: Context,
            json: Json,
            item: ApplicationMainDataType,
        ): PendingIntent {
            val intent = MainActivity.getOpenItemEditorIntent(context, json, item)
            return PendingIntent.getActivity(
                context,
                item.id.toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        private fun buildNotificationIdForItem(item: ApplicationMainDataType): Int =
            "${item.id}${item::class.simpleName}".hashCode()
    }
}