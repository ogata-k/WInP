package com.ogata_k.mobile.winp.infra.database.with_relatioin

import androidx.room.Embedded
import androidx.room.Relation
import com.ogata_k.mobile.winp.infra.database.entity.Work
import com.ogata_k.mobile.winp.infra.database.entity.WorkTodo

data class WorkWithWorkTodo(
    @Embedded val work: Work,
    // ソートは取得後にDao内で対応する
    @Relation(
        parentColumn = "work_id",
        entityColumn = "work_id"
    )
    val workTodos: List<WorkTodo>,
)
