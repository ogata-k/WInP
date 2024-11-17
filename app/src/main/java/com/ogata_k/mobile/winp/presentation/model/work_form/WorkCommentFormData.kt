package com.ogata_k.mobile.winp.presentation.model.work_form

import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import com.ogata_k.mobile.winp.presentation.enumerate.hasError
import com.ogata_k.mobile.winp.presentation.model.ToDomainWithRelationId
import java.time.LocalDateTime
import com.ogata_k.mobile.winp.domain.model.work.WorkComment as DomainWorkComment

/**
 * タスクの進捗のコメントのフォーム用データ
 */
data class WorkCommentFormData(
    val workCommentId: Long,
    val comment: String,
    val createdAt: LocalDateTime,
) : ToDomainWithRelationId<DomainWorkComment, Long> {
    companion object {
        fun empty(): WorkCommentFormData {
            return WorkCommentFormData(
                workCommentId = AsCreate.CREATING_ID,
                comment = "",
                createdAt = LocalDateTime.now(),
            )
        }
    }

    val isInCreating: Boolean = workCommentId == AsCreate.CREATING_ID

    /**
     * relationIdはworkId
     */
    override fun toDomainModel(relationId: Long): DomainWorkComment {
        return DomainWorkComment(
            workCommentId = workCommentId,
            workId = relationId,
            comment = comment,
            modifiedAt = if (workCommentId == AsCreate.CREATING_ID) null else LocalDateTimeConverter.toOffsetDateTime(
                LocalDateTime.now()
            ),
            createdAt = LocalDateTimeConverter.toOffsetDateTime(createdAt),
        )
    }
}

/**
 * WorkCommentフォームデータのエラー一覧
 */
data class WorkCommentFormValidateExceptions(
    val comment: ValidationException,
) {
    companion object {
        fun empty(): WorkCommentFormValidateExceptions {
            return WorkCommentFormValidateExceptions(
                comment = ValidationException.empty(),
            )
        }
    }

    /**
     * エラーがあればtrue
     */
    fun hasError(): Boolean {
        return comment.hasError()
    }
}