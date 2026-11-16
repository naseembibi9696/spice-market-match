package com.spicemarket.match.presentation.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spicemarket.match.core.config.GameConfig
import com.spicemarket.match.data.sample.SampleData
import com.spicemarket.match.domain.model.AppSettings
import com.spicemarket.match.domain.model.PlayerProgress
import com.spicemarket.match.domain.repository.ProgressRepository
import com.spicemarket.match.domain.repository.SettingsRepository
import com.spicemarket.match.domain.usecase.BuildStallOrderUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(
    private val progressRepository: ProgressRepository,
    private val settingsRepository: SettingsRepository,
    private val buildStallOrder: BuildStallOrderUseCase
) : ViewModel() {

    val uiState: StateFlow<MenuUiState> = combine(
        progressRepository.progress,
        settingsRepository.settings
    ) { progress, settings ->
        toUiState(progress, settings)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = toUiState(
            progressRepository.progress.value,
            settingsRepository.settings.value
        )
    )

    private fun toUiState(progress: PlayerProgress, settings: AppSettings): MenuUiState {
        val window = GameConfig.STALLS_ON_STREET
        val first = ((progress.stall - 1) / window) * window + 1
        val slots = (first until first + window).map { number ->
            StallSlot(
                number = number,
                spice = SampleData.spices[(number - 1) % SampleData.spices.size],
                state = when {
                    number < progress.stall -> StallSlotState.COMPLETED
                    number == progress.stall -> StallSlotState.CURRENT
                    else -> StallSlotState.LOCKED
                }
            )
        }
        return MenuUiState(
            coins = progress.coins,
            stall = progress.stall,
            ordersFilled = progress.ordersFilled,
            bestScore = progress.bestScore,
            stallPoints = progress.stallPoints,
            stalls = slots,
            orderLines = buildStallOrder(progress.stall).lines,
            reducedMotion = settings.reducedMotion
        )
    }
}
