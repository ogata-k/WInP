package com.ogata_k.mobile.winp.presentation.page.work.edit

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.ogata_k.mobile.winp.presentation.constant.DummyID
import com.ogata_k.mobile.winp.presentation.page.IRouting
import com.ogata_k.mobile.winp.presentation.page.ISetupRouting

class WorkEditRouting(private val workId: Long, private val copyFromWorkId: Long? = null) :
    IRouting {
    companion object : ISetupRouting {
        const val WORK_ID_KEY = "work_id"
        const val COPY_FROM_WORK_ID_KEY = "copy_from_work_id"

        override val routingPath: String
            get() = "work/{$WORK_ID_KEY}/edit?copy_from_work={$COPY_FROM_WORK_ID_KEY}"

        override val routingArguments: List<NamedNavArgument>
            get() = listOf(
                navArgument(
                    name = WORK_ID_KEY,
                ) {
                    type = NavType.LongType
                },
                navArgument(
                    name = COPY_FROM_WORK_ID_KEY,
                ) {
                    type = NavType.LongType
                    // nullをサポートしているのは配列と文字列だけなので、ダミーのIDを指定しておく
                    defaultValue = DummyID.INVALID_ID
                },
            )
    }

    override fun toPath(): String = "work/$workId/edit?copy_from_work=$copyFromWorkId"
}