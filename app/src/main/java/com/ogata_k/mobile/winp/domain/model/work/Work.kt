package com.ogata_k.mobile.winp.domain.model.work

import java.time.OffsetDateTime

/**
 * タスク
 */
data class Work(
    val id: Long,
    val title: String,
    val description: String,
    val beganAt: OffsetDateTime?,
    val endedAt: OffsetDateTime?,
    val completedAt: OffsetDateTime?,
    val createdAt: OffsetDateTime,
    val workTodos: List<WorkTodo>,
)