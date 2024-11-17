package com.ogata_k.mobile.winp.presentation.model.work

import com.ogata_k.mobile.winp.common.formatter.buildFullDatePatternFormatter
import com.ogata_k.mobile.winp.common.formatter.buildFullDateTimePatternFormatter
import com.ogata_k.mobile.winp.common.formatter.buildFullTimePatternFormatter
import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.presentation.model.FromDomain
import com.ogata_k.mobile.winp.presentation.model.ToDomain
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.ogata_k.mobile.winp.domain.model.work.Work as DomainWork

/**
 * タスク
 */
data class Work(
    val workId: Long,
    val title: String,
    val description: String,
    val beganAt: LocalDateTime?,
    val endedAt: LocalDateTime?,
    val completedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
    val todoItems: List<WorkTodo>,
) : ToDomain<DomainWork> {
    companion object : FromDomain<DomainWork, Work> {
        override fun fromDomainModel(domain: DomainWork): Work {
            return Work(
                workId = domain.workId,
                title = domain.title,
                description = domain.description,
                beganAt = domain.beganAt?.let { LocalDateTimeConverter.fromOffsetDateTime(it) },
                endedAt = domain.endedAt?.let { LocalDateTimeConverter.fromOffsetDateTime(it) },
                completedAt = domain.completedAt?.let { LocalDateTimeConverter.fromOffsetDateTime(it) },
                createdAt = LocalDateTimeConverter.fromOffsetDateTime(domain.createdAt),
                todoItems = domain.workTodos.map { WorkTodo.fromDomainModel(it) },
            )
        }
    }

    val isExpired: Boolean = endedAt?.isBefore(LocalDateTime.now()) ?: false
    val isCompleted: Boolean = completedAt != null

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
            workId = workId,
            title = title,
            description = description,
            beganAt = beganAt?.let { LocalDateTimeConverter.toOffsetDateTime(it) },
            endedAt = endedAt?.let { LocalDateTimeConverter.toOffsetDateTime(it) },
            completedAt = completedAt?.let { LocalDateTimeConverter.toOffsetDateTime(it) },
            workTodos = todoItems.map { it.toDomainModel() },
            createdAt = LocalDateTimeConverter.toOffsetDateTime(createdAt),
        )
    }
}