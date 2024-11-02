package com.ogata_k.mobile.winp.presentation.page

import androidx.compose.material3.SnackbarHostState

/**
 * シンプルなスナックバーを表示
 * WInPでは意図しない限りメッセージを取り消すことができるのがデフォルト
 */
suspend fun showSimpleSnackbar(snackbarHostState: SnackbarHostState, message: String) {
    snackbarHostState.showSnackbar(
        message,
        withDismissAction = true,
    )
}