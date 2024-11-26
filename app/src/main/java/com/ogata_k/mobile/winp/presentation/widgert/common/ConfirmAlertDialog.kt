package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.presentation.constant.AppIcons

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
    enabledButtons: Boolean = true,
) {
    AlertDialog(
        modifier = modifier,
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside,
        ),
        icon = {
            Icon(
                imageVector = AppIcons.confirmIcon,
                contentDescription = null,
            )
        },
        title = {
            TitleLargeText(text = dialogTitle)
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
                    enabled = enabledButtons,
                )
            } else {
                DialogButton(
                    buttonText = confirmButtonAction.first,
                    action = confirmButtonAction.second,
                    isDanger = confirmActionIsDanger,
                    enabled = enabledButtons,
                )
            }
        },
        dismissButton = if (confirmButtonAction == null) null else {
            {
                DialogButton(
                    buttonText = stringResource(id = R.string.dismiss),
                    action = onDismissRequest,
                    enabled = enabledButtons,
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
    enabled: Boolean = true,
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
        enabled = enabled,
        onClick = {
            action()
        }
    ) {
        ButtonLargeText(buttonText)
    }
}