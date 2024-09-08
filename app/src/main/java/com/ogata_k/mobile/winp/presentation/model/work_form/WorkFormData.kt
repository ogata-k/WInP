package com.ogata_k.mobile.winp.presentation.model.work_form

import com.ogata_k.mobile.winp.presentation.constant.AsCreate
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
    val id: Int,
    val title: String,
    val description: String,
    val beganDate: LocalDate?,
    val beganTime: LocalTime?,
    val endedDate: LocalDate?,
    val endedTime: LocalTime?,
    val completedAt: LocalDateTime?,
    val editingTodoItem: WorkTodoFormData,
    val todoItems: List<WorkTodoFormData>,
) : ToDomain<DomainWork> {
    companion object : FromDomain<DomainWork, WorkFormData> {
        fun empty(): WorkFormData {
            return WorkFormData(
                id = AsCreate.CREATING_ID,
                title = "",
                description = "",
                beganDate = null,
                beganTime = null,
                endedDate = null,
                endedTime = null,
                completedAt = null,
                editingTodoItem = WorkTodoFormData.empty(UUID.randomUUID()),
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
                id = domain.id ?: AsCreate.CREATING_ID,
                title = domain.title,
                description = domain.description,
                beganDate = domain.beganAt?.toLocalDate(),
                beganTime = domain.beganAt?.toLocalTime(),
                endedDate = domain.endedAt?.toLocalDate(),
                endedTime = domain.endedAt?.toLocalTime(),
                completedAt = domain.completedAt,
                editingTodoItem = WorkTodoFormData.empty(initialUuid),
                todoItems = todoFormItems.toList(),
            )
        }
    }

    val isCompleted: Boolean = completedAt != null

    override fun toDomainModel(): DomainWork {
        return DomainWork(
            id = id,
            title = title,
            description = description,
            beganAt = beganDate?.atTime(beganTime ?: LocalTime.MIN),
            endedAt = endedDate?.atTime(endedTime ?: LocalTime.MAX),
            completedAt = completedAt,
            workTodos = todoItems.map { it.toDomainModel() },
        )
    }
}

/**
 * Workフォームデータのエラー一覧
 */
data class WorkFormValidateExceptions(
    val title: ValidationException,
    val description: ValidationException,
    val beganDateTime: ValidationException,
    val endedDateTime: ValidationException,
    val editingTodoItem: WorkTodoFormValidateExceptions,
) {
    companion object {
        fun empty(): WorkFormValidateExceptions {
            return WorkFormValidateExceptions(
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
        return title.hasError() || description.hasError() || beganDateTime.hasError() || endedDateTime.hasError() || editingTodoItem.hasError()
    }
}