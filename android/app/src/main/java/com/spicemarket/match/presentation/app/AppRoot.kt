package com.spicemarket.match.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spicemarket.match.core.di.AppViewModelFactory
import com.spicemarket.match.core.navigation.Screen
import com.spicemarket.match.presentation.decor.StallDecorScreen
import com.spicemarket.match.presentation.game.GameScreen
import com.spicemarket.match.presentation.gameover.GameOverScreen
import com.spicemarket.match.presentation.menu.MenuScreen
import com.spicemarket.match.presentation.recipes.RecipeBookScreen
import com.spicemarket.match.presentation.settings.SettingsScreen
import com.spicemarket.match.presentation.splash.SplashScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    AnimatedContent(
        targetState = screen,
        label = "screen",
        transitionSpec = {
            when (targetState) {
                is Screen.Game ->
                    slideInHorizontally { full -> full / 4 } + fadeIn(tween(260)) togetherWith
                        fadeOut(tween(220))
                is Screen.GameOver ->
                    scaleIn(initialScale = 0.92f) + fadeIn(tween(280)) togetherWith
                        fadeOut(tween(220))
                else -> fadeIn(tween(300)) togetherWith fadeOut(tween(300))
            }
        }
    ) { target ->
        when (target) {
            is Screen.Splash -> SplashScreen(
                onFinished = { appViewModel.replaceWith(Screen.Menu) }
            )
            is Screen.Menu -> MenuScreen(
                onPlay = { appViewModel.startRound() },
                onDecor = { appViewModel.navigateTo(Screen.StallDecor) },
                onRecipes = { appViewModel.navigateTo(Screen.RecipeBook) },
                onSettings = { appViewModel.navigateTo(Screen.Settings) }
            )
            is Screen.Game -> GameScreen(
                roundId = target.roundId,
                onRoundFinished = { result -> appViewModel.showResult(Screen.GameOver(result)) },
                onMenu = { appViewModel.openMenu() }
            )
            is Screen.GameOver -> GameOverScreen(
                result = target.result,
                onPlayAgain = { appViewModel.restartRound() },
                onMenu = { appViewModel.openMenu() }
            )
            is Screen.StallDecor -> StallDecorScreen(onBack = { appViewModel.back() })
            is Screen.RecipeBook -> RecipeBookScreen(onBack = { appViewModel.back() })
            is Screen.Settings -> SettingsScreen(onBack = { appViewModel.back() })
        }
    }
}
