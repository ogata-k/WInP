package com.ogata_k.mobile.winp.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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
    NavHost(navController = navController, startDestination = "screen1") {
        composable(
            route = "screen1"
        ) { _ ->
            Screen1(navController = navController)
        }
        composable(
            route = "screen2/{name}",
            arguments = listOf(navArgument("name") { type = NavType.StringType })
        ) { backStackEntry ->
            val name: String = backStackEntry.arguments?.getString("name") ?: "android"
            Screen2(navController = navController, name = name)
        }
    }
}

@Composable
fun Screen1(navController: NavHostController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column {
            Greeting(
                name = "android",
                modifier = Modifier.padding(innerPadding),
            )
            Button(onClick = {
                navController.navigate("screen2/HOGE")
            }) {
                Text(text = "TAP HERE")
            }
        }
    }
}

@Composable
fun Screen2(navController: NavHostController, name: String) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column {
            Greeting(
                name = name,
                modifier = Modifier.padding(innerPadding),
            )
            Button(onClick = {
                navController.navigate("screen1")
            }) {
                Text(text = "TAP HERE")
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column {
        Text(
            text = "Hello $name!",
            modifier = modifier
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    WInPTheme {
        Greeting("Android")
    }
}