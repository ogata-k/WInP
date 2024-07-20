package com.ogata_k.mobile.winp.presentation.page.work.index

import com.ogata_k.mobile.winp.presentation.page.IRouting
import com.ogata_k.mobile.winp.presentation.page.ISetupRouting

class WorkIndexRouting : IRouting {
    companion object : ISetupRouting {
        override val routingPath: String
            get() = "work/index"
    }

    override fun getPath(): String = routingPath
}