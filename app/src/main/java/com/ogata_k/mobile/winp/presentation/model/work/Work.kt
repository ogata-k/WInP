package com.ogata_k.mobile.winp.presentation.model.work

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * タスク
 */
data class Work(
    val id: Int,
    val title: String,
    val description: String,
    val beganAt: LocalDateTime?,
    val endedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    val hasPeriod: Boolean = beganAt !== null || endedAt !== null
    val isExpired: Boolean = endedAt?.isBefore(LocalDateTime.now()) ?: false
    val isCompleted: Boolean = completedAt != null

    fun splitToFormattedPeriod(formatter: DateTimeFormatter): Pair<String, String> {
        val formatBeganAt: String = beganAt?.format(formatter) ?: ""
        val formatEndedAt: String = endedAt?.format(formatter) ?: ""

        return Pair(formatBeganAt, formatEndedAt)
    }
}