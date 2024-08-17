package com.ogata_k.mobile.winp.presentation.enumerate

import android.content.Context
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.buildFullDateTimePatternFormatter
import java.time.LocalDateTime
import java.util.Optional

typealias ValidationException = Optional<out ValidationExceptionType>

/**
 * エラー有無のチェック。バリデーションエラーがあるならtrue
 */
fun ValidationException.hasError(): Boolean {
    return isPresent
}

/**
 * エラーメッセージに変換する
 */
fun ValidationException.toErrorMessage(
    context: Context,
    override: ((context: Context, type: ValidationExceptionType) -> String?)? = null
): String? {
    if (isPresent) return override?.invoke(context, get()) ?: get().toErrorMessage(context)

    return null
}

/**
 * バリデーションエラーの種別
 */
sealed class ValidationExceptionType {
    data class EmptyValue(val selectable: Boolean = false) : ValidationExceptionType()
    data class UnderflowValue(val minimum: Int, val isNumber: Boolean = false) :
        ValidationExceptionType()

    data class OverflowValue(val maximum: Int, val isNumber: Boolean = false) :
        ValidationExceptionType()

    data object InvalidDateTime : ValidationExceptionType()
    data object NeedDateInput : ValidationExceptionType()
    data class NeedBiggerThanDatetime(val other: LocalDateTime) :
        ValidationExceptionType()

    data class NeedSmallerThanDatetime(val other: LocalDateTime) :
        ValidationExceptionType()

    /**
     * 表示用のエラーメッセージに変換する
     */
    fun toErrorMessage(context: Context): String {
        return when (this) {
            is EmptyValue -> if (selectable) {
                context.getString(R.string.validation_exception_empty_select_value)
            } else {
                context.getString(R.string.validation_exception_empty_input_value)
            }

            is UnderflowValue -> if (isNumber) {
                context.getString(R.string.validation_exception_underflow_number_value)
                    .format(minimum)
            } else {
                context.getString(R.string.validation_exception_underflow_input_value)
                    .format(minimum)
            }

            is OverflowValue -> if (isNumber) {
                context.getString(R.string.validation_exception_overflow_number_value)
                    .format(maximum)
            } else {
                context.getString(R.string.validation_exception_overflow_input_value)
                    .format(maximum)
            }

            is InvalidDateTime -> context.getString(R.string.validation_exception_invalid_date_time)

            is NeedDateInput -> context.getString(R.string.validation_exception_need_date)

            is NeedBiggerThanDatetime -> String.format(
                context.getString(R.string.validation_exception_need_bigger_than_other_date_time),
                buildFullDateTimePatternFormatter().format(other)
            )

            is NeedSmallerThanDatetime -> String.format(
                context.getString(R.string.validation_exception_need_smaller_than_other_date_time),
                buildFullDateTimePatternFormatter().format(other)
            )
        }
    }
}