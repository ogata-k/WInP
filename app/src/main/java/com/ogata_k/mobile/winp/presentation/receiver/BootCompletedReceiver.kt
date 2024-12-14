package com.ogata_k.mobile.winp.presentation.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RescheduleAllScheduledNotificationAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.local_notification.RescheduleAllScheduledNotificationInput
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@AndroidEntryPoint(BroadcastReceiver::class)
class BootCompletedReceiver : Hilt_BootCompletedReceiver() {
    companion object {
        const val TAG = "winp.receiver.BootCompletedReceiver"
    }

    @Inject
    lateinit var rescheduleAllScheduledNotificationUseCase: RescheduleAllScheduledNotificationAsyncUseCase

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        if (intent.action != Intent.ACTION_BOOT_COMPLETED) {
            // 想定していない呼び出し
            return
        }

        val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
        val pendingResult = goAsync()

        coroutineScope.launch {
            try {
                try {
                    rescheduleAllScheduledNotificationUseCase.call(
                        RescheduleAllScheduledNotificationInput
                    ).getOrThrow()
                } catch (e: CancellationException) {
                    throw e
                } catch (t: Throwable) {
                    Log.e(TAG, "BootCompletedReceiver execution failed", t)
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