package com.ogata_k.mobile.winp.presentation.widgert.work_form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.buildFullDatePatternFormatter
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.DialogOfDatePicker
import com.ogata_k.mobile.winp.presentation.widgert.common.fromDateToMills
import com.ogata_k.mobile.winp.presentation.widgert.common.fromMillsToDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFormColumnItem(
    date: LocalDate?,
    isInShowDatePicker: Boolean,
    switchShowDatePicker: (toShow: Boolean) -> Unit,
    updateDate: (date: LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
    canDelete: Boolean = true,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { switchShowDatePicker(true) }) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
                imageVector = Icons.Filled.DateRange,
                contentDescription = stringResource(id = R.string.select_date)
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        TextField(
            value = if (date == null) "" else buildFullDatePatternFormatter().format(date),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.titleMedium,
        )
        if (canDelete) {
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
            IconButton(onClick = { updateDate(null) }) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium)),
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.clear_form_value)
                )
            }
        }
    }
    if (isInShowDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = if (date == null) null else fromDateToMills(date),
            initialDisplayMode = DisplayMode.Picker,
        )
        DialogOfDatePicker(
            state = datePickerState,
            onDismissRequest = { /* ignore background dismiss */ },
            dismissButton = {
                Button(onClick = {
                    switchShowDatePicker(false)
                }) {
                    ButtonLargeText(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(onClick = {
                    val dateTimestamp: Long? =
                        datePickerState.selectedDateMillis
                    if (dateTimestamp != null) {
                        updateDate(fromMillsToDate(dateTimestamp))
                    }
                    switchShowDatePicker(false)
                }) {
                    ButtonLargeText(text = stringResource(R.string.ok))
                }
            },
        )
    }
}
