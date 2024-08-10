package com.ogata_k.mobile.winp.presentation.page

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.Composable
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * ルーティング用のヘルパ
 */
fun <R : ISetupRouting> NavGraphBuilder.composableByRouting(
    routing: R,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = routing.routingPath,
        arguments = routing.routingArguments,
        deepLinks = routing.routingDeepLink,
        enterTransition = routing.getEnterTransition(),
        exitTransition = routing.getExitTransition(),
        popEnterTransition = routing.getPopEnterTransition(),
        popExitTransition = routing.getPopExitTransition(),
        content = content,
    )
}

/**
 * ルーティングセットアップ用の基本パラメータ群
 */
interface ISetupRouting {
    val routingPath: String

    val routingArguments: List<NamedNavArgument>
        get() = emptyList()

    val routingDeepLink: List<NavDeepLink>
        get() = emptyList()

    fun getEnterTransition(): (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        null

    fun getExitTransition(): (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        null

    fun getPopEnterTransition(): (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        null

    fun getPopExitTransition(): (@JvmSuppressWildcards AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        null
}

/**
 * ルーティング必須項目用のインターフェース
 */
interface IRouting {
    fun toPath(): String
}
