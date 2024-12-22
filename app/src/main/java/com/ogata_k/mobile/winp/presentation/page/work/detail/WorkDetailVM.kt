package com.ogata_k.mobile.winp.presentation.page.work.detail

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.common.exception.InvalidArgumentException
import com.ogata_k.mobile.winp.common.type_converter.LocalDateTimeConverter
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkCommentAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.CreateWorkCommentInput
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.DeleteWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.FetchAllWorkCommentsAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.FetchAllWorkCommentsInput
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.GetWorkInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkCommentAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkCommentInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkStateAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkStateInput
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateAsyncUseCase
import com.ogata_k.mobile.winp.domain.use_case.work.UpdateWorkTodoStateInput
import com.ogata_k.mobile.winp.presentation.constant.DummyID
import com.ogata_k.mobile.winp.presentation.enumerate.ScreenLoadingState
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationException
import com.ogata_k.mobile.winp.presentation.enumerate.ValidationExceptionType
import com.ogata_k.mobile.winp.presentation.event.EventAction
import com.ogata_k.mobile.winp.presentation.event.EventBus
import com.ogata_k.mobile.winp.presentation.event.snackbar.SnackbarEvent
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.DoneWork
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.FailedDeleteWork
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.FailedUpdateWork
import com.ogata_k.mobile.winp.presentation.event.snackbar.work_comment.DoneWorkComment
import com.ogata_k.mobile.winp.presentation.event.snackbar.work_comment.FailedCreateWorkComment
import com.ogata_k.mobile.winp.presentation.event.snackbar.work_comment.FailedUpdateWorkComment
import com.ogata_k.mobile.winp.presentation.event.snackbar.work_todo.FailedUpdateWorkTodo
import com.ogata_k.mobile.winp.presentation.event.snackbar.work_todo.SucceededUpdateWorkTodo
import com.ogata_k.mobile.winp.presentation.event.toast.common.ErrorOccurred
import com.ogata_k.mobile.winp.presentation.event.toast.work.NotFoundWork
import com.ogata_k.mobile.winp.presentation.event.toast.work.SucceededCreateWork
import com.ogata_k.mobile.winp.presentation.event.toast.work.SucceededDeleteWork
import com.ogata_k.mobile.winp.presentation.event.toast.work.SucceededUpdateWork
import com.ogata_k.mobile.winp.presentation.model.common.BasicScreenState
import com.ogata_k.mobile.winp.presentation.model.work.Work
import com.ogata_k.mobile.winp.presentation.model.work.WorkComment
import com.ogata_k.mobile.winp.presentation.model.work.WorkTodo
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkCommentFormData
import com.ogata_k.mobile.winp.presentation.model.work_form.WorkCommentFormValidateExceptions
import com.ogata_k.mobile.winp.presentation.page.AbstractViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.Optional
import javax.inject.Inject
import kotlin.jvm.optionals.getOrNull
import com.ogata_k.mobile.winp.presentation.event.snackbar.work.SucceededUpdateWork as SnackbarSucceededUpdateWork

@HiltViewModel
class WorkDetailVM @Inject constructor(
    private val getWorkUseCase: GetWorkAsyncUseCase,
    private val deleteWorkUseCase: DeleteWorkAsyncUseCase,
    private val updateWorkStateUseCase: UpdateWorkStateAsyncUseCase,
    private val updateWorkTodoStateUseCase: UpdateWorkTodoStateAsyncUseCase,
    private val fetchAllWorkCommentsUseCase: FetchAllWorkCommentsAsyncUseCase,
    private val createWorkCommentUseCase: CreateWorkCommentAsyncUseCase,
    private val updateWorkCommentUseCase: UpdateWorkCommentAsyncUseCase,
) : AbstractViewModel<ScreenLoadingState, WorkDetailVMState, ScreenLoadingState, WorkDetailUiState>() {
    companion object {
        private const val TAG = "WorkDetailVM"

        const val COMMENT_MAX_LENGTH = 500
    }

    override val viewModelStateFlow: MutableStateFlow<WorkDetailVMState> = MutableStateFlow(
        WorkDetailVMState(
            // 初期状態は未初期化状態とする
            loadingState = ScreenLoadingState.READY,
            basicState = BasicScreenState.initialState(),
            needForcePopThisScreen = false,
            workId = DummyID.INVALID_ID,
            work = Optional.empty(),
            isInShowCommentForm = false,
            workComments = Result.success(listOf()),
            commentFormData = WorkCommentFormData.empty(),
            validateCommentExceptions = WorkCommentFormValidateExceptions.empty(),
            inShowMoreAction = false,
            inShowMoreCommentAction = false,
            inConfirmDelete = false,
            inConfirmCopy = false,
            inConfirmWorkState = false,
            inConfirmWorkTodoState = null,
        )
    )

    override val uiStateFlow: StateFlow<WorkDetailUiState> =
        asUIStateFlow(viewModelScope, viewModelStateFlow)

    /**
     * workIdを初期化
     */
    fun setWorkId(workId: Long) {
        val vmState = readVMState()
        updateVMState(vmState.copy(workId = workId))
    }

    override fun initializeVM() {
        val vmState = readVMState()
        if (vmState.loadingState.isInitialized()) {
            // 初期化済みなので追加対応の必要なし
            return
        }
        // 事前にsetWorkId()で設定しておく必要がある
        val workId = vmState.workId

        if (workId == DummyID.INVALID_ID) {
            // workId不正
            val loadingState = ScreenLoadingState.ERROR
            updateVMState(
                vmState.copy(
                    loadingState = loadingState,
                    basicState = vmState.basicState.updateInitialize(loadingState),
                    work = Optional.empty(),
                    workComments = Result.failure(InvalidArgumentException("workComments")),
                )
            )

            viewModelScope.launch {
                Log.e(
                    TAG,
                    "Cannot Start Find Work at work_id: $workId"
                )
                EventBus.postToastEvent(ErrorOccurred())
            }
        } else {
            // DBデータでFormの初期化をしたときに初期化を完了とする
            viewModelScope.launch {
                val workResult = getWorkUseCase.call(GetWorkInput(workId))
                if (workResult.isFailure || !workResult.getOrThrow().isPresent) {
                    val loadingState = ScreenLoadingState.NOT_FOUND_EXCEPTION
                    updateVMState(
                        readVMState().copy(
                            loadingState = loadingState,
                            basicState = vmState.basicState.updateInitialize(loadingState),
                            work = Optional.empty(),
                            workComments = Result.failure(InvalidArgumentException("workComments")),
                        )
                    )

                    EventBus.postToastEvent(NotFoundWork(vmState.workId))
                    return@launch
                }

                val loadingState = ScreenLoadingState.NO_ERROR_INITIALIZED
                updateVMState(
                    readVMState().copy(
                        loadingState = loadingState,
                        basicState = vmState.basicState.updateInitialize(loadingState),
                        work = Optional.of(Work.fromDomainModel(workResult.getOrThrow().get())),
                        workComments = fetchAllWorkCommentsUseCase.call(
                            FetchAllWorkCommentsInput(
                                workId
                            )
                        ).let {
                            it.map { list ->
                                list.map { domainComment ->
                                    WorkComment.fromDomainModel(domainComment)
                                }
                            }
                        },
                    )
                )
            }
        }
    }

    override fun reloadVM() {
        reloadVMWithOptional(readVMState())
    }

    /**
     * アクションの実行結果を消費しつつVMをリロードする
     */
    override fun reloadVMWithConsumeEvent() {
        reloadVMWithOptional(readVMState()) { it.toConsumeSnackbarEvent() }
    }

    /**
     * 必要なら追加アクションを対応しつつVMをリロードする
     */
    private fun reloadVMWithOptional(
        vmState: WorkDetailVMState,
        optionalUpdater: (basicState: BasicScreenState) -> BasicScreenState = { it },
    ) {
        if (readVMState().loadingState == ScreenLoadingState.READY) {
            // 初期化中相当の時は無視する
            return
        }

        val loadingState = ScreenLoadingState.READY
        updateVMState(
            vmState.copy(
                loadingState = loadingState,
                basicState = optionalUpdater(vmState.basicState.updateInitialize(loadingState)),
            )
        )

        // 初期化をそのまま呼び出す
        initializeVM()
    }

    override fun replaceVMBasicScreenState(
        viewModelState: WorkDetailVMState,
        basicScreenState: BasicScreenState,
    ): WorkDetailVMState {
        return viewModelState.copy(basicState = basicScreenState)
    }

    /**
     * 強制的に画面を取り除くようにフラグを立てる
     */
    private fun toNeedPopThisScreenState() {
        val vmState = readVMState()
        // 初期化に成功していれば通常に閉じることができるので問題ないかをチェック。そうでない場合、画面上でうまくハンドリングするのでここでは対応しない
        if (!vmState.loadingState.isNoErrorInitialized()) {
            return
        }

        updateVMState(
            vmState.copy(
                needForcePopThisScreen = true,
            )
        )
    }

    /**
     * Eventの監視
     * LaunchedEffect内で呼び出さないと何度も同じOwnerで監視してしまうので注意
     */
    fun listenEvent(
        screenLifecycle: LifecycleOwner,
    ) {
        // 詳細で表示しているWorkはすでに作成済みなので作成イベントの監視は不要だが、この画面から作成している可能性があるので監視する
        EventBus.onEvent<SucceededCreateWork>(screenLifecycle) {
            val vmState = readVMState()
            if (it.fromCopyWorkId != vmState.workId) {
                return@onEvent
            }

            toNeedPopThisScreenState()
        }

        EventBus.onEvent<SucceededUpdateWork>(screenLifecycle) {
            val vmState = readVMState()
            if (it.workId != vmState.workId) {
                return@onEvent
            }
            reloadVM()
        }
    }

    /**
     * さらなる操作を要求するための操作一覧を表示するかどうかを切り替える
     */
    fun showMoreAction(show: Boolean = true) {
        val vmState = readVMState()
        if (vmState.inShowMoreAction || !show) {
            // 表示中なら非表示にする
            updateVMState(
                vmState.copy(
                    inShowMoreAction = show,
                )
            )
            return
        }

        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                inShowMoreAction = true,
            )
        )
    }

    /**
     * 削除をするかどうかの確認ダイアログを表示する
     */
    fun showDeleteConfirmDialog(show: Boolean = true) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                inConfirmDelete = show,
            )
        )
    }

    /**
     * コピーをするかどうかの確認ダイアログを表示する
     */
    fun showCopyConfirmDialog(show: Boolean = true) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                inConfirmCopy = show,
            )
        )
    }

    /**
     * タスクの状態を更新するかどうかの確認ダイアログを表示する
     */
    fun showWorkStateConfirmDialog(show: Boolean = true) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら更新はできない
            return
        }

        updateVMState(
            vmState.copy(
                inConfirmWorkState = show,
            )
        )
    }

    /**
     * 表示しているタスクを削除
     */
    fun deleteWork() {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            val snackbarEvent = FailedDeleteWork(vmState.workId)

            // 正常に初期化ができていないなら削除はできないので失敗として通知
            updateVMState(
                vmState.copy(
                    basicState = vmState.basicState
                        .toDoneAction()
                        .toAcceptSnackbarEvent(snackbarEvent),
                    // 削除確認中ダイアログを非表示にしつつscreenStateを更新する
                    inConfirmDelete = false,
                )
            )
            return
        }

        val newVmState = vmState.copy(basicState = vmState.basicState.toDoingAction())
        updateVMState(newVmState)

        viewModelScope.launch {
            // アプリを強制停止する場合を除いて、実行中に画面をpopしたりしたときなどに最後まで実行されないのは困る
            val result: Result<Unit> = async(Dispatchers.IO + SupervisorJob()) {
                return@async deleteWorkUseCase.call(
                    DeleteWorkInput(
                        vmState.work.get().toDomainModel()
                    )
                )
            }.await()

            val oldVmState = readVMState()
            // 成功時がDoneなのはComposerに通知するため
            val snackbarEvent =
                if (result.isSuccess) DoneWork(
                    vmState.workId,
                    EventAction.DELETE
                ) else FailedDeleteWork(vmState.workId)
            var newBasicState = oldVmState.basicState
                .toDoneAction()
                .toAcceptSnackbarEvent(snackbarEvent)
            if (result.isSuccess) {
                newBasicState = newBasicState
                    // 強制アップデートを要求する
                    .toRequestForceUpdate()
            }
            // 実行結果を通知
            updateVMState(
                oldVmState.copy(
                    basicState = newBasicState,
                    // 削除確認中ダイアログを非表示にする
                    inConfirmDelete = false,
                )
            )

            if (result.isSuccess) {
                // 削除に成功したときは詳細画面をPOPするので、トーストで通知
                val toastEvent = SucceededDeleteWork(workId = oldVmState.workId)
                EventBus.postToastEvent(toastEvent)
            }
        }
    }

    /**
     * 表示しているタスクの状態を更新（完了しているなら完了していない状態にし、完了していないなら完了している状態にする）
     */
    fun updateWorkState() {
        val vmState = readVMState()
        val work: Work? = vmState.work.getOrNull()
        // 初期化が完了していなかったり取得に失敗していたらそれで終了
        if (!vmState.loadingState.isNoErrorInitialized() || work == null) {
            val snackbarEvent = FailedUpdateWork(vmState.workId)

            // 正常に初期化ができていなかったり対象が見つからないのはフローとしておかしいのでエラーとして通知
            updateVMState(
                vmState.copy(
                    basicState = vmState.basicState
                        .toDoneAction()
                        .toAcceptSnackbarEvent(snackbarEvent),
                    inConfirmWorkState = false,
                )
            )
            return
        }

        val newVmState = vmState.copy(basicState = vmState.basicState.toDoingAction())
        updateVMState(newVmState)

        viewModelScope.launch {
            // アプリを強制停止する場合を除いて、実行中に画面をpopしたりしたときなどに最後まで実行されないのは困る
            val result: Result<Work> =
                async(Dispatchers.IO + SupervisorJob()) {
                    return@async updateWorkStateUseCase.call(
                        UpdateWorkStateInput(
                            vmState.workId,
                            if (work.completedAt == null) LocalDateTimeConverter.toOffsetDateTime(
                                LocalDateTime.now()
                            ) else null
                        )
                    ).map { Work.fromDomainModel(it) }
                }.await()

            val oldVmState = readVMState()
            val snackbarEvent: SnackbarEvent =
                if (result.isSuccess) SnackbarSucceededUpdateWork(
                    vmState.workId,
                ) else FailedUpdateWork(
                    vmState.workId,
                )
            val newBasicState = oldVmState.basicState
                .toDoneAction()
                .toAcceptSnackbarEvent(snackbarEvent)
            // 実行結果を通知
            updateVMState(
                oldVmState.copy(
                    // 強制アップデートは要求しない
                    basicState = newBasicState,
                    // ダイアログを非表示にする
                    inConfirmWorkState = false,
                    work = if (result.isSuccess) Optional.of(result.getOrThrow()) else oldVmState.work,
                )
            )

            // 詳細画面上での出来事を想定しているので通知はスナックバーだけで充分
            // しかし、一覧画面を更新してほしいのでEventBusで通知する
            if (result.isSuccess) {
                EventBus.postSnackbarEvent(snackbarEvent)
            }
        }
    }

    /**
     * 指定したタスクの対応予定の項目の状態を更新するかどうかの確認ダイアログを表示する
     */
    fun showWorkTodoStateConfirmDialog(show: Long? = null) {
        val vmState = readVMState()
        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                inConfirmWorkTodoState = show,
            )
        )
    }

    /**
     * 表示している対応項目の状態を更新（完了しているなら完了していない状態にし、完了していないなら完了している状態にする）
     */
    fun updateWorkTodoState() {
        val vmState = readVMState()
        val todoItem: WorkTodo? = vmState.inConfirmWorkTodoState?.let {
            vmState.work.getOrNull()?.todoItems?.firstOrNull { it.workTodoId == vmState.inConfirmWorkTodoState }
        }

        if (!vmState.loadingState.isNoErrorInitialized() || todoItem == null) {
            val snackbarEvent = FailedUpdateWorkTodo(vmState.workId, todoItem?.workTodoId)

            // 正常に初期化ができていなかったり対象が見つからないのはフローとしておかしいのでエラーとして通知
            updateVMState(
                vmState.copy(
                    basicState = vmState.basicState
                        .toDoneAction()
                        .toAcceptSnackbarEvent(snackbarEvent),
                    inConfirmWorkTodoState = null,
                )
            )
            return
        }

        val newVmState = vmState.copy(basicState = vmState.basicState.toDoingAction())
        updateVMState(newVmState)

        viewModelScope.launch {
            // アプリを強制停止する場合を除いて、実行中に画面をpopしたりしたときなどに最後まで実行されないのは困る
            val result: Result<Work> = async(Dispatchers.IO + SupervisorJob()) {
                return@async updateWorkTodoStateUseCase.call(
                    UpdateWorkTodoStateInput(
                        vmState.workId,
                        todoItem.workTodoId,
                        if (todoItem.completedAt == null) LocalDateTimeConverter.toOffsetDateTime(
                            LocalDateTime.now()
                        ) else null
                    )
                ).map { Work.fromDomainModel(it) }
            }.await()

            val oldVmState = readVMState()
            val snackbarEvent =
                if (result.isSuccess) SucceededUpdateWorkTodo(
                    vmState.workId,
                    todoItem.workTodoId
                ) else FailedUpdateWorkTodo(
                    vmState.workId,
                    todoItem.workTodoId
                )
            val newBasicState = oldVmState.basicState
                .toDoneAction()
                .toAcceptSnackbarEvent(snackbarEvent)
            // 実行結果を通知
            updateVMState(
                oldVmState.copy(
                    // 強制アップデートは要求しない
                    basicState = newBasicState,
                    // ダイアログを非表示にする
                    inConfirmWorkTodoState = null,
                    work = if (result.isSuccess) Optional.of(result.getOrThrow()) else oldVmState.work,
                )
            )

            // 詳細画面上での出来事を想定しているので通知はスナックバーだけで充分
        }
    }

    /**
     * 進捗のコメントに対するさらなる操作を要求するための操作一覧を表示するかどうかを切り替える
     */
    fun showMoreCommentAction(show: Boolean = true) {
        val vmState = readVMState()
        if (vmState.inShowMoreCommentAction || !show) {
            // 表示中なら非表示にする
            updateVMState(
                vmState.copy(
                    inShowMoreCommentAction = show,
                )
            )
            return
        }

        if (!vmState.loadingState.isNoErrorInitialized()) {
            // 正常に初期化ができていないなら削除はできない
            return
        }

        updateVMState(
            vmState.copy(
                inShowMoreCommentAction = true,
            )
        )
    }

    /**
     * タスクの進捗コメントのフォームの表示状態を切り替える
     */
    fun showWorkCommentForm(commentId: Long?) {
        val vmState = readVMState()
        if (commentId == null) {
            val newVmState = vmState.copy(
                isInShowCommentForm = false,
            )
            updateVMState(newVmState)
            return
        }

        if (vmState.workComments.isFailure) {
            // 対象となるデータがうまく取得できていないなら作成編集もさせない
            return
        }
        val comment: WorkComment? = vmState.workComments.getOrNull()
            ?.let { it.firstOrNull({ comment -> comment.workCommentId == commentId }) }
        val newFormData = vmState.commentFormData.copy(
            workCommentId = comment?.workCommentId ?: AsCreate.CREATING_ID,
            comment = comment?.comment ?: "",
        )
        val newVmState = vmState.copy(
            isInShowCommentForm = true,
            commentFormData = newFormData,
            validateCommentExceptions = validateCommentFormData(newFormData),
        )
        updateVMState(newVmState)
    }

    /**
     * タスクの進捗のコメントのバリデーション
     */
    private fun validateCommentFormData(formData: WorkCommentFormData): WorkCommentFormValidateExceptions {
        val commentValidated = if (formData.comment.isEmpty()) ValidationException.of(
            ValidationExceptionType.EmptyValue(false)
        )
        else if (formData.comment.length > COMMENT_MAX_LENGTH) ValidationException.of(
            ValidationExceptionType.OverflowValue(
                COMMENT_MAX_LENGTH, false
            )
        )
        else ValidationException.empty()

        return WorkCommentFormValidateExceptions(
            comment = commentValidated,
        )
    }

    /**
     * タスクの進捗のコメントの入力値更新
     */
    fun updateCommentFormComment(value: String) {
        val vmState = readVMState()
        val newWorkCommentFormData = vmState.commentFormData.copy(comment = value)

        val newVmState = vmState.copy(
            commentFormData = newWorkCommentFormData,
            validateCommentExceptions = validateCommentFormData(newWorkCommentFormData),
        )

        updateVMState(newVmState)
    }

    /**
     * タスクの進捗のコメントの作成もしくは更新
     */
    fun createOrUpdateWorkComment() {
        val vmState = readVMState()
        if (!vmState.canLaunchAction() || vmState.validateCommentExceptions.hasError()) {
            // エラーがある場合は作成や更新の処理を行わずに終了
            return
        }


        val newTempVmState = vmState.copy(basicState = vmState.basicState.toDoingAction())
        updateVMState(newTempVmState)

        viewModelScope.launch {
            val formData = vmState.commentFormData
            // アプリを強制停止する場合を除いて、実行中に画面をpopしたりしたときなどに最後まで実行されないのは困る
            val result: Result<Unit> = async(Dispatchers.IO + SupervisorJob()) {
                if (formData.isInCreating) {
                    return@async createWorkCommentUseCase.call(
                        CreateWorkCommentInput(
                            formData.toDomainModel(
                                vmState.workId
                            )
                        )
                    )
                } else {
                    return@async updateWorkCommentUseCase.call(
                        UpdateWorkCommentInput(
                            workId = vmState.workId,
                            workCommentId = formData.workCommentId,
                            comment = formData.comment,
                            modifiedAt = LocalDateTimeConverter.toOffsetDateTime(LocalDateTime.now()),
                        )
                    )
                }
            }.await()

            val oldVmState = readVMState()
            // 成功時がDoneなのはComposerに通知するため
            val snackbarEvent = if (result.isSuccess) DoneWorkComment(
                workCommentId = formData.workCommentId,
                if (formData.isInCreating) EventAction.CREATE else EventAction.UPDATE,
            ) else (if (formData.isInCreating) FailedCreateWorkComment(formData.workCommentId) else FailedUpdateWorkComment(
                formData.workCommentId
            ))

            val newBasicState = oldVmState.basicState
                .toDoneAction()
                .toAcceptSnackbarEvent(snackbarEvent)
            var newVmState = oldVmState.copy(
                basicState = newBasicState,
            )
            if (result.isSuccess) {
                newVmState = newVmState.copy(
                    // フォームなどの表示を非表示にする
                    inShowMoreCommentAction = false,
                    isInShowCommentForm = false,
                    workComments = fetchAllWorkCommentsUseCase.call(
                        FetchAllWorkCommentsInput(
                            vmState.workId
                        )
                    ).let {
                        it.map { list ->
                            list.map { domainComment ->
                                WorkComment.fromDomainModel(domainComment)
                            }
                        }
                    },
                )
            }

            // 実行結果を通知
            updateVMState(newVmState)
        }
    }
}