package com.ogata_k.mobile.winp.domain.model.work

import java.time.LocalDateTime

/**
 * タスク
 */
data class Work(
    // nullなら保存前のデータ
    val id: Int?,
    val title: String,
    val description: String,
    val beganAt: LocalDateTime?,
    val endedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val workTodos: List<WorkTodo>,
)