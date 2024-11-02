package com.ogata_k.mobile.winp.presentation.activity

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ogata_k.mobile.winp.common.constant.AsCreate
import com.ogata_k.mobile.winp.presentation.event.EventBus
import com.ogata_k.mobile.winp.presentation.event.toast.ToastEvent
import com.ogata_k.mobile.winp.presentation.page.composableByRouting
import com.ogata_k.mobile.winp.presentation.page.work.detail.WorkDetailRouting
import com.ogata_k.mobile.winp.presentation.page.work.detail.WorkDetailScreen
import com.ogata_k.mobile.winp.presentation.page.work.detail.WorkDetailVM
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditRouting
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditScreen
import com.ogata_k.mobile.winp.presentation.page.work.edit.WorkEditVM
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexRouting
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexScreen
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexVM
import com.ogata_k.mobile.winp.presentation.theme.WInPTheme
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WInPTheme {
                val navController = rememberNavController()

                SetupRouting(navController = navController)
                SetupToastNotifier()
            }
        }
    }
}

@Composable
fun SetupRouting(navController: NavHostController) {
    NavHost(navController = navController, startDestination = WorkIndexRouting.routingPath) {
        // Workの一覧
        composableByRouting(WorkIndexRouting) { _ ->
            val vm: WorkIndexVM = hiltViewModel()
            vm.initializeVM()
            WorkIndexScreen(navController = navController, viewModel = vm)
        }

        // Workの詳細
        composableByRouting(WorkDetailRouting) { entry ->
            val vm: WorkDetailVM = hiltViewModel()

            val workId: Long? = entry.arguments?.getLong(WorkDetailRouting.WORK_ID_KEY)
            if (workId != null) {
                vm.setWorkId(workId)
            }
            vm.initializeVM()

            WorkDetailScreen(navController = navController, viewModel = vm)
        }

        // Workの作成編集
        composableByRouting(WorkEditRouting) { entry ->
            val vm: WorkEditVM = hiltViewModel()

            // デフォルトは作成
            val workId: Long =
                entry.arguments?.getLong(WorkEditRouting.WORK_ID_KEY) ?: AsCreate.CREATING_ID
            vm.setWorkId(workId)
            vm.initializeVM()

            WorkEditScreen(navController = navController, viewModel = vm)
        }
    }
}

@Composable
fun SetupToastNotifier() {
    val toastContext = LocalContext.current
    var receivedDateTime by remember { mutableStateOf(LocalDateTime.now()) }
    val events = remember { mutableStateListOf<ToastEvent>() }
    EventBus.onEvent<ToastEvent>(LocalLifecycleOwner.current) {
        events.add(it)
        receivedDateTime = LocalDateTime.now()
    }

    val message = events.firstOrNull()?.toMessage()
    LaunchedEffect(receivedDateTime, message) {
        if (message != null) {
            Toast.makeText(
                toastContext,
                message,
                LENGTH_LONG
            ).show()

            events.removeAt(0)
        }
    }
}