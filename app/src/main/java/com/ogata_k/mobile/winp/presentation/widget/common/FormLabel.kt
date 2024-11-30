package com.ogata_k.mobile.winp.presentation.widget.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R

@Composable
fun FormLabel(
    isRequired: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isRequired) {
        RequireLabel(modifier)
    } else {
        OptionalLabel(modifier)
    }
}

@Composable
private fun RequireLabel(
    modifier: Modifier = Modifier
) {
    Label(
        labelStatus = stringResource(R.string.required),
        bgColor = colorResource(id = R.color.required_label),
        modifier = modifier,
    )
}

@Composable
private fun OptionalLabel(
    modifier: Modifier = Modifier
) {
    Label(
        labelStatus = stringResource(R.string.optional),
        bgColor = colorResource(id = R.color.optional_label),
        modifier = modifier,
    )
}
