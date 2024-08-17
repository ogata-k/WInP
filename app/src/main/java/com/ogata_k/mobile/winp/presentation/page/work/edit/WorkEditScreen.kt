package com.ogata_k.mobile.winp.presentation.page.work.edit

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.enumerate.UiInitializeState
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationExceptionType
import com.ogata_k.mobile.winp.presentation.enumerate.hasError
import com.ogata_k.mobile.winp.presentation.enumerate.toErrorMessage
import com.ogata_k.mobile.winp.presentation.widgert.common.AppBarBackButton
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonLargeText
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.ColumnScrollBar
import com.ogata_k.mobile.winp.presentation.widgert.common.DraggableBottomSheet
import com.ogata_k.mobile.winp.presentation.widgert.common.ErrorText
import com.ogata_k.mobile.winp.presentation.widgert.common.FormLabel
import com.ogata_k.mobile.winp.presentation.widgert.common.FormTitle
import com.ogata_k.mobile.winp.presentation.widgert.common.HeadlineSmallText
import com.ogata_k.mobile.winp.presentation.widgert.common.MaxLengthTextField
import com.ogata_k.mobile.winp.presentation.widgert.common.RadioButtonWithLabel
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleMediumText
import com.ogata_k.mobile.winp.presentation.widgert.common.WithLoadingButton
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widgert.work_form.DateFormColumnItem
import com.ogata_k.mobile.winp.presentation.widgert.work_form.TimeFormColumnItem
import com.ogata_k.mobile.winp.presentation.widgert.work_form.WorkTodoFormColumnItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
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
        val screenScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val scrollState = rememberScrollState()

        Scaffold(
            modifier = modifier,
            topBar = appBar,
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
        ) { padding ->
            Box {
                Column(
                    modifier = Modifier
                        .verticalScroll(scrollState)
                        .padding(padding)
                        .padding(dimensionResource(id = R.dimen.padding_large)),
                    horizontalAlignment = Alignment.Start,
                ) {
                    // TODO DBのエラーハンドリング(snackbarHostState.showSnackBar())をするためにinitializedをScreenLoadResult的なSealedClassにする->次はここから。
                    when (uiState.initializeState) {
                        // 初期化中
                        UiInitializeState.LOADING -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentWidth(Alignment.CenterHorizontally)
                                    .padding(dimensionResource(id = R.dimen.padding_medium))
                            )
                        }
                        // 初期化完了
                        UiInitializeState.INITIALIZED -> {
                            val formData = uiState.formData
                            val workValidateExceptions = uiState.validateExceptions

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
                                    value = formData.title,
                                    onValueChange = {
                                        viewModel.updateFormTitle(it)
                                    },
                                    maxLength = WorkEditVM.TITLE_MAX_LENGTH,
                                    isError = workValidateExceptions.title.hasError(),
                                )
                            }

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
                                    value = formData.description,
                                    onValueChange = {
                                        viewModel.updateFormDescription(it)
                                    },
                                    minLines = 5,
                                    maxLength = WorkEditVM.DESCRIPTION_MAX_LENGTH,
                                    isError = workValidateExceptions.description.hasError(),
                                )
                            }

                            FormBlock(
                                stringResource(id = R.string.work_todo),
                                // formTitle内でカスタマイズ
                                isRequired = null,
                                formTitleAndError = { t, e ->
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            FormLabel(isRequired = false)
                                            FormTitle(title = t)
                                        }
                                        IconButton(onClick = {
                                            viewModel.showWorkTodoCreateForm()
                                        }) {
                                            Icon(
                                                imageVector = Icons.Filled.Add,
                                                contentDescription = stringResource(R.string.create_work_todo),
                                            )
                                        }
                                    }

                                    if (e != null) {
                                        ErrorText(text = e)
                                    }
                                },
                            ) {
                                Column(
                                    verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_medium)),
                                ) {
                                    // TODO 表示アイテムが空のときの表示
                                    formData.todoItems.forEach {
                                        key(it.uuid) {
                                            // TODO リストアイテムの並び替えと削除ができるようにする
                                            WorkTodoFormColumnItem(
                                                todoFormData = it,
                                                modifier = Modifier.clickable {
                                                    viewModel.showWorkTodoForm(uuid = it.uuid)
                                                }
                                            )
                                        }
                                    }
                                }

                                if (uiState.isInShowEditingTodoForm) {
                                    TaskTodoBottomSheetForm(viewModel, uiState)
                                }
                            }

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

                            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding_medium_large)))

                            val validWork = !workValidateExceptions.hasError()
                            WithLoadingButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                // 入力内容のチェックだけでなく、フォームの状態的に実行できる状態かもチェック
                                enabled = validWork && uiState.formState.canDoAction(),
                                isLoading = uiState.formState.isInDoingAction(),
                                onClick = { viewModel.createOrUpdateWorkItem() },
                            ) {
                                ButtonLargeText(
                                    text = if (uiState.isInCreating)
                                        stringResource(id = R.string.create)
                                    else
                                        stringResource(id = R.string.update)
                                )
                            }

                            // TODO 今は仮実装だが、uiState.formStateを見て成功や失敗なら通知して画面を閉じて一覧に戻る
                            if (uiState.formState.isSuccess()) {
                                // TODO 実際の処理に置き換える
                                LaunchedEffect(uiState.formState, snackbarHostState) {
                                    screenScope.launch {
                                        val message = if (uiState.isInCreating) {
                                            "（仮実装）作成に成功しました。"
                                        } else {
                                            "（仮実装）更新に成功しました。"
                                        }
                                        snackbarHostState.showSnackbar(message)

                                        // スナックバーの表示が消えてから少し待って有効化
                                        delay(300)
                                        viewModel.updateToEditingFormState()
                                    }
                                }
                            }

                            if (uiState.formState.isFailure()) {
                                // TODO 実際の処理に置き換える
                                LaunchedEffect(uiState.formState, snackbarHostState) {
                                    screenScope.launch {
                                        val message = if (uiState.isInCreating) {
                                            "（仮実装）作成に失敗しました。"
                                        } else {
                                            "（仮実装）更新に失敗しました。"
                                        }
                                        snackbarHostState.showSnackbar(message)

                                        // スナックバーの表示が消えてから少し待って有効化
                                        delay(300)
                                        viewModel.updateToEditingFormState()
                                    }
                                }
                            }


                            Spacer(Modifier.height(dimensionResource(id = R.dimen.padding_medium_large)))
                        }

                        // エラーがあった場合
                        UiInitializeState.ERROR -> {
                            // TODO 初期化に失敗したことを通知して画面を閉じる（もしくは画面を閉じてから通知）
                        }
                    }
                }

                // Formが長くなってきたのでスクロールを表示するようにする
                ColumnScrollBar(
                    scrollState = scrollState,
                    isAlwaysShowScrollBar = false,
                )
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
private fun ColumnScope.FormBlock(
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
    canDelete: Boolean = true,
    isError: Boolean = false,
) {
    Column(modifier = modifier) {
        DateFormColumnItem(
            date = date,
            isInShowDatePicker = isInShowDatePicker,
            switchShowDatePicker = switchShowDatePicker,
            updateDate = updateDate,
            canDelete = canDelete,
            isError = isError,
        )
        Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.padding_large)))
        TimeFormColumnItem(
            time = time,
            isInShowTimePicker = isInShowTimePicker,
            switchShowTimePicker = switchShowTimePicker,
            updateTime = updateTime,
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
                title = stringResource(id = R.string.work_todo_complete_state),
                errorMessage = todoItemValidateExceptions.isCompleted.toErrorMessage(LocalContext.current),
                isRequired = true,
            ) {
                Column {
                    RadioButtonWithLabel(
                        selected = !todoItemFormData.isCompleted,
                        onClick = {
                            viewModel.updateWorkTodoFormCompleted(
                                false
                            )
                        },
                        text = stringResource(id = R.string.not_completed),
                    )
                    RadioButtonWithLabel(
                        selected = todoItemFormData.isCompleted,
                        onClick = {
                            viewModel.updateWorkTodoFormCompleted(
                                true
                            )
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
                onClick = { viewModel.applyEditingToWorkTodoItems() },
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