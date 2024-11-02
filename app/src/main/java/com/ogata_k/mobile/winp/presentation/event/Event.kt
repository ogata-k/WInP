package com.ogata_k.mobile.winp.presentation.event

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.event.EventKind.FAILED
import com.ogata_k.mobile.winp.presentation.event.EventKind.SUCCEEDED

enum class EventTarget {
    ERROR,
    WORK,
    WORK_TODO;

    @Composable
    fun getName(): String {
        return when (this) {
            ERROR -> stringResource(R.string.error)
            WORK -> stringResource(R.string.work)
            WORK_TODO -> stringResource(R.string.work_todo)
        }
    }
}

enum class EventKind {
    SUCCEEDED,
    FAILED;

    /**
     * アクションに成功したならtrue
     */
    fun isSucceeded(): Boolean {
        return this == SUCCEEDED
    }

    /**
     * アクションに失敗したならtrue
     */
    fun isFailed(): Boolean {
        return this == FAILED
    }
}

enum class EventFormat {
    SIMPLE,
    DETAIL;

    fun isSimple(): Boolean {
        return this == SIMPLE
    }

    fun isDetail(): Boolean {
        return this == DETAIL
    }
}

enum class EventAction {
    OTHER,
    CREATE,
    UPDATE,
    DELETE;

    @Composable
    private fun toSimpleMessage(kind: EventKind): String {
        return when (kind) {
            SUCCEEDED -> when (this) {
                OTHER -> stringResource(R.string.succeeded_simple)
                CREATE -> stringResource(R.string.succeeded_create_simple)
                UPDATE -> stringResource(R.string.succeeded_update_simple)
                DELETE -> stringResource(R.string.succeeded_delete_simple)
            }

            FAILED -> when (this) {
                OTHER -> stringResource(R.string.failed_simple)
                CREATE -> stringResource(R.string.failed_create_simple)
                UPDATE -> stringResource(R.string.failed_update_simple)
                DELETE -> stringResource(R.string.failed_delete_simple)
            }
        }
    }

    @Composable
    private fun toDetailNotFormatMessage(kind: EventKind): String {
        return when (kind) {
            SUCCEEDED -> when (this) {
                OTHER -> stringResource(R.string.succeeded)
                CREATE -> stringResource(R.string.succeeded_create)
                UPDATE -> stringResource(R.string.succeeded_update)
                DELETE -> stringResource(R.string.succeeded_delete)
            }

            FAILED -> when (this) {
                OTHER -> stringResource(R.string.failed)
                CREATE -> stringResource(R.string.failed_create)
                UPDATE -> stringResource(R.string.failed_update)
                DELETE -> stringResource(R.string.failed_delete)
            }
        }
    }

    @Composable
    fun toMessage(target: EventTarget, kind: EventKind, format: EventFormat): String {
        return when (format) {
            EventFormat.SIMPLE -> {
                when (target) {
                    EventTarget.ERROR -> stringResource(R.string.error_occurred)
                    else -> toSimpleMessage(kind)
                }
            }

            EventFormat.DETAIL -> {
                when (target) {
                    EventTarget.ERROR -> stringResource(R.string.error_occurred)
                    else -> toDetailNotFormatMessage(kind).format(target.getName())
                }
            }
        }
    }
}

/**
 * EventBusに流すことのできるイベント
 */
interface Event {
    fun getTarget(): EventTarget

    fun getKind(): EventKind

    fun getFormat(): EventFormat

    fun getAction(): EventAction

    /**
     * 実行結果を説明する文字列に変換
     */
    @Composable
    fun toMessage(): String {
        return getAction().toMessage(getTarget(), getKind(), getFormat())
    }
}