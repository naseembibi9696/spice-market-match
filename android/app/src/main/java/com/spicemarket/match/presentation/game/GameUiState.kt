package com.spicemarket.match.presentation.game

import com.spicemarket.match.core.config.GameConfig
import com.spicemarket.match.domain.model.Jar
import com.spicemarket.match.domain.model.OrderLine
import com.spicemarket.match.domain.model.RoundResult

enum class GamePhase {
    Loading,
    Ready,
    Selected,
    Swapping,
    Resolving,
    Invalid,
    Finished
}

data class GameUiState(
    val board: List<Jar> = emptyList(),
    val cols: Int = GameConfig.COLS,
    val rows: Int = GameConfig.ROWS,
    val selected: Int? = null,
    val clearing: Set<Int> = emptySet(),
    val invalid: Set<Int> = emptySet(),
    val hinted: Set<Int> = emptySet(),
    val orderLines: List<OrderLine> = emptyList(),
    val stall: Int = 1,
    val movesLeft: Int = 0,
    val score: Int = 0,
    val coinsEarned: Int = 0,
    val hintsLeft: Int = GameConfig.HINTS_PER_LEVEL,
    val phase: GamePhase = GamePhase.Loading,
    val paused: Boolean = false,
    val reducedMotion: Boolean = false
) {
    val interactive: Boolean
        get() = phase == GamePhase.Ready || phase == GamePhase.Selected
}

sealed interface GameEvent {
    data class RoundFinished(val result: RoundResult) : GameEvent
}
