package com.ogata_k.mobile.winp.presentation.enumerate

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R
import java.time.LocalDate

/**
 * 日にちの範囲選択のタイプ
 */
enum class SelectRangeDateType {
    ThisWeekMonToSun,
    PrevWeekMonToSun,
    ThisWeekSunToSat,
    PrevWeekSunToSat,
    ThisMonthly,
    PrevMonthly,
    ThisYear,
    PrevYear,
    Custom;

    /**
     * 区分で選択対象となるデフォルト値を取得する
     */
    fun getDefaultRange(now: LocalDate): Pair<LocalDate, LocalDate> {
        when (this) {
            ThisWeekMonToSun, Custom -> {
                val startDate = now.minusDays(((now.dayOfWeek.value - 1) % 7).toLong())
                val endDate = startDate.plusDays(6)

                return Pair(startDate, endDate)
            }

            PrevWeekMonToSun -> {
                val startDate = now.minusDays(((now.dayOfWeek.value - 1) % 7).toLong() + 7)
                val endDate = startDate.plusDays(6)

                return Pair(startDate, endDate)
            }

            ThisWeekSunToSat -> {
                val startDate = now.minusDays((now.dayOfWeek.value % 7).toLong())
                val endDate = startDate.plusDays(6)

                return Pair(startDate, endDate)
            }

            PrevWeekSunToSat -> {
                val startDate = now.minusDays((now.dayOfWeek.value % 7).toLong() + 7)
                val endDate = startDate.plusDays(6)

                return Pair(startDate, endDate)
            }

            ThisMonthly -> {
                val startDate = now.withDayOfMonth(1)
                val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())

                return Pair(startDate, endDate)
            }

            PrevMonthly -> {
                val startDate = now.minusMonths(1).withDayOfMonth(1)
                val endDate = startDate.withDayOfMonth(startDate.lengthOfMonth())

                return Pair(startDate, endDate)
            }

            ThisYear -> {
                val startDate = now.withDayOfYear(1)
                val endDate = startDate.withDayOfYear(startDate.lengthOfYear())

                return Pair(startDate, endDate)
            }

            PrevYear -> {
                val startDate = now.minusYears(1).withDayOfYear(1)
                val endDate = startDate.withDayOfYear(startDate.lengthOfYear())

                return Pair(startDate, endDate)
            }
        }
    }

    /**
     * 選択タイプの言語化
     */
    @Composable
    fun getTypeName(): String {
        return stringResource(
            when (this) {
                ThisWeekMonToSun -> R.string.select_range_date_type_ThisWeekMonToSun
                PrevWeekMonToSun -> R.string.select_range_date_type_PrevWeekMonToSun
                ThisWeekSunToSat -> R.string.select_range_date_type_ThisWeekSunToSat
                PrevWeekSunToSat -> R.string.select_range_date_type_PrevWeekSunToSat
                ThisMonthly -> R.string.select_range_date_type_ThisMonthly
                PrevMonthly -> R.string.select_range_date_type_PrevMonthly
                ThisYear -> R.string.select_range_date_type_ThisYear
                PrevYear -> R.string.select_range_date_type_PrevYear
                Custom -> R.string.select_range_date_type_Custom
            }
        )
    }
}