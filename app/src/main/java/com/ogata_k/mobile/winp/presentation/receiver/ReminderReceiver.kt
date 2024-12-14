package com.ogata_k.mobile.winp.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ogata_k.mobile.winp.domain.enumerate.LocalNotifyDiv
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.NotifyForWorkInput
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint(BroadcastReceiver::class)
class ReminderReceiver : Hilt_ReminderReceiver() {
    companion object {
        const val TAG = "winp.receiver.ReminderReceiver"

        const val ACTION_REMINDER_NOTIFICATION_ACTION =
            "com.ogata_k.mobile.winp.ACTION_REMINDER_NOTIFICATION"

        // LocalNotifyDivを表す値としてIntの値が指定される
        const val INPUT_LOCAL_NOTIFY_DIV = "ReminderReceiver_LocalNotifyDiv"
    }

    @Inject
    lateinit var notifyForWorkUseCase: NotifyForWorkAsyncUseCase

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action != ACTION_REMINDER_NOTIFICATION_ACTION) {
            // 想定していない呼び出し
            return
        }

        val localNotifyDivValue = intent.getIntExtra(INPUT_LOCAL_NOTIFY_DIV, -1)
        if (localNotifyDivValue < 0) {
            throw IllegalArgumentException()
        }
        val localNotifyDiv: LocalNotifyDiv = LocalNotifyDiv.lookup(localNotifyDivValue)

        val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        val pendingResult = goAsync()
        coroutineScope.launch {
            try {
                try {
                    notifyForWorkUseCase.call(NotifyForWorkInput(localNotifyDiv))
                } catch (e: CancellationException) {
                    throw e
                } catch (t: Throwable) {
                    Log.e(TAG, "ReminderReceiver execution failed", t)
                } finally {
                    // Nothing can be in the `finally` block after this, as this throws a
                    // `CancellationException`
                    coroutineScope.cancel()
                }
            } finally {
                // This must be the last call, as the process may be killed after calling this.
                try {
                    pendingResult.finish()
                } catch (e: IllegalStateException) {
                    // On some OEM devices, this may throw an error about "Broadcast already finished".
                    // See b/257513022.
                    Log.e(TAG, "Error thrown when trying to finish BootCompletedReceiver", e)
                }
            }
        }
    }
}