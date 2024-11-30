package com.ogata_k.mobile.winp.presentation.page.work.summary

import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.formatter.buildFullDatePatternFormatter
import com.ogata_k.mobile.winp.common.formatter.formatFullDateTimeOrEmpty
import com.ogata_k.mobile.winp.presentation.constant.AppIcons
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.enumerate.SelectRangeDateType
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.model.work.WorkComment
import com.ogata_k.mobile.winp.presentation.widget.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widget.common.BodyLargeText
import com.ogata_k.mobile.winp.presentation.widget.common.BodyMediumText
import com.ogata_k.mobile.winp.presentation.widget.common.BodySmallText
import com.ogata_k.mobile.winp.presentation.widget.common.ButtonMediumText
import com.ogata_k.mobile.winp.presentation.widget.common.DialogOfRangeDatePicker
import com.ogata_k.mobile.winp.presentation.widget.common.LazyColumnScrollBar
import com.ogata_k.mobile.winp.presentation.widget.common.TitleMediumText
import com.ogata_k.mobile.winp.presentation.widget.common.TitleSmallText
import com.ogata_k.mobile.winp.presentation.widget.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widget.common.fromDateToMills
import com.ogata_k.mobile.winp.presentation.widget.common.fromMillsToDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkSummaryScreen(navController: NavController, viewModel: WorkSummaryVM) {
    val uiState: WorkSummaryUiState by viewModel.uiStateFlow.collectAsState()
    val screenLoadingState = uiState.loadingState

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
            val listState = rememberLazyListState()

            Column(modifier = Modifier.padding(padding)) {
                // サマリーの選択期間を固定でヘッダーに表示
                // LazyColumnの一緒にスクロールされないように別で指定する
                SelectableRangeDateHeader(
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
                        contentPadding = PaddingValues(
                            bottom = dimensionResource(R.dimen.padding_large),
                        ),
                        state = listState,
                    ) {
                        when (screenLoadingState) {
                            // 初期化中はデータフェッチしている時を表すのでローディングの表示
                            ScreenLoadingState.READY -> {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(
                                                vertical = dimensionResource(id = R.dimen.padding_medium),
                                                horizontal = dimensionResource(id = R.dimen.padding_large),
                                            ),
                                        horizontalArrangement = Arrangement.Center,
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            ScreenLoadingState.NO_ERROR_INITIALIZED -> {
                                val summaryData = uiState.summaryData

                                // 未完了タスク一覧
                                expandableWorkListContent(
                                    titleResId = R.string.list_of_uncompleted_work,
                                    keyPrefix = "list_of_uncompleted_work",
                                    expanded = uiState.isUncompletedWorkExpanded,
                                    switchExpandState = {
                                        viewModel.expandUncompletedWorkView(it)
                                    },
                                    referenceWorks = summaryData.referenceWorks,
                                    targetWorkIds = summaryData.uncompletedWorkIds,
                                )

                                // 期限切れの未完了タスク一覧
                                expandableWorkListContent(
                                    titleResId = R.string.list_of_expired_uncompleted_work,
                                    keyPrefix = "list_of_expired_uncompleted_work",
                                    expanded = uiState.isExpiredUncompletedWorkExpanded,
                                    switchExpandState = {
                                        viewModel.expandExpiredUncompletedWorkView(it)
                                    },
                                    referenceWorks = summaryData.referenceWorks,
                                    targetWorkIds = summaryData.expiredUncompletedWorkIds,
                                )

                                // 対応済みタスクの一覧
                                expandableWorkListContent(
                                    titleResId = R.string.list_of_completed_work,
                                    keyPrefix = "list_of_completed_work",
                                    expanded = uiState.isCompletedWorkExpanded,
                                    switchExpandState = {
                                        viewModel.expandCompletedWorkView(it)
                                    },
                                    referenceWorks = summaryData.referenceWorks,
                                    targetWorkIds = summaryData.completedWorkIds,
                                )

                                // 期限切れの対応済みタスクの一覧
                                expandableWorkListContent(
                                    titleResId = R.string.list_of_expired_completed_work,
                                    keyPrefix = "list_of_expired_completed_work",
                                    expanded = uiState.isExpiredCompletedWorkExpanded,
                                    switchExpandState = {
                                        viewModel.expandExpiredCompletedWorkView(it)
                                    },
                                    referenceWorks = summaryData.referenceWorks,
                                    targetWorkIds = summaryData.expiredCompletedWorkIds,
                                )

                                // 期間内に投稿されたコメントの一覧
                                expandableWorkCommentListContent(
                                    titleResId = R.string.list_of_posted_work_comment,
                                    keyPrefix = "list_of_posted_work_comment",
                                    expanded = uiState.isPostedCommentExpanded,
                                    switchExpandState = {
                                        viewModel.expandPostedCommentView(it)
                                    },
                                    referenceWorks = summaryData.referenceWorks,
                                    targetWorkComments = summaryData.postedComments,
                                )
                            }

                            ScreenLoadingState.NOT_FOUND_EXCEPTION, ScreenLoadingState.ERROR -> {
                                item {
                                    Row(
                                        modifier = Modifier
                                            .animateItem()
                                            .fillMaxWidth()
                                            .padding(
                                                vertical = dimensionResource(id = R.dimen.padding_medium),
                                                horizontal = dimensionResource(id = R.dimen.padding_large),
                                            )
                                    ) {
                                        BodyLargeText(
                                            stringResource(R.string.failed_fetch_work_summary),
                                            modifier = Modifier.weight(1f),
                                            color = MaterialTheme.colorScheme.error,
                                        )
                                    }
                                }
                            }
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
private fun SelectableRangeDateHeader(
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
                Column(modifier = Modifier.weight(1f)) {
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
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
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

@OptIn(ExperimentalFoundationApi::class)
private fun <T> LazyListScope.expandableListContent(
    @StringRes titleResId: Int,
    @StringRes notExistListItemResId: Int,
    @StringRes notFoundWorkResId: Int,
    keyPrefix: String,
    expanded: Boolean,
    switchExpandState: (toExpand: Boolean) -> Unit,
    listItems: List<T>,
    toListItemId: (listItem: T) -> Long,
    getWork: (listItem: T) -> Work?,
    content: @Composable RowScope.(listItem: T, work: Work) -> Unit,
) {
    stickyHeader(
        key = "%s_header".format(keyPrefix),
    ) {
        Surface(
            modifier = Modifier.clickable { switchExpandState(!expanded) },
            color = MaterialTheme.colorScheme.tertiaryContainer,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        vertical = dimensionResource(id = R.dimen.padding_medium),
                        horizontal = dimensionResource(id = R.dimen.padding_large),
                    ),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TitleMediumText(
                    stringResource(titleResId),
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
                Icon(
                    imageVector = if (expanded) AppIcons.shrinkableHeaderIcon else AppIcons.expandableHeaderIcon,
                    contentDescription = stringResource(if (expanded) R.string.to_shrink else R.string.to_expand),
                )
            }
        }
    }
    item {
        HorizontalDivider(modifier = Modifier.fillMaxWidth())
    }

    if (expanded) {
        if (listItems.isEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .animateItem()
                        .fillMaxWidth()
                        .padding(
                            start = dimensionResource(id = R.dimen.padding_large),
                            top = dimensionResource(id = R.dimen.padding_medium),
                            end = dimensionResource(id = R.dimen.padding_large),
                            bottom = dimensionResource(id = R.dimen.padding_extra_large),
                        )
                ) {
                    BodyLargeText(
                        stringResource(notExistListItemResId),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            items(
                count = listItems.count(),
                key = { index ->
                    "%s_%d".format(keyPrefix, toListItemId(listItems[index]))
                },
                itemContent = { index ->
                    val listItem = listItems[index]
                    val work = getWork(listItem)

                    Surface(
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .animateItem()
                                .padding(
                                    vertical = dimensionResource(id = R.dimen.padding_medium),
                                    horizontal = dimensionResource(id = R.dimen.padding_large),
                                ),
                        ) {
                            if (index != 0) {
                                // 区切りをわかるようにしておく
                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        // 区切り線の上部の余白の分下に取っておく
                                        .padding(bottom = dimensionResource(id = R.dimen.padding_medium)),
                                )
                            }

                            Row {
                                if (work == null) {
                                    BodyLargeText(
                                        stringResource(notFoundWorkResId),
                                        modifier = Modifier.weight(1f),
                                        color = MaterialTheme.colorScheme.error,
                                    )
                                } else {
                                    content(listItem, work)
                                }
                            }
                        }
                    }
                },
            )
        }
    }
}

private fun LazyListScope.expandableWorkListContent(
    @StringRes titleResId: Int,
    keyPrefix: String,
    expanded: Boolean,
    switchExpandState: (toExpand: Boolean) -> Unit,
    referenceWorks: Map<Long, Work>,
    targetWorkIds: List<Long>,
) {
    expandableListContent(
        titleResId = titleResId,
        notExistListItemResId = R.string.not_exist_work,
        notFoundWorkResId = R.string.not_found_work,
        keyPrefix = keyPrefix,
        expanded = expanded,
        switchExpandState = switchExpandState,
        listItems = targetWorkIds,
        toListItemId = { it },
        getWork = { referenceWorks[it] },
        content = { _, work ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                val formattedPeriod =
                    work.formatPeriod(
                        rangeString = stringResource(id = R.string.period_range),
                        noPeriodString = stringResource(id = R.string.no_period),
                    )
                BodySmallText(formattedPeriod)
                BodyLargeText(work.title)
                Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
                val todos = work.todoItems
                val formattedPercent = if (todos.isEmpty()) {
                    stringResource(R.string.invalid_passed_percent)
                } else {
                    val todosCount = todos.count()
                    val completeTodosCount = todos.count { it.isCompleted }
                    stringResource(R.string.valid_passed_percent).format((completeTodosCount.toFloat() / todosCount.toFloat()) * 100f)
                }
                BodyMediumText(
                    "%s%s%s".format(
                        stringResource(R.string.work_todo_passed_rate),
                        stringResource(R.string.colon_separator),
                        formattedPercent
                    ),
                    modifier = Modifier.align(Alignment.End),
                )
            }
        },
    )
}

private fun LazyListScope.expandableWorkCommentListContent(
    @StringRes titleResId: Int,
    keyPrefix: String,
    expanded: Boolean,
    switchExpandState: (toExpand: Boolean) -> Unit,
    referenceWorks: Map<Long, Work>,
    targetWorkComments: List<WorkComment>,
) {
    expandableListContent(
        titleResId = titleResId,
        notExistListItemResId = R.string.not_exist_work_comment,
        // コメントの関連情報の表示にタスクが必要だが、タスクの参照に失敗したら表示しないのでコメントの取得失敗としてよい
        notFoundWorkResId = R.string.not_found_work_comment,
        keyPrefix = keyPrefix,
        expanded = expanded,
        switchExpandState = switchExpandState,
        listItems = targetWorkComments,
        toListItemId = { it.workCommentId },
        getWork = { referenceWorks[it.workId] },
        content = { comment, work ->
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start,
            ) {
                BodySmallText(work.title)
                BodyMediumText(
                    text = comment.comment,
                )
                Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))
                Row(
                    modifier = Modifier.align(Alignment.End),
                    verticalAlignment = Alignment.Bottom,
                ) {
                    if (comment.isModified) {
                        BodySmallText(
                            text = stringResource(R.string.modified_paren_label)
                                    + stringResource(R.string.extra_small_separator),
                        )
                    }

                    BodySmallText(
                        text = formatFullDateTimeOrEmpty(comment.createdAt),
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    )
}
