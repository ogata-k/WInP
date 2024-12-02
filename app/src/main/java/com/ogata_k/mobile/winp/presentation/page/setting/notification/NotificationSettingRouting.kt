package com.ogata_k.mobile.winp.presentation.page.setting.notification

import com.ogata_k.mobile.winp.presentation.page.IRouting
import com.ogata_k.mobile.winp.presentation.page.ISetupRouting

class NotificationSettingRouting : IRouting {
    companion object : ISetupRouting {
        override val routingPath: String
            get() = "setting/notification"

    }

    override fun toPath(): String = "setting/notification"
}