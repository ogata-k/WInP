package com.ogata_k.mobile.winp.domain.model.work

import java.time.OffsetDateTime

/**
 * タスクの対応予定の項目
 */
data class WorkTodo(
    val id: Long,
    val description: String,
    val completedAt: OffsetDateTime?,
    val createdAt: OffsetDateTime,
)