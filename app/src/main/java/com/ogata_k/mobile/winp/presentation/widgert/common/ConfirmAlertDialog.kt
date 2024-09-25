package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.ogata_k.mobile.winp.R

@Composable
fun ConfirmAlertDialog(
    dialogTitle: String,
    dialogText: String,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    confirmButtonAction: Pair<String, () -> Unit>? = null,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    confirmActionIsDanger: Boolean = false,
) {
    AlertDialog(
        modifier = modifier,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
        ),
        icon = {
            // 中央ぞろえのために空を指定
        },
        title = {
            TitleMediumText(text = dialogTitle)
        },
        text = {
            BodyMediumText(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            if (confirmButtonAction == null) {
                DialogButton(
                    buttonText = stringResource(id = R.string.confirm),
                    action = onDismissRequest,
                )
            } else {
                DialogButton(
                    buttonText = confirmButtonAction.first,
                    action = confirmButtonAction.second,
                    isDanger = confirmActionIsDanger,
                )
            }
        },
        dismissButton = if (confirmButtonAction == null) null else {
            {
                DialogButton(
                    buttonText = stringResource(id = R.string.dismiss),
                    action = onDismissRequest,
                )
            }
        },
    )
}

@Composable
private fun DialogButton(
    buttonText: String,
    action: () -> Unit,
    modifier: Modifier = Modifier,
    isDanger: Boolean = false,
) {
    var buttonColor = ButtonDefaults.textButtonColors()
    if (isDanger) {
        buttonColor = buttonColor.copy(
            contentColor = MaterialTheme.colorScheme.error,
        )
    }
    TextButton(
        modifier = modifier,
        colors = buttonColor,
        onClick = {
            action()
        }
    ) {
        ButtonMediumText(buttonText)
    }
}