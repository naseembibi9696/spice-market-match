package com.spicemarket.match.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spicemarket.match.domain.repository.ProgressRepository
import com.spicemarket.match.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class GameOverUiState(
    val coins: Int = 0,
    val bestScore: Int = 0,
    val ordersFilled: Int = 0,
    val reducedMotion: Boolean = false
)

class GameOverViewModel(
    progressRepository: ProgressRepository,
    settingsRepository: SettingsRepository
) : ViewModel() {

    val uiState: StateFlow<GameOverUiState> = combine(
        progressRepository.progress,
        settingsRepository.settings
    ) { progress, settings ->
        GameOverUiState(
            coins = progress.coins,
            bestScore = progress.bestScore,
            ordersFilled = progress.ordersFilled,
            reducedMotion = settings.reducedMotion
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = GameOverUiState(
            coins = progressRepository.progress.value.coins,
            bestScore = progressRepository.progress.value.bestScore,
            ordersFilled = progressRepository.progress.value.ordersFilled,
            reducedMotion = settingsRepository.settings.value.reducedMotion
        )
    )
}
