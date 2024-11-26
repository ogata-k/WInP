package com.ogata_k.mobile.winp.presentation.widgert.work_form

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.formatter.formatFullTimeOrEmpty
import com.ogata_k.mobile.winp.presentation.constant.AppIcons
import com.ogata_k.mobile.winp.presentation.widgert.common.BodyLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonMediumText
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
                imageVector = AppIcons.clockIcon,
                contentDescription = stringResource(id = R.string.select_time),
                tint = if (isError) MaterialTheme.colorScheme.error else LocalContentColor.current,
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        TextButton(
            onClick = { switchShowTimePicker(true) },
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = if (isError) MaterialTheme.colorScheme.error else LocalContentColor.current,
            ),
            enabled = canEdit,
        ) {
            BodyLargeText(
                formatFullTimeOrEmpty(time),
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (canDelete) {
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
            IconButton(
                onClick = { updateTime(null) },
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
    if (isInShowTimePicker) {
        val (baseTimeHour, baseTimeMinute) = if (time == null) {
            // 特に深い理由はないが切りのいい時間をデフォルトで選択しておく
            val baseTime = LocalTime.now()
            var baseTimeHour = baseTime.hour
            var baseTimeMinute = baseTime.minute
            if (55 < baseTimeMinute) {
                baseTimeHour = (baseTimeHour + 1) % 24
                baseTimeMinute = 0
            } else if (baseTimeMinute % 5 != 0) {
                baseTimeMinute = (baseTimeMinute - (baseTimeMinute % 5) + 5) % 60
            }

            Pair(baseTimeHour, baseTimeMinute)
        } else {
            // すでに入力してある値を利用
            Pair(time.hour, time.minute)
        }

        val timePickerState = rememberTimePickerState(
            initialHour = baseTimeHour,
            initialMinute = baseTimeMinute,
            is24Hour = true,
        )
        DialogOfTimePicker(
            state = timePickerState,
            onDismissRequest = { /* ignore background dismiss */ },
            dismissButton = {
                TextButton(
                    onClick = {
                        switchShowTimePicker(false)
                    },
                    enabled = canEdit,
                ) {
                    ButtonMediumText(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        updateTime(LocalTime.of(timePickerState.hour, timePickerState.minute))
                        switchShowTimePicker(false)
                    },
                    enabled = canEdit,
                ) {
                    ButtonMediumText(text = stringResource(R.string.ok))
                }
            },
        )
    }
}