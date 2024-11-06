package com.ogata_k.mobile.winp.presentation.page.work.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.formatter.formatFullDateTimeOrEmpty
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.DoneWork
import com.ogata_k.mobile.winp.presentation.event.snackbar.work_todo.SucceededUpdateWorkTodo
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.model.work.WorkTodo
import com.ogata_k.mobile.winp.presentation.page.showSimpleSnackbar
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditRouting
import com.ogata_k.mobile.winp.presentation.widgert.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widgert.common.BodyLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.BodySmallText
import com.ogata_k.mobile.winp.presentation.widgert.common.ConfirmAlertDialog
import com.ogata_k.mobile.winp.presentation.widgert.common.DropdownMenuButton
import com.ogata_k.mobile.winp.presentation.widgert.common.HeadlineSmallText
import com.ogata_k.mobile.winp.presentation.widgert.common.Label
import com.ogata_k.mobile.winp.presentation.widgert.common.LazyColumnScrollBar
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widgert.work.WorkTodoItem

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WorkDetailScreen(navController: NavController, viewModel: WorkDetailVM) {
    val uiState: WorkDetailUiState by viewModel.uiStateFlow.collectAsState()
    val screenLoadingState = uiState.loadingState
    val basicScreenState = uiState.basicState

    WithScaffoldSmallTopAppBar(
        text = null,
        navigationIcon = {
            AppBarBackButton(navController = navController)
        },
        actions = {
            if (screenLoadingState.isNoErrorInitialized()) {
                IconButton(
                    onClick = {
                        // 編集画面への遷移
                        navController.navigate(WorkEditRouting(uiState.workId).toPath())
                    },
                ) {
                    Icon(
                        imageVector = Icons.Filled.EditNote,
                        contentDescription = stringResource(
                            R.string.edit_work
                        ),
                    )
                }
                DropdownMenuButton(
                    expanded = uiState.inShowMoreAction,
                    showMoreAction = { viewModel.showMoreAction(it) },
                ) {
                    // 削除するかダイアログで確認して問題ないなら削除処理を行う
                    DropdownMenuItem(
                        text = {
                            TitleMediumText(stringResource(R.string.delete_work))
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = stringResource(
                                    R.string.delete_work
                                ),
                            )
                        },
                        onClick = {
                            viewModel.showDeleteConfirmDialog(true)
                        },
                    )
                }

                if (uiState.inConfirmDelete) {
                    ConfirmAlertDialog(
                        dialogTitle = stringResource(R.string.title_delete_work),
                        dialogText = stringResource(R.string.dialog_content_confirm_delete_work),
                        onDismissRequest = {
                            viewModel.showDeleteConfirmDialog(false)
                        },
                        confirmButtonAction = Pair(
                            stringResource(R.string.delete)
                        ) {
                            viewModel.deleteWork()
                        },
                        // 削除処理という大事な処理なので戻るボタンでは閉じれなくする
                        dismissOnBackPress = false,
                        dismissOnClickOutside = false,
                        confirmActionIsDanger = true,
                        enabledButtons = basicScreenState.actionState.canLaunch(),
                    )
                }
            }
        },
    ) { modifier, appBar ->
        val snackbarHostState = remember { SnackbarHostState() }

        Scaffold(
            modifier = modifier,
            topBar = appBar,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { padding ->

            // Eventを監視
            val eventLifecycle = LocalLifecycleOwner.current
            LaunchedEffect(Unit) {
                viewModel.listenEvent(eventLifecycle)
            }

            when (screenLoadingState) {
                // 初期化中
                ScreenLoadingState.READY -> {
                    Column(
                        modifier = Modifier
                            .padding(padding)
                            .padding(dimensionResource(id = R.dimen.padding_large)),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(Alignment.CenterHorizontally)
                                .padding(dimensionResource(id = R.dimen.padding_medium))
                        )
                    }
                }

                // 初期化完了
                ScreenLoadingState.NO_ERROR_INITIALIZED -> {
                    // 初期化が完了してエラーがない状態のはずなので、エラーを無視してgetして問題なし
                    val work: Work = uiState.work.get()
                    val listState = rememberLazyListState()

                    Box(modifier = Modifier.padding(padding)) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(
                                    vertical = dimensionResource(id = R.dimen.padding_large),
                                    horizontal = dimensionResource(id = R.dimen.padding_medium_large),
                                ),
                            state = listState,
                        ) {
                            item {
                                val formattedPeriod = work.formatPeriod(
                                    rangeString = stringResource(id = R.string.period_range),
                                    noPeriodString = stringResource(id = R.string.no_period),
                                )
                                BodySmallText(formattedPeriod)
                            }
                            item {
                                HeadlineSmallText(
                                    work.title,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                            item {
                                Row(
                                    modifier = Modifier.padding(
                                        top = dimensionResource(id = R.dimen.padding_small),
                                    ),
                                ) {
                                    Label(
                                        labelStatus = if (work.isCompleted) stringResource(id = R.string.completed_label)
                                        else if (work.isExpired) stringResource(id = R.string.expired_label)
                                        else stringResource(id = R.string.not_completed_label),
                                        bgColor = if (work.isCompleted) colorResource(id = R.color.completed_work_item_label)
                                        else if (work.isExpired) colorResource(id = R.color.expired_work_item_label)
                                        else colorResource(id = R.color.doing_work_item_label),
                                        textColor = colorResource(id = R.color.label_text),
                                    )

                                    BodySmallText(
                                        formatFullDateTimeOrEmpty(work.completedAt),
                                        modifier = Modifier.padding(
                                            horizontal = dimensionResource(
                                                id = R.dimen.padding_medium
                                            )
                                        ),
                                    )
                                }
                            }
                            item {
                                BodyLargeText(
                                    work.description,
                                    modifier = Modifier.padding(
                                        top = dimensionResource(id = R.dimen.padding_medium),
                                    ),
                                )
                            }
                            if (work.todoItems.isNotEmpty()) {
                                item {
                                    TitleLargeText(
                                        text = stringResource(id = R.string.work_todo),
                                        modifier = Modifier.padding(
                                            top = dimensionResource(id = R.dimen.padding_extra_large),
                                        ),
                                    )
                                }

                                items(
                                    count = work.todoItems.count(),
                                    key = { work.todoItems[it].workTodoId },
                                ) {
                                    val todoItem: WorkTodo = work.todoItems[it]
                                    WorkTodoItem(
                                        todoItem,
                                        modifier = Modifier
                                            // @todo 余白の取り方の問題でチケット状に成形した形に選択状態が反映されない。ほかの画面との兼ね合いもあるのでとりあえずこのままにしておく。
                                            .padding(dimensionResource(id = R.dimen.padding_medium))
                                            .combinedClickable(
                                                onLongClickLabel = stringResource(id = R.string.update_work_todo_complete_state),
                                                onLongClick = {
                                                    viewModel.showWorkTodoStateConfirmDialog(
                                                        todoItem.workTodoId
                                                    )
                                                }
                                            ) {
                                                // none onClick action
                                            },
                                    )

                                }
                            }
                        }

                        LazyColumnScrollBar(
                            listState = listState,
                            isAlwaysShowScrollBar = false,
                        )
                    }

                    if (uiState.inConfirmWorkTodoState != null) {
                        ConfirmAlertDialog(
                            dialogTitle = stringResource(R.string.title_update_work_todo_complete_state),
                            dialogText = stringResource(R.string.dialog_content_confirm_update_work_todo_complete_state),
                            onDismissRequest = {
                                viewModel.showWorkTodoStateConfirmDialog(null)
                            },
                            confirmButtonAction = Pair(
                                stringResource(R.string.update)
                            ) {
                                viewModel.updateWorkTodoState()
                            },
                            // キャンセルしても問題ないので実行中でなければいくらでも戻ることができるようにする
                            dismissOnBackPress = basicScreenState.actionState.canLaunch(),
                            dismissOnClickOutside = basicScreenState.actionState.canLaunch(),
                            confirmActionIsDanger = false,
                            enabledButtons = basicScreenState.actionState.canLaunch(),
                        )
                    }

                    val event: SnackbarEvent? = uiState.peekSnackbarEvent()
                    if (event != null) {
                        val text = event.toMessage()
                        LaunchedEffect(event) {
                            if (event.getKind().isSucceeded()) {
                                if (event is DoneWork && event.workId == uiState.workId && event.getAction() == EventAction.DELETE) {
                                    // タスク削除
                                    navController.popBackStack()
                                } else if (event is SucceededUpdateWorkTodo && event.workId == uiState.workId) {
                                    // 対応項目更新
                                    showSimpleSnackbar(snackbarHostState, text)

                                    viewModel.consumeEvent()
                                } else {
                                    if (basicScreenState.needForceUpdate) {
                                        viewModel.reloadVMWithConsumeEvent()
                                    } else {
                                        viewModel.consumeEvent()
                                    }
                                }
                            } else {
                                showSimpleSnackbar(snackbarHostState, text)

                                viewModel.consumeEvent()
                            }
                        }
                    }
                }

                // アイテムが見つからず終了
                ScreenLoadingState.NOT_FOUND_EXCEPTION -> {
                    // 続いての処理はできないので前の画面に戻る
                    navController.popBackStack()
                }

                // 予期せぬエラーがあった場合
                ScreenLoadingState.ERROR -> {
                    // 続いての処理はできないので前の画面に戻る
                    navController.popBackStack()
                }
            }
        }
    }
}