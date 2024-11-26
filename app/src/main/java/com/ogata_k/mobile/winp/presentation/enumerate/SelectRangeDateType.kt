package com.ogata_k.mobile.winp.presentation.enumerate

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
                val endDate = startDate.plusDays(7)

                return Pair(startDate, endDate)
            }

            PrevWeekMonToSun -> {
                val startDate = now.minusDays(((now.dayOfWeek.value - 1) % 7).toLong() + 7)
                val endDate = startDate.plusDays(7)

                return Pair(startDate, endDate)
            }

            ThisWeekSunToSat -> {
                val startDate = now.minusDays((now.dayOfWeek.value % 7).toLong())
                val endDate = startDate.plusDays(7)

                return Pair(startDate, endDate)
            }

            PrevWeekSunToSat -> {
                val startDate = now.minusDays((now.dayOfWeek.value % 7).toLong() + 7)
                val endDate = startDate.plusDays(7)

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
}