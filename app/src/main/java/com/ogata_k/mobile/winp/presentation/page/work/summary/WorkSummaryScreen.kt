package com.ogata_k.mobile.winp.presentation.page.work.summary

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.formatter.buildFullDatePatternFormatter
import com.ogata_k.mobile.winp.presentation.constant.AppIcons
import com.ogata_k.mobile.winp.presentation.enumerate.SelectRangeDateType
import com.ogata_k.mobile.winp.presentation.widgert.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.DialogOfRangeDatePicker
import com.ogata_k.mobile.winp.presentation.widgert.common.LazyColumnScrollBar
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleSmallText
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widgert.common.fromDateToMills
import com.ogata_k.mobile.winp.presentation.widgert.common.fromMillsToDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkSummaryScreen(navController: NavController, viewModel: WorkSummaryVM) {
    val uiState: WorkSummaryUiState by viewModel.uiStateFlow.collectAsState()
    val screenLoadingState = uiState.loadingState
    val basicScreenState = uiState.basicState

    WithScaffoldSmallTopAppBar(
        text = stringResource(id = R.string.title_work_summary),
        navigationIcon = {
            AppBarBackButton(navController = navController)
        }
    ) { modifier, appBar ->

        // Eventの監視は必要だが監視するイベントがない

        Scaffold(
            modifier = modifier,
            topBar = appBar,
        ) { padding ->
            val summary = uiState.summaryData
            val listState = rememberLazyListState()

            Column(modifier = Modifier.padding(padding)) {
                // サマリーの選択期間を固定でヘッダーに表示
                // LazyColumnの一緒にスクロールされないように別で指定する
                WorkSummaryHeader(
                    uiState,
                    { toShow -> viewModel.showSelectRangeType(toShow) },
                    { rangeDateType, keepInSelectRangeDateTypeShow ->
                        // 選択を非表示にする
                        viewModel.updateSelectRangeDateType(
                            rangeDateType,
                            keepInSelectRangeDateTypeShow
                        )
                    },
                    { toShow -> viewModel.showRangeDatePicker(toShow) },
                    { from, to ->
                        viewModel.updateRangeDate(
                            from,
                            to,
                            isInSelectRangeDateType = false
                        )
                    }
                )
                HorizontalDivider()
                Box {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large)),
                        contentPadding = PaddingValues(
                            dimensionResource(R.dimen.padding_none),
                            dimensionResource(R.dimen.padding_large),
                            dimensionResource(R.dimen.padding_large),
                            dimensionResource(R.dimen.padding_large),
                        ),
                        state = listState,
                    ) {
                        // TODO 初期化などの読み込み状態を考慮した表示

                        // TODO これらはExpandして表示とかできるので、VMStateなどでExpand中かどうかのフラグをつかって表示を切り分ける
                        // TODO stickyHeaderでExpandするかどうかのヘッダーを扱う
                        item {
                            // TODO 未完了タスクの一覧
                        }
                        item {
                            // TODO 未完了のうち期限切れのタスクの一覧
                        }
                        item {
                            // TODO 完了タスクの一覧
                        }
                        item {
                            // TODO 完了しているけど期限切れのタスクの一覧
                        }
                        item {
                            // TODO コメントの一覧
                        }
                    }

                    LazyColumnScrollBar(
                        listState = listState,
                        isAlwaysShowScrollBar = false,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkSummaryHeader(
    uiState: WorkSummaryUiState,
    switchShowSelectRangeDateType: (toShow: Boolean) -> Unit,
    updateAndHideSelectRangeDateType: (rangeDateType: SelectRangeDateType, keepInSelectRangeDateTypeShow: Boolean) -> Unit,
    switchShowRangeDatePicker: (toShow: Boolean) -> Unit,
    updateAndHideRangeDatePicker: (from: LocalDate, to: LocalDate) -> Unit
) {
    val dateFormatter = buildFullDatePatternFormatter()
    val rangeDateFrom = uiState.summaryRangeFrom
    val rangeDateTo = uiState.summaryRangeTo

    Surface(
        color = MaterialTheme.colorScheme.primary,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { switchShowSelectRangeDateType(true) }
                .padding(
                    vertical = dimensionResource(id = R.dimen.padding_medium),
                    horizontal = dimensionResource(id = R.dimen.padding_large),
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    AnimatedContent(
                        targetState = uiState.rangeDateType.getTypeName(),
                        label = "rangeDateTypeName",
                    ) {
                        TitleMediumText(
                            it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    AnimatedContent(
                        targetState = "%s %s %s".format(
                            dateFormatter.format(rangeDateFrom),
                            stringResource(R.string.period_range),
                            dateFormatter.format(rangeDateTo)
                        ),
                        label = "rangeDateValues",
                    ) {
                        TitleSmallText(
                            it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = AppIcons.selectFromList,
                    contentDescription = stringResource(R.string.select_range_date_type),
                )
            }

            if (uiState.isInSelectRangeDateType) {
                DropdownMenu(
                    expanded = true,
                    // メニューの外がタップされた時に閉じる
                    onDismissRequest = { switchShowSelectRangeDateType(false) },
                ) {
                    SelectRangeDateType.entries.forEachIndexed { index, item ->
                        if (index != 0) {
                            // 最初のアイテム以外はセパレータを挟む
                            HorizontalDivider(
                                modifier = Modifier
                                    .padding(vertical = dimensionResource(R.dimen.padding_small)),
                            )
                        }

                        DropdownMenuItem(
                            text = {
                                TitleMediumText(item.getTypeName())
                            },
                            onClick = {
                                updateAndHideSelectRangeDateType(
                                    item,
                                    item == SelectRangeDateType.Custom
                                )
                            },
                        )
                    }
                }

                if (uiState.isInShowRangeDatePicker) {
                    val rangeDatePickerState = rememberDateRangePickerState(
                        initialSelectedStartDateMillis = fromDateToMills(uiState.summaryRangeFrom.toLocalDate()),
                        initialSelectedEndDateMillis = fromDateToMills(uiState.summaryRangeTo.toLocalDate()),
                        yearRange = DatePickerDefaults.YearRange,
                        initialDisplayMode = DisplayMode.Picker,
                        selectableDates = DatePickerDefaults.AllDates
                    )
                    DialogOfRangeDatePicker(
                        state = rangeDatePickerState,
                        onDismissRequest = { /* ignore background dismiss */ },
                        dismissButton = {
                            TextButton(onClick = {
                                switchShowRangeDatePicker(false)
                            }) {
                                ButtonMediumText(text = stringResource(R.string.cancel))
                            }
                        },
                        confirmButton = {
                            // @todo 選択している日にちを表示する文言がうまく表示範囲に収まらない。
                            TextButton(onClick = {
                                val fromDate = rangeDatePickerState.selectedStartDateMillis?.let {
                                    fromMillsToDate(it)
                                }
                                val toDate = rangeDatePickerState.selectedEndDateMillis?.let {
                                    fromMillsToDate(it)
                                }
                                if (fromDate != null && toDate !== null) {
                                    updateAndHideRangeDatePicker(fromDate, toDate)
                                }
                            }) {
                                ButtonMediumText(text = stringResource(R.string.ok))
                            }
                        },
                    )
                }
            }
        }
    }
}
