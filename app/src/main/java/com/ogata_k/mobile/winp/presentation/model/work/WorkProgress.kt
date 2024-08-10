package com.ogata_k.mobile.winp.presentation.model.work

import java.time.LocalDateTime

/**
 * タスクの対応状況
 */
data class WorkProgress(
    val id: Int,
    val description: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)