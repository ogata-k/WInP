package com.ogata_k.mobile.winp.presentation.model.work

import com.ogata_k.mobile.winp.presentation.model.FromDomain
import java.time.LocalDateTime
import com.ogata_k.mobile.winp.domain.model.work.WorkTodo as DomainWorkTodo

/**
 * タスクの対応予定の項目
 */
data class WorkTodo(
    val id: Int,
    val description: String,
    val completedAt: LocalDateTime?,
) {
    companion object : FromDomain<DomainWorkTodo, WorkTodo> {
        override fun fromDomainModel(domain: DomainWorkTodo): WorkTodo {
            if (domain.id == null) {
                throw IllegalArgumentException()
            }

            return WorkTodo(
                id = domain.id,
                description = domain.description,
                completedAt = domain.completedAt,
            )
        }
    }
}