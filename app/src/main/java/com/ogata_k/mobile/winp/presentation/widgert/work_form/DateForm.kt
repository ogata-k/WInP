package com.ogata_k.mobile.winp.presentation.widgert.work_form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.formatter.formatFullDateOrEmpty
import com.ogata_k.mobile.winp.presentation.constant.AppIcons
import com.ogata_k.mobile.winp.presentation.widgert.common.BodyLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonMediumText
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
    canEdit: Boolean = true,
    canDelete: Boolean = true,
    isError: Boolean = false,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(
            onClick = { switchShowDatePicker(true) },
            enabled = canEdit,
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
                imageVector = AppIcons.calendarIcon,
                contentDescription = stringResource(id = R.string.select_date),
                tint = if (isError) MaterialTheme.colorScheme.error else LocalContentColor.current,
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        TextButton(
            onClick = { switchShowDatePicker(true) },
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = if (isError) MaterialTheme.colorScheme.error else LocalContentColor.current,
            ),
            enabled = canEdit,
        ) {
            BodyLargeText(
                formatFullDateOrEmpty(date),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (canDelete) {
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
            IconButton(
                onClick = { updateDate(null) },
                enabled = canEdit,
            ) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium)),
                    imageVector = AppIcons.closeIcon,
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
                TextButton(
                    onClick = {
                        switchShowDatePicker(false)
                    },
                    enabled = canEdit,
                ) {
                    ButtonMediumText(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val dateTimestamp: Long? =
                            datePickerState.selectedDateMillis
                        if (dateTimestamp != null) {
                            updateDate(fromMillsToDate(dateTimestamp))
                        }
                        switchShowDatePicker(false)
                    },
                    enabled = canEdit,
                ) {
                    ButtonMediumText(text = stringResource(R.string.ok))
                }
            },
        )
    }
}
