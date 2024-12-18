package com.ogata_k.mobile.winp.presentation.model.work_form

import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import com.ogata_k.mobile.winp.presentation.enumerate.hasError
import com.ogata_k.mobile.winp.presentation.model.FromDomain
import com.ogata_k.mobile.winp.presentation.model.ToDomain
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID
import com.ogata_k.mobile.winp.domain.model.work.Work as DomainWork

/**
 * Workのフォーム用データ
 */
data class WorkFormData(
    val workId: Long,
    val title: String,
    val description: String,
    val beganDate: LocalDate?,
    val beganTime: LocalTime?,
    val endedDate: LocalDate?,
    val endedTime: LocalTime?,
    val completedAt: LocalDateTime?,
    val editingTodoItem: WorkTodoFormData,
    val createdAt: LocalDateTime,
    val todoItems: List<WorkTodoFormData>,
) : ToDomain<DomainWork> {
    companion object : FromDomain<DomainWork, WorkFormData> {
        fun empty(): WorkFormData {
            return WorkFormData(
                workId = AsCreate.CREATING_ID,
                title = "",
                description = "",
                beganDate = null,
                beganTime = null,
                endedDate = null,
                endedTime = null,
                completedAt = null,
                editingTodoItem = WorkTodoFormData.empty(UUID.randomUUID()),
                createdAt = LocalDateTime.now(),
                todoItems = emptyList(),
            )
        }

        override fun fromDomainModel(domain: DomainWork): WorkFormData {
            val todoFormItems: MutableList<WorkTodoFormData> = mutableListOf()
            val initialUuid = UUID.randomUUID()
            val usingUuid: MutableList<UUID> = mutableListOf(initialUuid)
            domain.workTodos.forEach {
                var uuid = UUID.randomUUID()
                while (usingUuid.contains(uuid)) {
                    uuid = UUID.randomUUID()
                }
                usingUuid.add(uuid)
                todoFormItems.add(WorkTodoFormData.fromDomainModel(it, uuid))
            }

            return WorkFormData(
                workId = domain.workId,
                title = domain.title,
                description = domain.description,
                beganDate = domain.beganAt?.toLocalDate(),
                beganTime = domain.beganAt?.toLocalTime(),
                endedDate = domain.endedAt?.toLocalDate(),
                endedTime = domain.endedAt?.toLocalTime(),
                completedAt = domain.completedAt?.let { LocalDateTimeConverter.fromOffsetDateTime(it) },
                editingTodoItem = WorkTodoFormData.empty(initialUuid),
                createdAt = LocalDateTimeConverter.fromOffsetDateTime(domain.createdAt),
                todoItems = todoFormItems.toList(),
            )
        }

        /**
         * タスク作成用に複製元のタスクをもとにフォームデータを作成する。
         */
        fun fromDomainModelFromCopyWork(domain: DomainWork): WorkFormData {
            val todoFormItems: MutableList<WorkTodoFormData> = mutableListOf()
            val initialUuid = UUID.randomUUID()
            val usingUuid: MutableList<UUID> = mutableListOf(initialUuid)
            domain.workTodos.forEach {
                var uuid = UUID.randomUUID()
                while (usingUuid.contains(uuid)) {
                    uuid = UUID.randomUUID()
                }
                usingUuid.add(uuid)
                todoFormItems.add(WorkTodoFormData.fromDomainModelFromCopyWork(it, uuid))
            }

            return WorkFormData(
                // あくまでも作成用
                workId = AsCreate.CREATING_ID,
                title = domain.title,
                description = domain.description,
                beganDate = domain.beganAt?.toLocalDate(),
                beganTime = domain.beganAt?.toLocalTime(),
                endedDate = domain.endedAt?.toLocalDate(),
                endedTime = domain.endedAt?.toLocalTime(),
                // 作成するタスクは完了していることはあまりないので、未完了のタスクとして作成
                completedAt = null,
                editingTodoItem = WorkTodoFormData.empty(initialUuid),
                createdAt = LocalDateTime.now(),
                todoItems = todoFormItems.toList(),
            )
        }
    }

    val isCompleted: Boolean = completedAt != null

    override fun toDomainModel(): DomainWork {
        return DomainWork(
            workId = workId,
            title = title,
            description = description,
            beganAt = beganDate?.atTime(beganTime ?: LocalTime.MIN)
                ?.let { LocalDateTimeConverter.toOffsetDateTime(it) },
            endedAt = endedDate?.atTime(endedTime ?: LocalTime.MAX)
                ?.let { LocalDateTimeConverter.toOffsetDateTime(it) },
            completedAt = completedAt?.let { LocalDateTimeConverter.toOffsetDateTime(it) },
            workTodos = todoItems.map { it.toDomainModel() },
            createdAt = LocalDateTimeConverter.toOffsetDateTime(createdAt),
        )
    }
}

/**
 * Workフォームデータのエラー一覧
 */
data class WorkFormValidateExceptions(
    val isCompleted: ValidationException,
    val title: ValidationException,
    val description: ValidationException,
    val beganDateTime: ValidationException,
    val endedDateTime: ValidationException,
    val editingTodoItem: WorkTodoFormValidateExceptions,
) {
    companion object {
        fun empty(): WorkFormValidateExceptions {
            return WorkFormValidateExceptions(
                isCompleted = ValidationException.empty(),
                title = ValidationException.empty(),
                description = ValidationException.empty(),
                beganDateTime = ValidationException.empty(),
                endedDateTime = ValidationException.empty(),
                editingTodoItem = WorkTodoFormValidateExceptions.empty(),
            )
        }
    }

    /**
     * エラーがあればtrue
     */
    fun hasError(): Boolean {
        return isCompleted.hasError() || title.hasError() || description.hasError() || beganDateTime.hasError() || endedDateTime.hasError() || editingTodoItem.hasError()
    }
}