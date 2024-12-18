package com.ogata_k.mobile.winp.presentation.model.work_form

import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
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
    val workTodoId: Long,
    val description: String,
    val completedAt: LocalDateTime?,
    val createdAt: LocalDateTime,
) : ToDomain<DomainWorkTodo> {
    companion object : FromDomainWithUuid<DomainWorkTodo, WorkTodoFormData> {
        /**
         * 空データを作成
         */
        fun empty(uuid: UUID): WorkTodoFormData {
            return WorkTodoFormData(
                uuid = uuid,
                workTodoId = AsCreate.CREATING_ID,
                description = "",
                completedAt = null,
                createdAt = LocalDateTime.now(),
            )
        }

        override fun fromDomainModel(domain: DomainWorkTodo, uuid: UUID): WorkTodoFormData {
            return WorkTodoFormData(
                uuid = uuid,
                workTodoId = domain.workTodoId,
                description = domain.description,
                completedAt = domain.completedAt?.let { LocalDateTimeConverter.fromOffsetDateTime(it) },
                createdAt = LocalDateTimeConverter.fromOffsetDateTime(domain.createdAt),
            )
        }

        /**
         * タスク作成用に複製元の対応項目をもとにフォームデータを作成する。
         */
        fun fromDomainModelFromCopyWork(domain: DomainWorkTodo, uuid: UUID): WorkTodoFormData {
            return WorkTodoFormData(
                uuid = uuid,
                // あくまでも作成用
                workTodoId = AsCreate.CREATING_ID,
                description = domain.description,
                // 作成する対応項目は完了していることはあまりないので、未完了の対応項目として作成
                completedAt = null,
                createdAt = LocalDateTime.now(),
            )
        }
    }

    val isCompleted: Boolean = completedAt != null

    override fun toDomainModel(): DomainWorkTodo {
        return DomainWorkTodo(
            workTodoId = workTodoId,
            description = description,
            completedAt = completedAt?.let { LocalDateTimeConverter.toOffsetDateTime(it) },
            createdAt = LocalDateTimeConverter.toOffsetDateTime(createdAt),
        )
    }
}

/**
 * WorkTodoフォームデータのエラー一覧
 */
data class WorkTodoFormValidateExceptions(
    val isCompleted: ValidationException,
    val description: ValidationException,
) {
    companion object {
        fun empty(): WorkTodoFormValidateExceptions {
            return WorkTodoFormValidateExceptions(
                isCompleted = ValidationException.empty(),
                description = ValidationException.empty(),
            )
        }
    }

    /**
     * エラーがあればtrue
     */
    fun hasError(): Boolean {
        return isCompleted.hasError() || description.hasError()
    }
}
