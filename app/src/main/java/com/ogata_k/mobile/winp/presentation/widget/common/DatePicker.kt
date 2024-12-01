package com.ogata_k.mobile.winp.presentation.widget.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerDefaults
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.unit.dp
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.formatter.formatFullDateOrEmpty
import com.ogata_k.mobile.winp.common.formatter.formatFullYearMonthOrEmpty
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogOfDatePicker(
    state: DatePickerState,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
) {
    DatePickerDialog(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_extra_large)),
        onDismissRequest = onDismissRequest,
        dismissButton = dismissButton,
        confirmButton = confirmButton,
    ) {
        DatePicker(state, showModeToggle = true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogOfRangeDatePicker(
    state: DateRangePickerState,
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable (() -> Unit)? = null,
) {
    val rangeDatePickerFormatter = object : DatePickerFormatter {
        override fun formatDate(
            dateMillis: Long?,
            locale: CalendarLocale,
            forContentDescription: Boolean
        ): String {
            return formatFullDateOrEmpty(dateMillis?.let { fromMillsToDate(it) })
        }

        override fun formatMonthYear(monthMillis: Long?, locale: CalendarLocale): String {
            return formatFullYearMonthOrEmpty(monthMillis?.let { fromMillsToDate(it) })
        }
    }

    DatePickerDialog(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_extra_large)),
        onDismissRequest = onDismissRequest,
        dismissButton = dismissButton,
        confirmButton = confirmButton,
    ) {
        DateRangePicker(
            state,
            showModeToggle = true,
            dateFormatter = rangeDatePickerFormatter,
            title = {
                DateRangePickerDefaults.DateRangePickerTitle(
                    displayMode = state.displayMode,
                    modifier = Modifier.padding(start = 32.dp, top = 16.dp, end = 12.dp)
                )
            },
            headline = {
                val selectedStartDateMillis = state.selectedStartDateMillis
                val selectedEndDateMillis = state.selectedEndDateMillis

                val defaultLocale = CalendarLocale.getDefault()
                val formatterStartDate =
                    rangeDatePickerFormatter.formatDate(
                        dateMillis = selectedStartDateMillis,
                        locale = defaultLocale
                    )

                val formatterEndDate =
                    rangeDatePickerFormatter.formatDate(
                        dateMillis = selectedEndDateMillis,
                        locale = defaultLocale
                    )

                val verboseStartDateDescription =
                    if (selectedStartDateMillis == null) stringResource(R.string.no_specified) else stringResource(
                        R.string.specified
                    )

                val verboseEndDateDescription =
                    if (selectedEndDateMillis == null) stringResource(R.string.no_specified) else stringResource(
                        R.string.specified
                    )

                val startHeadlineDescription = "$formatterStartDate: $verboseStartDateDescription"
                val endHeadlineDescription = "$formatterEndDate: $verboseEndDateDescription"

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 32.dp, top = 8.dp, end = 8.dp, bottom = 12.dp)
                        .clearAndSetSemantics {
                            liveRegion = LiveRegionMode.Polite
                            contentDescription =
                                "$startHeadlineDescription, $endHeadlineDescription"
                        },
                ) {
                    Text(text = formatterStartDate)
                    Text(
                        text = "%s %s".format(
                            stringResource(R.string.period_range),
                            formatterEndDate
                        )
                    )
                }
            },
        )
    }
}

fun fromDateToMills(date: LocalDate): Long {
    return date
        .atStartOfDay()
        .toInstant(ZoneOffset.UTC)
        .toEpochMilli()
}

fun fromMillsToDate(millis: Long): LocalDate {
    return Instant
        .ofEpochMilli(millis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}