package com.ogata_k.mobile.winp.presentation.page.work.edit

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ogata_k.mobile.winp.presentation.page.IRouting
import com.ogata_k.mobile.winp.presentation.page.ISetupRouting

class WorkEditRouting(private val workId: Long) : IRouting {
    companion object : ISetupRouting {
        const val WORK_ID_KEY = "work_id"

        override val routingPath: String
            get() = "work/{$WORK_ID_KEY}/edit"

        override val routingArguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(
                    name = WORK_ID_KEY,
                ) {
                    type = NavType.LongType
                },
            )
    }

    override fun toPath(): String = "work/$workId/edit"
}