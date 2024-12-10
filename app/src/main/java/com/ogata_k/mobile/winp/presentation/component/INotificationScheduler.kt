package com.ogata_k.mobile.winp.presentation.component

import android.app.NotificationManager
import android.content.Context
import com.ogata_k.mobile.winp.domain.component.NotificationScheduler

class INotificationScheduler(
    private val context: Context,
    private val manager: NotificationManager
) :
    NotificationScheduler