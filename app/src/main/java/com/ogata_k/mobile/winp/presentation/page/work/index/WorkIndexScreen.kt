package com.ogata_k.mobile.winp.presentation.page.work.index

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.common.formatter.buildFullDatePatternFormatter
import com.ogata_k.mobile.winp.presentation.constant.AppIcons
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.page.setting.notification.NotificationSettingRouting
import com.ogata_k.mobile.winp.presentation.page.showSimpleSnackbar
import com.ogata_k.mobile.winp.presentation.page.work.detail.WorkDetailRouting
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditRouting
import com.ogata_k.mobile.winp.presentation.page.work.summary.WorkSummaryRouting
import com.ogata_k.mobile.winp.presentation.widget.common.ButtonMediumText
import com.ogata_k.mobile.winp.presentation.widget.common.DefaultErrorColumnItemBuilder
import com.ogata_k.mobile.winp.presentation.widget.common.DialogOfDatePicker
import com.ogata_k.mobile.winp.presentation.widget.common.DropdownMenuButton
import com.ogata_k.mobile.winp.presentation.widget.common.PagingLoadColumn
import com.ogata_k.mobile.winp.presentation.widget.common.TitleMediumText
import com.ogata_k.mobile.winp.presentation.widget.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widget.common.fromDateToMills
import com.ogata_k.mobile.winp.presentation.widget.common.fromMillsToDate
import com.ogata_k.mobile.winp.presentation.widget.work.WorkItem
import kotlinx.coroutines.launch
import java.time.LocalDate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkIndexScreen(navController: NavController, viewModel: WorkIndexVM) {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val uiState: WorkIndexUiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    // collect fLow and remember state and launch event
    val workPagingItems: LazyPagingItems<Work> = uiState.workPagingData.collectAsLazyPagingItems()

    WithScaffoldSmallTopAppBar(
        text = stringResource(id = R.string.app_name),
        actions = {
            val mContext = LocalContext.current

            IconButton(onClick = { navController.navigate(WorkEditRouting(AsCreate.CREATING_ID).toPath()) }) {
                Icon(
                    imageVector = AppIcons.addIcon,
                    contentDescription = stringResource(R.string.create_work),
                )
            }

            DropdownMenuButton(
                expanded = uiState.inShowMoreAction,
                showMoreAction = { viewModel.showMoreAction(it) },
            ) {
                DropdownMenuItem(
                    text = {
                        TitleMediumText(stringResource(R.string.open_work_summary))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.summaryIcon,
                            contentDescription = stringResource(
                                R.string.work_summary
                            ),
                        )
                    },
                    onClick = {
                        // サマリー画面に遷移
                        navController.navigate(WorkSummaryRouting().toPath())
                        // 遷移した先でも表示が少し残ってしまうのですぐ消えるように指定しておく
                        viewModel.showMoreAction(false)
                    },
                )

                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                DropdownMenuItem(
                    text = {
                        TitleMediumText(stringResource(R.string.setting_notification))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.notificationIcon,
                            contentDescription = stringResource(
                                R.string.setting_notification
                            ),
                        )
                    },
                    onClick = {
                        // 通知設定画面に遷移
                        navController.navigate(NotificationSettingRouting().toPath())
                        // 遷移した先でも表示が少し残ってしまうのですぐ消えるように指定しておく
                        viewModel.showMoreAction(false)
                    },
                )

                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                val privacyPolicyErrorMessage: String = stringResource(R.string.fail_open_page)
                DropdownMenuItem(
                    text = {
                        TitleMediumText(stringResource(R.string.list_of_privacy_policy))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.privacyPolicyIcon,
                            contentDescription = stringResource(
                                R.string.list_of_privacy_policy
                            ),
                        )
                    },
                    onClick = {
                        try {
                            // プライバシーポリシーを外部ブラウザで表示
                            val privacyPolicyUrl =
                                "https://doc-hosting.flycricket.io/winp-privacy-policy/0742c828-021f-4fdf-a406-8b2d37823ade/privacy"
                            val intent = Intent(Intent.ACTION_VIEW)
                            intent.setData(Uri.parse(privacyPolicyUrl))
                            mContext.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            scope.launch {
                                showSimpleSnackbar(snackbarHostState, privacyPolicyErrorMessage)
                            }
                        }

                        // 遷移した先でも表示が少し残ってしまうのですぐ消えるように指定しておく
                        viewModel.showMoreAction(false)
                    },
                )

                HorizontalDivider(modifier = Modifier.fillMaxWidth())

                val licensePageTitle: String = stringResource(R.string.list_of_license)
                DropdownMenuItem(
                    text = {
                        TitleMediumText(stringResource(R.string.list_of_license))
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = AppIcons.licenseIcon,
                            contentDescription = stringResource(
                                R.string.list_of_license
                            ),
                        )
                    },
                    onClick = {
                        // ライセンス画面に遷移
                        val intent = Intent(
                            mContext,
                            OssLicensesMenuActivity::class.java
                        )
                        intent.putExtra("title", licensePageTitle)
                        mContext.startActivity(intent)

                        // 遷移した先でも表示が少し残ってしまうのですぐ消えるように指定しておく
                        viewModel.showMoreAction(false)
                    },
                )
            }
        }
    ) { modifier, appBar ->
        val pullToRefreshState = rememberPullToRefreshState()

        // Eventを監視
        val eventLifecycle = LocalLifecycleOwner.current
        LaunchedEffect(Unit) {
            viewModel.listenEvent(eventLifecycle) {
                workPagingItems.refresh()
            }
        }
        LaunchedEffect(uiState.isInRefreshing, workPagingItems.loadState.refresh) {
            if (uiState.isInRefreshing && workPagingItems.loadState.refresh is LoadState.NotLoading) {
                viewModel.updateListRefreshState(false)
            }
        }

        Scaffold(
            modifier = modifier,
            topBar = appBar,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { padding ->
            PullToRefreshBox(
                modifier = Modifier.padding(padding),
                state = pullToRefreshState,
                isRefreshing = uiState.isInRefreshing,
                onRefresh = {
                    viewModel.updateListRefreshState(true)
                    workPagingItems.refresh()
                },
            ) {
                Column {
                    WorkIndexHeader(
                        uiState = uiState,
                        switchShowDatePickerForSearch = { toShow ->
                            viewModel.showDatePickerForSearch(toShow)
                        },
                        updateAndHideDialogSearchQuery = { date ->
                            viewModel.updateAndHideDialogSearchQuery(
                                workPagingItems,
                                date,
                            )
                        },
                    )
                    PagingLoadColumn(
                        pagingItems = workPagingItems,
                        emptyBuilder = {
                            TitleMediumText(
                                text = stringResource(R.string.not_exist_work),
                                modifier = Modifier.padding(
                                    vertical = dimensionResource(id = R.dimen.padding_medium),
                                    horizontal = dimensionResource(id = R.dimen.padding_medium_large),
                                ),
                            )
                        },
                        errorItemBuilder = { state ->
                            val errorMessage = state.error.message ?: "UNKNOWN ERROR"
                            LaunchedEffect(state.error) {
                                showSimpleSnackbar(snackbarHostState, errorMessage)
                            }
                            DefaultErrorColumnItemBuilder(
                                state = state,
                                modifier = Modifier.padding(
                                    vertical = dimensionResource(id = R.dimen.padding_medium),
                                    horizontal = dimensionResource(id = R.dimen.padding_medium_large),
                                ),
                            )
                        },
                    ) { work ->
                        WorkItem(
                            work,
                            modifier = Modifier.padding(
                                vertical = dimensionResource(id = R.dimen.padding_medium),
                                horizontal = dimensionResource(id = R.dimen.padding_medium_large),
                            ),
                        ) {
                            // 編集画面への遷移
                            navController.navigate(WorkDetailRouting(work.workId).toPath())
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkIndexHeader(
    uiState: WorkIndexUiState,
    switchShowDatePickerForSearch: (toShow: Boolean) -> Unit,
    updateAndHideDialogSearchQuery: (date: LocalDate) -> Unit,
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .clickable { switchShowDatePickerForSearch(true) }
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = AppIcons.calendarIcon,
                contentDescription = stringResource(id = R.string.select_date)
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
            TitleMediumText(text = buildFullDatePatternFormatter().format(uiState.searchDate))
        }
        if (uiState.isInSearchDate) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = fromDateToMills(uiState.searchDate),
                initialDisplayMode = DisplayMode.Picker,
            )
            DialogOfDatePicker(
                state = datePickerState,
                onDismissRequest = { /* ignore background dismiss */ },
                dismissButton = {
                    TextButton(onClick = {
                        switchShowDatePickerForSearch(false)
                    }) {
                        ButtonMediumText(text = stringResource(R.string.cancel))
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val dateTimestamp: Long? =
                            datePickerState.selectedDateMillis
                        if (dateTimestamp != null) {
                            updateAndHideDialogSearchQuery(fromMillsToDate(dateTimestamp))
                        }
                    }) {
                        ButtonMediumText(text = stringResource(R.string.decide))
                    }
                },
            )
        }
    }
}