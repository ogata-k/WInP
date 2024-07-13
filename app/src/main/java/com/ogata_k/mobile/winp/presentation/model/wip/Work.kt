package com.ogata_k.mobile.winp.presentation.model.wip

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
    val deadline: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val cratedAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    val hasPeriod: Boolean = beganAt !== null || deadline !== null
    val isExpired: Boolean = deadline?.isBefore(LocalDateTime.now()) ?: false
    val isCompleted: Boolean = completedAt != null

    fun splitToFormattedPeriod(formatter: DateTimeFormatter): Pair<String, String> {
        val startDateTime: String = beganAt?.format(formatter) ?: ""
        val deadlineDateTime: String = deadline?.format(formatter) ?: ""

        return Pair(startDateTime, deadlineDateTime)
    }
}