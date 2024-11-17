package com.ogata_k.mobile.winp.presentation.page.work.detail

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.common.formatter.formatFullDateTimeOrEmpty
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.enumerate.hasError
import com.ogata_k.mobile.winp.presentation.enumerate.toErrorMessage
import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.DoneWork
import com.ogata_k.mobile.winp.presentation.event.snackbar.work_todo.SucceededUpdateWorkTodo
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.model.work.WorkComment
import com.ogata_k.mobile.winp.presentation.model.work.WorkTodo
import com.ogata_k.mobile.winp.presentation.page.showSimpleSnackbar
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditRouting
import com.ogata_k.mobile.winp.presentation.widgert.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widgert.common.BodyLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.BodyMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.BodySmallText
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.ConfirmAlertDialog
import com.ogata_k.mobile.winp.presentation.widgert.common.DraggableBottomSheet
import com.ogata_k.mobile.winp.presentation.widgert.common.DropdownMenuButton
import com.ogata_k.mobile.winp.presentation.widgert.common.FormBlock
import com.ogata_k.mobile.winp.presentation.widgert.common.FormErrorText
import com.ogata_k.mobile.winp.presentation.widgert.common.HeadlineSmallText
import com.ogata_k.mobile.winp.presentation.widgert.common.Label
import com.ogata_k.mobile.winp.presentation.widgert.common.LazyColumnScrollBar
import com.ogata_k.mobile.winp.presentation.widgert.common.MaxLengthTextField
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.WithCounterTitle
import com.ogata_k.mobile.winp.presentation.widgert.common.WithLoading
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widgert.work.WorkCommentItem
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
                                    TitleMediumText(
                                        text = stringResource(id = R.string.work_todo),
                                        modifier = Modifier.padding(
                                            top = dimensionResource(id = R.dimen.padding_extra_large),
                                        ),
                                    )
                                }

                                items(
                                    count = work.todoItems.count(),
                                    key = { Pair("work_todo", work.todoItems[it].workTodoId) },
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

                            // タスクの進捗コメント
                            item {
                                Row(
                                    modifier = Modifier.padding(
                                        top = dimensionResource(id = R.dimen.padding_extra_large),
                                        bottom = dimensionResource(id = R.dimen.padding_medium),
                                    ),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    TitleMediumText(
                                        text = stringResource(id = R.string.work_comment),
                                    )
                                    Spacer(Modifier.weight(1f))
                                    if (uiState.workComments.isSuccess) {
                                        DropdownMenuButton(
                                            expanded = uiState.inShowMoreCommentAction,
                                            showMoreAction = { viewModel.showMoreCommentAction(it) },
                                            modifier = Modifier
                                                .padding(
                                                    end = dimensionResource(id = R.dimen.padding_small),
                                                )
                                                .size(dimensionResource(R.dimen.icon_size_with_text)),
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    TitleMediumText(stringResource(R.string.create_work_comment))
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        imageVector = Icons.Filled.Add,
                                                        contentDescription = stringResource(
                                                            R.string.create_work_comment
                                                        ),
                                                    )
                                                },
                                                onClick = {
                                                    // コメントの作成フォームを表示
                                                    viewModel.showWorkCommentForm(AsCreate.CREATING_ID)
                                                },
                                            )

                                            if (uiState.workComments.getOrDefault(listOf())
                                                    .isNotEmpty()
                                            ) {
                                                HorizontalDivider(
                                                    modifier = Modifier
                                                        .padding(vertical = dimensionResource(R.dimen.padding_small)),
                                                )

                                                DropdownMenuItem(
                                                    text = {
                                                        TitleMediumText(stringResource(R.string.modify_latest_work_comment))
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            imageVector = Icons.Filled.Edit,
                                                            contentDescription = stringResource(
                                                                R.string.modify_latest_work_comment
                                                            ),
                                                        )
                                                    },
                                                    onClick = {
                                                        // コメントの編集フォームを表示
                                                        // 作成日時が最新のものが最初に来るようにソート済み
                                                        val latestCommentId =
                                                            uiState.workComments.getOrThrow()
                                                                .first().workCommentId
                                                        viewModel.showWorkCommentForm(
                                                            latestCommentId
                                                        )
                                                    },
                                                )
                                            }
                                        }
                                    } else {
                                        // アイコン分のダミーの余白
                                        Spacer(Modifier.width(dimensionResource(id = R.dimen.padding_extra_large)))
                                    }
                                }

                                if (uiState.isInShowCommentForm) {
                                    WorkCommentBottomSheetForm(viewModel, uiState)
                                }

                                HorizontalDivider(
                                    thickness = dimensionResource(R.dimen.border_width),
                                    color = colorResource(R.color.border_gray),
                                    modifier = Modifier
                                        .padding(
                                            horizontal = dimensionResource(
                                                id = R.dimen.padding_small
                                            )
                                        ),
                                )
                            }

                            val workCommentsResult: Result<List<WorkComment>> = uiState.workComments
                            if (workCommentsResult.isSuccess) {
                                val workComments = workCommentsResult.getOrThrow()

                                if (workComments.isEmpty()) {
                                    item {
                                        BodyMediumText(
                                            text = stringResource(R.string.not_found_comment),
                                            modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                                        )
                                    }
                                } else {
                                    items(
                                        count = workComments.count(),
                                        key = {
                                            Pair(
                                                "work_comment",
                                                workComments[it].workCommentId
                                            )
                                        },
                                    ) {
                                        val comment: WorkComment = workComments[it]

                                        WorkCommentItem(
                                            comment = comment,
                                            modifier = Modifier
                                                .padding(dimensionResource(id = R.dimen.padding_medium)),
                                        )

                                        HorizontalDivider(
                                            thickness = dimensionResource(R.dimen.border_width),
                                            color = colorResource(R.color.border_gray),
                                            modifier = Modifier.padding(
                                                horizontal = dimensionResource(
                                                    id = R.dimen.padding_small
                                                )
                                            ),
                                        )
                                    }
                                }
                            } else {
                                item {
                                    BodyMediumText(
                                        text = stringResource(R.string.fail_fetch_error),
                                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_medium)),
                                        color = MaterialTheme.colorScheme.error,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WorkCommentBottomSheetForm(
    viewModel: WorkDetailVM,
    uiState: WorkDetailUiState,
) {
    val formData = uiState.commentFormData
    val basicScreenState = uiState.basicState
    val isInDoing = basicScreenState.actionState.isInDoingAction()
    val canLaunchAction = basicScreenState.actionState.canLaunch()

    DraggableBottomSheet(
        onDismissRequest = { viewModel.showWorkCommentForm(null) },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
    ) {
        val focusManager = LocalFocusManager.current
        // スクロールバーを表示させるとシートのサイズがうまく決まらないようなので、Columnに指定してスクロールはさせるがスクロールバーは表示させない
        val sheetScrollState = rememberScrollState()

        val commentFormData = uiState.commentFormData
        val validateCommentExceptions = uiState.validateCommentExceptions

        Column(
            modifier = Modifier
                .verticalScroll(sheetScrollState)
                .padding(
                    // topはBottomSheetの時点で十分余白あるので追加の余白なし
                    end = dimensionResource(id = R.dimen.padding_large),
                    bottom = dimensionResource(id = R.dimen.padding_medium),
                    start = dimensionResource(id = R.dimen.padding_large),
                ),
            horizontalAlignment = Alignment.Start,
        ) {
            HeadlineSmallText(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                text = if (commentFormData.isInCreating)
                    stringResource(id = R.string.title_create_work_comment)
                else
                    stringResource(id = R.string.title_edit_work_comment)
            )

            FormBlock(
                title = stringResource(R.string.work_comment_title),
                errorMessage = validateCommentExceptions.comment.toErrorMessage(LocalContext.current),
                formTitleAndError = { t, e ->
                    WithCounterTitle(
                        title = t,
                        current = formData.comment.length,
                        max = WorkDetailVM.COMMENT_MAX_LENGTH,
                    )

                    if (e != null) {
                        FormErrorText(text = e)
                    }
                },
                isRequired = true,
            ) {
                MaxLengthTextField(
                    modifier = Modifier.weight(1f),
                    readOnly = !canLaunchAction,
                    value = formData.comment,
                    onValueChange = {
                        viewModel.updateCommentFormComment(it)
                    },
                    maxLength = WorkDetailVM.COMMENT_MAX_LENGTH,
                    singleLine = false,
                    minLines = 3,
                    isError = validateCommentExceptions.comment.hasError(),
                )
            }

            val isInCreating: Boolean = uiState.commentFormData.isInCreating
            WithLoading(
                isLoading = isInDoing,
                button = {
                    FilledTonalButton(
                        modifier = it,
                        enabled = !validateCommentExceptions.hasError() && canLaunchAction,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.createOrUpdateWorkComment()
                        },
                    ) {
                        ButtonMediumText(
                            text = if (isInCreating)
                                stringResource(id = R.string.add)
                            else
                                stringResource(id = R.string.update)
                        )
                    }
                },
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding_medium_large)))
        }
    }
}
