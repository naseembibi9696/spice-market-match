package com.spicemarket.match.core.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spicemarket.match.presentation.app.AppViewModel
import com.spicemarket.match.presentation.decor.StallDecorViewModel
import com.spicemarket.match.presentation.game.GameViewModel
import com.spicemarket.match.presentation.gameover.GameOverViewModel
import com.spicemarket.match.presentation.menu.MenuViewModel
import com.spicemarket.match.presentation.recipes.RecipeBookViewModel
import com.spicemarket.match.presentation.settings.SettingsViewModel
import com.spicemarket.match.presentation.splash.SplashViewModel

object AppViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val created: ViewModel = when (modelClass) {
            AppViewModel::class.java -> AppViewModel()
            SplashViewModel::class.java -> SplashViewModel()
            MenuViewModel::class.java -> MenuViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.settingsRepository,
                ServiceLocator.buildStallOrder
            )
            GameViewModel::class.java -> GameViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.settingsRepository,
                ServiceLocator.buildStallOrder,
                ServiceLocator.generateBoard,
                ServiceLocator.findMatches,
                ServiceLocator.applySwap,
                ServiceLocator.findHint,
                ServiceLocator.resolveCascade,
                ServiceLocator.scoreRound
            )
            GameOverViewModel::class.java -> GameOverViewModel(
                ServiceLocator.progressRepository,
                ServiceLocator.settingsRepository
            )
            StallDecorViewModel::class.java -> StallDecorViewModel(
                ServiceLocator.decorRepository,
                ServiceLocator.progressRepository
            )
            RecipeBookViewModel::class.java -> RecipeBookViewModel(
                ServiceLocator.progressRepository
            )
            SettingsViewModel::class.java -> SettingsViewModel(
                ServiceLocator.settingsRepository,
                ServiceLocator.progressRepository,
                ServiceLocator.decorRepository
            )
            else -> throw IllegalArgumentException(modelClass.name)
        }
        return created as T
    }
}
