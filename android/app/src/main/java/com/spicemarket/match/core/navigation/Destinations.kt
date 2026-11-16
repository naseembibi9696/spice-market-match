package com.spicemarket.match.core.navigation

import com.spicemarket.match.domain.model.RoundResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val roundId: Int) : Screen
    data class GameOver(val result: RoundResult) : Screen
    data object StallDecor : Screen
    data object RecipeBook : Screen
    data object Settings : Screen
}
