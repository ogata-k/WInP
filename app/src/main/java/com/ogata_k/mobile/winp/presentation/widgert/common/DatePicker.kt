package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DateRangePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.ogata_k.mobile.winp.R
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
    DatePickerDialog(
        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_extra_large)),
        onDismissRequest = onDismissRequest,
        dismissButton = dismissButton,
        confirmButton = confirmButton,
    ) {
        DateRangePicker(state, showModeToggle = true)
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