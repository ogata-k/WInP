package com.ogata_k.mobile.winp.presentation.page.work.summary

import com.ogata_k.mobile.winp.presentation.page.IRouting
import com.ogata_k.mobile.winp.presentation.page.ISetupRouting

class WorkSummaryRouting : IRouting {
    companion object : ISetupRouting {
        override val routingPath: String
            get() = "work/summary"
    }

    override fun toPath(): String = "work/summary"
}