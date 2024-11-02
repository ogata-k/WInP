package com.ogata_k.mobile.winp.presentation.page.work.edit

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationExceptionType
import com.ogata_k.mobile.winp.presentation.enumerate.hasError
import com.ogata_k.mobile.winp.presentation.enumerate.toErrorMessage
import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.DoneWork
import com.ogata_k.mobile.winp.presentation.page.showSimpleSnackbar
import com.ogata_k.mobile.winp.presentation.widgert.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widgert.common.BodyMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.DraggableBottomSheet
import com.ogata_k.mobile.winp.presentation.widgert.common.ErrorText
import com.ogata_k.mobile.winp.presentation.widgert.common.FormLabel
import com.ogata_k.mobile.winp.presentation.widgert.common.FormTitle
import com.ogata_k.mobile.winp.presentation.widgert.common.HeadlineSmallText
import com.ogata_k.mobile.winp.presentation.widgert.common.LazyColumnScrollBar
import com.ogata_k.mobile.winp.presentation.widgert.common.MaxLengthTextField
import com.ogata_k.mobile.winp.presentation.widgert.common.RadioButtonWithLabel
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.WithLoadingButton
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widgert.common.draggableColumnContainer
import com.ogata_k.mobile.winp.presentation.widgert.common.draggableColumnItems
import com.ogata_k.mobile.winp.presentation.widgert.common.rememberDragDropColumnState
import com.ogata_k.mobile.winp.presentation.widgert.work_form.DateFormColumnItem
import com.ogata_k.mobile.winp.presentation.widgert.work_form.TimeFormColumnItem
import com.ogata_k.mobile.winp.presentation.widgert.work_form.WorkTodoFormColumnItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime

@SuppressLint("SuspiciousIndentation")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WorkEditScreen(navController: NavController, viewModel: WorkEditVM) {
    val uiState: WorkEditUiState by viewModel.uiStateFlow.collectAsState()
    val screenLoadingState = uiState.loadingState
    val basicScreenState = uiState.basicState

    WithScaffoldSmallTopAppBar(
        text = uiState.getFormTitle(LocalContext.current),
        navigationIcon = {
            AppBarBackButton(navController = navController)
        }
    ) { modifier, appBar ->
        val screenScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val focusManager = LocalFocusManager.current

        Scaffold(
            modifier = modifier,
            topBar = appBar,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { padding ->
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
                    val isInDoing = basicScreenState.actionState.isInDoingAction()
                    val canLaunchAction = basicScreenState.actionState.canLaunch()
                    val formData = uiState.formData
                    val workValidateExceptions = uiState.validateExceptions

                    val listState = rememberLazyListState()

                    val dragDropState =
                        rememberDragDropColumnState(
                            lazyListState = listState,
                            draggableItemsNum = formData.todoItems.size,
                            onMove = { fromIndex, toIndex ->
                                viewModel.swapWorkTodoItem(fromIndex, toIndex)
                            })

                    Box(modifier = Modifier.padding(padding)) {
                        LazyColumn(
                            modifier = Modifier
                                .draggableColumnContainer(dragDropState, canLaunchAction),
                            state = listState,
                            horizontalAlignment = Alignment.Start,
                            contentPadding = PaddingValues(dimensionResource(id = R.dimen.padding_large)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
                        ) {
                            item {
                                FormBlock(
                                    title = stringResource(id = R.string.complete_state),
                                    errorMessage = workValidateExceptions.isCompleted.toErrorMessage(
                                        LocalContext.current
                                    ),
                                    isRequired = true,
                                ) {
                                    Column {
                                        RadioButtonWithLabel(
                                            selected = !formData.isCompleted,
                                            onClick = {
                                                focusManager.clearFocus()
                                                viewModel.updateWorkFormCompleted(false)
                                            },
                                            text = stringResource(id = R.string.not_completed),
                                        )
                                        RadioButtonWithLabel(
                                            selected = formData.isCompleted,
                                            onClick = {
                                                focusManager.clearFocus()
                                                viewModel.updateWorkFormCompleted(true)
                                            },
                                            text = stringResource(id = R.string.completed),
                                        )
                                    }
                                }
                            }

                            item {
                                FormBlock(
                                    title = stringResource(id = R.string.work_title),
                                    errorMessage = workValidateExceptions.title.toErrorMessage(
                                        LocalContext.current
                                    ),
                                    formTitleAndError = { t, e ->
                                        WithCounterTitle(
                                            title = t,
                                            current = formData.title.length,
                                            max = WorkEditVM.TITLE_MAX_LENGTH,
                                        )

                                        if (e != null) {
                                            ErrorText(text = e)
                                        }
                                    },
                                    isRequired = true,
                                ) {
                                    MaxLengthTextField(
                                        modifier = Modifier.weight(1f),
                                        readOnly = !canLaunchAction,
                                        value = formData.title,
                                        onValueChange = {
                                            viewModel.updateFormTitle(it)
                                        },
                                        maxLength = WorkEditVM.TITLE_MAX_LENGTH,
                                        isError = workValidateExceptions.title.hasError(),
                                    )
                                }
                            }

                            item {
                                FormBlock(
                                    title = stringResource(id = R.string.work_description),
                                    errorMessage = workValidateExceptions.description.toErrorMessage(
                                        LocalContext.current
                                    ),
                                    formTitleAndError = { t, e ->
                                        WithCounterTitle(
                                            title = t,
                                            current = formData.description.length,
                                            max = WorkEditVM.DESCRIPTION_MAX_LENGTH,
                                        )

                                        if (e != null) {
                                            ErrorText(text = e)
                                        }
                                    },
                                    isRequired = true,
                                ) {
                                    MaxLengthTextField(
                                        modifier = Modifier.weight(1f),
                                        readOnly = !canLaunchAction,
                                        value = formData.description,
                                        onValueChange = {
                                            viewModel.updateFormDescription(it)
                                        },
                                        minLines = 5,
                                        maxLength = WorkEditVM.DESCRIPTION_MAX_LENGTH,
                                        isError = workValidateExceptions.description.hasError(),
                                    )
                                }
                            }

                            //
                            // リスト表示をしてLazyColumnをネストさせるためのFormBlockの実装のインライン化はここから
                            //
                            item {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        FormLabel(isRequired = false)
                                        FormTitle(title = stringResource(id = R.string.work_todo))
                                    }
                                    IconButton(
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.showWorkTodoCreateForm()
                                        },
                                        enabled = canLaunchAction,
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Add,
                                            contentDescription = stringResource(
                                                R.string.create_work_todo
                                            ),
                                        )
                                    }
                                }
                                // エラーの表示は不要
                            }

                            item {
                                if (formData.todoItems.isEmpty()) {
                                    BodyMediumText(stringResource(R.string.form_help_to_add_work_todo_form))
                                } else {
                                    BodyMediumText(stringResource(R.string.form_help_to_modify_work_todo_form))
                                }
                            }

                            draggableColumnItems(
                                items = formData.todoItems,
                                key = { _, item -> item.uuid },
                                dragDropState = dragDropState,
                            ) { modifier, item ->
                                val dismissState = rememberSwipeToDismissBoxState(
                                    initialValue = SwipeToDismissBoxValue.Settled,
                                    confirmValueChange = {
                                        if (it == SwipeToDismissBoxValue.StartToEnd) {
                                            viewModel.removeWorkTodoForm(item.uuid)

                                            return@rememberSwipeToDismissBoxState true
                                        }

                                        return@rememberSwipeToDismissBoxState false
                                    },
                                    positionalThreshold = { it * .6f },
                                )

                                SwipeToDismissBox(
                                    state = dismissState,
                                    modifier = modifier
                                        .animateContentSize()
                                        .animateItemPlacement()
                                        .padding(
                                            horizontal = dimensionResource(id = R.dimen.padding_small),
                                        )
                                        .clickable(enabled = canLaunchAction) {
                                            if (dismissState.currentValue == SwipeToDismissBoxValue.Settled) {
                                                viewModel.showWorkTodoForm(uuid = item.uuid)
                                            }
                                        },
                                    // フォームとして編集ができる状態ならスワイプ削除も有効化
                                    enableDismissFromStartToEnd = canLaunchAction,
                                    enableDismissFromEndToStart = false,
                                    backgroundContent = {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(colorResource(id = R.color.work_todo_dismiss_background)),
                                            contentAlignment = Alignment.CenterStart,
                                        ) {
                                            Icon(
                                                modifier = Modifier
                                                    .padding(
                                                        vertical = dimensionResource(id = R.dimen.padding_medium),
                                                        horizontal = dimensionResource(id = R.dimen.padding_large),
                                                    )
                                                    .size(dimensionResource(id = R.dimen.icon_size_medium)),
                                                imageVector = Icons.Filled.Delete,
                                                contentDescription = stringResource(id = R.string.delete_work_todo),
                                            )
                                        }
                                    },
                                    content = {
                                        WorkTodoFormColumnItem(
                                            todoFormData = item,
                                        )
                                    },
                                )
                            }

                            item {
                                // リスト要素の下なので少し余白を大きめに設ける
                                Spacer(Modifier.height(dimensionResource(id = R.dimen.padding_medium)))
                            }
                            //
                            // リスト表示をしてLazyColumnをネストさせるためのFormBlockの実装のインライン化はここまで
                            //

                            item {
                                FormBlock(
                                    title = stringResource(id = R.string.work_began_at),
                                    errorMessage = workValidateExceptions.beganDateTime.toErrorMessage(
                                        LocalContext.current
                                    ) { context, type ->
                                        if (type is ValidationExceptionType.NeedBiggerThanDatetime) {
                                            return@toErrorMessage String.format(
                                                context.getString(R.string.validation_exception_inconsistent_order_for_before),
                                                context.getString(R.string.work_began_at),
                                                context.getString(R.string.work_ended_at)
                                            )
                                        }

                                        return@toErrorMessage null
                                    },
                                ) {
                                    DateTimeForm(
                                        canEdit = canLaunchAction,
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
                                        isError = workValidateExceptions.beganDateTime.hasError(),
                                    )
                                }
                            }

                            item {
                                FormBlock(
                                    stringResource(id = R.string.work_ended_at),
                                    errorMessage = workValidateExceptions.endedDateTime.toErrorMessage(
                                        LocalContext.current
                                    ) { context, type ->
                                        if (type is ValidationExceptionType.NeedSmallerThanDatetime) {
                                            return@toErrorMessage String.format(
                                                context.getString(R.string.validation_exception_inconsistent_order_for_after),
                                                context.getString(R.string.work_ended_at),
                                                context.getString(R.string.work_began_at)
                                            )
                                        }

                                        return@toErrorMessage null
                                    },
                                ) {
                                    DateTimeForm(
                                        canEdit = canLaunchAction,
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
                                        isError = workValidateExceptions.endedDateTime.hasError(),
                                    )
                                }
                            }

                            item {
                                // 作成更新のボタンの直前なので余白を余分に設ける
                                Spacer(Modifier.height(dimensionResource(id = R.dimen.padding_small)))
                            }

                            val validWork = !workValidateExceptions.hasError()
                            item {
                                Box(
                                    modifier = Modifier.fillParentMaxWidth(),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    WithLoadingButton(
                                        // 入力内容のチェックだけでなく、フォームの状態的に実行できる状態かもチェック
                                        enabled = validWork && canLaunchAction,
                                        isLoading = isInDoing,
                                        onClick = {
                                            focusManager.clearFocus()
                                            viewModel.createOrUpdateWorkItem()
                                        },
                                    ) {
                                        ButtonLargeText(
                                            text = if (uiState.isInCreating)
                                                stringResource(id = R.string.create)
                                            else
                                                stringResource(id = R.string.update)
                                        )
                                    }
                                }
                            }
                        }

                        // Formが長くなってきたのでスクロールを表示するようにする
                        LazyColumnScrollBar(
                            listState = listState,
                            isAlwaysShowScrollBar = false,
                        )
                    }

                    // 外に出したいが、Composableの中でないといけないので余白の指定と一緒に指定する
                    if (uiState.isInShowEditingTodoForm) {
                        TaskTodoBottomSheetForm(viewModel, uiState)
                    }

                    val event: SnackbarEvent? = uiState.peekSnackbarEvent()
                    if (event != null) {
                        val text = event.toMessage()
                        LaunchedEffect(
                            event,
                            basicScreenState.snackbarEvents.count()
                        ) {
                            if (event.getKind().isSucceeded()) {
                                // この画面のメインの処理である作成更新が行われていると確認できたなら画面除去
                                if (event is DoneWork && event.workId == uiState.formData.workId && (event.getAction() == EventAction.CREATE || event.getAction() == EventAction.UPDATE)) {
                                    navController.popBackStack()
                                } else {
                                    if (basicScreenState.needForceUpdate) {
                                        viewModel.reloadVMWithConsumeEvent()
                                    } else {
                                        viewModel.consumeEvent()
                                    }
                                }
                            } else {
                                screenScope.launch {
                                    // 画面を跨がない通知はスナックバーで表示する
                                    showSimpleSnackbar(snackbarHostState, text)

                                    // スナックバーの表示が消えてから少し待って有効化
                                    delay(300)
                                    viewModel.updateToEditingFormState()
                                }
                                viewModel.consumeEvent()
                            }
                        }
                    }
                }

                // アイテムが見つからず終了
                ScreenLoadingState.NOT_FOUND_EXCEPTION -> {
                    navController.popBackStack()
                }

                // 予期せぬエラーがあった場合
                ScreenLoadingState.ERROR -> {
                    navController.popBackStack()
                }
            }
        }
    }
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
private fun FormBlock(
    title: String,
    // 自前でハンドリングしたい場合や非表示の場合はnullを指定する
    isRequired: Boolean? = false,
    errorMessage: String? = null,
    formTitleAndError: @Composable (title: String, errorMessage: String?) -> Unit = { t, e ->
        Row(verticalAlignment = Alignment.Bottom) {
            FormTitle(title = t, modifier = Modifier.weight(1f))
        }

        if (e != null) {
            ErrorText(text = e)
        }
    },
    content: @Composable (RowScope.(errorMessage: String?) -> Unit),
) {
    if (isRequired != null) {
        FormLabel(isRequired = isRequired)
    }
    formTitleAndError(title, errorMessage)
    Row(
        modifier = Modifier
            .padding(
                vertical = dimensionResource(id = R.dimen.padding_medium),
                horizontal = dimensionResource(id = R.dimen.padding_small),
            ),
    ) {
        content(errorMessage)
    }
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
    canEdit: Boolean = true,
    canDelete: Boolean = true,
    isError: Boolean = false,
) {
    Column(modifier = modifier) {
        DateFormColumnItem(
            date = date,
            isInShowDatePicker = isInShowDatePicker,
            switchShowDatePicker = switchShowDatePicker,
            updateDate = updateDate,
            canEdit = canEdit,
            canDelete = canDelete,
            isError = isError,
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))
        TimeFormColumnItem(
            time = time,
            isInShowTimePicker = isInShowTimePicker,
            switchShowTimePicker = switchShowTimePicker,
            updateTime = updateTime,
            canEdit = canEdit,
            canDelete = canDelete,
            isError = isError,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTodoBottomSheetForm(
    viewModel: WorkEditVM,
    uiState: WorkEditUiState,
) {
    val formData = uiState.formData

    DraggableBottomSheet(
        onDismissRequest = { viewModel.showWorkTodoForm(uuid = null) },
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        ),
    ) {
        val focusManager = LocalFocusManager.current
        // スクロールバーを表示させるとシートのサイズがうまく決まらないようなので、Columnに指定してスクロールはさせるがスクロールバーは表示させない
        val sheetScrollState = rememberScrollState()

        val todoItemFormData = formData.editingTodoItem
        val todoItemValidateExceptions = uiState.validateExceptions.editingTodoItem

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
                text = if (uiState.isInCreateWorkTodoForm(
                        uiState.editingTodoItemUuid
                    )
                )
                    stringResource(id = R.string.title_create_work_todo)
                else
                    stringResource(id = R.string.title_edit_work_todo)
            )

            FormBlock(
                title = stringResource(id = R.string.complete_state),
                errorMessage = todoItemValidateExceptions.isCompleted.toErrorMessage(LocalContext.current),
                isRequired = true,
            ) {
                Column {
                    RadioButtonWithLabel(
                        selected = !todoItemFormData.isCompleted,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.updateWorkTodoFormCompleted(false)
                        },
                        text = stringResource(id = R.string.not_completed),
                    )
                    RadioButtonWithLabel(
                        selected = todoItemFormData.isCompleted,
                        onClick = {
                            focusManager.clearFocus()
                            viewModel.updateWorkTodoFormCompleted(true)
                        },
                        text = stringResource(id = R.string.completed),
                    )
                }
            }

            FormBlock(
                title = stringResource(id = R.string.work_todo_description),
                errorMessage = todoItemValidateExceptions.description.toErrorMessage(LocalContext.current),
                formTitleAndError = { t, e ->
                    WithCounterTitle(
                        title = t,
                        current = todoItemFormData.description.length,
                        max = WorkEditVM.WORK_TODO_ITEM_DESCRIPTION_MAX_LENGTH,
                    )

                    if (e != null) {
                        ErrorText(text = e)
                    }
                },
                isRequired = true,
            ) {
                MaxLengthTextField(
                    modifier = Modifier.weight(1f),
                    value = todoItemFormData.description,
                    onValueChange = {
                        viewModel.updateWorkTodoFormDescription(
                            it
                        )
                    },
                    minLines = 3,
                    maxLength = WorkEditVM.WORK_TODO_ITEM_DESCRIPTION_MAX_LENGTH,
                    isError = todoItemValidateExceptions.description.hasError(),
                )
            }

            val isInCreateWorkTodo = uiState.isInCreateWorkTodoForm(uiState.editingTodoItemUuid)
            val validWorkTodo = !todoItemValidateExceptions.hasError()
            FilledTonalButton(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                enabled = validWorkTodo,
                onClick = {
                    focusManager.clearFocus()
                    viewModel.applyEditingToWorkTodoItems()
                },
            ) {
                ButtonMediumText(
                    text = if (isInCreateWorkTodo)
                        stringResource(id = R.string.add)
                    else
                        stringResource(id = R.string.update)
                )
            }
            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding_medium_large)))
        }
    }
}