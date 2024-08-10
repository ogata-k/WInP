package com.ogata_k.mobile.winp.presentation.page.work.edit

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ogata_k.mobile.winp.presentation.page.IRouting
import com.ogata_k.mobile.winp.presentation.page.ISetupRouting

class WorkEditRouting(private val workId: Int) : IRouting {
    companion object : ISetupRouting {
        const val WORK_ID_KEY = "work_id"
        const val CREATE_WORK_ID: Int = 0

        override val routingPath: String
            get() = "work/edit/{$WORK_ID_KEY}"

        override val routingArguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(
                    name = WORK_ID_KEY,
                ) {
                    type = NavType.IntType
                },
            )

    }

    override fun toPath(): String = "work/edit/$workId"
}