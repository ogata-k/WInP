package com.ogata_k.mobile.winp.presentation.model.wip

import java.time.LocalDateTime

/**
 * タスク
 */
data class Work(
    val id: Int,
    val title: String,
    val description: String,
    val progresses: List<ProgressDescription>,
    val deadline: LocalDateTime,
    val completedAt: LocalDateTime?,
    val cratedAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    val isExpired: Boolean = deadline.isBefore(LocalDateTime.now())
    val isCompleted: Boolean = completedAt != null
}