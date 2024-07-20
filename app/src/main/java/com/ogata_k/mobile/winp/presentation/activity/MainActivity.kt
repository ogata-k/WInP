package com.ogata_k.mobile.winp.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ogata_k.mobile.winp.presentation.page.composableByRouting
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexRouting
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexScreen
import com.ogata_k.mobile.winp.presentation.page.work.index.WorkIndexVM
import com.ogata_k.mobile.winp.presentation.theme.WInPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        composableByRouting(WorkIndexRouting) { _ ->
            val vm: WorkIndexVM = hiltViewModel()
            WorkIndexScreen(navController = navController, viewModel = vm)
        }
    }
}