package com.ogata_k.mobile.winp.presentation.enumerate

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R

/**
 * アクションの実行結果を表すEnum
 */
enum class ActionDoneResult {
    SUCCEEDED_CREATE, SUCCEEDED_UPDATE, SUCCEEDED_DELETE, FAILED_CREATE, FAILED_UPDATE, FAILED_DELETE;

    /**
     * 作成に関するアクションならtrue
     */
    fun isCreateAction(): Boolean {
        return this == SUCCEEDED_CREATE || this == FAILED_CREATE
    }

    /**
     * 更新に関するアクションならtrue
     */
    fun isUpdateAction(): Boolean {
        return this == SUCCEEDED_UPDATE || this == FAILED_UPDATE
    }

    /**
     * 削除するアクションならtrue
     */
    fun isDeleteAction(): Boolean {
        return this == SUCCEEDED_DELETE || this == FAILED_DELETE
    }

    /**
     * アクションに成功したならtrue
     */
    fun isSucceededAction(): Boolean {
        return this == SUCCEEDED_CREATE || this == SUCCEEDED_UPDATE || this == SUCCEEDED_DELETE
    }

    /**
     * アクションに失敗したならtrue
     */
    fun isFailedAction(): Boolean {
        return this == FAILED_CREATE || this == FAILED_UPDATE || this == FAILED_DELETE
    }

    /**
     * 実行結果を説明する文字列に変換
     */
    @Composable
    fun toMessage(): String {
        return when (this) {
            SUCCEEDED_CREATE -> stringResource(R.string.succeeded_create)
            SUCCEEDED_UPDATE -> stringResource(R.string.succeeded_update)
            SUCCEEDED_DELETE -> stringResource(R.string.succeeded_delete)
            FAILED_CREATE -> stringResource(R.string.failed_create)
            FAILED_UPDATE -> stringResource(R.string.failed_update)
            FAILED_DELETE -> stringResource(R.string.failed_delete)
        }
    }
}
