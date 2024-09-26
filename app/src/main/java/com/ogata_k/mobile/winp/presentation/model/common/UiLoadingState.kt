package com.ogata_k.mobile.winp.presentation.model.common

import com.ogata_k.mobile.winp.presentation.enumerate.UiFormState
import com.ogata_k.mobile.winp.presentation.enumerate.UiInitializeState
import com.ogata_k.mobile.winp.presentation.enumerate.UiNextScreenState

/**
 * UIで管理したい状態を保持するクラス
 */
data class UiLoadingState(
    val initializeState: UiInitializeState,
    val screenState: UiNextScreenState,
    val formState: UiFormState,
) {
    companion object {
        /**
         * 初期状態
         */
        fun initialState(): UiLoadingState {
            return UiLoadingState(
                initializeState = UiInitializeState.LOADING,
                screenState = UiNextScreenState.LOADING,
                formState = UiFormState.NOT_INITIALIZE,
            )
        }
    }

    /**
     * 初期化済みにする
     */
    fun toInitialized(): UiLoadingState {
        return copy(
            initializeState = UiInitializeState.INITIALIZED,
            screenState = UiNextScreenState.INITIALIZED,
            formState = UiFormState.USING_FORM,
        )
    }

    /**
     * 初期化済みにする（NotFoundException）
     */
    fun toInitializedWithNotFoundException(): UiLoadingState {
        return copy(
            initializeState = UiInitializeState.NOT_FOUND_EXCEPTION,
            screenState = UiNextScreenState.ERROR,
            formState = UiFormState.NOT_INITIALIZE,
        )
    }

    /**
     * 初期化済みにする（Other Exception）
     */
    fun toInitializedWithRuntimeException(): UiLoadingState {
        return copy(
            initializeState = UiInitializeState.ERROR,
            screenState = UiNextScreenState.ERROR,
            formState = UiFormState.NOT_INITIALIZE,
        )
    }

    /**
     * 処理を実行中とする
     */
    fun toDoingAction(): UiLoadingState {
        return copy(formState = UiFormState.DOING_ACTION)
    }

    /**
     * 次の画面をもとに現在の画面をリロードする状態にする
     */
    fun toStartReloadStateBy(nextScreenState: UiNextScreenState): UiLoadingState {
        return copy(
            initializeState = UiInitializeState.LOADING,
            screenState = nextScreenState,
        )
    }

    /**
     * 次の画面をもとに現在の画面をリロードを完了した状態にする
     */
    fun toReloadedStateBy(nextScreenState: UiNextScreenState): UiLoadingState {
        return copy(
            initializeState = UiInitializeState.INITIALIZED,
            screenState = nextScreenState,
        )
    }

    /**
     * リロード済みにする（NotFoundException）
     */
    fun toReloadedWithNotFoundException(): UiLoadingState {
        return copy(
            initializeState = UiInitializeState.NOT_FOUND_EXCEPTION,
            screenState = UiNextScreenState.ERROR,
        )
    }

    /**
     * リロード済みにする（Other Exception）
     */
    fun toReloadedWithRuntimeException(): UiLoadingState {
        return copy(
            initializeState = UiInitializeState.ERROR,
            screenState = UiNextScreenState.ERROR,
        )
    }

    /**
     * 作成に成功したとしてアクションを終了
     */
    fun toDoneCreate(): UiLoadingState {
        return copy(
            screenState = UiNextScreenState.CREATED,
            formState = UiFormState.USING_FORM,
        )
    }

    /**
     * 更新に成功したとしてアクションを終了
     */
    fun toDoneUpdate(): UiLoadingState {
        return copy(
            screenState = UiNextScreenState.UPDATED,
            formState = UiFormState.USING_FORM,
        )
    }

    /**
     * 削除に成功したとしてアクションを終了
     */
    fun toDoneDelete(): UiLoadingState {
        return copy(
            screenState = UiNextScreenState.DELETED,
            formState = UiFormState.USING_FORM,
        )
    }

    /**
     * 処理に失敗したとしてアクションを終了
     */
    fun toDoneWithError(): UiLoadingState {
        return copy(
            screenState = UiNextScreenState.ERROR,
            formState = UiFormState.USING_FORM,
        )
    }

    /**
     * フォーム操作可能な状態に強制する
     */
    fun forceToUsingForm(): UiLoadingState {
        return copy(
            initializeState = UiInitializeState.INITIALIZED,
            formState = UiFormState.USING_FORM,
        )
    }
}
