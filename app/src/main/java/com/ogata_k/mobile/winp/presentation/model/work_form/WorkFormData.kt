package com.ogata_k.mobile.winp.presentation.model.work_form

import java.time.LocalDate
import java.time.LocalTime

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
                todoItems = emptyList(),
            )
        }
    }
}