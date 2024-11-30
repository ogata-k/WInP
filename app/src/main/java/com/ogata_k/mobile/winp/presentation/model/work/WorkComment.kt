package com.ogata_k.mobile.winp.presentation.model.work

import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.presentation.model.FromDomain
import com.ogata_k.mobile.winp.presentation.model.ToDomainWithRelationId
import java.time.LocalDateTime
import com.ogata_k.mobile.winp.domain.model.work.WorkComment as DomainWorkComment

/**
 * タスクの進捗のコメント
 */
data class WorkComment(
    val workCommentId: Long,
    val workId: Long,
    val comment: String,
    val modifiedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
) : ToDomainWithRelationId<DomainWorkComment, Long> {
    companion object : FromDomain<DomainWorkComment, WorkComment> {
        override fun fromDomainModel(domain: DomainWorkComment): WorkComment {
            return WorkComment(
                workCommentId = domain.workCommentId,
                workId = domain.workId,
                comment = domain.comment,
                modifiedAt = domain.modifiedAt?.let { LocalDateTimeConverter.fromOffsetDateTime(it) },
                createdAt = LocalDateTimeConverter.fromOffsetDateTime(domain.createdAt),
            )
        }
    }

    // relation_idはworkId
    override fun toDomainModel(relationId: Long): DomainWorkComment {
        return DomainWorkComment(
            workCommentId = workCommentId,
            workId = relationId,
            comment = comment,
            modifiedAt = modifiedAt?.let { LocalDateTimeConverter.toOffsetDateTime(it) },
            createdAt = LocalDateTimeConverter.toOffsetDateTime(createdAt),
        )
    }

    val isModified: Boolean = modifiedAt != null
}
