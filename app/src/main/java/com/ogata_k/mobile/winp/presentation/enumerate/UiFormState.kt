package com.ogata_k.mobile.winp.presentation.enumerate

/**
 * Formを伴う作成もしくは編集画面のUI上の状態
 */
enum class UiFormState {
    NOT_INITIALIZE,
    FORM_EDITING,
    DOING_ACTION,
    FAIL_ACTION,
    SUCCESS_ACTION;

    /**
     * 作成や更新などのアクションが実行できる状態ならtrue
     */
    fun canDoAction(): Boolean {
        return this == FORM_EDITING
    }

    /**
     * 作成や更新などのアクションが実行できる状態ならtrue
     */
    fun isInDoingAction(): Boolean {
        return this == DOING_ACTION
    }

    /**
     * アクションに成功したならtrue
     */
    fun isSuccess(): Boolean {
        return this == SUCCESS_ACTION
    }

    /**
     * アクションに失敗したならtrue
     */
    fun isFailure(): Boolean {
        return this == FAIL_ACTION
    }

    /**
     * Formが編集可能な状態ならtrue
     */
    fun canEditForm(): Boolean {
        return canDoAction() || isFailure()
    }
}