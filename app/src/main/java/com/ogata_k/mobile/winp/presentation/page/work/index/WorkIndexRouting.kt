package com.ogata_k.mobile.winp.presentation.page.work.index

import com.ogata_k.mobile.winp.presentation.page.IRouting
import com.ogata_k.mobile.winp.presentation.page.ISetupRouting

class WorkIndexRouting : IRouting {
    companion object : ISetupRouting {
        override val routingPath: String
            get() = "work/index"

        // 2024-12-11のようなDate::parse()でパースできる文字列でintentのputExtra()で保存するキー
        const val SEARCH_DATE_INTENT_EXTRA_KEY = "work_index_initial_date"
    }

    override fun toPath(): String = routingPath
}