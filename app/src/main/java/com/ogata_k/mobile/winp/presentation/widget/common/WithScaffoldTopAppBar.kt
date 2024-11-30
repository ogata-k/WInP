package com.ogata_k.mobile.winp.presentation.widget.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll

// cf: https://developer.android.com/develop/ui/compose/components/app-bars?hl=ja

/**
 * 中央ぞろえの基本的なAppBarがついたScaffold用
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithScaffoldCenteredSmallTopAppBar(
    // nullとするとタイトルなし
    text: String?,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    // 上部に固定でスケールが変化したりしないので固定したいときに色変化させないように指定する想定
    canChangeColor: Boolean = true,
    scaffoldBuilder: @Composable (scaffoldModifier: Modifier, topAppBar: @Composable () -> Unit) -> Unit,
) {
    if (!canChangeColor) {
        scaffoldBuilder(Modifier) {
            CenterAlignedTopAppBar(
                modifier = modifier,
                colors = colors.copy(
                    scrolledContainerColor = colors.containerColor,
                ),
                title = {
                    if (text != null) {
                        HeadlineMediumText(text = text)
                    }
                },
                navigationIcon = navigationIcon,
                actions = actions,
                windowInsets = windowInsets,
            )
        }
        return
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    scaffoldBuilder(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
        CenterAlignedTopAppBar(
            modifier = modifier,
            colors = colors,
            title = {
                if (text != null) {
                    HeadlineMediumText(text = text)
                }
            },
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            scrollBehavior = scrollBehavior,
        )
    }
}

/**
 * 左寄せの基本的なAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithScaffoldSmallTopAppBar(
    // nullとするとタイトルなし
    text: String?,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    // 上部に固定でスケールが変化したりしないので固定したいときに色変化させないように指定する想定
    canChangeColor: Boolean = true,
    scaffoldBuilder: @Composable (scaffoldModifier: Modifier, topAppBar: @Composable () -> Unit) -> Unit,
) {
    if (!canChangeColor) {
        scaffoldBuilder(Modifier) {
            TopAppBar(
                modifier = modifier,
                colors = colors.copy(
                    scrolledContainerColor = colors.containerColor,
                ),
                title = {
                    if (text != null) {
                        HeadlineMediumText(text = text)
                    }
                },
                navigationIcon = navigationIcon,
                actions = actions,
                windowInsets = windowInsets,
            )
        }
        return
    }

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

    scaffoldBuilder(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
        TopAppBar(
            modifier = modifier,
            colors = colors,
            title = {
                if (text != null) {
                    HeadlineMediumText(text = text)
                }
            },
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            scrollBehavior = scrollBehavior,
        )
    }
}

/**
 * 左寄せの中サイズの基本的なAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithScaffoldMediumTopAppBar(
    text: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.mediumTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    scaffoldBuilder: @Composable (scaffoldModifier: Modifier, topAppBar: @Composable () -> Unit) -> Unit,
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    scaffoldBuilder(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
        MediumTopAppBar(
            modifier = modifier,
            colors = colors,
            title = {
                HeadlineMediumText(text = text)
            },
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            scrollBehavior = scrollBehavior,
        )
    }
}

/**
 * 左寄せの大サイズの基本的なAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WithScaffoldLargeTopAppBar(
    text: String,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    windowInsets: WindowInsets = TopAppBarDefaults.windowInsets,
    colors: TopAppBarColors = TopAppBarDefaults.largeTopAppBarColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        titleContentColor = MaterialTheme.colorScheme.primary,
    ),
    scaffoldBuilder: @Composable (scaffoldModifier: Modifier, topAppBar: @Composable () -> Unit) -> Unit,
) {
    val scrollBehavior =
        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    scaffoldBuilder(Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)) {
        LargeTopAppBar(
            modifier = modifier,
            colors = colors,
            title = {
                HeadlineMediumText(text = text)
            },
            navigationIcon = navigationIcon,
            actions = actions,
            windowInsets = windowInsets,
            scrollBehavior = scrollBehavior,
        )
    }
}
