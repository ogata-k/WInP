package com.ogata_k.mobile.winp.presentation.page.work.edit

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.buildFullDatePatternFormatter
import com.ogata_k.mobile.winp.common.buildHourMinutePatternFormatter
import com.ogata_k.mobile.winp.presentation.widgert.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonText
import com.ogata_k.mobile.winp.presentation.widgert.common.DialogOfDatePicker
import com.ogata_k.mobile.winp.presentation.widgert.common.DialogOfTimePicker
import com.ogata_k.mobile.winp.presentation.widgert.common.MaxLengthTextField
import com.ogata_k.mobile.winp.presentation.widgert.common.OptionalLabel
import com.ogata_k.mobile.winp.presentation.widgert.common.RequireLabel
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widgert.common.fromDateToMills
import com.ogata_k.mobile.winp.presentation.widgert.common.fromMillsToDate
import java.time.LocalDate
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkEditScreen(navController: NavController, viewModel: WorkEditVM) {
    val uiState: WorkEditUiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    WithScaffoldSmallTopAppBar(
        text = uiState.getFormTitle(LocalContext.current),
        navigationIcon = {
            AppBarBackButton(navController = navController)
        }
    ) { modifier, appBar ->
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = modifier,
            topBar = appBar,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(padding)
                    .padding(dimensionResource(id = R.dimen.padding_large)),
                horizontalAlignment = Alignment.Start,
            ) {
                // TODO DBのエラーハンドリング(snackbarHostState.showSnackBar())をするためにinitializedをScreenLoadResult的なSealedClassにする->次はここから。
                when (uiState.initialized) {
                    // 初期化中
                    false -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(dimensionResource(id = R.dimen.padding_medium))
                        )
                    }
                    // 初期化完了
                    true -> {
                        val formData = uiState.formData

                        // TODO 正しい文字数の指定
                        val titleMaxLength = 100
                        FormBlock(
                            title = stringResource(id = R.string.work_title),
                            formTitle = {
                                WithCounterTitle(
                                    title = it,
                                    current = formData.title.length,
                                    max = titleMaxLength,
                                )
                            },
                            isRequired = true,
                        ) {
                            MaxLengthTextField(
                                modifier = Modifier.weight(1f),
                                value = formData.title,
                                onValueChange = {
                                    viewModel.updateFormTitle(it)
                                },
                                maxLength = titleMaxLength,
                            )
                        }

                        // TODO 正しい文字数の指定
                        val descriptionMaxLength = 2000
                        FormBlock(
                            title = stringResource(id = R.string.work_description),
                            formTitle = {
                                WithCounterTitle(
                                    title = it,
                                    current = formData.description.length,
                                    max = descriptionMaxLength,
                                )
                            },
                            isRequired = true,
                        ) {
                            MaxLengthTextField(
                                modifier = Modifier.weight(1f),
                                value = formData.description,
                                onValueChange = {
                                    viewModel.updateFormDescription(it)
                                },
                                minLines = 5,
                                maxLength = descriptionMaxLength,
                            )
                        }

                        FormBlock(
                            stringResource(id = R.string.work_todo),
                            // TODO header with add button
                        ) {
                            // TODO next
                            // formData.todoItems
                        }

                        FormBlock(
                            stringResource(id = R.string.work_began_at),
                        ) {
                            DateTimeForm(
                                date = formData.beganDate,
                                isInShowDatePicker = uiState.isInShowBeganDatePicker,
                                switchShowDatePicker = {
                                    viewModel.showBeganDatePicker(it)
                                },
                                updateDate = {
                                    viewModel.updateBeganDate(it)
                                },
                                time = formData.beganTime,
                                isInShowTimePicker = uiState.isInShowBeganTimePicker,
                                switchShowTimePicker = {
                                    viewModel.showBeganTimePicker(it)
                                },
                                updateTime = {
                                    viewModel.updateBeganTime(it)
                                },
                            )
                        }

                        FormBlock(
                            stringResource(id = R.string.work_ended_at),
                        ) {
                            DateTimeForm(
                                date = formData.endedDate,
                                isInShowDatePicker = uiState.isInShowEndedDatePicker,
                                switchShowDatePicker = {
                                    viewModel.showEndedDatePicker(it)
                                },
                                updateDate = {
                                    viewModel.updateEndedDate(it)
                                },
                                time = formData.endedTime,
                                isInShowTimePicker = uiState.isInShowEndedTimePicker,
                                switchShowTimePicker = {
                                    viewModel.showEndedTimePicker(it)
                                },
                                updateTime = {
                                    viewModel.updateEndedTime(it)
                                },
                            )
                        }

                        // TODO 更新や保存するためのボタン
                    }
                }
            }
        }
    }
}

@Composable
private fun FormTitle(
    title: String,
    modifier: Modifier = Modifier,
) {
    TitleLargeText(
        text = title,
        modifier = modifier,
    )
}

@Composable
private fun CounterText(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier,
) {
    val countFormat = stringResource(R.string.counter_format)
    TitleMediumText(
        text = countFormat.format(current, max),
        modifier = modifier,
    )
}

@Composable
private fun WithCounterTitle(
    title: String,
    current: Int,
    max: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
    ) {
        FormTitle(
            modifier = Modifier.weight(1f),
            title = title,
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        CounterText(
            current = current,
            max = max,
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
    }
}

@Composable
private fun ColumnScope.FormBlock(
    title: String,
    isRequired: Boolean = false,
    formTitle: @Composable (title: String) -> Unit = {
        Row(verticalAlignment = Alignment.Bottom) {
            FormTitle(title = title, modifier = Modifier.weight(1f))
        }
    },
    content: @Composable (RowScope.() -> Unit),
) {
    if (isRequired) {
        RequireLabel()
    } else {
        OptionalLabel()
    }
    formTitle(title)
    Row(
        modifier = Modifier
            .padding(
                vertical = dimensionResource(id = R.dimen.padding_medium),
                horizontal = dimensionResource(id = R.dimen.padding_small),
            ),
    ) {
        content()
    }
    Spacer(Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
}

@Composable
private fun DateTimeForm(
    date: LocalDate?,
    isInShowDatePicker: Boolean,
    switchShowDatePicker: (toShow: Boolean) -> Unit,
    updateDate: (date: LocalDate?) -> Unit,
    time: LocalTime?,
    isInShowTimePicker: Boolean,
    switchShowTimePicker: (toShow: Boolean) -> Unit,
    updateTime: (time: LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        DateForm(
            date = date,
            isInShowDatePicker = isInShowDatePicker,
            switchShowDatePicker = switchShowDatePicker,
            updateDate = updateDate,
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))
        TimeForm(
            time = time,
            isInShowTimePicker = isInShowTimePicker,
            switchShowTimePicker = switchShowTimePicker,
            updateTime = updateTime,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateForm(
    date: LocalDate?,
    isInShowDatePicker: Boolean,
    switchShowDatePicker: (toShow: Boolean) -> Unit,
    updateDate: (date: LocalDate?) -> Unit,
    modifier: Modifier = Modifier,
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
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
        IconButton(onClick = { updateDate(null) }) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium)),
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.clear_form_value)
            )
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
                    ButtonText(text = stringResource(R.string.cancel))
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
                    ButtonText(text = stringResource(R.string.ok))
                }
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimeForm(
    time: LocalTime?,
    isInShowTimePicker: Boolean,
    switchShowTimePicker: (toShow: Boolean) -> Unit,
    updateTime: (time: LocalTime?) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = { switchShowTimePicker(true) }) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_large)),
                imageVector = Icons.Filled.AccessTime,
                contentDescription = stringResource(id = R.string.select_time)
            )
        }
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        TextField(
            value = if (time == null) "" else buildHourMinutePatternFormatter().format(time),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.weight(1f),
            textStyle = MaterialTheme.typography.titleMedium,
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
        IconButton(onClick = { updateTime(null) }) {
            Icon(
                modifier = Modifier.size(dimensionResource(id = R.dimen.icon_size_medium)),
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.clear_form_value)
            )
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
                Button(onClick = {
                    switchShowTimePicker(false)
                }) {
                    ButtonText(text = stringResource(R.string.cancel))
                }
            },
            confirmButton = {
                Button(onClick = {
                    updateTime(LocalTime.of(timePickerState.hour, timePickerState.minute))
                    switchShowTimePicker(false)
                }) {
                    ButtonText(text = stringResource(R.string.ok))
                }
            },
        )
    }
}