package com.spicemarket.match.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spicemarket.match.domain.model.AppSettings
import com.spicemarket.match.domain.model.PlayerProgress
import com.spicemarket.match.domain.repository.DecorRepository
import com.spicemarket.match.domain.repository.ProgressRepository
import com.spicemarket.match.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class SettingsUiState(
    val sound: Boolean = true,
    val music: Boolean = true,
    val reducedMotion: Boolean = false,
    val ordersFilled: Int = 0
)

class SettingsViewModel(
    private val settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository,
    private val decorRepository: DecorRepository
) : ViewModel() {

    val uiState: StateFlow<SettingsUiState> = combine(
        settingsRepository.settings,
        progressRepository.progress
    ) { settings, progress ->
        toUiState(settings, progress)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = toUiState(
            settingsRepository.settings.value,
            progressRepository.progress.value
        )
    )

    private val confirmState = MutableStateFlow(false)
    val resetRequested: StateFlow<Boolean> = confirmState.asStateFlow()

    fun setSound(enabled: Boolean) = settingsRepository.setSound(enabled)

    fun setMusic(enabled: Boolean) = settingsRepository.setMusic(enabled)

    fun setReducedMotion(enabled: Boolean) = settingsRepository.setReducedMotion(enabled)

    fun askReset() {
        confirmState.value = true
    }

    fun cancelReset() {
        confirmState.value = false
    }

    fun confirmReset() {
        progressRepository.reset()
        decorRepository.reset()
        confirmState.value = false
    }

    private fun toUiState(settings: AppSettings, progress: PlayerProgress): SettingsUiState =
        SettingsUiState(
            sound = settings.sound,
            music = settings.music,
            reducedMotion = settings.reducedMotion,
            ordersFilled = progress.ordersFilled
        )
}
