package com.ogata_k.mobile.winp.presentation.model.work

import com.ogata_k.mobile.winp.common.formatter.buildFullDatePatternFormatter
import com.ogata_k.mobile.winp.common.formatter.buildFullDateTimePatternFormatter
import com.ogata_k.mobile.winp.common.formatter.buildFullTimePatternFormatter
import com.ogata_k.mobile.winp.presentation.model.FromDomain
import com.ogata_k.mobile.winp.presentation.model.ToDomain
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
    val todoItems: List<WorkTodo>,
) : ToDomain<DomainWork> {
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
                todoItems = domain.workTodos.map { WorkTodo.fromDomainModel(it) },
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

    /**
     * 期間を適切な条件でフォーマット
     */
    fun formatPeriod(rangeString: String, noPeriodString: String): String {
        if (beganAt == null && endedAt == null) {
            return noPeriodString
        }

        val dateFormatter = buildFullDatePatternFormatter()
        val timeFormatter = buildFullTimePatternFormatter()
        val dateTimeFormatter = buildFullDateTimePatternFormatter()

        if (beganAt == null || endedAt == null) {
            val formatBeganAt: String = beganAt?.format(dateTimeFormatter) ?: ""
            val formatEndedAt: String = endedAt?.format(dateTimeFormatter) ?: ""
            return "%s %s %s".format(formatBeganAt, rangeString, formatEndedAt)
        }

        // 同日の場合は時間だけレンジ表示
        if (beganAt.toLocalDate() == endedAt.toLocalDate()) {
            val formatDate: String = beganAt.toLocalDate().format(dateFormatter)
            val formatBeganTime: String = beganAt.toLocalTime().format(timeFormatter)
            val formatEndedTime: String = endedAt.toLocalTime().format(timeFormatter)
            return "%s %s %s %s".format(formatDate, formatBeganTime, rangeString, formatEndedTime)
        }

        // 期間の両端の時間の場合は時間を省略して表示
        val localTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        // 文字列に変換して比較しているのはミリ秒までで比較するとDBで保存可能な条件を超えるため
        if (beganAt.toLocalTime().format(localTimeFormatter) == "00:00" && endedAt.toLocalTime()
                .format(localTimeFormatter) == "23:59"
        ) {
            val formatBeganDate: String = beganAt.toLocalDate().format(dateFormatter)
            val formatEndedDate: String = endedAt.toLocalDate().format(dateFormatter)
            return "%s %s %s".format(formatBeganDate, rangeString, formatEndedDate)
        }

        val formatBeganAt: String = beganAt.format(dateTimeFormatter)
        val formatEndedAt: String = endedAt.format(dateTimeFormatter)
        return "%s %s %s".format(formatBeganAt, rangeString, formatEndedAt)
    }

    override fun toDomainModel(): DomainWork {
        return DomainWork(
            id = id,
            title = title,
            description = description,
            beganAt = beganAt,
            endedAt = endedAt,
            completedAt = completedAt,
            workTodos = todoItems.map { it.toDomainModel() },
        )
    }
}