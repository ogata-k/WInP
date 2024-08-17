package com.ogata_k.mobile.winp.presentation.model.work_form

import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import com.ogata_k.mobile.winp.presentation.enumerate.hasError
import java.util.UUID

/**
 * WorkTodoのフォームデータ
 */
data class WorkTodoFormData(
    val uuid: UUID,
    val id: Int?,
    val description: String,
    val isCompleted: Boolean,
) {
    companion object {
        /**
         * 空データを作成
         */
        fun empty(uuid: UUID): WorkTodoFormData {
            return WorkTodoFormData(
                uuid = uuid,
                id = null,
                description = "",
                isCompleted = false,
            )
        }
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
