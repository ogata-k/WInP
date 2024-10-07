package com.ogata_k.mobile.winp.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
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

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WInPTheme {
                val navController = rememberNavController()

                SetupRouting(navController = navController)
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

            val workId: Int? = entry.arguments?.getInt(WorkDetailRouting.WORK_ID_KEY)
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
            val workId = entry.arguments?.getInt(WorkEditRouting.WORK_ID_KEY)
            if (workId != null) {
                vm.setWorkId(workId)
            }
            vm.initializeVM()

            WorkEditScreen(navController = navController, viewModel = vm)
        }
    }
}