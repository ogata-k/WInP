package com.ogata_k.mobile.winp.presentation.model.work

import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.presentation.model.FromDomain
import com.ogata_k.mobile.winp.presentation.model.ToDomain
import java.time.LocalDateTime
import com.ogata_k.mobile.winp.domain.model.work.WorkTodo as DomainWorkTodo

/**
 * タスクの対応予定の項目
 */
data class WorkTodo(
    val id: Long,
    val description: String,
    val completedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
) : ToDomain<DomainWorkTodo> {
    companion object : FromDomain<DomainWorkTodo, WorkTodo> {
        override fun fromDomainModel(domain: DomainWorkTodo): WorkTodo {
            return WorkTodo(
                id = domain.id,
                description = domain.description,
                completedAt = domain.completedAt?.let { LocalDateTimeConverter.fromOffsetDateTime(it) },
                createdAt = LocalDateTimeConverter.fromOffsetDateTime(domain.createdAt),
            )
        }
    }

    val isCompleted: Boolean = completedAt != null
    
    override fun toDomainModel(): DomainWorkTodo {
        return DomainWorkTodo(
            id = id,
            description = description,
            completedAt = completedAt?.let { LocalDateTimeConverter.toOffsetDateTime(it) },
            createdAt = LocalDateTimeConverter.toOffsetDateTime(createdAt),
        )
    }
}