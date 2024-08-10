package com.ogata_k.mobile.winp.presentation.model.work

import java.time.LocalDateTime

/**
 * タスクの対応予定の項目
 */
data class WorkTodo(
    val id: Int,
    val todoTask: String,
    val completedAt: LocalDateTime?,
    val position: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)