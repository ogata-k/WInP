package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonElevation
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ogata_k.mobile.winp.R

/**
 * AppBarなどでの戻るボタン
 */
@Composable
fun AppBarBackButton(navController: NavController) {
    var isPopped by remember { mutableStateOf(false) }

    LaunchedEffect(isPopped) {
        if (isPopped) {
            navController.popBackStack()
        }
    }
    BackHandlerSetter(!isPopped) { isPopped = true }
    IconButton(enabled = !isPopped, onClick = { isPopped = true }) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = stringResource(R.string.back),
        )
    }
}

@Composable
private fun BackHandlerSetter(enabled: Boolean = true, callback: () -> Unit) {
    BackHandler(enabled = enabled) { callback() }
}

@Composable
fun WithLoading(
    button: @Composable (modifier: Modifier) -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
) {
    if (isLoading) {
        CircularProgressIndicator(modifier = modifier)
    } else {
        button(modifier)
    }
}

@Composable
fun WithLoadingButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    WithLoading(
        button = { m ->
            Button(
                onClick = onClick,
                modifier = m,
                enabled = enabled,
                shape = shape,
                colors = colors,
                elevation = elevation,
                border = border,
                contentPadding = contentPadding,
                interactionSource = interactionSource,
                content = content,
            )
        },
        isLoading = isLoading,
        modifier = modifier,
    )
}