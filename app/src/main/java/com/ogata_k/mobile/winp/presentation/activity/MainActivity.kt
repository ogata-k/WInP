package com.ogata_k.mobile.winp.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.presentation.constant.DummyID
import com.ogata_k.mobile.winp.presentation.event.EventBus
import com.ogata_k.mobile.winp.presentation.event.toast.ToastEvent
import com.ogata_k.mobile.winp.presentation.page.composableByRouting
import com.ogata_k.mobile.winp.presentation.page.setting.notification.NotificationSettingRouting
import com.ogata_k.mobile.winp.presentation.page.setting.notification.NotificationSettingScreen
import com.ogata_k.mobile.winp.presentation.page.setting.notification.NotificationSettingVM
import com.ogata_k.mobile.winp.presentation.page.work.detail.WorkDetailRouting
import com.ogata_k.mobile.winp.presentation.page.work.detail.WorkDetailScreen
import com.ogata_k.mobile.winp.presentation.page.work.detail.WorkDetailVM
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditRouting
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditScreen
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditVM
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexRouting
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexScreen
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexVM
import com.ogata_k.mobile.winp.presentation.page.work.summary.WorkSummaryRouting
import com.ogata_k.mobile.winp.presentation.page.work.summary.WorkSummaryScreen
import com.ogata_k.mobile.winp.presentation.page.work.summary.WorkSummaryVM
import com.ogata_k.mobile.winp.presentation.theme.WInPTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.format.DateTimeParseException

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WInPTheme {
                val navController = rememberNavController()

                SetupRouting(navController = navController, intent = intent)
                SetupToastNotifier()
            }
        }
    }
}

@Composable
fun SetupRouting(navController: NavHostController, intent: Intent) {
    NavHost(navController = navController, startDestination = WorkIndexRouting.routingPath) {
        //
        // work関連
        //

        // Workのサマリー
        composableByRouting(WorkSummaryRouting) { _ ->
            val vm: WorkSummaryVM = hiltViewModel()

            LaunchedEffect(Unit) {
                vm.initializeVM()
            }
            WorkSummaryScreen(navController = navController, viewModel = vm)
        }

        // Workの一覧
        composableByRouting(WorkIndexRouting) { _ ->
            val vm: WorkIndexVM = hiltViewModel { factory: WorkIndexVM.WorkIndexVMFactory ->
                val initialSearchDate =
                    intent.getStringExtra(WorkIndexRouting.SEARCH_DATE_INTENT_EXTRA_KEY)?.let {
                        try {
                            return@let LocalDate.parse(it)
                        } catch (e: DateTimeParseException) {
                            Log.e(WorkIndexRouting.toString(), e.toString())
                        }

                        return@let null
                    } ?: LocalDate.now()

                factory.create(initialSearchDate)
            }

            LaunchedEffect(Unit) {
                vm.initializeVM()
            }
            WorkIndexScreen(navController = navController, viewModel = vm)
        }

        // Workの詳細
        composableByRouting(WorkDetailRouting) { entry ->
            val vm: WorkDetailVM = hiltViewModel()

            LaunchedEffect(Unit) {
                val workId: Long? = entry.arguments?.getLong(WorkDetailRouting.WORK_ID_KEY)
                if (workId != null) {
                    vm.setWorkId(workId)
                }
                vm.initializeVM()
            }
            WorkDetailScreen(navController = navController, viewModel = vm)
        }

        // Workの作成編集
        composableByRouting(WorkEditRouting) { entry ->
            val vm: WorkEditVM = hiltViewModel()

            LaunchedEffect(Unit) {
                // デフォルトは作成
                val workId: Long =
                    entry.arguments?.getLong(WorkEditRouting.WORK_ID_KEY) ?: AsCreate.CREATING_ID
                val copyFromWorkId: Long? =
                    entry.arguments?.getLong(WorkEditRouting.COPY_FROM_WORK_ID_KEY)?.let {
                        if (it == DummyID.INVALID_ID) {
                            // 不正な入力の場合なのでnullに変換しておく
                            null
                        } else {
                            it
                        }
                    }
                vm.setWorkId(workId)
                if (copyFromWorkId != null) {
                    vm.setCopyFromWorkId(copyFromWorkId)
                }
                vm.initializeVM()
            }
            WorkEditScreen(navController = navController, viewModel = vm)
        }


        //
        // 設定関連
        //

        // 通知設定
        composableByRouting(NotificationSettingRouting) { _ ->
            val vm: NotificationSettingVM = hiltViewModel()
            vm.initializeVM()

            NotificationSettingScreen(navController, vm)
        }
    }
}

@Composable
fun SetupToastNotifier() {
    val toastContext = LocalContext.current
    val eventLifecycle = LocalLifecycleOwner.current
    val events = remember { mutableStateListOf<ToastEvent>() }
    LaunchedEffect(Unit) {
        EventBus.onEvent<ToastEvent>(eventLifecycle) {
            events.add(it)
        }
    }

    val event = events.firstOrNull()
    val message = event?.toMessage()
    LaunchedEffect(event) {
        if (message != null) {
            Toast.makeText(
                toastContext, message, LENGTH_LONG
            ).show()

            events.removeAt(0)
        }
    }
}