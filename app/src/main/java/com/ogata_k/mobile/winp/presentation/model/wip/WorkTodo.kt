package com.ogata_k.mobile.winp.presentation.model.wip

import java.time.LocalDateTime

/**
 * タスクの対応予定の項目
 */
data class WorkTodo(
    val id: Int,
    val todoTask: String,
    val position: Int,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)