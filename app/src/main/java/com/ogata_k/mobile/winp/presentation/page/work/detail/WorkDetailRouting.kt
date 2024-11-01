package com.ogata_k.mobile.winp.presentation.page.work.detail

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ogata_k.mobile.winp.presentation.page.IRouting
import com.ogata_k.mobile.winp.presentation.page.ISetupRouting

class WorkDetailRouting(private val workId: Long) : IRouting {
    companion object : ISetupRouting {
        const val WORK_ID_KEY = "work_id"

        override val routingPath: String
            get() = "work/{$WORK_ID_KEY}"

        override val routingArguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(
                    name = WORK_ID_KEY,
                ) {
                    type = NavType.LongType
                },
            )
    }

    override fun toPath(): String = "work/$workId"
}