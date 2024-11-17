package com.ogata_k.mobile.winp.presentation.widgert.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.ogata_k.mobile.winp.R

@Composable
fun CounterText(
    current: Int,
    max: Int,
    modifier: Modifier = Modifier,
) {
    val countFormat = stringResource(R.string.counter_format)
    TitleMediumText(
        text = countFormat.format(current, max),
        modifier = modifier,
    )
}

@Composable
fun WithCounterTitle(
    title: String,
    current: Int,
    max: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Bottom,
    ) {
        FormTitle(
            modifier = Modifier.weight(1f),
            title = title,
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
        CounterText(
            current = current,
            max = max,
        )
        Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_small)))
    }
}

@Composable
fun FormBlock(
    title: String,
    // 自前でハンドリングしたい場合や非表示の場合はnullを指定する
    isRequired: Boolean? = false,
    errorMessage: String? = null,
    formTitleAndError: @Composable (title: String, errorMessage: String?) -> Unit = { t, e ->
        Row(verticalAlignment = Alignment.Bottom) {
            FormTitle(title = t, modifier = Modifier.weight(1f))
        }

        if (e != null) {
            FormErrorText(text = e)
        }
    },
    content: @Composable (RowScope.(errorMessage: String?) -> Unit),
) {
    if (isRequired != null) {
        FormLabel(isRequired = isRequired)
    }
    formTitleAndError(title, errorMessage)
    Row(
        modifier = Modifier
            .padding(
                vertical = dimensionResource(id = R.dimen.padding_medium),
                horizontal = dimensionResource(id = R.dimen.padding_small),
            ),
    ) {
        content(errorMessage)
    }
}
