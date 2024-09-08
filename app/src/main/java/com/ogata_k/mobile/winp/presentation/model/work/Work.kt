package com.ogata_k.mobile.winp.presentation.model.work

import com.ogata_k.mobile.winp.presentation.model.FromDomain
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.ogata_k.mobile.winp.domain.model.work.Work as DomainWork

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
) {
    companion object : FromDomain<DomainWork, Work> {
        override fun fromDomainModel(domain: DomainWork): Work {
            if (domain.id == null) {
                throw IllegalArgumentException()
            }

            return Work(
                id = domain.id,
                title = domain.title,
                description = domain.description,
                beganAt = domain.beganAt,
                endedAt = domain.endedAt,
                completedAt = domain.completedAt,
            )
        }
    }

    val hasPeriod: Boolean = beganAt !== null || endedAt !== null
    val isExpired: Boolean = endedAt?.isBefore(LocalDateTime.now()) ?: false
    val isCompleted: Boolean = completedAt != null

    fun splitToFormattedPeriod(formatter: DateTimeFormatter): Pair<String, String> {
        val formatBeganAt: String = beganAt?.format(formatter) ?: ""
        val formatEndedAt: String = endedAt?.format(formatter) ?: ""

        return Pair(formatBeganAt, formatEndedAt)
    }
}