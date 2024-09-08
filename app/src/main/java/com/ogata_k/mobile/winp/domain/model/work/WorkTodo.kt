package com.ogata_k.mobile.winp.domain.model.work

import java.time.LocalDateTime

/**
 * タスクの対応予定の項目
 */
data class WorkTodo(
    // nullなら保存前のデータ
    val id: Int?,
    val description: String,
    val completedAt: LocalDateTime?,
)