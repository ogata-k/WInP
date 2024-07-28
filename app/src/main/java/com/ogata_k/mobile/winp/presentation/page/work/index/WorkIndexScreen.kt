package com.ogata_k.mobile.winp.presentation.page.work.index

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.ogata_k.mobile.winp.R
import com.ogata_k.mobile.winp.common.buildFullDatePatternFormatter
import com.ogata_k.mobile.winp.presentation.model.wip.Work
import com.ogata_k.mobile.winp.presentation.widgert.common.ButtonText
import com.ogata_k.mobile.winp.presentation.widgert.common.DialogOfDatePicker
import com.ogata_k.mobile.winp.presentation.widgert.common.PagingLoadColumn
import com.ogata_k.mobile.winp.presentation.widgert.common.TitleSmallText
import com.ogata_k.mobile.winp.presentation.widgert.common.WithScaffoldSmallTopAppBar
import com.ogata_k.mobile.winp.presentation.widgert.common.fromDateToMills
import com.ogata_k.mobile.winp.presentation.widgert.common.fromMillsToDate
import com.ogata_k.mobile.winp.presentation.widgert.work.WorkItem
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun WorkIndexScreen(navController: NavController, viewModel: WorkIndexVM) {
    val uiState: WorkIndexUiState by viewModel.uiStateFlow.collectAsStateWithLifecycle()

    // collect fLow and remember state and launch event
    val workPagingItems: LazyPagingItems<Work> = uiState.workPagingData.collectAsLazyPagingItems()

    WithScaffoldSmallTopAppBar(
        text = stringResource(id = R.string.app_name),
        canChangeColor = false,
    ) { modifier, appBar ->
        Scaffold(
            modifier = modifier,
            topBar = appBar,
        ) { padding ->
            Column(
                modifier = Modifier.padding(padding),
            ) {
                BuildHeader(
                    uiState = uiState,
                    switchShowDatePickerForSearch = { toShow ->
                        viewModel.showDatePickerForSearch(toShow)
                    },
                    updateAndHideDialogSearchQuery = { date ->
                        viewModel.updateAndHideDialogSearchQuery(
                            workPagingItems,
                            date,
                        )
                    },
                )
                PagingLoadColumn(
                    pagingItems = workPagingItems,
                ) { work ->
                    WorkItem(
                        work, modifier = Modifier.padding(
                            vertical = dimensionResource(id = R.dimen.padding_medium),
                            horizontal = dimensionResource(id = R.dimen.padding_medium_large),
                        )
                    ) {
                        // @todo onClick
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BuildHeader(
    uiState: WorkIndexUiState,
    switchShowDatePickerForSearch: (toShow: Boolean) -> Unit,
    updateAndHideDialogSearchQuery: (date: LocalDate) -> Unit
) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxWidth()
            .padding(dimensionResource(id = R.dimen.padding_medium)),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .clickable { switchShowDatePickerForSearch(true) }
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = stringResource(id = R.string.select_date)
            )
            Spacer(modifier = Modifier.width(dimensionResource(id = R.dimen.padding_medium)))
            TitleSmallText(text = buildFullDatePatternFormatter().format(uiState.searchDate))
        }
        if (uiState.isInSearchDate) {
            val datePickerState = rememberDatePickerState(
                initialSelectedDateMillis = fromDateToMills(uiState.searchDate),
                initialDisplayMode = DisplayMode.Picker,
            )
            DialogOfDatePicker(
                state = datePickerState,
                onDismissRequest = { /* ignore background dismiss */ },
                dismissButton = {
                    Button(onClick = {
                        switchShowDatePickerForSearch(false)
                    }) {
                        ButtonText(text = stringResource(R.string.cancel))
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val dateTimestamp: Long? =
                            datePickerState.selectedDateMillis
                        if (dateTimestamp != null) {
                            updateAndHideDialogSearchQuery(fromMillsToDate(dateTimestamp))
                        }
                    }) {
                        ButtonText(text = stringResource(R.string.ok))
                    }
                },
            )
        }
    }
}