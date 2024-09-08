package com.ogata_k.mobile.winp.presentation.model.work_form

import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import com.ogata_k.mobile.winp.presentation.enumerate.hasError
import com.ogata_k.mobile.winp.presentation.model.FromDomainWithUuid
import com.ogata_k.mobile.winp.presentation.model.ToDomain
import java.time.LocalDateTime
import java.util.UUID
import com.ogata_k.mobile.winp.domain.model.work.WorkTodo as DomainWorkTodo

/**
 * WorkTodoのフォームデータ
 */
data class WorkTodoFormData(
    val uuid: UUID,
    val id: Int?,
    val description: String,
    val completedAt: LocalDateTime?,
) : ToDomain<DomainWorkTodo> {
    companion object : FromDomainWithUuid<DomainWorkTodo, WorkTodoFormData> {
        /**
         * 空データを作成
         */
        fun empty(uuid: UUID): WorkTodoFormData {
            return WorkTodoFormData(
                uuid = uuid,
                id = null,
                description = "",
                completedAt = null,
            )
        }

        override fun fromDomainModel(domain: DomainWorkTodo, uuid: UUID): WorkTodoFormData {
            return WorkTodoFormData(
                uuid = uuid,
                id = domain.id,
                description = domain.description,
                completedAt = domain.completedAt,
            )
        }
    }

    val isCompleted: Boolean = completedAt != null

    override fun toDomainModel(): DomainWorkTodo {
        return DomainWorkTodo(
            id = id,
            description = description,
            completedAt = completedAt,
        )
    }
}

/**
 * WorkTodoフォームデータのエラー一覧
 */
data class WorkTodoFormValidateExceptions(
    val description: ValidationException,
    val isCompleted: ValidationException,
) {
    companion object {
        fun empty(): WorkTodoFormValidateExceptions {
            return WorkTodoFormValidateExceptions(
                description = ValidationException.empty(),
                isCompleted = ValidationException.empty(),
            )
        }
    }

    /**
     * エラーがあればtrue
     */
    fun hasError(): Boolean {
        return description.hasError() || isCompleted.hasError()
    }
}
