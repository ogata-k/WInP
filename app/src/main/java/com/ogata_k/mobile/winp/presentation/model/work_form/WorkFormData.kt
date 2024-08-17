package com.ogata_k.mobile.winp.presentation.model.work_form

import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import com.ogata_k.mobile.winp.presentation.enumerate.hasError
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

/**
 * Workのフォーム用データ
 */
data class WorkFormData(
    val title: String,
    val description: String,
    val beganDate: LocalDate?,
    val beganTime: LocalTime?,
    val endedDate: LocalDate?,
    val endedTime: LocalTime?,
    val editingTodoItem: WorkTodoFormData,
    val todoItems: List<WorkTodoFormData>,
) {
    companion object {
        fun empty(): WorkFormData {
            return WorkFormData(
                title = "",
                description = "",
                beganDate = null,
                beganTime = null,
                endedDate = null,
                endedTime = null,
                editingTodoItem = WorkTodoFormData.empty(UUID.randomUUID()),
                todoItems = emptyList(),
            )
        }
    }
}

/**
 * Workフォームデータのエラー一覧
 */
data class WorkFormValidateExceptions(
    val title: ValidationException,
    val description: ValidationException,
    val beganDateTIme: ValidationException,
    val endedDateTime: ValidationException,
    val editingTodoItem: WorkTodoFormValidateExceptions,
) {
    companion object {
        fun empty(): WorkFormValidateExceptions {
            return WorkFormValidateExceptions(
                title = ValidationException.empty(),
                description = ValidationException.empty(),
                beganDateTIme = ValidationException.empty(),
                endedDateTime = ValidationException.empty(),
                editingTodoItem = WorkTodoFormValidateExceptions.empty(),
            )
        }
    }

    /**
     * エラーがあればtrue
     */
    fun hasError(): Boolean {
        return title.hasError() || description.hasError() || beganDateTIme.hasError() || endedDateTime.hasError() || editingTodoItem.hasError()
    }
}