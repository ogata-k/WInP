package com.ogata_k.mobile.winp.presentation.widgert.work_form

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.buildHourMinutePatternFormatter
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.DialogOfTimePicker
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeFormColumnItem(
    time: LocalTime?,
    isInShowTimePicker: Boolean,
    switchShowTimePicker: (toShow: Boolean) -> Unit,
    updateTime: (time: LocalTime?) -> Unit,
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
            onClick = { switchShowTimePicker(true) },
            enabled = canEdit,
        ) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
                imageVector = Icons.Filled.AccessTime,
                contentDescription = stringResource(id = R.string.select_time),
                tint = if (isError) MaterialTheme.colorScheme.error else LocalContentColor.current,
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        TextField(
            value = if (time == null) "" else buildHourMinutePatternFormatter().format(time),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .clickable { switchShowTimePicker(true) }
                .weight(1f),
            textStyle = MaterialTheme.typography.titleMedium,
            isError = isError,
        )
        if (canDelete) {
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
            IconButton(
                onClick = { updateTime(null) },
                enabled = canEdit,
            ) {
                Icon(
                    modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium)),
                    imageVector = Icons.Filled.Close,
                    contentDescription = stringResource(id = R.string.clear_form_value)
                )
            }
        }
    }
    if (isInShowTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = time?.hour ?: LocalTime.now().hour,
            initialMinute = 0,
            is24Hour = true,
        )
        DialogOfTimePicker(
            state = timePickerState,
            onDismissRequest = { /* ignore background dismiss */ },
            dismissButton = {
                Button(
                    onClick = {
                        switchShowTimePicker(false)
                    },
                    enabled = canEdit,
                ) {
                    ButtonLargeText(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        updateTime(LocalTime.of(timePickerState.hour, timePickerState.minute))
                        switchShowTimePicker(false)
                    },
                    enabled = canEdit,
                ) {
                    ButtonLargeText(text = stringResource(R.string.ok))
                }
            },
        )
    }
}